#!/bin/bash
# 第一行为指定使用bash程序执行此脚本，不要做任何改动！！！
# ./mes.sh start 启动
# ./mes.sh stop 停止
# ./mes.sh restart 重启
# ./mes.sh status 状态
AppName=@project.name@
BaseDir="/home/mes/app/@project.name@/@project.name@-@timestamp@"
AppJarPath="$BaseDir/$AppName.jar"

# JVM参数
JVM_OPTS="-Dname=$AppName -Duser.dir=$BaseDir --add-opens=java.base/java.lang=ALL-UNNAMED -Duser.timezone=Asia/Shanghai -Xms512M -Xmx2048M
-XX:+HeapDumpOnOutOfMemoryError -XX:NewRatio=1 -XX:SurvivorRatio=30 -XX:+UseParallelGC "

if [ "$1" = "" ];
then
    echo -e "\033[0;31m 未输入操作名 \033[0m  \033[0;34m {start|stop|restart|status} \033[0m"
    exit 1
fi

if [ "$AppName" = "" ];
then
    echo -e "\033[0;31m 未输入应用名 \033[0m"
    exit 1
fi

function start()
{
    PID=`ps -ef |grep java|grep mes|grep $AppName|grep -v grep|awk '{print $2}'`

	if [ x"$PID" != x"" ]; then
	    echo "$AppName is running..."
	else
		nohup java -jar  $JVM_OPTS $AppJarPath >/dev/null 2>&1 &
		echo "Start $AppName success..."
	fi
}

function stop()
{
    echo "Stop $AppName"
	
	PID=""
	query(){
		PID=`ps -ef |grep java|grep mes|grep $AppName|grep -v grep|awk '{print $2}'`
	}

	query
	if [ x"$PID" != x"" ]; then
		kill -15 $PID
		echo "$AppName (pid:$PID) exiting..."
		waitSec=10
		while [ x"$PID" != x"" ]
		do
			sleep 1
			if [ $waitSec == 0 ]; then
			  echo "kill process (pid:$PID) ..."
      	kill -9 $PID
      else
        echo "wait $waitSec sec to kill process ..."
        waitSec=$((waitSec-1))
      fi
      query
		done
		echo "$AppName exited."
	else
		echo "$AppName already stopped."
	fi
}

function restart()
{
    stop
    sleep 2
    start
}

function status()
{
    PID=`ps -ef |grep java|grep $AppName|grep -v grep|wc -l`
    if [ $PID != 0 ];then
        echo "$AppName is running..."
    else
        echo "$AppName is not running..."
    fi
}

case $1 in
    start)
    start;;
    stop)
    stop;;
    restart)
    restart;;
    status)
    status;;
    *)

esac
