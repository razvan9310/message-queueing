#!/bin/bash

trap 'kill $(jobs -p)' EXIT

client_path=$1
logs_dest=$2
n=$((10#$3))
t=$((10#$4))
i=0
rm -rf ~/ASL/logs && mkdir -p ~/ASL/logs
while (($i < $n)); do
	if (($i % 2 == 0)); then
		cd $logs_dest
		java -jar $client_path dryad01.ethz.ch 10002 $i $n > /dev/null &
	else
		cd $logs_dest
		java -jar $client_path dryad02.ethz.ch 10002 $i $n > /dev/null &
	fi
	i=$(($i + 1))
done

echo "Running a total of $n clients for a total of $t seconds."
echo "Log data will be available at ~/ASL/logs/"
i=0
while (($i < $t)); do
	echo -ne "$(($t-$i)) seconds remaining... \\r"
	i=$(($i + 1))
	sleep 1s
done

echo "Done.   
