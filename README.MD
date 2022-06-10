# Forum - Service
## Description
Backend service for Forum Application.

This is a cut version of an API: there're no image uploading and email verification capabilities.
 
Briefly it uses Spring, Redis for caching, TICK stack for monitoring, RabbitMQ for learning purposes,
JUnit, Mockito, Rest Assured for testing and some helpful libraries.

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

## Using an API

You can explore endpoints [here](https://hub.apitree.com/dosipov/spring-forum-cut/). 

There's initially a user with login "gogo@ya.ru" and password "dan".
You can log in with these credentials or register your own user.