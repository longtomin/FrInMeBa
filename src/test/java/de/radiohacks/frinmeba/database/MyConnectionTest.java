package de.radiohacks.frinmeba.database;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.SQLException;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

public class MyConnectionTest {

    @Test
    public void testMyConnection() {
        MyConnection o = new MyConnection();
        assertNotNull("can not create object MyConnect", o);
    }

    @Test
    public void testConnection() {
        MyConnection o = new MyConnection();
        assertNotNull("can not create object MyConnect", o);
        Connection con = o.getConnection();
        assertNotNull("can not create object Connection", con);
        
        try {
            assertThat("connection is not valid", con.isValid(10), is(true));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        o.closeConnection();
    }

}
