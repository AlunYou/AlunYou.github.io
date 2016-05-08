#!/bin/bash
docker-compose -f docker-compose-qjm.yml up -d 

docker exec -it slave1 bash -c ' echo "starting slave1 journal nodes";
	$HADOOP_PREFIX/sbin/hadoop-daemon.sh start journalnode;'

docker exec -it slave2 bash -c ' echo "starting slave2 journal nodes";
	$HADOOP_PREFIX/sbin/hadoop-daemon.sh start journalnode;'

docker exec -it slave3 bash -c ' echo "starting slave3 journal nodes";
	$HADOOP_PREFIX/sbin/hadoop-daemon.sh start journalnode;'

docker exec -it namenode1 bash -c ' echo "format hdfs...";
	echo "Y" | $HADOOP_PREFIX/bin/hdfs namenode -format;'

#here must start dfs, then can bootstrap standby node
docker exec -it namenode1 bash -c ' 	echo "start dfs...";
    $HADOOP_PREFIX/sbin/start-dfs.sh;'

docker exec -it namenode2 bash -c ' echo "bootstrap standby namenode...";
	echo "Y" | $HADOOP_PREFIX/bin/hdfs namenode -bootstrapStandby;'

#if zooker is enabled, no need to manually set active node?
#docker exec -it namenode1 bash -c ' echo "make namenode1 active node.....";
#    $HADOOP_PREFIX/bin/hdfs haadmin -transitionToActive namenode1;'

docker exec -it namenode1 bash -c ' echo "format zkfc...";
    echo "Y" | $HADOOP_PREFIX/bin/hdfs zkfc -formatZK;'

docker exec -it namenode1 bash -c ' echo "stop dfs...";
    $HADOOP_PREFIX/sbin/stop-dfs.sh;
    echo "start dfs...";
    $HADOOP_PREFIX/sbin/start-dfs.sh;'
    

docker exec -it namenode1 bash -c 'echo "create user folder...";
	$HADOOP_PREFIX/bin/hdfs dfs -mkdir -p /user/root; '
docker exec -it resourcemanager bash -c '$HADOOP_PREFIX/sbin/start-yarn.sh'
docker exec -it jobhistory bash -c '$HADOOP_PREFIX/sbin/mr-jobhistory-daemon.sh --config $HADOOP_CONF_DIR start historyserver'

#docker exec -it namenode bash -c 'source /etc/profile.d/hadoop-custom-env.sh && $HADOOP_PREFIX/bin/hdfs dfs -put $HADOOP_PREFIX/etc/hadoop input'
