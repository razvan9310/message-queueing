#!/bin/bash

if (($# != 11)); then
	echo "Arguments are: amazon_key_path, database_url, server_jar, num_servers, run_time, port, log_throughput, log_db_response_time, log_db_throughput, log_db_msg_count, dest_log_dir"
	exit 1
fi

key=$1
db_url=$2
server_jar=$3
ns=$((10#$4))
run_time=$((10#$5))
port=$6
log_tp=$7
log_db_rt=$8
log_db_tp=$9
log_msgcount=${10}
dest_log_dir=${11}

hosts=()
while IFS= read -r line; do
        hosts+=("$line")
done

i=0
while (($i < $ns)); do
	scp -i $key $server_jar ${hosts[$i]}:~/
	ssh -i $key ${hosts[$i]} "rm -f ~/*.log"
	cmd="DATABASE_URL=$db_url java -jar ~/server.jar $port $i $log_tp $log_db_rt $log_db_tp $log_msgcount"
	echo $cmd
	ssh -i $key ${hosts[$i]} $cmd &
	i=$(($i + 1))
done

echo "Running $ns servers for $run_time seconds."
i=0
while (($i < $run_time)); do
	echo -ne "$(($run_time - $i)) seconds remaining... \\r"
	i=$(($i + 1))
	sleep 1s
done

i=0
while (($i < $ns)); do
	scp -i $key -r ${hosts[$i]}:~/*.log $dest_log_dir
	ssh -i $key ${hosts[$i]} "pkill -9 java"
	i=$(($i + 1))
done

echo "Done.                        "
