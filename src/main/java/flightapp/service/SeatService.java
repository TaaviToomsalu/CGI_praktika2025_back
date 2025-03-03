package flightapp.service;

import flightapp.model.Seat;
import flightapp.repository.SeatRepository;
import org.springframework.stereotype.Service;

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
                .filter(seat -> !seat.isOccupied())
                .filter(seat -> seat.getSeatType().equalsIgnoreCase(preference))
                .findFirst();
    }
}
