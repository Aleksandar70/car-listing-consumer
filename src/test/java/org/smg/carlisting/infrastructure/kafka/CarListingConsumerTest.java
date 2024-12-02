package org.smg.carlisting.infrastructure.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.smg.carlisting.application.CarListingService;
import org.smg.carlisting.domain.event.CarListingEvent;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CarListingConsumerTest {

    private static final String KEY = "key";
    private static final String CAR_LISTING_TOPIC = "car-listing-topic";

    @InjectMocks
    private CarListingConsumer carListingConsumer;

    @Mock
    private CarListingService carListingService;

    @Test
    public void testConsumeValidEvent() throws JsonProcessingException {
        String kafkaMessage = "{\"eventType\": \"CREATE\", \"carListing\": {\"make\": \"Peugeot\", \"model\": \"308\"," +
                " \"year\": \"2015\", \"minPrice\": 100, \"maxPrice\": 200, \"color\": \"blue\"}}";
        ObjectMapper objectMapper = new ObjectMapper();
        CarListingEvent event = objectMapper.readValue(kafkaMessage, CarListingEvent.class);
        ConsumerRecord<String, String> record = new ConsumerRecord<>(CAR_LISTING_TOPIC, 0, 0L, KEY, kafkaMessage);

        carListingConsumer.consume(record);

        verify(carListingService).processCarListing(event);
    }

    @Test
    public void testConsumeInvalidEvent() {
        ConsumerRecord<String, String> record = new ConsumerRecord<>(CAR_LISTING_TOPIC, 0, 0L, KEY, "invalid message");

        carListingConsumer.consume(record);

        verify(carListingService, never()).processCarListing(any(CarListingEvent.class));
    }
}