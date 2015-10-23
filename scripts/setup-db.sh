#!/bin/bash

echo ""
echo "Creating tables"
cd ~/ASL && PGPORT=9986 DATABASE_USER=damachir DATABASE_NAME=asl_db ant create-tables

echo ""
echo "Creating stored procedures"
cd ~/ASL && PGPORT=9986 DATABASE_USER=damachir DATABASE_NAME=asl_db ant create-stored-procedures

echo ""
echo "Creating indices"
cd ~/ASL && PGPORT=9986 DATABASE_USER=damachir DATABASE_NAME=asl_db ant create-indices
