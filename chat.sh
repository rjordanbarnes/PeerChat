#!/bin/bash

if [ "$#" -lt 3 ]; then
	echo "Usage: $0 ADDRESS PORT PEERNAME OPTIONALPORT"
	exit 1
fi

javac -sourcepath src -d "out/production/Peer Chat" src/Applications/ChatConsoleApp.java
java -classpath "out/production/Peer Chat" Applications.ChatConsoleApp $1 $2 $3 $4
