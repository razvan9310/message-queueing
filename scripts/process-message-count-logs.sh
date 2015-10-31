#!/bin/bash

if (($# != 4)); then
	echo "Arguments: logs_dir, num_servers, msgcount_py, msgcount_gnuplot"
	exit 1
fi

logs_dir=$1
ns=$((10#$2))
msgcount_py=$3
msgcount_gnuplot=$4

cd $logs_dir

echo "Processing message count logs..."
ARR=()
i=1
while (($i <= $ns)); do
	ARR+=("messages-count$i.log")
	i=$(($i + 1))
done
echo "Running msg count python script: $msgcount_py"
sort -n ${ARR[@]} | python $msgcount_py > message-count.dat
echo "Running msg count gnuplot script: $msgcount_gnuplot"
gnuplot $msgcount_gnuplot
echo "Done processing msg count logs."
echo ""
