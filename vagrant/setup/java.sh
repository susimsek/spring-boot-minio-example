#!/bin/bash

sudo apt install openjdk-11-jdk -y

# If JAVA_HOME already set then DO Not set it again
if [ -z $JAVA_HOME ]
then
    echo "export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64" >> ~/.profile
    echo "export PATH=$PATH:/usr/lib/jvm/java-11-openjdk-amd64/bin" >> ~/.profile
else
    echo "======== No Change made to .profile ====="
fi

echo "======= Done. PLEASE LOG OUT & LOG Back In ===="
echo "Then validate by executing    'java -version'"