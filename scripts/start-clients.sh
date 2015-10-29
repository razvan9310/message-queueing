#!/bin/bash

if (($# != 7)); then
	echo "Arguments: path_to_client, logs_dest, num_clients, num_servers, run_time, msg_length, log_response_time"
	exit 1	
fi

trap 'kill $(jobs -p)' EXIT

client_path=$1
logs_dest=$2
n=$((10#$3))
ns=$((10#$4))
t=$((10#$5))
msg_len=$((10#$6))
log_resp_time=$((10#$7))

hosts=$()
ports=$()
i=0
j=0
k=0
while IFS= read -r line; do
	if (($i % 2 == 0)); then
		hosts[$j]="$line"
		j=$(($j + 1))
	else
		ports[$k]="$line"
		k=$(($k + 1))
	fi
	i=$(($i + 1))
done

rm -rf $logs_dest && mkdir -p $logs_dest
cd $logs_dest

i=0
j=0
while (($i < $n)); do
	mod1=$(($i % $ns))
	cmd="java -jar $client_path ${hosts[$mod1]} ${ports[$mod1]} $i $n $msg_len $log_resp_time > /dev/null &"
	eval $cmd
	i=$(($i + 1))
done

echo "Running $n clients on $ns servers for a total of $t seconds."
echo "Message length set to $msg_len."
echo "Log data will be available at $logs_dest"
i=0
while (($i < $t)); do
	echo -ne "$(($t-$i)) seconds remaining... \\r"
	i=$(($i + 1))
	sleep 1s
done

echo "Done.                         "
