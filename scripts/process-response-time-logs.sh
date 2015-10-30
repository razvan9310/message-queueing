#!/bin/bash

if (($# != 4)); then
	echo "Arguments: logs_dir, num_clients, rt_py, rt_gnuplot"
	exit 1
fi

logs_dir=$1
nc=$((10#$2))
rt_py=$3
rt_gnuplot=$4

cd $logs_dir

echo "Processing response time logs..."
ARR=()
i=0
while (($i < $nc)); do
	ARR+=("requests$i.log")
	i=$(($i + 1))
done
echo "Running response time python script: $rt_py"
sort -n ${ARR[@]} | python $rt_py > response-time.dat
echo "Running response time gnuplot script: $rt_gnuplot"
gnuplot $rt_gnuplot
echo "Done processing response time logs."
echo ""