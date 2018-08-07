#!/bin/bash
export JAVA_HOME=/usr/java/jdk1.8.0_131
export PATH=$PATH:/usr/bin:/usr/local/bin:/bin:/sbin:$JAVA_HOME
cartedir=/home/cloud-user/data-integration
num=`ps aux | grep carte | grep -v grep | wc -l`
if [ "$num" -eq 0 ];then
    nc -zv bigdata-5 18080
    if [ "$?" = "0" ]; then
        echo "begin start"
        cd "${cartedir}"
        timestamp=`date`
        echo "${timestamp}: start ./carte.sh pwd/carte-config-18081.xml" 2>&1
        nohup ./carte.sh pwd/carte-config-18081.xml >>/dev/null 2>&1 &
        if [ "$?" = "0" ]; then
            echo "start ok" 2>&1
        else
            pids=`ps aux | grep carte | grep -v grep | awk '{print $2}'`
            for pid in $pids
            do
                kill -9 $pid
            done
            echo "start failed" 2>&1
            exit 1
        fi
    else
        echo "waiting for master" 2>&1
        exit 1
    fi
elif [ "$num" -lt 7 ];then
    pids=`ps aux | grep carte | grep -v grep | awk '{print $2}'`
    for pid in $pids
    do
        kill -9 $pid
    done
    echo "pre process error" 2>&1
    exit 1
fi
