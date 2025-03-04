package flightapp.controller;

import flightapp.model.Seat;
import flightapp.service.SeatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public Optional<Seat> suggestSeat(@PathVariable Long flightId, @RequestParam String preference) {
        return seatService.suggestSeat(flightId, preference);
    }

    @PutMapping("/{seatId}/reserve")
    public ResponseEntity<String> reserveSeat(@PathVariable Long seatId) {
        try {
            seatService.reserveSeat(seatId);
            return ResponseEntity.ok("Seat reserved succesfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}
