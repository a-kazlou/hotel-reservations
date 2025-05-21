package org.example.repository.dataprovider;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.example.exception.DataLoadingException;
import org.example.repository.DataProvider;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class FileDataProvider<T> implements DataProvider<T> {

    private static final String INITIALIZATION_EXCEPTION = "Failed to initialize data provider";
    private static final String FILE_NOT_FOUND_EXCEPTION = "File not found: ";
    private static final String DATA_LOAD_EXCEPTION = "Failed to load data from file: ";
    private final String filePath;
    private final ObjectMapper objectMapper;
    private final Class<T[]> arrayType;
    private final Function<T, String> idExtractor;
    private List<T> dataCache;

    public FileDataProvider(String filePath, 
                          Class<T[]> arrayType, 
                          Function<T, String> idExtractor,
                          ObjectMapper objectMapper) {
        this.filePath = filePath;
        this.arrayType = arrayType;
        this.idExtractor = idExtractor;
        this.objectMapper = objectMapper;
        this.dataCache = new ArrayList<>();
        init();
    }

    public void init() {
        try {
            refresh();
        } catch (DataLoadingException e) {
            throw new RuntimeException(INITIALIZATION_EXCEPTION, e);
        }
    }

    @Override
    public List<T> getAll() throws DataLoadingException {
        return new ArrayList<>(dataCache);
    }

    @Override
    public Optional<T> getById(String id) throws DataLoadingException {
        return dataCache.stream()
            .filter(item -> idExtractor.apply(item).equals(id))
            .findFirst();
    }

    @Override
    public void refresh() throws DataLoadingException {
        try {
            InputStream inputStream = getClass().getResourceAsStream(filePath);
            if (inputStream == null) {
                throw new DataLoadingException(FILE_NOT_FOUND_EXCEPTION + filePath);
            }
            T[] items = objectMapper.readValue(inputStream, arrayType);
            this.dataCache = Arrays.asList(items);
        } catch (Exception e) {
            throw new DataLoadingException(DATA_LOAD_EXCEPTION + filePath, e);
        }
    }
}