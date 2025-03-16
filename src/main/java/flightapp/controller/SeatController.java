package flightapp.controller;

import flightapp.model.Seat;
import flightapp.service.SeatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import flightapp.request.SeatReservationRequest;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/seats")
public class SeatController {
    private final SeatService seatService;

    public SeatController(SeatService seatService) {
        this.seatService = seatService;
    }

    //Tagastab kõik istekoha valikud konkreetse lennu jaoks
    @GetMapping("/{flightId}")
    public List<Seat> getSeats(@PathVariable Long flightId,
                               @RequestParam(value = "class", required = false) String seatClass) {
        //System.out.println("Fetching seats for flightId: " + flightId);
        //System.out.println("seatClass from frontend: '" + seatClass + "'");

        List<Seat> seats = seatService.getSeatsForFlight(flightId);

        if (seatClass != null) {
            seats = seats.stream()
                    .filter(seat -> seatClass.equalsIgnoreCase(seat.getClassType().name()))
                    .toList();
        }

        System.out.println("Returning " + seats.size() + " seats");
        return seats;
    }
    //Soovitab istekoha eelistuse järgi
    @GetMapping("/{flightId}/suggest")
    public List<Seat> suggestSeats(@PathVariable Long flightId,
                                   @RequestParam int numSeats,
                                   @RequestParam List<String> preferences,
                                   @RequestParam(required = false, defaultValue = "false") boolean requireAdjacent,
                                   @RequestParam String seatClass) {

        System.out.println("Received preferences: " + preferences); // Debugging log

        return seatService.suggestSeats(flightId, numSeats, preferences, requireAdjacent, seatClass);
    }

    @GetMapping("/{flightId}/available")
    public List<Seat> getAvailableSeats(@PathVariable Long flightId) {
        return seatService.getAvailableSeatsForFlight(flightId);
    }

    @PutMapping("/reserve")
    public ResponseEntity<String> reserveSeats(@RequestBody SeatReservationRequest request) {
        try {
            seatService.reserveSeats(request.getFlightId(), request.getSeatIds());
            return ResponseEntity.ok("Seats reserved successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
