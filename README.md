# Command-line Decoder for Strack Trace from ESP8266

based on the work from https://github.com/me-no-dev/EspExceptionDecoder, 
I rewrote the decoder completely for command-line usage.


# Status  [![Build Status](https://travis-ci.org/littleyoda/EspStackTraceDecoder.svg?branch=master)](https://travis-ci.org/littleyoda/EspStackTraceDecoder)
- works for me (I'm using Eclipse for ESP8266)
- not much tested
- no good error handling
- no documentation


## Usage

Download the jar: https://github.com/littleyoda/EspStrackTraceDecoder/releases/latest

java -jar EspStackTraceDecoder.jar \<Path to xtensa-lx106-elf-addr2line> \<Elf-File> \<Dump of Exception>

For a program created within eclipse, I use the following command-line:
java -jar EspStackTraceDecoder.jar /home/XXXX/.arduino15/packages/esp8266/tools/xtensa-lx106-elf-gcc/1.20.0-26-gb404fb9-2/bin/xtensa-lx106-elf-addr2line /home/XXXX/project/Release/project.elf /tmp/dump.txt
