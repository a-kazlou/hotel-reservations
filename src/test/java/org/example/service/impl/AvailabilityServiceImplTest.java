package org.example.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;

import org.example.exception.HotelNotFoundException;
import org.example.model.entity.Booking;
import org.example.model.entity.Hotel;
import org.example.model.entity.Room;
import org.example.repository.BookingRepository;
import org.example.repository.HotelRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AvailabilityServiceImplTest {

    private static LocalDate testDate = LocalDate.now().plusYears(1);
    private static int testYear = testDate.getYear();
    @Mock
    private HotelRepository hotelRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private AvailabilityServiceImpl availabilityService;

    private Hotel testHotel;

    @Test
    void checkAvailability_shouldThrowWhenHotelNotFound() {
        String hotelId = "NON_EXISTENT";
        when(hotelRepository.findById(hotelId)).thenReturn(Optional.empty());

        assertThrows(HotelNotFoundException.class, () ->
                availabilityService.checkAvailability(hotelId, LocalDate.now(), "SGL")
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    void checkAvailability_shouldValidateHotelId(String invalidHotelId) {
        assertThrows(IllegalArgumentException.class, () ->
                availabilityService.checkAvailability(invalidHotelId, LocalDate.now(), "SGL")
        );
    }

    @Test
    void checkAvailability_shouldHandleNullDate() {
        assertThrows(IllegalArgumentException.class, () ->
                availabilityService.checkAvailability("H1", null, "SGL")
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    void checkAvailability_shouldValidateRoomType(String invalidRoomType) {
        assertThrows(IllegalArgumentException.class, () ->
                availabilityService.checkAvailability("H1", LocalDate.now(), invalidRoomType)
        );
    }

    @ParameterizedTest
    @MethodSource("availabilityTestCases")
    void checkAvailability_shouldReturnCorrectCount(
            String hotelId,
            LocalDate date,
            String roomType,
            int expectedAvailable,
            List<Room> rooms,
            List<Booking> bookings
    ) {
        testHotel = new Hotel(hotelId, "Test Hotel", Collections.emptyList(), rooms);
        when(hotelRepository.findById(hotelId)).thenReturn(Optional.of(testHotel));
        when(bookingRepository.findByHotelAndDateRange(
                hotelId, date, date.plusDays(1), roomType))
                .thenReturn(bookings);

        int actual = availabilityService.checkAvailability(hotelId, date, roomType);

        assertEquals(expectedAvailable, actual);
    }

    @ParameterizedTest
    @MethodSource("dateRangeTestCases")
    void checkAvailabilityForDateRange_shouldReturnMinimumAvailability(
            LocalDate startDate,
            LocalDate endDate,
            int expectedAvailable
    ) {
        String hotelId = "H1";
        String roomType = "SGL";

        testHotel = new Hotel(hotelId, "Test Hotel", Collections.emptyList(),
                List.of(
                        new Room("101", "SGL"),
                        new Room("102", "SGL")
                ));

        when(hotelRepository.findById(hotelId)).thenReturn(Optional.of(testHotel));

        when(bookingRepository.findByHotelAndDateRange(
                eq(hotelId), any(LocalDate.class), any(LocalDate.class), eq(roomType)))
                .thenAnswer(invocation -> {
                    LocalDate date = invocation.getArgument(1);
                    if (date.equals(LocalDate.of(testYear, 9, 1))) {
                        return List.of(new Booking(hotelId, date, date.plusDays(2), roomType, "Standard"));
                    }
                    return Collections.emptyList();
                });

        int actual = availabilityService.checkAvailability(
                hotelId, startDate, endDate, roomType);

        assertEquals(expectedAvailable, actual);
    }

    @ParameterizedTest
    @MethodSource("consecutiveRangeProvider")
    void checkAvailability_consecutiveRanges(
            LocalDate startDate,
            LocalDate endDate,
            int expectedAvailable,
            List<Booking> mockBookings
    ) {
        String hotelId = "H1";
        String roomType = "SGL";

        testHotel = new Hotel(hotelId, "Test Hotel", Collections.emptyList(),
                List.of(
                        new Room("101", "SGL"),
                        new Room("102", "SGL"),
                        new Room("103", "SGL")
                ));

        when(hotelRepository.findById(hotelId)).thenReturn(Optional.of(testHotel));
        when(bookingRepository.findByHotelAndDateRange(
                eq(hotelId), eq(startDate), eq(endDate), eq(roomType)))
                .thenReturn(mockBookings);

        int actual = availabilityService.checkAvailability(hotelId, startDate, endDate, roomType);

        assertEquals(expectedAvailable, actual);
    }

    private static Stream<Arguments> consecutiveRangeProvider() {
        LocalDate today = LocalDate.of(testYear, 5, 20);
        LocalDate tomorrow = today.plusDays(1);
        LocalDate dayAfter = today.plusDays(2);

        return Stream.of(
                Arguments.of(
                        today, tomorrow, 2,
                        List.of(new Booking("H1", today, tomorrow, "SGL", "Standard"))
                ),
                Arguments.of(
                        tomorrow, dayAfter, 1,
                        List.of(
                                new Booking("H1", tomorrow, dayAfter, "SGL", "Standard"),
                                new Booking("H1", today, dayAfter, "SGL", "Premium")
                        )
                )
        );
    }

    private static Stream<Arguments> dateRangeTestCases() {
        return Stream.of(
                // startDate, endDate, expectedAvailable
                Arguments.of(
                        LocalDate.of(testYear, 9, 1),
                        LocalDate.of(testYear, 9, 3),
                        1  // Minimum availability during this range
                ),
                Arguments.of(
                        LocalDate.of(testYear, 9, 2),
                        LocalDate.of(testYear, 9, 4),
                        2  // No bookings during this period
                ),
                Arguments.of(
                        LocalDate.of(testYear, 8, 30),
                        LocalDate.of(testYear, 9, 2),
                        1  // Minimum availability during overlapping period
                )
        );
    }

    private static Stream<Arguments> availabilityTestCases() {
        return Stream.of(
                // hotelId, date, roomType, expectedAvailable, rooms, bookings
                Arguments.of(
                        "H1",
                        LocalDate.of(testYear, 9, 1),
                        "SGL",
                        2,
                        List.of(
                                new Room("101", "SGL"),
                                new Room("102", "SGL"),
                                new Room("201", "DBL")
                        ),
                        Collections.emptyList()
                ),
                Arguments.of(
                        "H1",
                        LocalDate.of(testYear, 9, 1),
                        "SGL",
                        1,
                        List.of(
                                new Room("101", "SGL"),
                                new Room("102", "SGL")
                        ),
                        List.of(
                                new Booking("H1", LocalDate.of(testYear, 9, 1),
                                        LocalDate.of(testYear, 9, 3), "SGL", "Standard")
                        )
                ),
                Arguments.of(
                        "H2",
                        LocalDate.of(testYear, 9, 5),
                        "DBL",
                        0,
                        List.of(
                                new Room("101", "DBL")
                        ),
                        List.of(
                                new Booking("H2", LocalDate.of(testYear, 9, 4),
                                        LocalDate.of(testYear, 9, 6), "DBL", "Premium")
                        )
                )
        );
    }
}
