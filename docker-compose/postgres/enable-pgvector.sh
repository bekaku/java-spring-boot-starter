#!/bin/bash

# Enable
docker exec -it postgres \
  psql -U postgres_user \
  -d spring_starter_postgres \
  -c "CREATE EXTENSION IF NOT EXISTS vector;"

# Test result
docker exec -it postgres \
  psql -U postgres_user \
  -d spring_starter_postgres \
  -c "CREATE EXTENSION IF NOT EXISTS vector;"