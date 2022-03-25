package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;

public class ParkingService {

    public static final String ERROR_PARSING = "Error parsing user input for type of vehicle";
    public static final String ERROR_FETCHING = "Error fetching next available parking slot";
    public static final String EXPECTED_INFO_MESSAGE = "Error vehicle already parked";
    public static final String USER_RECCURENT = "Welcome back! As a recurring user of our parking lot, you'll benefit from a 5% discount";
    public static final String VEHICLE_REGISTRATION ="Please type the vehicle registration number and press enter key";
    public static final String ERROR_UPDATE_TICKET = "Unable to update ticket information. Error occurred";
    private static final Logger logger = LogManager.getLogger(ParkingService.class);

    private FareCalculatorService fareCalculatorService;

    private InputReaderUtil inputReaderUtil;
    private ParkingSpotDAO parkingSpotDAO;
    private TicketDAO ticketDAO;

    public ParkingService(InputReaderUtil inputReaderUtil, ParkingSpotDAO parkingSpotDAO, TicketDAO ticketDAO){
        this.inputReaderUtil = inputReaderUtil;
        this.parkingSpotDAO = parkingSpotDAO;
        this.ticketDAO = ticketDAO;
        this.fareCalculatorService = new FareCalculatorService(ticketDAO);
    }

    public void processIncomingVehicle() {
        ParkingSpot parkingSpot = getNextParkingNumberIfAvailable();
        if(parkingSpot !=null && parkingSpot.getId() > 0){
            String vehicleRegNumber = getVehichleRegNumber();

            if (ticketDAO.getTicket(vehicleRegNumber) != null) {
                logger.info(EXPECTED_INFO_MESSAGE);
                return;
            }

            parkingSpot.setAvailable(false);
            parkingSpotDAO.updateParking(parkingSpot);//allot this parking space and mark it's availability as false

            Date inTime = new Date();
            Ticket ticket = new Ticket();
            //ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
            //ticket.setId(ticketID);
            ticket.setParkingSpot(parkingSpot);
            ticket.setVehicleRegNumber(vehicleRegNumber);
            ticket.setPrice(0);
            ticket.setInTime(inTime);
            ticket.setOutTime(null);
            ticketDAO.saveTicket(ticket);

            if(ticketDAO.countTicket(vehicleRegNumber)>= 2){
                logger.info(USER_RECCURENT);
            }
            System.out.println("Generated Ticket and saved in DB");
            System.out.println("Please park your vehicle in spot number:"+parkingSpot.getId());
            System.out.println("Recorded in-time for vehicle number:"+vehicleRegNumber+" is:"+inTime);

        }
    }

    private String getVehichleRegNumber() {
        logger.info(VEHICLE_REGISTRATION);
        return inputReaderUtil.readVehicleRegistrationNumber();
    }

    public ParkingSpot getNextParkingNumberIfAvailable(){
        int parkingNumber;
        ParkingSpot parkingSpot = null;
        try{
            ParkingType parkingType = getVehichleType();
            parkingNumber = parkingSpotDAO.getNextAvailableSlot(parkingType);
            if(parkingNumber > 0){
                parkingSpot = new ParkingSpot(parkingNumber,parkingType, true);
            }else{
                throw new Exception("Error fetching parking number from DB. Parking slots might be full");
            }
        }catch(IllegalArgumentException ie){
            logger.error(ERROR_PARSING, ie);
        }catch(Exception e){
            logger.error(ERROR_FETCHING, e);
        }
        return parkingSpot;
    }

    private ParkingType getVehichleType(){
        System.out.println("Please select vehicle type from menu");
        System.out.println("1 CAR");
        System.out.println("2 BIKE");
        int input = inputReaderUtil.readSelection();
        switch(input){
            case 1: {
                return ParkingType.CAR;
            }
            case 2: {
                return ParkingType.BIKE;
            }
            default: {
                System.out.println("Incorrect input provided");
                throw new IllegalArgumentException("Entered input is invalid");
            }
        }
    }

    public void processExitingVehicle() {
        String vehicleRegNumber = getVehichleRegNumber();
        Ticket ticket = ticketDAO.getTicket(vehicleRegNumber);
        if(ticket == null) {
            System.out.println("Error any vehicle found with number : " + vehicleRegNumber);
            return;
        }

        Date outTime = new Date();
        ticket.setOutTime(outTime);
        fareCalculatorService.calculateFare(ticket);
        if(ticketDAO.updateTicket(ticket)) {
            ParkingSpot parkingSpot = ticket.getParkingSpot();
            parkingSpot.setAvailable(true);
            parkingSpotDAO.updateParking(parkingSpot);
            System.out.println("Please pay the parking fare:" + ticket.getPrice());
            System.out.println("Recorded out-time for vehicle number:" + ticket.getVehicleRegNumber() + " is:" + outTime);
        }else{
            logger.error(ERROR_UPDATE_TICKET);
        }
    }

}
