#!/bin/bash

if (($# != 4)); then
	echo "Arguments: logs_dir, num_servers, tp_py, tp_gnuplot"
	exit 1
fi

logs_dir=$1
ns=$((10#$2))
tp_py=$3
tp_gnuplot=$4

cd $logs_dir

echo "Processing throughput logs..."
ARR=()
i=1
while (($i <= $ns)); do
	ARR+=("throughput$i.log")
	i=$(($i + 1))
done
echo "Running throughput python script: $tp_py"
sort -n ${ARR[@]} | python $tp_py > throughput.dat
echo "Running throughput gnuplot script: $tp_gnuplot"
gnuplot $tp_gnuplot
echo "Done processing throughput logs."
echo ""
