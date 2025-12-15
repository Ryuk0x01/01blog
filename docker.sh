#!/bin/bash

echo "Starting PostgreSQL..."
docker compose up -d

echo "PostgreSQL is running on port 5432"

#  docker exec -it postgres_db psql -U sohachimi -d springdb