package flightapp.repository;

import flightapp.model.Flight;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long>{
    List<Flight> findByDestinationContainingIgnoreCase(String destination);
    List<Flight> findByDepartureTimeBetween(LocalDateTime start, LocalDateTime end);
    List<Flight> findByEconomyPriceLessThanEqual(Double maxPrice);
    List<Flight> findByBusinessPriceLessThanEqual(Double maxPrice);
    List<Flight> findByFirstClassPriceLessThanEqual(Double maxPrice);

}
