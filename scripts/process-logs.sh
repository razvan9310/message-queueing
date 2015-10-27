#!/bin/bash

logs_dir=$1
n=$((10#$2))
py_script=$3
gnuplot_script=$4

ARR=()
i=0

cd $logs_dir

while (($i < $n)); do
	ARR+=("requests$i.log")
	i=$(($i + 1))
done

sort -n ${ARR[@]} | python $py_script > log.dat
gnuplot $gnuplot_script
