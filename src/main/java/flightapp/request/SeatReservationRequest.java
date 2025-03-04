package flightapp.dto; // ⬅ Kontrolli, et see sobib sinu projekti struktuuriga!

import java.util.List;

public class SeatReservationRequest {
    private Long flightId;
    private List<Long> seatIds;

    public Long getFlightId() {
        return flightId;
    }

    public void setFlightId(Long flightId) {
        this.flightId = flightId;
    }

    public List<Long> getSeatIds() {
        return seatIds;
    }

    public void setSeatIds(List<Long> seatIds) {
        this.seatIds = seatIds;
    }
}