package org.example.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.model.entity.Booking;
import org.example.model.entity.Hotel;
import org.example.repository.DataProvider;
import org.example.repository.dataprovider.FileDataProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    private static final String DEFAULT_HOTELS_PATH = "/data/hotels.json";
    private static final String DEFAULT_BOOKINGS_PATH = "/data/bookings.json";
    @Value("${hotels.file.path:${HOTELS_FILE_PATH:" + DEFAULT_HOTELS_PATH + "}}")
    private String hotelsFilePath;

    @Value("${bookings.file.path:${BOOKINGS_FILE_PATH:" + DEFAULT_BOOKINGS_PATH + "}}")
    private String bookingsFilePath;
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
    
    @Bean
    public DataProvider<Hotel> hotelDataProvider(ObjectMapper objectMapper) {
        return new FileDataProvider<>(
                hotelsFilePath,
            Hotel[].class,
            Hotel::getId,
            objectMapper
        );
    }
    
    @Bean
    public DataProvider<Booking> bookingDataProvider(ObjectMapper objectMapper) {
        return new FileDataProvider<>(
                bookingsFilePath,
            Booking[].class,
            Booking::getHotelId,
            objectMapper
        );
    }
}