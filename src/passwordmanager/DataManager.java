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
    
    public void registerUser(String username, char[] passwordIn, String email)
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
        
        createSettings(username);
    }
    
    public boolean verifyLogin(String username, String pwAttempt) 
            throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException {
        String sqlStmt;
        sqlStmt = String.format("SELECT password, salt FROM users WHERE username = '%s';", username);
        PreparedStatement pstmt = con.prepareStatement(sqlStmt);
        ResultSet res = pstmt.executeQuery();
        byte[] password = res.getBytes(1);
        byte[] salt = res.getBytes(2);
        
        PwHasher hasher = new PwHasher();
        
        return hasher.authenticate(pwAttempt, password, salt);
    }

    public void createAccount() {
        
    }
    
    public void getAcccountInfo() {
        
    }
    
    public SettingsObject getSettings(String username) throws SQLException {
        String sqlStmt;
        sqlStmt = String.format("SELECT length, lowercase, uppercase, digits, specials, extremities, user FROM settings WHERE user = '%s';", username);
        PreparedStatement pstmt = con.prepareStatement(sqlStmt);
        ResultSet result = pstmt.executeQuery();
        
        SettingsObject toReturn = new SettingsObject();
        if (result != null) {
            toReturn.length = result.getInt("length");
            toReturn.lcase = result.getInt("lowercase");
            toReturn.ucase = result.getInt("uppercase");
            toReturn.digits = result.getInt("digits");
            toReturn.specials = result.getInt("specials");
            toReturn.extremities = result.getInt("extremities");
            toReturn.username = result.getString("user");
        }
        return toReturn;
    }
    
    public void createSettings(String username) throws SQLException {
        String sqlStmt;
        sqlStmt = String.format("INSERT INTO settings (user) VALUES ('%s');", username);
        PreparedStatement pstmt = con.prepareStatement(sqlStmt);
        pstmt.execute();
    }
    
    public void updateSettings(String username, int length, boolean lowercase, 
                               boolean uppercase, boolean digits, boolean specials,
                               boolean extremities) throws SQLException {
        String sqlStmt;
        sqlStmt = String.format("UPDATE settings SET length = %s, lowercase = %x, uppercase = %x, digits = %x, specials = %x, extremities = %x WHERE user = '%s';",
                                length, bool2Int(lowercase), bool2Int(uppercase), bool2Int(digits), bool2Int(specials), bool2Int(extremities), username);
        PreparedStatement pstmt = con.prepareStatement(sqlStmt);
        pstmt.execute();
    }
    
    private int bool2Int(boolean toConvert) {
        if (toConvert)
            return 1;
        else
            return 0;
    }
}
