#! /bin/sh



#run
java UDPLoggerServer 12344 &
echo " Waiting for logger server to start ..."
sleep 5
java Coordinator 12345 12344 10 500 A B &
echo " Waiting for coordinator to start ..."
sleep 5
java Participant 12345 12344 12346 500 &
sleep 1
java Participant 12345 12344 12347 500 &
sleep 1
java Participant 12345 12344 12348 500 &
sleep 1
java Participant 12345 12344 12349 500 &

sleep 1
java Participant 12345 12344 12350 500 &
sleep 1
java Participant 12345 12344 12351 500 &
sleep 1
java Participant 12345 12344 12352 500 &
sleep 1
java Participant 12345 12344 12353 500 &
sleep 1
java Participant 12345 12344 12354 500 &
sleep 1
java Participant 12345 12344 12355 500 &
