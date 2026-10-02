#!/usr/bin/env sh
set -eu

mkdir -p out
javac -d out backend/src/hotel/model/*.java backend/src/hotel/decorators/*.java backend/src/hotel/server/*.java
exec java -cp out hotel.server.HotelServer
