#!/bin/bash

if (($# != 4)); then
	echo "Arguments: logs_dir, num_servers, rt_py, rt_gnuplot"
	exit 1
fi

logs_dir=$1
ns=$((10#$2))
rt_py=$3
rt_gnuplot=$4

cd $logs_dir

echo "Processing db response time logs..."
ARR=()
i=0
while (($i < $ns)); do
	ARR+=("db-response-time$i.log")
	i=$(($i + 1))
done
echo "Running db response time python script: $rt_py"
sort -n ${ARR[@]} | python $rt_py > db-response-time.dat
echo "Running db response time gnuplot script: $rt_gnuplot"
gnuplot $rt_gnuplot
echo "Done processing db response time logs."
echo ""
