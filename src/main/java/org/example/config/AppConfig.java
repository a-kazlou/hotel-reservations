package org.example.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.model.entity.Booking;
import org.example.model.entity.Hotel;
import org.example.repository.DataProvider;
import org.example.repository.dataprovider.FileDataProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    private static final String HOTELS_FILE_PATH = "/data/hotels.json";
    private static final String BOOKINGS_FILE_PATH = "/data/bookings.json";
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
    
    @Bean
    public DataProvider<Hotel> hotelDataProvider(ObjectMapper objectMapper) {
        return new FileDataProvider<>(
                HOTELS_FILE_PATH,
            Hotel[].class,
            Hotel::getId,
            objectMapper
        );
    }
    
    @Bean
    public DataProvider<Booking> bookingDataProvider(ObjectMapper objectMapper) {
        return new FileDataProvider<>(
                BOOKINGS_FILE_PATH,
            Booking[].class,
            Booking::getHotelId,
            objectMapper
        );
    }
}