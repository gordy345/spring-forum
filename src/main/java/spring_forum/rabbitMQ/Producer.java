package spring_forum.rabbitMQ;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class Producer {

    private final RabbitTemplate rabbitTemplate;

    public void send(String message) {
        rabbitTemplate.convertAndSend("exceptions", message);
    }
}
