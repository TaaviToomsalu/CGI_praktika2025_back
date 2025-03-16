package flightapp;

import flightapp.model.Flight;
import flightapp.model.Seat;
import flightapp.repository.FlightRepository;
import flightapp.repository.SeatRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import flightapp.model.SeatClass;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Configuration
public class DataInitializer {

    @Bean
    public ApplicationRunner loadData(FlightRepository flightRepository, SeatRepository seatRepository) {
        return args -> {
            if (flightRepository.count() == 0) {
                Flight flight1 = flightRepository.save(new Flight("London", LocalDateTime.now().plusDays(1), 150.0, 300.0, 600.0));
                Flight flight2 = flightRepository.save(new Flight("Paris", LocalDateTime.now().plusDays(2), 120.0, 250.0, 500.0));
                Flight flight3 = flightRepository.save(new Flight("New York", LocalDateTime.now().plusDays(3), 300.0, 600.0, 1200.0));

                generateSeatsForFlight(flight1, seatRepository);
                generateSeatsForFlight(flight2, seatRepository);
                generateSeatsForFlight(flight3, seatRepository);
            }
        };
    }

    // Meetod, mis genereerib 6x4 istekoha plaani igale lennule
    private void generateSeatsForFlight(Flight flight, SeatRepository seatRepository) {
        // Tähtede jaotus klasside kaupa
        String firstClassSeats = "ABCD";
        String businessClassSeats = "ABCDEFG";
        String economyClassSeats = "ABCDEFGH";

        // Near exit read
        Set<Integer> exitRows = Set.of(1, 2, 10, 11, 20, 21, 29, 30);

        for (int row = 1; row <= 30; row++) {
            String seatRowLetters;
            SeatClass classType;

            if (row <= 2) {
                classType = SeatClass.FIRST;
                seatRowLetters = firstClassSeats;
            } else if (row <= 9) {
                classType = SeatClass.BUSINESS;
                seatRowLetters = businessClassSeats;
            } else {
                classType = SeatClass.ECONOMY;
                seatRowLetters = economyClassSeats;
            }

            for (char seat : seatRowLetters.toCharArray()) {
                boolean occupied = Math.random() < 0.3;
                List<String> seatTypes = new ArrayList<>();

                // Lisa aknaalused kohad
                if (seat == seatRowLetters.charAt(0) || seat == seatRowLetters.charAt(seatRowLetters.length() - 1)) {
                    seatTypes.add("window");
                }

                // Extra legroom ainult iga klassi esimesel real
                if (row == 1 || row == 3 || row == 10) {
                    seatTypes.add("extra_legroom");
                }

                // Near exit read
                if (exitRows.contains(row)) {
                    seatTypes.add("near_exit");
                }

                seatRepository.save(new Seat(row + "" + seat, occupied, seatTypes, classType, flight));
            }
        }
    }
}


