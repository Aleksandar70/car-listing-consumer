package org.smg.carlisting.infrastructure.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.smg.carlisting.application.CarListingService;
import org.smg.carlisting.domain.event.CarListingEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Kafka consumer for car listing events.
 * <p>
 * This service listens for messages on a Kafka topic related to car listing events
 * and processes them accordingly. It handles different types of events, including
 * creating, updating, and deleting car listings.
 * </p>
 */
@Service
@Slf4j
public class CarListingConsumer {

    private final CarListingService carListingService;

    public CarListingConsumer(CarListingService carListingService) {
        this.carListingService = carListingService;
    }

    /**
     * Consumes a Kafka message representing a car listing event.
     * <p>
     * This method is triggered when a new message is available on the Kafka topic.
     * It deserializes the message into a CarListingEvent and processes it based on
     * the event type.
     * </p>
     *
     * @param record The Kafka message containing the car listing event data.
     */
    @KafkaListener(topics = "car-listing-topic", groupId = "car-listing-group")
    public void consume(ConsumerRecord<String, String> record) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            CarListingEvent carListingEvent = objectMapper.readValue(record.value(), CarListingEvent.class);
            carListingService.processCarListing(carListingEvent);
        } catch (JsonProcessingException ex) {
            log.error("Error occurred on consuming event: {}", ex.getMessage());
        }
    }
}
