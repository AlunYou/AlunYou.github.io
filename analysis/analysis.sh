#!/bin/bash

export HADOOP_HOME=/Users/You/workspace/hadoop-2.7.2
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_66.jdk/Contents/Home
PATH=${JAVA_HOME}/bin:$HADOOP_HOME/bin:$HADOOP_HOME/sbin:$PATH
export WORK_DIR=/Users/You/workspace/gitblog/AlunYou.github.io/analysis
export HADOOP_CLASSPATH=${JAVA_HOME}/lib/tools.jar:$WORK_DIR/contributor_analysis.jar:$HADOOP_CLASSPATH

rm -rf data
mkdir data

for entry in "$WORK_DIR"/input/*
do
  echo "processing $entry"
  name=$(basename "$entry" ".log")
  rm -rf $WORK_DIR/middle
  rm -rf $WORK_DIR/output
  $HADOOP_HOME/bin/hadoop jar contributor_analysis.jar contributor_analysis.ContributorAnalysisJob "$entry" middle output
  if [ -f "$WORK_DIR/output/_SUCCESS" ]
  then
    cp output/part-r-00000 data/$name
    echo $name >> data/repo_list
  else
      echo "fails on: $entry"
  fi
done

echo "processing allrepo"
rm -rf $WORK_DIR/middle
rm -rf $WORK_DIR/output
$HADOOP_HOME/bin/hadoop jar contributor_analysis.jar contributor_analysis.ContributorAnalysisJob input middle output
if [ -f "$WORK_DIR/output/_SUCCESS" ]
then
  cp output/part-r-00000 data/allrepo
  echo "allrepo" >> data/repo_list
else
  echo "fails on: allrepo"
fi

echo "finish"

