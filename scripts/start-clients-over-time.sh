if (($# != 9)); then
        echo "Arguments: amazon_key_path, remote_address, path_to_client, logs_dest, num_clients, new_client_delay_ms, num_servers, msg_length, log_response_time"
        exit 1
fi

key=$1
address=$2
client_path=$3
logs_dest=$4
n=$((10#$5))
delay=$((10#$6))
ns=$((10#$7))
msg_len=$((10#$8))
log_resp_time=$((10#$9))

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

scp -i $key $client_path $address:~/
ssh -i $key $address "rm -f ~/*.log"

ssh_cmd='
cd ~/;
n='"$n"';
ns='"$ns"';
hosts=('"${hosts[@]}"');
ports=('"${ports[@]}"');
msg_len='"$msg_len"';
log_resp_time='"$log_resp_time"';
delay='"$(($delay / 1000))"';
i=0;
while (($i < $n)); do
	mod1=$(($i % $ns));
	cmd="java -jar ~/client.jar ${hosts[$mod1]} ${ports[$mod1]} $i $(($i + 1)) $msg_len $log_resp_time > /dev/null";
	echo $cmd;
	eval $cmd&
	i=$(($i + 1));
	sleep ${delay}s;
done
'
ssh -i $key $address $ssh_cmd &

# i=0
# while (($i < $n)); do
#         mod1=$(($i % $ns))
#         cmd="java -jar ~/client.jar ${hosts[$mod1]} ${ports[$mod1]} $i $n $msg_len $log_resp_time > /dev/null"
#         echo $cmd
#         ssh -i $key $address $cmd &
#         i=$(($i + 1))
# done

t=$(($n * $delay / 1000))
echo "Adding 1 client every $delay ms to $ns servers for a total of $t seconds."
echo "Message length set to $msg_len."
echo "Log data will be available at $logs_dest"
i=0
while (($i < $t)); do
        echo -ne "$(($t-$i)) seconds remaining... \\r"
        i=$(($i + 1))
        sleep 1s
done

ssh -i $key $address "pkill -9 java"
scp -i $key -r $address:~/*.log $logs_dest

echo "Done.                                                              "
