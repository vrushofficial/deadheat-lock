package com.vrush.microservices.financial.events.listeners;

import java.io.IOException;
import java.util.Random;

import com.vrush.microservices.booking.dtos.BookingPayloadDTO;
import com.vrush.microservices.financial.annotations.TrackMethod;
import com.vrush.microservices.financial.dtos.PaymentAnalyzedDTO;
import com.vrush.microservices.financial.dtos.TransactionDTO;
import com.vrush.microservices.financial.events.publishers.EventPaymentAnalyzedPublisher;
import com.vrush.microservices.financial.exception.RetryableException;
import com.vrush.microservices.financial.service.TransactionService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradeshift.amqp.annotation.EnableRabbitRetryAndDlq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class EventBookingCreatedListener {

    private final TransactionService transactionService;
    private final EventPaymentAnalyzedPublisher eventPaymentAnalyzedPublisher;

    @TrackMethod
    @RabbitListener(containerFactory = "booking-created", queues = "${spring.rabbitmq.custom.booking-created.queue}")
    @EnableRabbitRetryAndDlq(event = "booking-created", retryWhen = RetryableException.class)
    public void onMessage(Message message) throws IOException {
        BookingPayloadDTO bookingPayloadDTO = new ObjectMapper().readValue(message.getBody(), BookingPayloadDTO.class);
        log.info("Message received on queue [{}] with payload {}", "queue.booking.created", bookingPayloadDTO);

        if (new Random().nextBoolean()) {
            throw new RetryableException("Simulating a Retryable Scenario");
        }

        PaymentAnalyzedDTO analyze = transactionService.analyze(bookingPayloadDTO);
        transactionService.save(new TransactionDTO(bookingPayloadDTO.getIdBooking(), analyze.getState().name(), bookingPayloadDTO.getTotalValue()));
        eventPaymentAnalyzedPublisher.sendEvent(analyze);
    }

}