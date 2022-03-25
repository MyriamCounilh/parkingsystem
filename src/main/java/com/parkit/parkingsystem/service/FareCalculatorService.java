package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.Ticket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.spi.AbstractLogger;
import org.apache.logging.log4j.Logger;


public class FareCalculatorService {

    public static final String EXPECTED_INFO_MESSAGE = "Add free parking feature for the first 30 minutes";
    private static final Logger LOGGER = LogManager.getLogger(FareCalculatorService.class);

    private TicketDAO ticketDAO;

    public static final double REDUC_5_PCT = 0.95;

    public FareCalculatorService(TicketDAO ticketDAO) {
        this.ticketDAO = ticketDAO;
    }

    public long durationInMinute(Ticket ticket) {
        long inHour = ticket.getInTime().getTime();
        long outHour = ticket.getOutTime().getTime();

        long durationInMinute = (outHour - inHour) / (60 * 1000);

        /**
         * Add free parking feature for the first 30 minutes
         */
        if (durationInMinute <= 30) {
            LOGGER.info(EXPECTED_INFO_MESSAGE);
             durationInMinute = 0;
        }
        return durationInMinute;
    }

    public void reducUser(Ticket ticket) {
        /**
         * Add reduc User 5%
         */
        if (ticketDAO.countTicket(ticket.getVehicleRegNumber()) >= 2) {
            ticket.setPrice(ticket.getPrice() * REDUC_5_PCT);
            System.out.println("5 % the reduc");
        }
    }

    public void calculateFare(Ticket ticket){

        long duration = durationInMinute(ticket);

        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

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
        reducUser(ticket);
    }
}
