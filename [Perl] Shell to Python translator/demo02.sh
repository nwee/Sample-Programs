#!/bin/bash
sum=15
for n in "$@" # use "$@" to preserve args
do
	sum=`expr $sum + "$n"`
done
echo $sum

echo allshell files!

for c_file in *.sh
do
    echo gcc -c $c_file
done
