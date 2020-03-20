#!/bin/bash

javac -sourcepath src -d "out/production/Peer Chat" src/Applications/ChatGUIApp.java
java -classpath "out/production/Peer Chat" Applications.ChatGUIApp
