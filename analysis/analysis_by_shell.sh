#!/bin/bash

export WORK_DIR=/Users/You/workspace/gitblog/AlunYou.github.io/analysis

result=result_shell
rm -rf $result
mkdir $result

jobs=(by_zone by_author by_core_author by_cross by_related by_hour)

by_zone () {
   echo "by_zone $1 $2"
   name=$1
   mkdir -p $result/$name/by_zone/output
   mkdir -p $result/$name/by_zone/extra
   awk ' BEGIN {OFS=" "} {print $4, $2, $5}' $result/$name/preprocess/preprocess.log   |
   sort -nr | uniq -c |
   awk ' BEGIN {OFS=" "} {print $2, $3, $4, $1}' | sort -nr  |
   awk 'BEGIN {OFS=" ";author_num=0;commit_num=0;has_data=0;} {if ($1 == pre_zone && $2 == pre_overtime) {author_num+=1; commit_num+=$4; }else{if(has_data) print pre_zone, pre_overtime, author_num, commit_num; author_num=1; commit_num=$4;}
   }; {pre_zone=$1;pre_overtime=$2;has_data=1;} END {print pre_zone, pre_overtime, author_num, commit_num;}' | sort -n |
   awk ' {print $1":"$2, $3":"$4}' > $result/$name/by_zone/output/part-r-00000
}

by_author () {
   echo "by_author $1 $2"
   name=$1
   mkdir -p $result/$name/by_author/output
   mkdir -p $result/$name/by_author/extra
   awk ' BEGIN {OFS=" "} {print $5,$2}' $result/$name/preprocess/preprocess.log  | sort -nr | uniq -c |
   awk ' BEGIN {OFS=" "} {print $2,$3,$1}' |
   awk 'BEGIN {OFS=" "} {if ($1 == pre_email) {if($3>(pre_commit+$3)*0.6) print pre_email, 0;else print pre_email, 1; getline; }else{print pre_email, pre_overtime;}
   }; {pre_email=$1;pre_overtime=$2;pre_commit=$3;}' |
   awk ' BEGIN {OFS=" "} {print $2}' | sort -nr | uniq -c |
   sed "1s/^[ \t]*\([0-9]*\)\(.*\)/\1/gw $result/$name/by_author/extra/part_time_author_number" |
   sed -n "2s/^[ \t]*\([0-9]*\)\(.*\)/\1/gw $result/$name/by_author/extra/full_time_author_number"
}

by_core_author () {
   echo "by_core_author $1 $2"
   mkdir -p $result/$name/by_core_author/output
   mkdir -p $result/$name/by_core_author/extra
   awk ' BEGIN {OFS=" "} {print $5}' $result/$name/preprocess/preprocess.log  |
   sort -nr | uniq -c | sort -nr > $result/$name/by_core_author/output/part-r-00000

   wc -l $result/$name/preprocess/preprocess.log |
   sed -n "1s/^[ \t]*\([0-9]*\)\(.*\)/\1/gw $result/$name/by_core_author/extra/commit_number"
}

by_cross () {
   echo "by_cross $1 $2"
   mkdir -p $result/$name/by_cross/output
   mkdir -p $result/$name/by_cross/extra

   awk ' BEGIN {OFS=" "} {print $5, $1}' $result/$name/preprocess/preprocess.log   |
   sort -nr | uniq | awk ' BEGIN {OFS=" "} {print $1}' | uniq -d |
   wc -l > $result/$name/by_cross/extra/cross_author_number

   awk ' BEGIN {OFS=" "} {print $5}' $result/$name/preprocess/preprocess.log |
   sort -nr | uniq | wc -l > $result/$name/by_cross/extra/author_number
}

by_related () {
   echo "by_related $1 $2"
   mkdir -p $result/$name/by_related/output
   mkdir -p $result/$name/by_related/extra
   awk ' BEGIN {OFS=" "} {print $5, $1}' $result/$name/preprocess/preprocess.log   |
   sort -nr | uniq |
   awk 'BEGIN {OFS=" ";repo_list="";} {if ($1 == pre_email) {repo_list=(repo_list" "$2);  }else{if(repo_list ~ / /)print repo_list;repo_list=$2;}
   }; {pre_email=$1;pre_repo=$2;}' |
   awk '{for(i=1;i<=NF;i++){for(j=i+1;j<=NF;j++){if($i < $j)print $i,$j; else print $j, $i;}}}' |
   sort -nr | uniq -c | sort -nr > $result/$name/by_related/output/part-r-00000
}

by_hour () {
   echo "by_hour $1 $2"
   name=$1
   mkdir -p $result/$name/by_hour/middle/1
   awk ' BEGIN {OFS=" "} {print $3}' $result/$name/preprocess/preprocess.log  | sort -nr |
   uniq -c | awk ' {print $2, $1}' | sort -n  > $result/$name/by_hour/middle/1/part-r-00000
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
      $job $name
      echo "succeed on: $entry, $job"
  done
done

echo "finish"

