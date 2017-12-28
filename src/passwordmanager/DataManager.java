package passwordmanager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

/**
 *
 * @author Nathan
 */
public class DataManager {
    private static Connection con;
    
    private void getConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection("jdbc:sqlite:db/PasswordManager.db");
        } catch (Exception e) {
            System.out.println("DB Connection Status: Failed");
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("DB Connection Status: Successful");
    }
}
