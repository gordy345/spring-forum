package spring_forum;

import org.springframework.amqp.core.Queue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringForumApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringForumApplication.class, args);
    }

    @Bean
    public Queue exceptionsQueue() {
        return new Queue("exceptions");
    }

}
