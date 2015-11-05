#!/bin/bash

if (($# != 4)); then
	echo "Arguments: clients_delay, logs_dir, tp_py, tp_gnuplot"
	exit 1
fi

delay=$((10#$1))
logs_dir=$2
tp_py=$3
tp_gnuplot=$4

cd $logs_dir

echo "Running throughput python script: $tp_py"
sort -n $logs_dir/*.log | python $tp_py $delay > throughput.dat
echo "Running throughput gnuplot script: $tp_gnuplot"
gnuplot $tp_gnuplot
echo "Done processing throughput logs."
echo ""
