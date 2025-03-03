package flightapp;

import flightapp.model.Flight;
import flightapp.model.Seat;
import flightapp.repository.FlightRepository;
import flightapp.repository.SeatRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import java.time.LocalDateTime;

@Configuration
public class DataInitializer {

    @Bean
    public ApplicationRunner loadData(FlightRepository flightRepository, SeatRepository seatRepository) {
        return args -> {
            if (flightRepository.count() == 0) {
                Flight flight1 = flightRepository.save(new Flight("London", LocalDateTime.now().plusDays(1), 150.0));
                Flight flight2 = flightRepository.save(new Flight("Paris", LocalDateTime.now().plusDays(2), 120.0));
                Flight flight3 = flightRepository.save(new Flight("New York", LocalDateTime.now().plusDays(3), 300.0));

                generateSeatsForFlight(flight1, seatRepository);
                generateSeatsForFlight(flight2, seatRepository);
                generateSeatsForFlight(flight3, seatRepository);
            }
        };
    }

    // Meetod, mis genereerib 6x4 istekoha plaani igale lennule
    private void generateSeatsForFlight(Flight flight, SeatRepository seatRepository) {
        for (int row = 1; row <= 6; row++) {
            for (char seat = 'A'; seat <= 'D'; seat++) {
                boolean occupied = Math.random() < 0.3;

                String seatType = switch (seat) {
                    case 'A' -> "window";
                    case 'B' -> "aisle";
                    case 'C' -> "aisle";
                    case 'D' -> "window";
                    default -> "standard";
                };

                seatRepository.save(new Seat(row + "" + seat, occupied, seatType, flight));
            }
        }
    }
}


