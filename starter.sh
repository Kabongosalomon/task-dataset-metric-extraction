#!/bin/bash 
wget https://github.com/kermitt2/grobid/archive/0.6.0.zip
unzip 0.6.0.zip
mv grobid-0.6.0 ../
rm 0.6.0.zip
mkdir -p build/libs
wget --load-cookies /tmp/cookies.txt "https://docs.google.com/uc?export=download&confirm=$(wget --quiet --save-cookies /tmp/cookies.txt --keep-session-cookies --no-check-certificate 'https://docs.google.com/uc?export=download&id=1xxXlJGz6EElOZAnLgj-lHAiYssvecGfe' -O- | sed -rn 's/.*confirm=([0-9A-Za-z_]+).*/\1\n/p')&id=1xxXlJGz6EElOZAnLgj-lHAiYssvecGfe" -O build/libs/task-dataset-metric-extraction-1.0.jar && rm -rf /tmp/cookies.txt