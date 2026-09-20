# 创建一个以 .bat 为结尾的文件，并输入以下内容
set Redis_home=D:\study\Redis
echo %Redis_home%
%Redis_home%\redis-server.exe %Redis_home%\redis.windows.conf