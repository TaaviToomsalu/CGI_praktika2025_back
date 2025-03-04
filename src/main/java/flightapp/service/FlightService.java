package flightapp.service;

import flightapp.model.Flight;
import flightapp.repository.FlightRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FlightService {
    private final FlightRepository flightRepository;

    public FlightService(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    public List<Flight> getAllFlights() {
        return flightRepository.findAll();
    }

    public List<Flight> searchFlights(String destination, LocalDateTime startDate, LocalDateTime endDate, Double maxPrice) {
        if (destination != null) {
            return flightRepository.findByDestinationContainingIgnoreCase(destination);
        } else if (startDate != null && endDate != null) {
            return flightRepository.findByDepartureTimeBetween(startDate, endDate);
        } else if (maxPrice != null) {
            return flightRepository.findByPriceLessThanEqual(maxPrice);
        }
        return flightRepository.findAll();
    }
}
