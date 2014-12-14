#!/bin/bash

export _JAVA_OPTIONS="-Xms64m -Xmx128m"

if [ -f ./apache-maven-3.2.3/bin/mvn ]; then
    echo "Maven downloaded! Skipping"
else
    wget http://apache.miloslavbrada.cz/maven/maven-3/3.2.3/binaries/apache-maven-3.2.3-bin.tar.gz

    tar -xf apache-maven-3.2.3-bin.tar.gz
fi

./apache-maven-3.2.3/bin/mvn clean process-resources install

if [ $1 == "with-dependencies" ]; then
    export _JAVA_OPTIONS="-Xms256m -Xmx512m"
    ./apache-maven-3.2.3/bin/mvn assembly:assembly
fi
