#!/bin/sh

# 推荐应用包名，需要能查询到进程以及表述应用名
app_name="app_name"

echo -e "\033[32m------detail------\033[0m"
pid=`ps ax | grep -i ${app_name} | grep java | grep -v grep | awk '{print $1}'`
if [ -z "$pid" ] ; then
  echo "${app_name} is not running."
  exit -1;
fi
  app_pwd=`ls -l /proc/${pid} | grep "cwd ->" | grep -v "grep" | awk '{print $NF}'`
  echo "${app_name}(${pid}) is running, app dir is ${app_pwd}"

# 不能-9，SFTP等连接需要销毁
kill -9 ${pid}
  echo "send force shutdown request to ${app_name}(${pid}) OK"
