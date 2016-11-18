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


public class EspStackTraceDecoder  {
	File tool;
	File elf;

	private static String[] exceptions = {
		    "Illegal instruction",
		    "SYSCALL instruction",
		    "InstructionFetchError: Processor internal physical address or data error during instruction fetch",
		    "LoadStoreError: Processor internal physical address or data error during load or store",
		    "Level1Interrupt: Level-1 interrupt as indicated by set level-1 bits in the INTERRUPT register",
		    "Alloca: MOVSP instruction, if caller's registers are not in the register file",
		    "IntegerDivideByZero: QUOS, QUOU, REMS, or REMU divisor operand is zero",
		    "reserved",
		    "Privileged: Attempt to execute a privileged operation when CRING ? 0",
		    "LoadStoreAlignmentCause: Load or store to an unaligned address",
		    "reserved",
		    "reserved",
		    "InstrPIFDataError: PIF data error during instruction fetch",
		    "LoadStorePIFDataError: Synchronous PIF data error during LoadStore access",
		    "InstrPIFAddrError: PIF address error during instruction fetch",
		    "LoadStorePIFAddrError: Synchronous PIF address error during LoadStore access",
		    "InstTLBMiss: Error during Instruction TLB refill",
		    "InstTLBMultiHit: Multiple instruction TLB entries matched",
		    "InstFetchPrivilege: An instruction fetch referenced a virtual address at a ring level less than CRING",
		    "reserved",
		    "InstFetchProhibited: An instruction fetch referenced a page mapped with an attribute that does not permit instruction fetch",
		    "reserved",
		    "reserved",
		    "reserved",
		    "LoadStoreTLBMiss: Error during TLB refill for a load or store",
		    "LoadStoreTLBMultiHit: Multiple TLB entries matched for a load or store",
		    "LoadStorePrivilege: A load or store referenced a virtual address at a ring level less than CRING",
		    "reserved",
		    "LoadProhibited: A load referenced a page mapped with an attribute that does not permit loads",
		    "StoreProhibited: A store referenced a page mapped with an attribute that does not permit stores"
		};


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
		Pattern p = Pattern.compile("Exception \\(([0-9]*)\\):");
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
		EspStackTraceDecoder esp = new EspStackTraceDecoder();
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
