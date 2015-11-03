#!/bin/bash

echo ""
echo "Creating tables"
cd ~/ASL && DATABASE_USER=ec2-user DATABASE_NAME=asl_db ant create-tables

echo ""
echo "Creating stored procedures"
cd ~/ASL && DATABASE_USER=ec2-user DATABASE_NAME=asl_db ant create-stored-procedures

echo ""
echo "Creating indices"
cd ~/ASL && DATABASE_USER=ec2-user DATABASE_NAME=asl_db ant create-indices
