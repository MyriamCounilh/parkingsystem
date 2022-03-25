package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

    //Class to be tested
    private static ParkingService parkingService;

    private static LogCaptor logCaptor;

    @BeforeAll
    public static void setupLogCaptor() {
        logCaptor = LogCaptor.forClass(ParkingService.class);
    }

    @Mock
    private static InputReaderUtil inputReaderUtil;
    @Mock
    private static ParkingSpotDAO parkingSpotDAO;
    @Mock
    private static TicketDAO ticketDAO;

    private Ticket ticket;

    @AfterEach
    public void clearLogs() {
        logCaptor.clearLogs();
    }

    @AfterAll
    public static void tearDown() {
        logCaptor.close();
    }

    @BeforeEach
    private void setUpPerTest() {
        try {
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
            ticket = new Ticket();
            ticket.setInTime(new Date(System.currentTimeMillis() - (60*60*1000)));
            ticket.setParkingSpot(parkingSpot);
            ticket.setVehicleRegNumber("ABCDEF");
            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        } catch (Exception e) {
            e.printStackTrace();
            throw  new RuntimeException("Failed to set up test mock objects");
        }
    }

    @Test
    public void processExitingVehicleTest(){
        //Given
        when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
        when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
        when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

        //When
        parkingService.processExitingVehicle();

        //Then
        verify(parkingSpotDAO, times(1)).updateParking(any(ParkingSpot.class));
    }

    @Test
    public void processCantExitingVehicleTest(){
        //Given
        when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
        when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(false);

        //When
        parkingService.processExitingVehicle();

        //Then
        verify(parkingSpotDAO, times(0)).updateParking(any(ParkingSpot.class));
        assertThat(logCaptor.getErrorLogs()).containsExactly(ParkingService.ERROR_UPDATE_TICKET);
        assertThat(logCaptor.getLogs()).hasSize(2);
    }

    @Test
    public void processExitingVehicleNoInParkingTest(){
        //Given
        when(ticketDAO.getTicket(anyString())).thenReturn(null);

        //When
        parkingService.processExitingVehicle();

        //Then
        verify(parkingSpotDAO, times(0)).updateParking(any(ParkingSpot.class));
    }


    @Test
    public void processIncomingCarTest() {
        //Given
        when(inputReaderUtil.readSelection()).thenReturn(2);
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);
        when(ticketDAO.getTicket(anyString())).thenReturn(null);
        when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
        when(ticketDAO.saveTicket(any(Ticket.class))).thenReturn(true);

        //When
        parkingService.processIncomingVehicle();

        //Then
        verify(ticketDAO,times(1)).saveTicket(any(Ticket.class));
        verify(parkingSpotDAO,times(1)).updateParking(any(ParkingSpot.class));
    }

    @Test
    public void processIncomingCarWelcomeBackTest() {
        //Given
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);
        when(ticketDAO.getTicket(anyString())).thenReturn(null);
        when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
        when(ticketDAO.saveTicket(any(Ticket.class))).thenReturn(true);

        when(ticketDAO.countTicket(ticket.getVehicleRegNumber())).thenReturn(2);

        //When
        parkingService.processIncomingVehicle();

        //Then
        assertThat(logCaptor.getInfoLogs()).containsExactly(ParkingService.VEHICLE_REGISTRATION, ParkingService.USER_RECCURENT);
        assertThat(logCaptor.getLogs()).hasSize(2);
    }

    @Test
    public void processIncomingBikeTest() {
        //Given
        when(inputReaderUtil.readSelection()).thenReturn(2);
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(2);
        when(ticketDAO.getTicket(anyString())).thenReturn(null);
        when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
        when(ticketDAO.saveTicket(any(Ticket.class))).thenReturn(true);

        //When
        parkingService.processIncomingVehicle();

        //When
        verify(ticketDAO,times(1)).saveTicket(any(Ticket.class));
        verify(parkingSpotDAO,times(1)).updateParking(any(ParkingSpot.class));
    }

    @Test
    public void processNotIncomingTest() {
        //Given
        when(inputReaderUtil.readSelection()).thenReturn(2);
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(4);
        when(ticketDAO.getTicket(anyString())).thenReturn(null);
        when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(false);
        when(ticketDAO.saveTicket(any(Ticket.class))).thenReturn(false);

        //When
        parkingService.processIncomingVehicle();

        //Then
        verify(ticketDAO,times(1)).saveTicket(any(Ticket.class));
        verify(parkingSpotDAO,times(1)).updateParking(any(ParkingSpot.class));
    }

    @Test
    public void processNotIncomingCarTest() {
        //Given
        when(inputReaderUtil.readSelection()).thenReturn(2);
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);
        when(ticketDAO.getTicket(anyString())).thenReturn(null);
        when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(false);
        when(ticketDAO.saveTicket(any(Ticket.class))).thenReturn(false);

        //When
        parkingService.processIncomingVehicle();

        //Then
        verify(ticketDAO,times(1)).saveTicket(any(Ticket.class));
        verify(parkingSpotDAO,times(1)).updateParking(any(ParkingSpot.class));
    }

    @Test
    public void processNotIncomingBikeTest() {
        //Given
        when(inputReaderUtil.readSelection()).thenReturn(2);
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(2);
        when(ticketDAO.getTicket(anyString())).thenReturn(null);
        when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(false);
        when(ticketDAO.saveTicket(any(Ticket.class))).thenReturn(false);

        //When
        parkingService.processIncomingVehicle();

        //Then
        verify(ticketDAO,times(1)).saveTicket(any(Ticket.class));
        verify(parkingSpotDAO,times(1)).updateParking(any(ParkingSpot.class));
    }

    @Test
    public void processIncomingVehicleGetTicket() {
        //Given
        when(inputReaderUtil.readSelection()).thenReturn(2);
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(2);
        when(ticketDAO.getTicket(anyString())).thenReturn(ticket);

        //When
        parkingService.processIncomingVehicle();

        //Then
        assertThat(logCaptor.getInfoLogs()).containsExactly(ParkingService.VEHICLE_REGISTRATION, ParkingService.EXPECTED_INFO_MESSAGE);
        assertThat(logCaptor.getLogs()).hasSize(2);
    }

    @Test
    public void processIncomingVehicleWithoutParking()  {
        //Given
        ParkingService parkingServiceMock = mock(ParkingService.class);
        doCallRealMethod().when(parkingServiceMock).processIncomingVehicle();
        ParkingSpot parkingSpot = new ParkingSpot(0, ParkingType.CAR, false);
        when(parkingServiceMock.getNextParkingNumberIfAvailable()).thenReturn(parkingSpot);

        //When
        inputReaderUtil.readVehicleRegistrationNumber();
        parkingServiceMock.processIncomingVehicle();

        //Then
        verify(ticketDAO,times(0)).getTicket(any(String.class));
    }

    @Test
    public void processIncomingVehicleWithoutPlaceInParking() {
        //Given
        when(inputReaderUtil.readSelection()).thenReturn(2);
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(0);

        //When
        inputReaderUtil.readVehicleRegistrationNumber();
        parkingService.processIncomingVehicle();

        //Then
        assertThat(logCaptor.getErrorLogs()).containsExactly(ParkingService.ERROR_FETCHING);
        assertThat(logCaptor.getLogs()).hasSize(1);
    }

    @Test
    public void processIncomingVehicleGetVehichleType() {
        //Given
        when(inputReaderUtil.readSelection()).thenReturn(4);

        //When
        inputReaderUtil.readVehicleRegistrationNumber();
        parkingService.processIncomingVehicle();

        //Then
        assertThat(logCaptor.getErrorLogs()).containsExactly(ParkingService.ERROR_PARSING);
        assertThat(logCaptor.getLogs()).hasSize(1);
    }

}
