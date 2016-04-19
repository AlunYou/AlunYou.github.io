#!/bin/bash
docker-compose up -d

docker exec -it namenode bash -c 'source /etc/profile.d/hadoop-custom-env.sh && $HADOOP_PREFIX/sbin/start-dfs.sh'
docker exec -it resourcemanager bash -c 'source /etc/profile.d/hadoop-custom-env.sh && $HADOOP_PREFIX/sbin/start-yarn.sh'
docker exec -it jobhistory bash -c 'source /etc/profile.d/hadoop-custom-env.sh && $HADOOP_PREFIX/sbin/mr-jobhistory-daemon.sh --config $HADOOP_CONF_DIR start historyserver'

docker exec -it namenode bash -c 'source /etc/profile.d/hadoop-custom-env.sh && $HADOOP_PREFIX/bin/hdfs dfs -mkdir /user'
docker exec -it namenode bash -c 'source /etc/profile.d/hadoop-custom-env.sh && $HADOOP_PREFIX/bin/hdfs dfs -mkdir /user/root'
#docker exec -it namenode bash -c 'source /etc/profile.d/hadoop-custom-env.sh && $HADOOP_PREFIX/bin/hdfs dfs -put $HADOOP_PREFIX/etc/hadoop input'
