package de.radiohacks.frinmeba.database;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

public class MyConnectionTest {

    @Test
    public void testMyConnection() {
        MyConnection o = new MyConnection();
        assertNotNull(o);
    }

    @Test
    public void testConnection() {
        MyConnection o = new MyConnection();
        assertNotNull(o);
        Connection con = o.getConnection();
        assertNotNull(con);
        
        try {
            assertThat(con.isValid(10), is(true));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        o.closeConnection();
    }

}
