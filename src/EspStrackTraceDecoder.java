import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class EspStrackTraceDecoder  {
	File tool;
	File elf;

	private static String[] exceptions = {"IllegalInstructionCause",
			"SyscallCause",
			"InstructionFetchErrorCause",
			"LoadStoreErrorCause",
			"Level1InterruptCause",
			"AllocaCause",
			"IntegerDivideByZeroCause",
			"Reserved for Tensilica",
			"PrivilegedCause",
			"LoadStoreAlignmentCause",
			"Reserved for Tensilica",
			"InstrPIFDataErrorCause",
			"LoadStorePIFDataErrorCause",
			"InstrPIFAddrErrorCause",
			"LoadStorePIFAddrErrorCause",
			"InstTLBMissCause",
			"InstTLBMultiHitCause",
			"InstFetchPrivilegeCause",
			"Reserved for Tensilica",
			"InstFetchProhibitedCause",
			"Reserved",
			"Reserved",
			"Reserved",
			"LoadStoreTLBMissCause",
			"LoadStoreTLBMultiHitCause",
			"LoadStorePrivilegeCause",
			"Reserved for Tensilica",
			"LoadProhibitedCause",
	"StoreProhibitedCause"};


	private String[] analyseStracktrace(String content){
		Pattern p = Pattern.compile("40[0-2][0-9a-f]{5}\\b");
		List<String> list = new ArrayList<String>();
		list.add(tool.getAbsolutePath());
		list.add("-aipfC");
		list.add("-e");
		list.add(elf.getAbsolutePath());
		Matcher m = p.matcher(content);
		while(m.find()) {
			list.add(m.group());
		}
		if (list.size() == 4) {
			return null;
		}
		return list.toArray(new String[list.size()]);
	}

	private String analyseException(String content){
		Pattern p = Pattern.compile("Exception \\((28)\\):");
		Matcher m = p.matcher(content);
		String description = "Exception Cause: "; 
		if (!m.find()) {
			description += "Not found";
		} else {
			int nummer = Integer.parseInt(m.group(1));
			description += m.group(1) + " ";
			if (nummer >= 0 && nummer < exceptions.length) {
				description += " [" + exceptions[nummer] + "]";
			} else {
				description += " [Unknown]";
			}
		}
		return description;
	}


	public static void main(String[] args) throws IOException, InterruptedException{
		if (args.length == 0) {
			usage();
			System.exit(1);
		}
		EspStrackTraceDecoder esp = new EspStrackTraceDecoder();
		esp.elf = new File(args[1]);
		esp.tool = new File(args[0]);
		String content = "";
		if (args.length > 2) {
			content = new String(Files.readAllBytes(Paths.get(args[2])));
		} else {
			String line = null;
			while ((line = System.console().readLine()) != null) {
				content = content + line + "\n";
			}
		}
		System.out.println(esp.analyseException(content));
		System.out.println();
		String[] commands = esp.analyseStracktrace(content);
		if (commands == null) {
			System.out.println("No Addresse found in Stracktrace!");
			System.exit(1);
		}

		ProcessBuilder   ps=new ProcessBuilder(commands);
		ps.redirectErrorStream(true);
		Process pr = ps.start();  

		BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()));
		String line;
		while ((line = in.readLine()) != null) {
			System.out.println(line);
		}
		pr.waitFor();
		in.close();		
	}

	private static void usage() {
		System.out.println("Usage:");
		System.out.println("java -jar EspEception <Path to xtensa-lx106-elf-addr2line> <Elf-File> <Dump of Exception>");
	}   
}
