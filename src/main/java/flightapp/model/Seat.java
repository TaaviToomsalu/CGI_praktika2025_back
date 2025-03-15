package flightapp.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "seats")
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String seatNumber;
    private boolean occupied;
    @ElementCollection
    private List<String> seatTypes; // "window", "aisle", "extra_legroom", "exit_row"

    @Enumerated(EnumType.STRING)
    private SeatClass classType;

    @ManyToOne
    @JoinColumn(name = "flight_id")
    private Flight flight;

    public Seat() {}

    public Seat(String seatNumber, boolean occupied, List<String> seatTypes, SeatClass classType, Flight flight) {
        this.seatNumber = seatNumber;
        this.occupied = occupied;
        this.seatTypes = seatTypes;
        this.classType = classType;
        this.flight = flight;
    }


    public Long getId() {
        return id;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    public List<String> getSeatTypes() {
        return seatTypes;
    }

    public void setSeatTypes(List<String> seatTypes) {
        this.seatTypes = seatTypes;
    }

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    public SeatClass getClassType() {
        return classType;
    }

    public void setClassType(SeatClass classType) {
        this.classType = classType;
    }
}
