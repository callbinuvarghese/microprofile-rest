# Inspired from
https://cicekhayri.github.io/ebook-Building-an-API-Backend-with-MicroProfile/building-an-api-backend-with-microprofile.pdf

Tested..
Open API 
Rest Client 
Config
Metrics 
CDI 
JAX-RS 
JSON-B

Included in microprofile, did not test in this project 
JSON-P 
Health Check
Propagation 
JWT 
Open Tracing 
Fault Tolerance 

Java microprofile 
# Build
mvn clean package && docker build -t com.binu.microprofile/book-store .

# RUN
Compile and run locally
mvn clean package payara-micro:start

See Instructions.txt
docker rm -f book-store || true && docker run -d -p 8080:8080 -p 4848:4848 --name book-store com.binu.microprofile/book-store 
