package passwordmanager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 *
 * @author Nathan
 */
public class DataManager {
    private static Connection con;
    
    public DataManager() {
        getConnection();
    }
    
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
    
    public void registerUser(String username, String passwordIn, String email)
            throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException {
        
        PwHasher hasher = new PwHasher();
        byte[] salt = hasher.generateSalt();
        byte[] password = hasher.getEncryptedPw(passwordIn, salt);
        PreparedStatement pstmt = con.prepareStatement("INSERT INTO users values(?,?,?,?)");
        pstmt.setString(1, username);
        pstmt.setBytes(2, password);
        pstmt.setBytes(3, salt);
        pstmt.setString(4, email);
        pstmt.execute();      
    }
    
    public boolean verifyLogin(String username, String pwAttempt) 
            throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException {
        
        PreparedStatement pstmt = con.prepareStatement("SELECT password, salt FROM users WHERE username = '" + username + "'");
        ResultSet res = pstmt.executeQuery();
        byte[] password = res.getBytes(1);
        byte[] salt = res.getBytes(2);
        
        PwHasher hasher = new PwHasher();
        
        return hasher.authenticate(pwAttempt, password, salt);
    }
}
