package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static final String VEHICLE_REG_NUMBER = "VehicleIT";

    private static final DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

    @Mock
    private static InputReaderUtil inputReaderUtil;

    @BeforeAll
    private static void setUp() {
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    private void setUpPerTest() {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(VEHICLE_REG_NUMBER);
        dataBasePrepareService.clearDataBaseEntries();
    }

    @AfterAll
    private static void tearDown(){

    }

    @Test
    public void testParkingACar(){
        //Given
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        //When
        int current = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);
        parkingService.processIncomingVehicle();
        Ticket ticket = ticketDAO.getTicket(VEHICLE_REG_NUMBER);

        //Then
        assertNotNull(ticket);
        assertEquals(current + 1, parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR));
    }

    @Test
    public void testParkingLotExit(){
        testParkingACar();

        Ticket ticket = ticketDAO.getTicket(VEHICLE_REG_NUMBER);
        ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000) - 30 * 1000));
        ticketDAO.updateTicket(ticket);

        //When
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processExitingVehicle();
        ticket = ticketDAO.getTicketById(ticket.getId());

        //Then
        if (ticketDAO.countTicket(VEHICLE_REG_NUMBER) >= 2) {
            assertEquals(Fare.CAR_RATE_PER_HOUR * FareCalculatorService.REDUC_5_PCT, ticket.getPrice());
        } else {
            assertEquals( Fare.CAR_RATE_PER_HOUR, ticket.getPrice() );
        }
    }

}
