#!/bin/bash

number=0
Astley='cool' # He is very cool

for word in Never Gonna Give You Up
do
	while [ $number -le 5 ] # Chanting of the crowd
	do
		if test Rick == Roll
		then
			echo correct
		elif test $Astley != lame
		then
			echo yeah!
		else
			echo error
		fi
		number=`expr $number + 1`
	done
	echo "Rick says: $word~"
done
