#!/bin/bash

if (($# != 3)); then
	echo "Arguments: response-time-200.dat, response-time-2000.dat, gnuplot_script"
	exit 1
fi

rt200=$1
rt2000=$2
gp=$3

cp $rt200 response-time-200.dat
cp $rt2000 response-time-2000.dat
gnuplot $gp

