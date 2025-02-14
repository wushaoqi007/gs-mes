#!/bin/sh

# 复制项目的文件到对应docker路径，便于一键生成镜像。
usage() {
	echo "Usage: sh copy.sh"
	exit 1
}


# copy sql
echo "begin copy sql "
cp ../sql/ry_20210908.sql ./mysql/db
cp ../sql/ry_config_20211118.sql ./mysql/db

# copy html
echo "begin copy html "
cp -r ../gs-mes-ui/dist/** ./nginx/html/dist


# copy jar
echo "begin copy gs-mes-gateway "
cp ../gs-mes-gateway/target/gs-mes-gateway.jar ./ruoyi/gateway/jar

echo "begin copy gs-mes-auth "
cp ../gs-mes-auth/target/gs-mes-auth.jar ./ruoyi/auth/jar

echo "begin copy gs-mes-visual "
cp ../gs-mes-visual/gs-mes-monitor/target/gs-mes-visual-monitor.jar  ./ruoyi/visual/monitor/jar

echo "begin copy gs-mes-modules-system "
cp ../gs-mes-modules/gs-mes-system/target/gs-mes-modules-system.jar ./ruoyi/modules/system/jar

echo "begin copy gs-mes-modules-file "
cp ../gs-mes-modules/gs-mes-file/target/gs-mes-modules-file.jar ./ruoyi/modules/file/jar

echo "begin copy gs-mes-modules-job "
cp ../gs-mes-modules/gs-mes-job/target/gs-mes-modules-job.jar ./ruoyi/modules/job/jar


