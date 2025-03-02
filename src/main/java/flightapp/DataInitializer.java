package flightapp;

import flightapp.model.Flight;
import flightapp.repository.FlightRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import java.time.LocalDateTime;

@Configuration
public class DataInitializer {

    @Bean
    public ApplicationRunner loadData(FlightRepository flightRepository) {
        return args -> {
            if (flightRepository.count() == 0) {
                flightRepository.save(new Flight("London", LocalDateTime.now().plusDays(1), 150.0));
                flightRepository.save(new Flight("Paris", LocalDateTime.now().plusDays(2), 120.0));
                flightRepository.save(new Flight("New York", LocalDateTime.now().plusDays(3), 300.0));
            }
        };
    }
}
