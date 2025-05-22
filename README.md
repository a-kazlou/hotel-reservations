# Hotel Availability Manager

A Java command-line application for managing hotel room availability and reservations using JSON data files.

## Features

- Check room availability for specific dates or date ranges
- Search for available booking periods
- Load data from external JSON files or embedded resources

## Installation

1. Ensure you have Java 17+ installed
2. Download the latest JAR file from Releases

## Usage

### Running the Application

```bash
# With custom data files
java -jar HotelReservation.jar \
    --hotels /path/to/hotels.json \
    --bookings /path/to/bookings.json

# With default embedded data
java -jar HotelReservation.jar
```

## Available Commands

### Check Availability

```plaintext
availability(HOTEL_ID, DATE, ROOM_TYPE)
availability(HOTEL_ID, START_DATE-END_DATE, ROOM_TYPE)
```

### Examples:

```plaintext
availability(H1, 20250801, DLX)
# Output: 2

availability(H2, 20250824-20250829, STD)
# Output: 1
```

### Search for Available Dates

```plaintext
search(HOTEL_ID, DAYS_AHEAD, ROOM_TYPE)
```
### Example:
```plaintext
search(H5, 50, STD)
# Output: (20250801-20250805,3), (20250810-20250815,2)
```

## Data File Formats

### hotels.json
```json
[
    {
        "id": "H1",
        "name": "Grand Hotel",
        "roomTypes": [
            {
                "code": "DLX",
                "description": "Deluxe Room",
                "amenities": ["WiFi", "TV", "Minibar"],
                "features": ["Ocean View"]
            }
        ],
        "rooms": [
            {"roomType": "DLX", "roomId": "101"},
            {"roomType": "DLX", "roomId": "102"}
        ]
    }
]
```

### bookings.json
```json
[
    {
        "hotelId": "H1",
        "arrival": "20250801",
        "departure": "20250803",
        "roomType": "DLX",
        "roomRate": "Premium"
    }
]
```

## Building from Source

###   1. Clone the repository:
```bash
git clone https://github.com/a-kazlou/hotel-reservations
cd hotel-reservations
```
###   2. Build the project:
```bash
mvn clean package
```
###   3. Run tests:
```bash
mvn test
```

## Notes
- Date format: YYYYMMDD (e.g., 20250801 = August 1, 2025)

- Date ranges use inclusive start and exclusive end dates

- Press Enter on an empty line to exit the application


## Development Process
### AI-Assisted Components:
- Generating JSON templates for hotels and bookings data structures

- Creating Java models that precisely match the JSON schemas

- Producing data for parameterized tests

- Developing the command parser with regexp
