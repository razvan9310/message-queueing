#!/bin/bash

if (($# != 11)); then
	echo "Arguments are: amazon_key_path, database_url, server_jar, num_servers, run_time, log_throughput, log_db_response_time, log_db_throughput, log_db_msg_count, dest_log_dir, db_threads"
	exit 1
fi

key=$1
db_url=$2
server_jar=$3
ns=$((10#$4))
run_time=$((10#$5))
log_tp=$6
log_db_rt=$7
log_db_tp=$8
log_msgcount=$9
dest_log_dir=${10}
db_threads=$((10#${11}))

hosts=()
ports=()
i=0
while IFS= read -r line; do
	if (($i % 2 == 0)); then
	        hosts+=("$line")
	else
		ports+=("$line")
	fi
	i=$(($i + 1))
done

i=0
while (($i < $ns)); do
	scp -i $key $server_jar ${hosts[$i]}:~/
	ssh -i $key ${hosts[$i]} "rm -f ~/*.log"
	cmd="DATABASE_URL=$db_url java -jar ~/server.jar ${ports[$i]} $i $log_tp $log_db_rt $log_db_tp $log_msgcount $db_threads"
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
