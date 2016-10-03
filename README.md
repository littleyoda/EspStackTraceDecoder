# Command-line Decoder for Strack Trace from ESP8266

based on the work from https://github.com/me-no-dev/EspExceptionDecoder, 
I rewrite the decoder completly for the command-line.

## Usage
java -jar EspEception <Path to xtensa-lx106-elf-addr2line> <Elf-File> <Dump of Exception>

For a program created within eclipse, I use the following command-line:
java -jar EspStrackTraceDecoder.jar /home/XXXX/.arduino15/packages/esp8266/tools/xtensa-lx106-elf-gcc/1.20.0-26-gb404fb9-2/bin/xtensa-lx106-elf-addr2line /home/XXXX/project/Release/project.elf /tmp/dump.txt
