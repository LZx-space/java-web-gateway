#!/bin/sh

# 推荐应用包名，需要能查询到进程以及表述应用名
app_name="app_name"
start_params="-Dapp.id=app_id -Denv=DEV -Dapollo.meta=http://100.89.32.82:8080,http://100.89.32.83:8080"

# 启动应用
function start_app() {
  nohup java \
  -Xdebug \
  -Xrunjdwp:transport=dt_socket,suspend=n,server=y,address=5005 \
  -jar ${start_params} ${app_name}.jar >/dev/null 2>&1 &
  app_pid=`ps ax | grep -i ${app_name} | grep java | grep -v grep | awk '{print $1}'`
  if [ -z "$app_pid" ] ; then
    echo "${app_name} start fail."
    echo -e "\033[32m------------------\033[0m"
    exit -1;
  fi  
    echo "${app_name} start with pid ${app_pid} success."
    echo -e "\033[32m------------------\033[0m"
} 

# start
echo -e "\033[32m------detail------\033[0m"
pid=`ps ax | grep -i ${app_name} | grep java | grep -v grep | awk '{print $1}'`
if [ -z "$pid" ] ; then
  echo "${app_name} is not running."
  start_app
  exit -1;
fi
  app_pwd=`ls -l /proc/${pid} | grep "cwd ->" | grep -v "grep" | awk '{print $NF}'`
  echo "${app_name}(${pid}) is running, app dir is ${app_pwd}."

kill -9 ${pid}
  echo "send force shutdown request to ${app_name}(${pid}) OK."

cd ${app_pwd}
  start_app
