---
layout: post
title:  "Hot github projects contribution: result validation with shell script"
date:   2016-03-27 18:02:03 +0800
categories: mapreduce
---

Busy Week. 

Shell script is said to be a good method for quick `pre-analysis` on a small subset of big data. In this blog I write shell scripts to do `result validation`.

The script mainly uses wc/sed/awk/uniq/sort commands to achieve the same results as the mapreduce work I did in last blog. 
When validating mapreduce results, I found and fixed a critical bug of parsing time in mapreduce code, and also updated the charts of the last two blogs. 
  
Conclusion: Shell script is also good for result validation on a small subset of big data. 
  
The source code of this script is at [https://github.com/AlunYou/AlunYou.github.io/blob/master/analysis/analysis_by_shell.sh][script-path]

The sed function w is not working inside curly braces, `sed '{n; w other.txt;}' test.txt`. But I did not find any document about this point. This issue blocked me for an hour. 
Just mark it here for revisiting later.

[script-path]: https://github.com/AlunYou/AlunYou.github.io/blob/master/analysis/analysis_by_shell.sh