#!/bin/bash

if (($# != 2)); then
	echo "Arguments: path_to_logfiles, path_to_py_script"
	exit 1
fi

logdir=$1
pyscript=$2

sort -n $logdir/*.log | python $pyscript
