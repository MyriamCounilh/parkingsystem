package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

    public long durationInMinute(Ticket ticket) {
        long inHour = ticket.getInTime().getTime();
        long outHour = ticket.getOutTime().getTime();

        long durationInMinute = (outHour - inHour) / (60 * 1000);

        /**
         * Add free parking feature for the first 30 minutes
         */
        if (durationInMinute <= 30) {
            System.out.println("Add free parking feature for the first 30 minutes");
            System.out.println(durationInMinute + " minutes");
            durationInMinute = 0;
        }

        return durationInMinute;
    }

    public void calculateFare(Ticket ticket){

        long duration = durationInMinute(ticket);

        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        //TODO: Some tests are failing here. Need to check if this logic is correct

        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
                ticket.setPrice( ( duration * Fare.CAR_RATE_PER_HOUR ) / 60 );
                break;
            }
            case BIKE: {
                ticket.setPrice( ( duration * Fare.BIKE_RATE_PER_HOUR ) / 60 );
                break;
            }
            default: throw new IllegalArgumentException("Unkown Parking Type");
        }
    }
}
