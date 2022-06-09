# Forum - Service
## Description
Backend service for Forum Application.
 
Briefly it uses Spring, Redis for caching, TICK stack for monitoring, RabbitMQ for learning purposes,
Junit, Mockito and Rest Assured for testing.

## Running locally
### Fully using docker - compose
Run these commands one by one in terminal in a root project folder:

```
./mvnw clean install
docker-compose -f docker-compose-all.yaml up -d
```

To stop app run this command in the same folder:

```docker-compose -f docker-compose-all.yaml down```

### Running services using docker - compose and app separately
Run this command in terminal in a root project folder to run services:

```docker-compose -f docker-compose-services.yaml up -d```

Then you can run app in your favourite IDE from class SpringForumApplication
or using maven: 
```
./mvnw clean install
./mvnw spring-boot:run
```

When you want to stop the whole application,
 stop app first and services next.

To stop services run this command in the same folder: 

```docker-compose -f docker-compose-services.yaml down```

## Using an API

You can explore endpoints on [Docs Page](https://hub.apitree.com/dosipov/spring-forum/). 

There's initially a user with login "gogo@ya.ru" and password "dan".
You can log in with these credentials or register your own user (don't forget about email verification).

## Important
You won't be able to run it on your machine because there are sensitive credentials
 I can't share and without them project won't work properly. 