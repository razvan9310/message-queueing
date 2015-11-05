#!/bin/bash

if (($# != 3)); then
	echo "Arguments: amazon_key_path, remote_address, postgres_cmd"
	exit 1
fi

key=$1
remote_address=$2
cmd=$3

ssh -i $key $remote_address "$cmd" 
