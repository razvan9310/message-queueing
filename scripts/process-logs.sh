#!/bin/bash

if (($# < 5)); then
	echo "Arguments: logs_dir, num_clients, num_servers, resp_time, throughput[, rt_py, rt_gnuplot][, tp_py, tp_gnuplot]"
	exit 1	
fi

logs_dir=$1
nc=$((10#$2))
ns=$((10#$3))

resp_time=$((10#$4))
throughput=$((10#$5))
if (($resp_time == 1)); then
	rt_py=$6
	rt_gnuplot=$7
	if (($throughput == 1)); then
		tp_py=$8
		tp_gnuplot=$9
	fi
elif (($throughput == 1)); then
	tp_py=$6
	tp_gnuplot=$7
else
	echo "No logs to process."
	exit 0
fi

cd $logs_dir

if (($resp_time == 1)); then
	echo "Processing response time logs..."
	ARR=()
	i=0
	while (($i < $nc)); do
		ARR+=("requests$i.log")
		i=$(($i + 1))
	done
	echo "Running response time python script: $rt_py"
	sort -n ${ARR[@]} | python $rt_py > response-time.dat
	echo "Running response time gnuplot script..."
	gnuplot $rt_gnuplot
	echo "Done processing response time logs."
	echo ""
fi

if (($throughput == 1)); then
	echo "Processing throughput logs..."
	ARR=()
	i=1
	while (($i <= $ns)); do
		ARR+=("throughput$i.log")
		i=$(($i + 1))	
	done
	echo "Running throughput python script: $tp_py"
	sort -n ${ARR[@]} | python $tp_py > throughput.dat
	echo "Running throughput gnuplot script..."
	gnuplot $tp_gnuplot
	echo "Done processing throughput logs."
	echo ""
fi
