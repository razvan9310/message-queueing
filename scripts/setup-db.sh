#!/bin/bash

echo ""
echo "Creating tables"
cd .. && PGPASSWORD=misu93 DATABASE_USER=postgres DATABASE_NAME=asl_db ant create-tables

echo ""
echo "Creating stored procedures"
PGPASSWORD=misu93 DATABASE_USER=postgres DATABASE_NAME=asl_db ant create-stored-procedures

echo ""
echo "Creating indices"
PGPASSWORD=misu93 DATABASE_USER=postgres DATABASE_NAME=asl_db ant create-indices

echo ""
echo "Setting DATABASE_URL env var"
export DATABASE_URL=jdbc:postgresql:asl_db
