#!/bin/bash

export HADOOP_HOME=/Users/You/workspace/hadoop-2.7.2
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_66.jdk/Contents/Home
PATH=${JAVA_HOME}/bin:$HADOOP_HOME/bin:$HADOOP_HOME/sbin:$PATH
export WORK_DIR=/Users/You/workspace/gitblog/AlunYou.github.io/analysis
export HADOOP_CLASSPATH=${JAVA_HOME}/lib/tools.jar:$WORK_DIR/contributor_analysis.jar:$HADOOP_CLASSPATH

rm -rf result
mkdir result

rm -rf data
mkdir data

jobs=(by_zone by_author by_core_author by_cross by_related by_hour)

for entry in "$WORK_DIR"/input/*
do
  echo "processing $entry"
  name=$(basename "$entry" ".log")
  mkdir result/$name
  echo $name >> result/repo_list

  for job in "${jobs[@]}"
  do
      echo "start job: $job"
      mkdir result/$name/$job
      temp_dir="result/$name/$job"

      input="$entry"
      if [ $name = "allrepo" ]
      then
        input="$WORK_DIR/input"
      fi

      $HADOOP_HOME/bin/hadoop jar contributor_analysis.jar $job.AnalysisJob "$input" $temp_dir/middle $temp_dir/output $temp_dir/extra
      if [ -f "$WORK_DIR/$temp_dir/output/_SUCCESS" ]
      then
        # cp result/$name/output/part-r-00000 data/$name
        echo "succeed on: $entry, $job" # $name >> data/repo_list
      else
        echo "fails on: $entry, $job"
      fi
  done
done

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

