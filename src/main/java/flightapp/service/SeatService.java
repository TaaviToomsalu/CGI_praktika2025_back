package flightapp.service;

import flightapp.model.Seat;
import flightapp.repository.SeatRepository;
import org.springframework.stereotype.Service;

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

    public Optional<Seat> suggestSeat(Long flightId, String preference) {
        List<Seat> seats = seatRepository.findByFlightId(flightId);

        //Filtreeri vabad istekohad ja leia sobiv
        return seats.stream()
                .filter(seat -> !seat.isOccupied()) //Ainult vabad istekohad
                // kui eelistus any, tagastab ükskõik millise vaba koha. min sorteerib rea numbri järgi, et eelistada eespool asuvaid kohti
                .filter(seat -> "any".equalsIgnoreCase(preference) || seat.getSeatType().equalsIgnoreCase(preference))
                .min(Comparator.comparingInt(seat -> Integer.parseInt(seat.getSeatNumber().replaceAll("[^0-9]", ""))));
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
