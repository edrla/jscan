# jscan (Java based multi-threaded port scanner)

A Java 25 based CLI-tool for scanning open ports of a given IPv4 address.

## Install
Move the contents of the `jscan/bin` directory into a installation directory of your choice. For Windows, one can omit the `jscan` file, for Linux the `jscan.bat` file. Add the installation directory to the respective PATH environment variable.

## Run
In a terminal, running `jscan -h` or `jscan --help`outputs:
```
Usage:
Quick Scan (ports 1 - 1024): jscan <ipv4-address>
Custom port range: jscan <ipv4-address> <start-port> <end-port>
```

## Build
A prebuilt `jscan-1.0-jar` file is given in the `jscan/bin` directory. To build this file oneself, download the Maven project and run `mvn clean package`.
