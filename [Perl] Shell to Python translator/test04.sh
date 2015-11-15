#!/bin/bash

number=0
finish=100
grow=1
while test $number -le $finish
do
    echo $number
    number=`expr $number + $grow + 1`  # increment number
    grow=`expr $grow + 2` 			#growth rate
done
