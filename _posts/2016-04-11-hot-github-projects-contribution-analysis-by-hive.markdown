---
layout: post
title:  "Hot github projects contribution: analysis by hive"
date:   2016-04-11 00:02:03 +0800
categories: mapreduce
---

I used the same preprocess script as the last blog to transfer the git log into structured table. (In real world, this should be done by a specialized mapreduce 
job to handle large data.)
 
Using hive is a much better choice, if you want to do analysis to the data set from many perspectives. 
With its sql-like Data Definition Language, it's much easier to use than the hand-written mapreduce job and shell scripting, as long as you can preprocess your data into structured data.
  
The main idea of code are similar for the previous two methods (because the basic tools are sort,group,reduce), but for hive, you can expect to solve your problem with new ideas. 
For example, in the related repo analysis, I was able to use inner join of the same table to find out the cross-repo-contribution authors, while in mapreduce or scripting I had to do more.
 
 
The source code of this script is at [https://github.com/AlunYou/AlunYou.github.io/blob/master/analysis/analysis_by_hive.sh][script-path]


[script-path]: https://github.com/AlunYou/AlunYou.github.io/blob/master/analysis/analysis_by_hive.sh