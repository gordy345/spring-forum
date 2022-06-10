# Forum - Service
## Description
Backend service for Forum Application.

It uses Spring (Boot, Security, Web, Data), Redis for caching, TICK stack for monitoring, RabbitMQ for learning purposes,
JUnit, Mockito, Rest Assured for testing, helpful libraries for identifying geolocation and language.

Also it stores image files on yandex disk and has email verification. 

You can explore endpoints [here](https://hub.apitree.com/dosipov/spring-forum/).

## Using an API
There's a deployed version of a service without TICK stack and RabbitMQ (you can find code on "deploy" branch)!

You can try it with this base url: https://spring-forum-service.herokuapp.com
(e. x. https://spring-forum-service.herokuapp.com/users to get all users)

There's initially a user with login "gogo@ya.ru" and password "dan".
You can log in with these credentials or register your own user (don't forget about email verification).

If you want to run and use app locally then base url will be http://localhost:8080
(e. x. http://localhost:8080/users to get all users)

Have fun!

## Running locally
### Fully using docker - compose
Run this command in terminal in a root project folder:

```docker-compose -f docker-compose-all.yaml up -d```

To stop app run this command in the same folder:

```docker-compose -f docker-compose-all.yaml down```

### Running services using docker - compose and app separately
Run this command in terminal in a root project folder to run services:

```docker-compose -f docker-compose-services.yaml up -d```

Then you can run app in your favourite IDE from class SpringForumApplication
or using maven: 
```
./mvnw spring-boot:run
```

When you want to stop the whole application,
 stop app first and services next.

To stop services run this command in the same folder: 

```docker-compose -f docker-compose-services.yaml down```