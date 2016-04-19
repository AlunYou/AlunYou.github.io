#!/bin/bash

#export HADOOP_HOME=/Users/You/workspace/hadoop-2.7.2
#export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_66.jdk/Contents/Home
export WORK_DIR=. #/Users/You/workspace/gitblog/AlunYou.github.io/analysis
#export HADOOP_CLASSPATH=${JAVA_HOME}/lib/tools.jar:$WORK_DIR/contributor_analysis.jar:$HADOOP_CLASSPATH
#PATH=${JAVA_HOME}/bin:$HADOOP_HOME/bin:$HADOOP_HOME/sbin:$PATH

export INPUT_DIR=$WORK_DIR/input
export RESULT_DIR=$WORK_DIR/result
rm -rf $RESULT_DIR
mkdir $RESULT_DIR

$HADOOP_HOME/bin/hadoop fs -rm -r hdfs_input
$HADOOP_HOME/bin/hadoop fs -rm -r hdfs_result
$HADOOP_HOME/bin/hadoop fs -mkdir hdfs_result
$HADOOP_HOME/bin/hadoop fs -copyFromLocal $INPUT_DIR hdfs_input

jobs=(by_hour) #(by_zone by_author by_core_author by_cross by_related by_hour)

for entry in "$INPUT_DIR"/*.log
do
  echo "processing $entry"
  name=$(basename "$entry" ".log")
  $HADOOP_HOME/bin/hadoop fs -mkdir hdfs_result/$name
  echo $name >> $RESULT_DIR/repo_list

  for job in "${jobs[@]}"
  do
      echo "start job: $job"
      $HADOOP_HOME/bin/hadoop fs -mkdir hdfs_result/$name/$job
      temp_dir="hdfs_result/$name/$job"

      input=hdfs_input/$name.log
      if [ $name = "allrepo" ]
      then
        input="hdfs_input"
      fi

      $HADOOP_HOME/bin/hadoop jar contributor_analysis.jar $job.AnalysisJob "$input" $temp_dir/middle $temp_dir/output $temp_dir/extra -Dhadoop.root.logger=DEBUG,CLA
      #if [ -f "$WORK_DIR/$temp_dir/output/_SUCCESS" ]
      $HADOOP_HOME/bin/hadoop fs -test -e $temp_dir/output/_SUCCESS
      if [ $? -eq 0 ]
      then
        # cp result/$name/output/part-r-00000 data/$name
        echo "succeed on: $entry, $job" # $name >> data/repo_list
      else
        echo "fails on: $entry, $job"
      fi
  done
done


$HADOOP_HOME/bin/hadoop fs -copyToLocal hdfs_result/* $RESULT_DIR

#echo "processing allrepo"
#rm -rf $WORK_DIR/middle
#rm -rf $WORK_DIR/output
#$HADOOP_HOME/bin/hadoop jar contributor_analysis.jar contributor_analysis.ContributorAnalysisJob input middle output
#if [ -f "$WORK_DIR/output/_SUCCESS" ]
#then
#  cp output/part-r-00000 data/allrepo
#  echo "allrepo" >> data/repo_list
#else
#  echo "fails on: allrepo"
#fi

echo "finish"

