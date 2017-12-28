package passwordmanager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author Nathan
 */
public class SQLiteTest{
    
    private static Connection con;
    private static boolean hasData = false;
    
    public ResultSet displayUsers() throws SQLException, ClassNotFoundException {
        if(con == null) {
            getConnection();
        }
        
        Statement stmt = con.createStatement();
        ResultSet res = stmt.executeQuery("SELECT fname, lname FROM user");
        return res;
    }
    
    private void getConnection() throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        con = DriverManager.getConnection("jdbc:sqlite:db/SQLiteTest1.db");
        initialise();
    }
    
    private void initialise() throws SQLException{
        if(!hasData) {
            hasData = true;
            
            Statement state = con.createStatement();
            ResultSet res = state.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='user'");
            if(!res.next()) {
                System.out.println("Building the User table with prepopulated values");
                //building table
                Statement stmt2 = con.createStatement();
                stmt2.execute("CREATE TABLE user(id integer," + "fName varchar(60)," + "lName varchar(60)," + "primary key(id))");
                //inserting data
                PreparedStatement pstmt = con.prepareStatement("INSERT INTO user values(?,?,?);");
                pstmt.setString(2, "John");
                pstmt.setString(3, "McNeil");
                pstmt.execute();
                
                PreparedStatement pstmt2 = con.prepareStatement("INSERT INTO user values(?,?,?);");
                pstmt2.setString(2, "Paul");
                pstmt2.setString(3, "Smith");
                pstmt2.execute();
            }
        }
    }
    
    public void addUser(String firstName, String lastName) throws SQLException, ClassNotFoundException {
        if(con == null) {
            getConnection();
        }
        
        PreparedStatement pstmt = con.prepareStatement("INSERT INTO user values(?,?,?);");
        pstmt.setString(2, firstName);
        pstmt.setString(3, lastName);
        pstmt.execute();
    }
    
}
