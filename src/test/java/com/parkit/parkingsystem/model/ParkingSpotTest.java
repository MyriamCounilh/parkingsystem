package com.parkit.parkingsystem.model;

import com.parkit.parkingsystem.constants.ParkingType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ParkingSpotTest {

    @Test
    public void setId() {
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
        parkingSpot.setId(4);
        assertEquals(4, parkingSpot.getId());
    }

    @Test
    public void setParkingType() {
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
        parkingSpot.setParkingType(ParkingType.BIKE);
        assertEquals(ParkingType.BIKE, parkingSpot.getParkingType());
    }

    @Test
    public void setAvailable() {
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
        parkingSpot.setAvailable(false);
        assertFalse(parkingSpot.isAvailable());
    }

    @Test
    public void testEquals() {
        ParkingSpot parkingSpotA = new ParkingSpot(1, ParkingType.CAR, true);
        ParkingSpot parkingSpotB = new ParkingSpot(1, ParkingType.CAR, true);
        assertEquals(parkingSpotA, parkingSpotB);
        assertEquals(1, parkingSpotA.hashCode());
        assertEquals(1, parkingSpotB.hashCode());
    }

    @Test
    public void testNotEquals() {
        ParkingSpot parkingSpotA = new ParkingSpot(1, ParkingType.CAR, true);
        ParkingSpot parkingSpotB = new ParkingSpot(2, ParkingType.BIKE, false);
        assertNotEquals(parkingSpotA, parkingSpotB);
        assertEquals(1, parkingSpotA.hashCode());
        assertEquals(2, parkingSpotB.hashCode());
    }

    @Test
    public void testNotEqualsWithAnother() {
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
        Ticket ticket = new Ticket();
        assertNotEquals(parkingSpot, ticket);
    }
}
