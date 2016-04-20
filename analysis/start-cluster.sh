#!/bin/bash
docker-compose up -d

docker exec -it namenode bash -c 'echo "Y" | $HADOOP_PREFIX/bin/hdfs namenode -format; $HADOOP_PREFIX/sbin/start-dfs.sh; $HADOOP_PREFIX/bin/hdfs dfs -mkdir -p /user/root; '
docker exec -it resourcemanager bash -c '$HADOOP_PREFIX/sbin/start-yarn.sh'
docker exec -it jobhistory bash -c '$HADOOP_PREFIX/sbin/mr-jobhistory-daemon.sh --config $HADOOP_CONF_DIR start historyserver'

#docker exec -it namenode bash -c 'source /etc/profile.d/hadoop-custom-env.sh && $HADOOP_PREFIX/bin/hdfs dfs -put $HADOOP_PREFIX/etc/hadoop input'
