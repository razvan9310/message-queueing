#!/bin/bash

if (($# != 2)); then
	echo "Arguments: amazon_key_path, remote_address"
	exit 1
fi

key=$1
remote_address=$2

ssh -i $key $remote_address "pkill -f postgres" 
