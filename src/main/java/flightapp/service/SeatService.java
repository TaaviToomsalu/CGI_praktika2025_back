package flightapp.service;

import flightapp.model.Seat;
import flightapp.repository.SeatRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SeatService {
    private final SeatRepository seatRepository;

    public SeatService(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }

    public List<Seat> getSeatsForFlight(Long flightId) {
        return seatRepository.findByFlightId(flightId);
    }

    public List<Seat> suggestSeats(Long flightId, int numSeats, List<String> preferences) {
        List<Seat> seats = seatRepository.findByFlightId(flightId);

        // Filtreeri vabad istekohad
        List<Seat> availableSeats = seats.stream()
                .filter(seat -> !seat.isOccupied()) // Ainult vabad istekohad
                .collect(Collectors.toList());

        // Filtreeri vastavalt iga eelistuse järgi
        for (String preference : preferences) {
            availableSeats = availableSeats.stream()
                    .filter(seat -> "any".equalsIgnoreCase(preference) || seat.getSeatTypes().contains(preference))
                    .collect(Collectors.toList());
        }

        // Kui mitu kohta tuleb broneerida, leia kõrvuti olevad kohad
        if (numSeats > 1) {
            return findAdjacentSeats(availableSeats, numSeats);
        }

        // Vali sobiv koht
        return availableSeats.stream()
                .min(Comparator.comparingInt(seat -> Integer.parseInt(seat.getSeatNumber().replaceAll("[^0-9]", ""))))
                .map(Collections::singletonList)
                .orElse(Collections.emptyList());
    }

    private List<Seat> findAdjacentSeats(List<Seat> availableSeats, int numSeats) {
        // Sorteeri kohad numbri järgi (eeldades, et istekohtadel on arvuline osa)
        availableSeats.sort(Comparator.comparingInt(seat -> Integer.parseInt(seat.getSeatNumber().replaceAll("[^0-9]", ""))));

        for (int i = 0; i <= availableSeats.size() - numSeats; i++) {
            boolean adjacent = true;

            for (int j = 0; j < numSeats - 1; j++) {
                int currentSeatNumber = Integer.parseInt(availableSeats.get(i + j).getSeatNumber().replaceAll("[^0-9]", ""));
                int nextSeatNumber = Integer.parseInt(availableSeats.get(i + j + 1).getSeatNumber().replaceAll("[^0-9]", ""));

                // Kontrolli, kas järgmine koht on järjestikune
                if (nextSeatNumber != currentSeatNumber + 1) {
                    adjacent = false;
                    break;
                }
            }

            if (adjacent) {
                return availableSeats.subList(i, i + numSeats);
            }
        }

        return Collections.emptyList(); // Kui ei leitud, tagasta tühi list
    }


    public void reserveSeat(Long seatId) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new RuntimeException("Seat not found"));

        if (seat.isOccupied()) {
            throw new RuntimeException("Seat is already occupied");
        }

        seat.setOccupied(true);
        seatRepository.save(seat);
    }

    public void reserveMultipleSeats(Long flightId, List<Long> seatIds) {
        List<Seat> seats = seatRepository.findAllById(seatIds);

        // Kontrollime, kas kõik valitud kohad on vabad
        for (Seat seat : seats) {
            if (seat.isOccupied() || !seat.getFlight().getId().equals(flightId)) {
                throw new RuntimeException("One or more seats are already occupied or do not belong to this flight");
            }
        }

        //Märgib kohad broneerituks
        seats.forEach(seat -> seat.setOccupied(true));
        seatRepository.saveAll(seats);
    }

    public List<Seat> getAvailableSeatsForFlight(Long flightId) {
        return seatRepository.findByFlightIdAndOccupiedFalse(flightId);
    }

}
