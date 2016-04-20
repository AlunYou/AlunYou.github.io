#!/usr/bin/env bash

service ssh start

$HADOOP_HOME/bin/hadoop fs -mkdir       /tmp;
$HADOOP_HOME/bin/hadoop fs -mkdir       /user/hive/warehouse;
$HADOOP_HOME/bin/hadoop fs -chmod g+w   /tmp;
$HADOOP_HOME/bin/hadoop fs -chmod g+w   /user/hive/warehouse;


if [[ $1 == "-d" ]]; then
  while true; do sleep 1000; done
else
  /bin/bash
fi
#if [[ $1 == "-bash" ]]; then
#  /bin/bash
#fi