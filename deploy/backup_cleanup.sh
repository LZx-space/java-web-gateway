#!/bin/sh

# 推荐应用包名，需要能查询到进程以及表述应用名
jar_name="app-name"
# 备份文件目录
backup_dir="./backup"

echo -e "\033[32m--------backup start--------\033[0m"
# 备份目录不存在则创建
if [ ! -d "${backup_dir}" ]; then
  mkdir $backup_dir
  echo "新建备份文件夹${backup_dir}"
fi
# 备份
if [ ! -f "${jar_name}.jar" ]; then
  echo "应用包${jar_name}.jar不存在"
  echo -e "\033[32m--------backup end----------\033[0m"
else
  backup_dest_file="${backup_dir}/${jar_name}.jar_$(date +%Y%m%d_%H%M%S)"
  mv $jar_name.jar $backup_dest_file
  echo "新增备份文件${backup_dest_file}"
fi
echo -e "\033[32m--------backup end----------\033[0m"

# clearup 3个版本外的jar
echo -e "\033[32m--------clearup start----------\033[0m"
# 反序将最新的放数组前面，以便删除超过下标2以后的元素，而不会当元素只有少于3个时也去删除最老的3个文件
find ${backup_dir} -name "${jar_name}.jar_*" \
| sort -rn \
| awk -F'./' 'BEGIN {count=0;}{backup_jars[count]=$0; count++};
END {
  for(i=0;i<NR;++i) {
    if (i<3)
      print i, backup_jars[i]
    else {
      cmd="rm -f "backup_jars[i]
      system(cmd)
      printf "%s %s 已删除\n", i, backup_jars[i]
    }
  }
}'
echo -e "\033[32m--------clearup end------------\033[0m"




