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

    public List<Seat> suggestSeats(Long flightId,
                                   int numSeats,
                                   List<String> preferences,
                                   boolean requireAdjacent) {
        List<Seat> seats = getSeatsForFlight(flightId);
        System.out.println("Total seats found for flight " + flightId + ": " + seats.size());


        // Filtreeri vabad istekohad
        List<Seat> availableSeats = seats.stream()
                .filter(seat -> !seat.isOccupied()) // Ainult vabad istekohad
                .collect(Collectors.toList());

        System.out.println("Available seats: " + availableSeats.size());

        // Filtreeri vastavalt iga eelistuse järgi
        for (String preference : preferences) {
            availableSeats = availableSeats.stream()
                    .filter(seat -> "any".equalsIgnoreCase(preference) || seat.getSeatTypes().contains(preference))
                    .collect(Collectors.toList());
        }

        System.out.println("Seats after filtering by preferences (" + preferences + "): " + availableSeats.size());

        if (numSeats > 1 && requireAdjacent) {
            List<Seat> adjacentSeats = findAdjacentSeats(availableSeats, numSeats);
            System.out.println("Adjacent seats found: " + adjacentSeats.size());
            if (!adjacentSeats.isEmpty()) {
                return adjacentSeats;  // ✅ Return adjacent seats if found
            }
        }

        // If adjacent seats weren’t found or not required, return any available ones
        List<Seat> selectedSeats = availableSeats.stream()
                .limit(numSeats)
                .collect(Collectors.toList());

        System.out.println("Final suggested seats: " + selectedSeats.size());

        return selectedSeats;
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

    public void reserveSeats(Long flightId, List<String> seatIds) {
        if (seatIds == null || seatIds.isEmpty()) { // Kontrollime, et list ei oleks tühi
            throw new RuntimeException("No seats selected for reservation.");
        }

        System.out.println("Checking seat IDs: " + seatIds);
        List<Seat> seats = seatRepository.findByFlightIdAndSeatNumberIn(flightId, seatIds);

        System.out.println("Seats found in DB: " + seats.size());

        // Kontrollime, kas kõik valitud kohad kuuluvad õigesse lendu ja on vabad
        for (Seat seat : seats) {
            if (seat.isOccupied()) {
                throw new RuntimeException("Seat " + seat.getSeatNumber() + " is already occupied");
            }
        }

        // Märgime kõik valitud kohad broneerituks
        seats.forEach(seat -> seat.setOccupied(true));
        seatRepository.saveAll(seats);
    }

    public List<Seat> getAvailableSeatsForFlight(Long flightId) {
        return seatRepository.findByFlightIdAndOccupiedFalse(flightId);
    }

}
