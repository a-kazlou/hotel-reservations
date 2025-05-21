package org.example.shell;

import lombok.RequiredArgsConstructor;
import org.example.service.AvailabilityService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchCommand implements Command {

    private static final String USAGE_MESSAGE = "Usage: search <hotelId> <daysAhead> <roomType>";
    private static final String DAYS_FORMAT_EXCEPTION = "Error: daysAhead must be a number";
    private static final String NO_AVAILABILITY_INFO = "No availability found";
    private static final String SEARCH_COMMAND_NAME = "search";
    private static final String SEARCH_COMMAND_DESCRIPTION = "Search availability for upcoming dates";

    private final AvailabilityService availabilityService;

    @Override
    public String execute(String[] args) {
        if (args.length != 3) {
            return USAGE_MESSAGE;
        }

        String hotelId = args[0];
        int daysAhead;
        try {
            daysAhead = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            return DAYS_FORMAT_EXCEPTION;
        }
        String roomType = args[2];

        try {
            Map<String, Integer> availability = availabilityService
                .searchAvailability(hotelId, roomType, daysAhead);

            if (availability.isEmpty()) {
                return NO_AVAILABILITY_INFO;
            }

            return availability.entrySet().stream()
                .map(e -> String.format("(%s, %d)", e.getKey(), e.getValue()))
                .collect(Collectors.joining(", "));
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @Override
    public String getName() {
        return SEARCH_COMMAND_NAME;
    }

    @Override
    public String getDescription() {
        return SEARCH_COMMAND_DESCRIPTION;
    }
}