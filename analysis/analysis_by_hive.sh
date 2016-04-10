#!/bin/bash

export WORK_DIR=/Users/You/workspace/gitblog/AlunYou.github.io/analysis

result=result_hive
rm -rf $result
mkdir $result

jobs=(by_zone by_author by_core_author by_cross by_related by_hour)

load_preprocess() {
    table=$1_commits
    echo $table

    preprocess=$2
    $HIVE_HOME/bin/hive -e "DROP TABLE $table;"
    $HIVE_HOME/bin/hive -e "CREATE TABLE $table (repo STRING, overtime INT, hour INT, zone STRING, email STRING) ROW FORMAT DELIMITED FIELDS TERMINATED BY ' ';"
    $HIVE_HOME/bin/hive -e "LOAD DATA LOCAL INPATH '$preprocess' OVERWRITE INTO TABLE $table;"

}

by_zone () {
   echo "by_zone $1 $2"
   name=$1
   table=$1_commits
   mkdir -p $result/$name/by_zone/output
   mkdir -p $result/$name/by_zone/extra

   $HIVE_HOME/bin/hive -e "select printf(\"%d:%d %d:%d\", temp.zone, temp.overtime, temp.author_num, temp.commit_num) from (select zone, overtime, count(distinct email) as author_num, count(*) as commit_num from $table where true group by zone, overtime order by zone, overtime) temp;" > $result/$name/by_zone/output/part-r-00000
}

by_author () {
   echo "by_author $1 $2"
   name=$1
   table=$1_commits
   mkdir -p $result/$name/by_author/output
   mkdir -p $result/$name/by_author/extra

   $HIVE_HOME/bin/hive -e "select count(*) from (select email, sum(if(overtime=1, 1, 0)) as overtime_true, sum(if(overtime=0, 1, 0)) as overtime_false from $table where true group by email) sub  where sub.overtime_false > (sub.overtime_true+sub.overtime_false) * 0.6;" > $result/$name/by_author/extra/full_time_author_number
   $HIVE_HOME/bin/hive -e "select count(distinct email) from $table where true;" > $result/$name/by_author/extra/all_author_number
   read full_time < $result/$name/by_author/extra/full_time_author_number
   read all < $result/$name/by_author/extra/all_author_number
   part_time=$(($all - $full_time))
   echo $part_time > $result/$name/by_author/extra/part_time_author_number
}

by_core_author () {
   echo "by_core_author $1 $2"
   name=$1
   table=$1_commits
   mkdir -p $result/$name/by_core_author/output
   mkdir -p $result/$name/by_core_author/extra

   $HIVE_HOME/bin/hive -e "select count(*) from $table;" > $result/$name/by_core_author/extra/commit_number
   $HIVE_HOME/bin/hive -e "select count(*) as commit_num, email from $table where true group by email order by commit_num desc;" > $result/$name/by_core_author/output/part-r-00000
}

by_cross () {
   echo "by_cross $1 $2"
   name=$1
   table=$1_commits
   mkdir -p $result/$name/by_cross/output
   mkdir -p $result/$name/by_cross/extra

   $HIVE_HOME/bin/hive -e "select count(distinct email) from $table where true;" > $result/$name/by_cross/extra/author_number
   $HIVE_HOME/bin/hive -e "select count(*) from (select count(distinct repo) as repo_num, email from $table where true group by email order by repo_num desc) sub where sub.repo_num > 1;" > $result/$name/by_cross/extra/cross_author_number
}

by_related () {
   echo "by_related $1 $2"
   name=$1
   table=$1_commits
   mkdir -p $result/$name/by_related/output
   mkdir -p $result/$name/by_related/extra

   $HIVE_HOME/bin/hive -e "select count(*) as count, joined_table.repo1, joined_table.repo2 from (select table1.email, table1.repo as repo1, table2.repo as repo2 from (select email, repo from $table where true group by email, repo order by email) table1 inner join (select email, repo from $table where true group by email, repo order by email) table2 on table1.email=table2.email where table1.repo < table2.repo) joined_table where true group by joined_table.repo1, joined_table.repo2 order by count desc;" > $result/$name/by_related/output/part-r-00000
}

by_hour () {
   echo "by_hour $1 $2"
   name=$1
   table=$1_commits
   mkdir -p $result/$name/by_hour/middle/1
   $HIVE_HOME/bin/hive -e "select hour, count(*) as commit_num from $table group by hour;" > $result/$name/by_hour/middle/1/part-r-00000
}

for entry in "$WORK_DIR"/input/*.log
do
  echo "processing $entry"
  name=$(basename "$entry" ".log")
  mkdir $result/$name
  echo $name >> $result/repo_list

  input="$entry"
  if [ $name = "allrepo" ]
  then
    input="$WORK_DIR/input/*.log"
  fi

  mkdir $result/$name/preprocess
  preprocess=$result/$name/preprocess/preprocess.log

  # preprocessing into columns: repo overtime hour zone email
  awk 'BEGIN {OFS="\n"} {if ($1 ~ /^Date:/) print $0,FILENAME; else print $0; }' $input |
  LANG=C LC_ALL=C sed -n '/^Author: .*\<\(.\{2,\}\)\>$/{s/^Author: .*\<\(.*\)\>$/\1/g; s/[ \t]*//g; h; n; s/Date:   \(.\{3\}\) .\{3\} [0-9]\{1,2\} \([0-9]\{2\}\):[0-9]\{2\}:[0-9]\{2\} [0-9]\{4\} \([+-]\{1\}[0-9]\{2\}\).*/\1-\2 \3/g; s/Sat-\([0-9]\{2\}\)/1 \1/g; s/Sun-\([0-9]\{2\}\)/1 \1/g; s/^.\{3\}-\(0[0-7]\)/1 \1/g; s/^.\{3\}-\(19\)/1 \1/g; s/^.\{3\}-\(2[0-3]\)/1 \1/g; s/^.\{3\}-\([0-9]\{2\}\)/0 \1/g; G; h; n; s=\(.*\/\)\([^/]*\)\(\.log\)$=\2=g; G; s/\n/ /g; p; }' > $preprocess

  for job in "${jobs[@]}"
  do
      echo "start job: $job"
      load_preprocess $name $preprocess
      $job $name
      echo "succeed on: $entry, $job"
  done
done

echo "finish"

