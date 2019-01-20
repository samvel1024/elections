### Election REST API


To run the app you need to have maven and Java 8 installed

#### Preparing the DB

Execute the following [script](https://raw.githubusercontent.com/samvel1024/elections/master/src/main/resources/schema.sql?token=ASgnMFy8CpWVB1EbQFc1oOGN_D9Rmpm4ks5cTOE9wA%3D%3D) against the database 

#### Preparing app configuration

Create a file named `elections.properties` with DB configurations like this

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
