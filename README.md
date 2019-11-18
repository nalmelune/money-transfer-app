# money-transfer

About the project
---

This is money transfer demo project, with deadlock prevention on database level. This is the project that shows how I work with new techlogies with a tight deadline. Basically, this is how "rushed in production" code looks like.

How to start the application
---

1. Run `mvn clean install` to build your application
1. Start application with `java -jar target/money-transfer-1.0-SNAPSHOT.jar server config.yml`
1. Learn how to use api using tests and try something on your own starting from `http://localhost:8080/api/accounts`

Health Check
---

To see your applications health enter url `http://localhost:8081/healthcheck`
