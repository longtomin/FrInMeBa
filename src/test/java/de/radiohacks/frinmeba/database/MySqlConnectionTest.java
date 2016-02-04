/**
 * 
 */
package de.radiohacks.frinmeba.database;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.Test;

/**
 * @author hcmai
 *
 */
public class MySqlConnectionTest {
    
    @Test
    public void testMyConnection() {
        MySqlConnection o = new MySqlConnection();
        assertNotNull(o);
    }

    /**
     * Test method for {@link de.radiohacks.frinmeba.database.MySqlConnection#getMySqlConnection()}.
     */
    @Test
    public void testConnection() {
        MySqlConnection o = new MySqlConnection();
        assertNotNull(o);
        Connection con = o.getMySqlConnection();
        assertNotNull(con);
        
        try {
            assertThat(con.isValid(10), is(true));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        closeConnection(con);
    }

    private void closeConnection(Connection con) {
        if(con != null) {
            try {
                if(! con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
