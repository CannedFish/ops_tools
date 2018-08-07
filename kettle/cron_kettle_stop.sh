#!/bin/bash
num=`ps aux | grep carte | grep -v grep | wc -l`
if [ "$num" -ne 0 ];then
    pids=`ps aux | grep carte | grep -v grep | awk '{print $2}'`
    # compile
    for pid in $pids
    do
        kill -9 $pid
    done
    exit 1
fi
