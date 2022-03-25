package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.*;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TicketDAOTest {

    //Class to be tested
    @Mock
    private TicketDAO ticketDAO;

    private static LogCaptor logCaptor;

    @BeforeAll
    public static void setupLogCaptor() {
        logCaptor = LogCaptor.forClass(TicketDAOTest.class);
    }

    @Mock
    private static DataBaseConfig mockDataBaseConfig;
    @Mock
    private static Connection mockConnection;
    @Mock
    private static PreparedStatement mockPreparedStatement;


    @BeforeAll
        public static void setUpClass() {
    }

    @AfterEach
    public void clearLogs() {
        logCaptor.clearLogs();
    }

    @AfterAll
    public static void tearDown() {
        logCaptor.close();
    }

    @BeforeEach
        public void setUp() throws SQLException, ClassNotFoundException {
            when(mockDataBaseConfig.getConnection()).thenReturn(mockConnection);
    }

    @Test
    public void ticketDAOSaveTicket() throws SQLException {
        //Given
        when(mockConnection.prepareStatement(DBConstants.SAVE_TICKET)).thenReturn(mockPreparedStatement);
        Ticket ticket = new Ticket();
        ticket.setId(1);
        ticket.setParkingSpot(new ParkingSpot(1, ParkingType.CAR, false));
        ticket.setVehicleRegNumber("vehiclSaveTicket");
        ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
        doCallRealMethod().when(ticketDAO).saveTicket(ticket);
        ticketDAO.dataBaseConfig = mockDataBaseConfig;

        //When
        ticketDAO.saveTicket(ticket);

        //then
        assertEquals(1, ticket.getId());
        verify(mockPreparedStatement).execute();
        verify(mockDataBaseConfig).closeConnection(any());
        verify(mockDataBaseConfig).closePreparedStatement(any());
    }

    @Test
    public void ticketDAOUpdateTicket() throws SQLException {
        //Given
        when(mockConnection.prepareStatement(DBConstants.UPDATE_TICKET)).thenReturn(mockPreparedStatement);
        Ticket ticket = new Ticket();
        ticket.setId(1);
        ticket.setPrice(0);
        ticket.setVehicleRegNumber("vehiclUpdateTicket");
        ticket.setInTime(new Date(System.currentTimeMillis() - (30 * 60 * 1000)));
        ticket.setOutTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
        doCallRealMethod().when(ticketDAO).updateTicket(ticket);
        ticketDAO.dataBaseConfig = mockDataBaseConfig;

        //When
        ticketDAO.updateTicket(ticket);

        //then
        verify(mockPreparedStatement).execute();
        verify(mockDataBaseConfig).closeConnection(any());
        verify(mockDataBaseConfig).closePreparedStatement(any());
    }

    @Test
    public void ticketDAOGetTicket() throws SQLException {
        //Given
        when(mockConnection.prepareStatement(DBConstants.GET_TICKET)).thenReturn(mockPreparedStatement);
        Ticket ticket = new Ticket();
        ticket.setId(2);
        ticket.setPrice(0);
        ticket.setVehicleRegNumber("vehiclGetTicket");
        ticket.setOutTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
        doCallRealMethod().when(ticketDAO).getTicket("vehiclGetTicket");
        ticketDAO.dataBaseConfig = mockDataBaseConfig;

        //When
        ticketDAO.getTicket("vehiclGetTicket");

        //then
       // verify(mockPreparedStatement).execute();
        verify(mockDataBaseConfig).closeConnection(any());
        verify(mockDataBaseConfig).closePreparedStatement(any());
    }

}


