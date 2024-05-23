## Module: spring-cloud-config-server

Spring Cloud Config provides server-side and client-side support for externalized
configuration in a distributed system.

The Spring Cloud Config Server acts as a central place to manage external properties
for applications across all environments (from development to test and production).

Clients (applications) can retrieve configuration properties from the Config Server
using HTTP requests.

For example:

    curl localhost:8888/log-rawdata-producer/development

