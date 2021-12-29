package spring_forum.rabbitMQ;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Consumer {

    @RabbitListener(queues = "exceptions")
    public void getMessage(String message) {
        log.warn("NotFoundException happened, message from consumer: " + message);
    }
}
