# Build
mvn clean package && docker build -t com.binu.microprofile/book-store .

# RUN

docker rm -f book-store || true && docker run -d -p 8080:8080 -p 4848:4848 --name book-store com.binu.microprofile/book-store 