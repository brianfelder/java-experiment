#!/bin/bash

mvn package exec:java -Dexec.mainClass="net.felder.keymapping.ix.sinkhandler.IxDataSinkHandler"
