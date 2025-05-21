    package org.example.shell;

    import lombok.RequiredArgsConstructor;
    import org.example.service.AvailabilityService;
    import org.springframework.stereotype.Service;

    import java.time.LocalDate;
    import java.time.format.DateTimeFormatter;

    @Service
    @RequiredArgsConstructor
    public class AvailabilityCommand implements Command {

    private static final String USAGE_MESSAGE = "Usage: availability <hotelId> <date/range> <roomType>";
    private static final String AVAILABILITY_COMMAND_NAME = "availability";
    private static final String AVAILABILITY_COMMAND_DESCRIPTION = "Check room availability";

    private final AvailabilityService availabilityService;

    @Override
    public String execute(String[] args) {
        if (args.length != 3) {
            return USAGE_MESSAGE;
        }
        
        String hotelId = args[0];
        String dateInput = args[1];
        String roomType = args[2];
        
        try {
            if (dateInput.contains("-")) {
                String[] dates = dateInput.split("-");
                LocalDate start = LocalDate.parse(dates[0], DateTimeFormatter.BASIC_ISO_DATE);
                LocalDate end = LocalDate.parse(dates[1], DateTimeFormatter.BASIC_ISO_DATE);
                int available = availabilityService.checkAvailability(hotelId, start, end, roomType);
                return String.valueOf(available);
            } else {
                LocalDate date = LocalDate.parse(dateInput, DateTimeFormatter.BASIC_ISO_DATE);
                int available = availabilityService.checkAvailability(hotelId, date, roomType);
                return String.valueOf(available);
            }
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @Override
    public String getName() {
        return AVAILABILITY_COMMAND_NAME;
    }

    @Override
    public String getDescription() {
        return AVAILABILITY_COMMAND_DESCRIPTION;
    }
}