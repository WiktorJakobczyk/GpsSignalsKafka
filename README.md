# GpsSignalsKafka
A small demo created as part of learning Kafka and WebSockets, showcasing a service for real-time monitoring of GPS signals.

# What is it?
This project was created as a learning exercise for Kafka and WebSocket. It is built using two microservices developed with Spring Boot: **GpsSignalsProducer** and **GpsSignalsConsumer**.

The Producer simulates the generation of signals from GPS devices and sends these signals to a Kafka topic in real time. \
The Consumer retrieves these signals, processes them, and forwards the most recent data to the frontend using WebSocket. 

This setup enables simple real-time monitoring of GPS signals. \
**Check out the demo below! :)**

### Components diagram
![GpsSignalsDiagram](https://github.com/user-attachments/assets/33dab96b-045b-41d5-b94a-4f892a417874)

### Kubernetes
I added a k8s file that launches everything needed for the application [frontend, microservices, and Kafka]. \
It can be launched, for instance, using Minikube. 

1. Enable the NGINX Ingress controller
```
minikube addons enable ingress
```

2. Apply a configuration
```
kubectl apply -f https://raw.githubusercontent.com/WiktorJakobczyk/GpsSignalsKafka/refs/heads/main/kafka-gps-deployment.yaml?token=GHSAT0AAAAAAC35HVSHEQ7RK35NOJT6O2CMZ3QGBJQ
```
3. Check pods 
```
kubectl get pods -n kafka
```
It should look like this: \
![obraz](https://github.com/user-attachments/assets/3c0ae2ad-fbc2-4b52-8199-d4f3d8102e24)

4. Connect to LoadBalancer services 
```
minikube tunnel
```
5. Open http://localhost:80/


# Demo
https://github.com/user-attachments/assets/d16777ef-28c5-431c-8d15-6d5b2ce593cb




