#!/bin/bash
cd /Users/You/workspace/docker/zookeeper/zoo1
bin/zkServer.sh start

cd /Users/You/workspace/docker/zookeeper/zoo2
bin/zkServer.sh start

cd /Users/You/workspace/docker/zookeeper/zoo3
bin/zkServer.sh start
