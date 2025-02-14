#!/bin/bash
# 解压ZIP文件
unzip @project.name@-@timestamp@.zip -d @project.name@-@timestamp@
# 删除ZIP文件
rm -f @project.name@-@timestamp@.zip

appDir=/home/mes/app/@project.name@
logDir=/home/mes/log/@project.name@
mkdir -p $appDir
mkdir -p $logDir
cd $appDir

#保留最新的5个版本
num=5
filenumb=$(ls -ltr | grep -v 'total' | grep "^d" | wc -l)
echo $filenumb
if [ ${filenumb} -gt ${num} ]
then
  let numbDel=${filenumb}-${num}
  ls -ltr | grep -v 'total' | grep "^d" | awk '{print $9}' | head -n  ${numbDel} | xargs rm -rf
fi

# 创建快捷文件夹
appDirLink=/opt/mes/app/@project.name@
logDirLink=/opt/mes/log/@project.name@
mkdir -p $appDirLink
mkdir -p $logDirLink
# 删除已有的软链接
rm -rf $appDirLink
rm -rf $logDirLink
# 创建软链接
ln -s ${appDir}/@project.name@-@timestamp@ /opt/mes/app
mv /opt/mes/app/@project.name@-@timestamp@ $appDirLink
ln -s $logDir /opt/mes/log
# 设置开机启动
#serviceFilePath=/usr/lib/systemd/system/mes-@project.name@.sh
#if [ -f $serviceFilePath ]
#then
#  rm $serviceFilePath
#fi
#
#echo '[Unit]' >> $serviceFilePath
#echo 'Description=mes-service' >> $serviceFilePath
#echo '' >> $serviceFilePath
#echo '[Service]' >> $serviceFilePath
#echo 'Type=oneshot' >> $serviceFilePath
#echo 'ExecStart=/bin/bash -e /opt/mes/app/@project.name@/bin/restart.sh' >> $serviceFilePath
#echo 'KillSignal=SIGINT' >> $serviceFilePath
#echo '' >> $serviceFilePath
#echo '[Install]' >> $serviceFilePath
#echo 'WantedBy=multi-user.target' >> $serviceFilePath
#
#chmod 755 $serviceFilePath
# 启动
sleep 1
cd $appDirLink
bash ${appDirLink}/bin/restart.sh
