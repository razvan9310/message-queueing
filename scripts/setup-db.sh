#!/bin/bash

echo ""
echo "Creating tables"
cd ~/Programming/Java/ASL && PGPASSWORD=misu93 DATABASE_USER=postgres DATABASE_NAME=asl_db ant create-tables

echo ""
echo "Creating stored procedures"
cd ~/Programming/Java/ASL && PGPASSWORD=misu93 DATABASE_USER=postgres DATABASE_NAME=asl_db ant create-stored-procedures

echo ""
echo "Creating indices"
cd ~/Programming/Java/ASL && PGPASSWORD=misu93 DATABASE_USER=postgres DATABASE_NAME=asl_db ant create-indices
