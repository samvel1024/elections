### Election REST API

#### Model of the database

![alt text](https://i.imgur.com/RqGaMVz.png)



#### Preparing the DB

Execute the following [script](https://raw.githubusercontent.com/samvel1024/elections/master/src/main/resources/schema.sql?token=ASgnMFy8CpWVB1EbQFc1oOGN_D9Rmpm4ks5cTOE9wA%3D%3D) against the database 

#### Preparing app configuration

To run the app you need to have maven and Java 8 installed.
First, create a file named `elections.properties` with DB configurations like this

```
spring.datasource.url=jdbc:postgresql://elephantsql.com:5432/my_db\?currentSchema=elections
spring.datasource.username=my_username 
spring.datasource.password=my_password
```
Tou might also need to add `server.port=1234` to override the default value of 8080

#### Running the app

To run the app you need to build it with `mvn package` which will run all the tests against the DB.

```bash
git clone https://github.com/samvel1024/elections.git;
cd elections;
mvn package -Dspring.config.location=classpath:/,file:{absolute_path_to_elections_properties};
cd target;
java -jar -Dspring.config.location=classpath:/,file:{absolute_path_to_elections_properties} election-0.0.1-SNAPSHOT.jar;
```

#### Accessing the documentation

Documentation is in swagger format and is accessible in `http://localhost:8080/swagger-ui.html`


####  Authentication

Authentication is managed through `/auth/signin` and `/auth/signup` endpoints. To be able to use the `/election/*` endpoint you need to set the `jwt-token` header, which is sent as a header in the response when you signin. As an example here is a raw request for election creation.

```
curl -X POST \
  http://localhost:8080/election \
  -H 'Content-Type: application/json' \
  -H 'cache-control: no-cache' \
  -H 'jwt-token: eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZDAwMDAwMCIsImF1dGgiOlt7ImF1dGhvcml0eSI6IkFETUlOIn1dLCJpYXQiOjE1NDc5MDQ0MzgsImV4cCI6MTU0ODI2NDQzOH0.rVY8sohj6Pfy6OshR22R0Cd3fGGdI68WHLh6J99YDZ4' \
  -d '{
  "deadline": "2019-01-19T17:03:27.616",
  "desc": "string",
  "end": "2019-01-19T17:05:27.616",
  "registryIds": [
    "aa663543", "aa327196", "aa674888", "wq123456"
  ],
  "start": "2019-01-19T17:04:27.618"
}
'
```


