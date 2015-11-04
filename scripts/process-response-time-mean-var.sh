#!/bin/bash

if (($# != 4)); then
	echo "Arguments: logs_dir, num_clients_or_servers, mean_var_script, for_db"
	exit 1
fi

logs_dir=$1
ns=$((10#$2))
mvs=$3
for_db=$((10#$4))

cd $logs_dir

echo "Processing response time logs..."
ARR=()
i=0
while (($i < $ns)); do
	if (($for_db == 1)); then
		ARR+=("db-response-time$i.log")
	else
		ARR+=("response-time$i.log")
	fi
	i=$(($i + 1))
done
echo "Running response time mean & variance python script: $mvs"
sort -n ${ARR[@]} | python $mvs
echo ""
