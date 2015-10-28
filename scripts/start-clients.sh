#!/bin/bash

if (($# != 6)); then
	echo "Arguments: path_to_client, logs_dest, num_clients, num_servers, run_time, msg_length"
	exit 1	
fi

trap 'kill $(jobs -p)' EXIT

client_path=$1
logs_dest=$2
n=$((10#$3))
ns=$((10#$4))
t=$((10#$5))
msg_len=$((10#$6))

read servers_list
read ports_list

rm -rf $logs_dest && mkdir -p $logs_dest
cd $logs_dest

i=0
while (($i < $n)); do
	java -jar $client_path ${servers_list[$(($i % $ns))]} ${ports_list[$(($i % $ns))]} $i $n $msg_len > /dev/null &
	i=$(($i + 1))
done

echo "Running $n clients for a total of $t seconds."
echo "Message length set to $msg_len."
echo "Log data will be available at $logs_dest"
i=0
while (($i < $t)); do
	echo -ne "$(($t-$i)) seconds remaining... \\r"
	i=$(($i + 1))
	sleep 1s
done

echo "Done.                         "
