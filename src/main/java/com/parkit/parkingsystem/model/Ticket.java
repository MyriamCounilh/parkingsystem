package com.parkit.parkingsystem.model;

import javax.annotation.Nullable;
import java.util.Date;

public class Ticket {
    private int id;
    private ParkingSpot parkingSpot;
    private String vehicleRegNumber;
    private double price;
    private Date inTime;
    private Date outTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ParkingSpot getParkingSpot() {
        return parkingSpot;
    }

    public void setParkingSpot(ParkingSpot parkingSpot) {
        this.parkingSpot = parkingSpot;
    }

    public String getVehicleRegNumber() {
        return vehicleRegNumber;
    }

    public void setVehicleRegNumber(String vehicleRegNumber) {
        this.vehicleRegNumber = vehicleRegNumber;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Date getInTime() {
        return (Date) this.inTime.clone();
    }

    public void setInTime(Date inTime) {
         this.inTime = (Date) inTime.clone();
    }

    @Nullable
    public Date getOutTime() {
        return this.outTime == null ? null : (Date) this.outTime.clone();
    }

    public void setOutTime(@Nullable Date outTime) {
        this.outTime = outTime == null ? null : (Date) outTime.clone();
    }
}
