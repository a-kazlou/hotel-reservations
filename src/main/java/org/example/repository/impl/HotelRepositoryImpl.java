package org.example.repository.impl;

import lombok.RequiredArgsConstructor;
import org.example.exception.DataLoadingException;
import org.example.exception.RepositoryException;
import org.example.model.entity.Hotel;
import org.example.repository.DataProvider;
import org.example.repository.HotelRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class HotelRepositoryImpl implements HotelRepository {

    private static final String FETCH_SPECIFIC_HOTEL_EXCEPTION = "Failed to fetch hotel by ID";
    private static final String FETCH_HOTEL_EXCEPTION = "Failed to fetch all hotels";
    private static final String REPOSITORY_REFRESH_EXCEPTION = "Failed to refresh hotel data";

    private final DataProvider<Hotel> hotelDataProvider;

    @Override
    public Optional<Hotel> findById(String hotelId) {
        try {
            return hotelDataProvider.getById(hotelId);
        } catch (DataLoadingException e) {
            throw new RepositoryException(FETCH_SPECIFIC_HOTEL_EXCEPTION, e);
        }
    }

    @Override
    public List<Hotel> findAll() {
        try {
            return hotelDataProvider.getAll();
        } catch (DataLoadingException e) {
            throw new RepositoryException(FETCH_HOTEL_EXCEPTION, e);
        }
    }

    @Override
    public void refresh() {
        try {
            hotelDataProvider.refresh();
        } catch (DataLoadingException e) {
            throw new RepositoryException(REPOSITORY_REFRESH_EXCEPTION, e);
        }
    }
}