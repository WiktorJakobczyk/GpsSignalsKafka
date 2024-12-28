# GpsSignalsKafka
A small demo created as part of learning Kafka and WebSockets. \
It showcases a service for monitoring GPS signals in real time.

# What is it?
This project was created as a learning exercise for Kafka and WebSocket. It is built using two microservices developed with Spring Boot: **GpsSignalsProducer** and **GpsSignalsConsumer**.

The Producer simulates the generation of signals from GPS devices and sends these signals to a Kafka topic in real time. \
The Consumer retrieves these signals, processes them, and forwards the most recent data to the frontend using WebSocket. 

This setup enables simple real-time monitoring of GPS signals. \
**Check out the demo below! :)**

### Components diagram
![GpsSignalsDiagram](https://github.com/user-attachments/assets/33dab96b-045b-41d5-b94a-4f892a417874)


# Demo
https://github.com/user-attachments/assets/d16777ef-28c5-431c-8d15-6d5b2ce593cb


