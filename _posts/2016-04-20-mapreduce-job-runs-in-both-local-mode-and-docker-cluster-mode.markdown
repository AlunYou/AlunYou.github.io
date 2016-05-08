---
layout: post
title:  "mapreduce job runs in both local mode and docker cluster mode"
date:   2016-04-20 21:02:03 +0800
categories: mapreduce
---

We usually need to run mapreduce job in local mode first, which is the quickest and best for debugging because the code is in a single local java process. 
Then we usually need to run job in distributed mode (or at least pseudo distributed mode) because it's the only way to find out distribution related bugs.

<h3>In this post, I'll first introduce my practice about how to achieve both with docker, mainly for development purpose.</h3>

First, I publish a docker image which enables basic features for hadoop, which runs ssh-server, enables passphraseless ssh login, 
installs java and hadoop native support and exports necessary environment variables. 
Please find Dockerfile here [https://hub.docker.com/r/higherone/alunyou.github.io/~/dockerfile/][docker-file].


Second, I use Docker Compose to generate clusters based on this standard image. I put the hadoop home folder into a mounted volume of each container, so that when I
modify config files in host machine, the containers will also get the changes immediately. 
Please find docker-compose file here [https://github.com/AlunYou/AlunYou.github.io/blob/master/analysis/docker-compose.yml][docker-compose-file].


Third, I write a shell script [start-cluster] to start cluster and do some init work, such as formatting hdfs amd creating user home folder.


Fourth, I write a [analysis script] to do complex data analysis. If the script is run in host machine, the hadoop command will be in local mode, 
and operate on local file system (relative to current dir in local file system). If it is run in container machine, the hadoop command will be run in distributed mode,
and operate on the hdfs file system in the cluster (relative to /user/root in hdfs file system, where root is the user name logins). 
So by using the same `relative path` in the script, I can reference correct files in hadoop job for both local and cluster mode.
 
<h3>And now here are some tips to develop hadoop jobs in this pattern.</h3>

<h6>1. Because all containers run in a single development machine, 
you need to <strong>tune the memory very carefully</strong> so that this cluster job will not make your computer unresponsive.
 </h6>
 
 My cluster has 6 nodes, name node and secondary name node, resource manager, job history server, slave1, slave2, slave3.
 
 Memory related configuration in mapred-site.xml and yarn-site.xml:
 
  ```xml
    <property>
        <name>yarn.app.mapreduce.am.resource.mb</name>
        <value>400</value>
    </property>
    <property>
        <name>mapreduce.map.memory.mb</name>
        <value>400</value>
    </property>
    <property>
        <name>mapreduce.reduce.memory.mb</name>
        <value>400</value>
    </property>
    <property>
        <name>mapreduce.map.java.opts</name>
        <value>-Xmx300m</value>
    </property>
    <property>
        <name>mapreduce.reduce.java.opts</name>
        <value>-Xmx300m</value>
    </property>
    <property>
        <name>yarn.app.mapreduce.am.command-opts</name>
        <value>-Xmx1200m</value>
    </property>
    <property>
        <name>yarn.nodemanager.resource.memory-mb</name>
        <value>1024</value>
    </property>
    <property>
        <name>yarn.scheduler.minimum-allocation-mb</name>
        <value>256</value>
    </property>
    <property>
        <name>yarn.scheduler.maximum-allocation-mb</name>
        <value>800</value>
    </property>
    <property>
        <name>yarn.nodemanager.vmem-pmem-ratio</name>
        <value>10</value>
    </property>
  ```
  + **yarn.nodemanager.resource.memory-mb is the physical memory each node machine can use for running hadoop containers**
  
  + **yarn.scheduler.minimum-allocation-mb and yarn.scheduler.maximum-allocation-mb are the hadoop container physical memory range, 
  and currently in 2.7.2 the minimum one is also used as an incremental step when deciding memory size.** 
  (I see this logic in DefaultResourceCalculator class in source code.)
  
  + **mapreduce.map.java.opts is JVM needed memory, it should be small than mapreduce.map.memory.mb which is the memory the hadoop scheduler should ask from the node machine**
   
<h6><br>2. <strong>Need to distinguish local file or hdfs file</strong>. Be reminded that you're writing for jobs for cluster run too. </h6>
 

<h3><br>Conclusion: I believe this pattern could be extracted into a development framework someday.</h3>


[docker-file]: https://hub.docker.com/r/higherone/alunyou.github.io/~/dockerfile/
[docker-compose-file]: https://github.com/AlunYou/AlunYou.github.io/blob/master/analysis/docker-compose.yml
[start-cluster]: https://github.com/AlunYou/AlunYou.github.io/blob/master/analysis/start-cluster.sh
[analysis script]: https://github.com/AlunYou/AlunYou.github.io/blob/master/analysis/analysis.sh