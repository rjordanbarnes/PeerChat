#!/bin/bash

if [ "$#" -ne 1 ]; then
	echo "Usage: $0 PORT"
	exit 1
fi

javac -sourcepath src -d "out/production/Peer Chat" src/Applications/RendezvousServerConsoleApp.java
java -classpath "out/production/Peer Chat" Applications.RendezvousServerConsoleApp $1
