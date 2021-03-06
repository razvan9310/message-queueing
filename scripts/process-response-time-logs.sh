#!/bin/bash

if (($# != 3)); then
	echo "Arguments: logs_dir, rt_py, rt_gnuplot"
	exit 1
fi

logs_dir=$1
rt_py=$2
rt_gnuplot=$3

cd $logs_dir

echo "Running response time python script: $rt_py"
sort -n $logs_dir/*.log | python $rt_py > response-time.dat
echo "Running response time gnuplot script: $rt_gnuplot"
gnuplot $rt_gnuplot
echo "Done processing response time logs."
echo ""
