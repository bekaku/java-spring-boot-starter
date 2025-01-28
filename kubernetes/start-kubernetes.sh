#!/bin/bash
kubectl apply -f deployment.yaml
kubectl apply -f service.yaml
#minikube tunnel --cleanup
# LoadBalancer type
# minikube service spring-api-service --url
kubectl port-forward service/spring-api-service 8080:8080
#Now access your app in the browse http://localhost:8080