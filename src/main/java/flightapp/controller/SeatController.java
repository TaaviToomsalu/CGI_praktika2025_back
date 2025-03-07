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
    public List<Seat> getSeats(@PathVariable Long flightId) {
        return seatService.getSeatsForFlight(flightId);
    }

    //Soovitab istekoha eelistuse järgi
    @GetMapping("/{flightId}/suggest")
    public List<Seat> suggestSeats(@PathVariable Long flightId,
                                   @RequestParam int numSeats,
                                   @RequestParam List<String> preferences) {
        return seatService.suggestSeats(flightId, numSeats, preferences);
    }

    @GetMapping("/{flightId}/available")
    public List<Seat> getAvailableSeats(@PathVariable Long flightId) {
        return seatService.getAvailableSeatsForFlight(flightId);
    }

    @PutMapping("/{seatId}/reserve")
    public ResponseEntity<String> reserveSeat(@PathVariable Long seatId) {
        try {
            seatService.reserveSeat(seatId);
            return ResponseEntity.ok("Seat reserved successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/reserve")
    public ResponseEntity<String> reserveSeats(@RequestBody SeatReservationRequest request) {
        try {
            seatService.reserveMultipleSeats(request.getFlightId(), request.getSeatIds());
            return ResponseEntity.ok("Seats reserved successfully");
        }catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
