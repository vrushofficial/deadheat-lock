package com.vrush.microservices.booking.events.publishers;

import com.vrush.microservices.booking.dtos.BookingPayloadDTO;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.tradeshift.amqp.rabbit.handlers.RabbitTemplateHandler;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EventBookingCreatedPublisher {

    @Value("${spring.rabbitmq.custom.booking-created.exchange}")
    private String exchange;

    @Value("${spring.rabbitmq.custom.booking-created.queueRoutingKey}")
    private String routingKey;

    private final RabbitTemplateHandler rabbitTemplateHandler;

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    public void sendEvent(@NonNull final BookingPayloadDTO payload){
        rabbitTemplateHandler.getRabbitTemplate("booking-created").setMessageConverter(jsonMessageConverter());
        rabbitTemplateHandler.getRabbitTemplate("booking-created")
                .convertAndSend(exchange, routingKey, payload);

    }
}
