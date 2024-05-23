#!/bin/bash

docker run --net=host confluentinc/cp-kafka kafka-console-consumer --bootstrap-server localhost:9092 --topic $1 --from-beginning --group syslog-kafka
