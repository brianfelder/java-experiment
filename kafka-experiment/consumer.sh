#!/bin/bash

mvn package exec:java -Dexec.mainClass="net.felder.keymapping.ix.IxKafkaConsumer"
