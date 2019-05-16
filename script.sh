#!/bin/bash
for i in $(ls Beispiele/ | grep -v ".out") 
do 
  echo $i
  java -jar ./groPro.jar $i Beispiele/
done
