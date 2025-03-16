package flightapp.service;

import flightapp.model.Seat;
import flightapp.model.SeatClass;
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
                                   boolean requireAdjacent,
                                   String seatClass) {
        List<Seat> seats = getSeatsForFlight(flightId);

        SeatClass enumClass;
        try {
            enumClass = SeatClass.valueOf(seatClass.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Collections.emptyList();
        }

        List<Seat> availableSeats = seats.stream()
                .filter(seat -> seat.getClassType() == enumClass)
                .filter(seat -> !seat.isOccupied())
                .collect(Collectors.toList());

        for (String preference : preferences) {
            availableSeats = availableSeats.stream()
                    .filter(seat -> "any".equalsIgnoreCase(preference) || seat.getSeatTypes().contains(preference))
                    .collect(Collectors.toList());
        } // <-- Sulge tsükkel õigesti!

        if (numSeats > 1 && requireAdjacent) {
            List<Seat> adjacentSeats = findAdjacentSeats(availableSeats, numSeats);
            if (!adjacentSeats.isEmpty()) {
                return adjacentSeats;
            }
        }

        return availableSeats.stream().limit(numSeats).collect(Collectors.toList()); // <-- Veendu, et siin on return!
    }

    private List<Seat> findAdjacentSeats(List<Seat> availableSeats, int numSeats) {
        // Eemaldame mittekohased sümbolid ja kontrollime, et seatNumber sisaldab arvu
        availableSeats.sort(Comparator.comparingInt(seat -> {
            String numberPart = seat.getSeatNumber().replaceAll("[^0-9]", "");
            return numberPart.isEmpty() ? Integer.MAX_VALUE : Integer.parseInt(numberPart);
        }));

        for (int i = 0; i <= availableSeats.size() - numSeats; i++) {
            boolean adjacent = true;

            // Enne tsüklit loome numbriliste istekohtade listi, et vältida topeltarvutusi
            List<Integer> seatNumbers = availableSeats.stream()
                    .map(seat -> {
                        String numPart = seat.getSeatNumber().replaceAll("[^0-9]", "");
                        return numPart.isEmpty() ? -1 : Integer.parseInt(numPart);
                    })
                    .collect(Collectors.toList());

            for (int j = 0; j < numSeats - 1; j++) {
                int currentSeatNumber = seatNumbers.get(i + j);
                int nextSeatNumber = seatNumbers.get(i + j + 1);

                // Kui numPart oli tühi ja me kasutasime -1, siis see koht on vigane, ignoreeri
                if (currentSeatNumber == -1 || nextSeatNumber == -1) {
                    adjacent = false;
                    break;
                }

                // Kontrollime, kas istekoht on järjestikune
                if (nextSeatNumber != currentSeatNumber + 1) {
                    adjacent = false;
                    break;
                }
            }
            if (adjacent) {
                return availableSeats.subList(i, i + numSeats);
            }
        }
        return Collections.emptyList();
    }

    public void reserveSeats(Long flightId, List<String> seatIds) {
        if (seatIds == null || seatIds.isEmpty()) { // Kontrollime, et list ei oleks tühi
            throw new RuntimeException("No seats selected for reservation.");
        }
        List<Seat> seats = seatRepository.findByFlightIdAndSeatNumberIn(flightId, seatIds);

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
