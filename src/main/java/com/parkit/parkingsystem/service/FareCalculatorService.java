package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        long inHour = ticket.getInTime().getTime();
        long outHour = ticket.getOutTime().getTime();

        //TODO: Some tests are failing here. Need to check if this logic is correct
        long durationInMinute = (outHour - inHour) / (60 * 1000);

        /**
         * Add free parking feature for the first 30 minutes
         */
        if (durationInMinute <= 30) {
            System.out.println("Add free parking feature for the first 30 minutes");
            System.out.println(durationInMinute + " minutes");
            durationInMinute = 0;
        }

        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
                ticket.setPrice( ( durationInMinute * Fare.CAR_RATE_PER_HOUR ) / 60 );
                break;
            }
            case BIKE: {
                ticket.setPrice( ( durationInMinute * Fare.BIKE_RATE_PER_HOUR ) / 60 );
                break;
            }
            default: throw new IllegalArgumentException("Unkown Parking Type");
        }
    }
}
