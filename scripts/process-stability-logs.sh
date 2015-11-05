#!/bin/bash

if (($# != 3)); then
	echo "Arguments: (db-)response-time.dat (db-)throughput.dat stability_gnuplot"
	exit 1
fi

rt=$1
tp=$2
gp=$3

cp $rt .
cp $tp .
gnuplot $gp

