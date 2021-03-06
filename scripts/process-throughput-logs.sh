#!/bin/bash

if (($# != 3)); then
	echo "Arguments: logs_dir, tp_py, tp_gnuplot"
	exit 1
fi

logs_dir=$1
tp_py=$2
tp_gnuplot=$3

cd $logs_dir

echo "Running throughput python script: $tp_py"
sort -n $logs_dir/*.log | python $tp_py > throughput.dat
echo "Running throughput gnuplot script: $tp_gnuplot"
gnuplot $tp_gnuplot
echo "Done processing throughput logs."
echo ""
