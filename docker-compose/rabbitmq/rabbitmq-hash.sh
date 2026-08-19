#!/bin/bash

docker exec -it rabbitmq rabbitmqctl hash_password 'strong_password'