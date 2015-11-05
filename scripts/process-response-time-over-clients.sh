#!/bin/bash

if (($# != 4)); then
	echo "Arguments: clients_delay, logs_dir, rt_py, rt_gnuplot"
	exit 1
fi

delay=$((10#$1))
logs_dir=$2
rt_py=$3
rt_gnuplot=$4

cd $logs_dir

echo "Running response time python script: $rt_py"
sort -n $logs_dir/*.log | python $rt_py $delay > response-time.dat
echo "Running response time gnuplot script: $rt_gnuplot"
gnuplot $rt_gnuplot
echo "Done processing response time logs."
echo ""
