package org.example.repository;

import org.example.exception.DataLoadingException;

import java.util.List;
import java.util.Optional;

public interface DataProvider<T> {
    List<T> getAll() throws DataLoadingException;
    Optional<T> getById(String id) throws DataLoadingException;
    void refresh() throws DataLoadingException;
}