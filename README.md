# <div align="center">Log files Monitoring with Microsevices and Kafka</div>

## <div align="center">Showcase</div>

## Introduction
Hardware and software components, e.g. network, operating systems,
applications, and servers generate continuously log data that should be
analysed and monitored. This task can be very time-consuming if it is
done manually.
Log are often hard to read, relevant data is hidden by cryptic developer 
output and related information is spread over many lines.

This showcase shows conceptual how log data can be analysed and monitored 
automatically, only through configuration. 

The following technology stack is used:

- Microservices based on Spring Boot Cloud
- Apache Kafka as a distributed event streaming platform
- MongoDB to store the analysed log data
- Grafana dashboard visualizes the data from the database
- Docker to automate the deployment of applications in lightweight containers

