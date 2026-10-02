@echo off
if not exist out mkdir out
javac -d out backend\src\hotel\model\*.java backend\src\hotel\decorators\*.java backend\src\hotel\server\*.java
if errorlevel 1 exit /b %errorlevel%
java -cp out hotel.server.HotelServer
