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
        byte[] pwSalt = hasher.generateSalt();
        byte[] dbSalt = hasher.generateSalt();
        byte[] hashedPw = hasher.getHashedPw(passwordIn, pwSalt);
        
        PreparedStatement pstmt = con.prepareStatement("INSERT INTO users values(?,?,?,?,?)");
        pstmt.setString(1, username);
        pstmt.setBytes(2, hashedPw);
        pstmt.setBytes(3, pwSalt);
        pstmt.setBytes(4, dbSalt);
        pstmt.setString(5, email);
        pstmt.execute();
        
        createSettings(username);
    }
    
    
    
    
    public void registerUser2(String username, char[] passwordIn, String email)
            throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException {
        
        PwHasher hasher = new PwHasher();
        //creating a new DB encryption/decryption key
        byte[] dbKey = hasher.generateDBKey();
        
        //generating a salt for the DB and the user entered password
        byte[] pwSalt = hasher.generateSalt();
        byte[] dbSalt = hasher.generateSalt();
        
        //creating a key to decrypt the original DB encrypt/decrypt key
        byte[] dbKeyEncryptionKey = hasher.getHashedPw(passwordIn, dbSalt);
        
        //creaing a hashed and salted password to store in DB
        byte[] hashedSaltedPw = hasher.getHashedPw(passwordIn, pwSalt);
        
        //
        
        PreparedStatement pstmt = con.prepareStatement("INSERT INTO users values(?,?,?,?,?)");
        pstmt.setString(1, username);
        pstmt.setBytes(2, hashedSaltedPw);
        pstmt.setBytes(3, pwSalt);
        pstmt.setBytes(4, dbSalt);
        pstmt.setString(5, email);
        pstmt.execute();
        
        createSettings(username);
    }
    
    
    
    
    
    public UserObject verifyLogin(String username, char[] pwAttempt) 
            throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException {
        String sqlStmt;
        sqlStmt = String.format("SELECT password, passSalt FROM users WHERE username = '%s';", username);
        PreparedStatement pstmt = con.prepareStatement(sqlStmt);
        ResultSet res = pstmt.executeQuery();
        byte[] password = res.getBytes(1);
        byte[] salt = res.getBytes(2);
        
        PwHasher hasher = new PwHasher();
        
        UserObject user = new UserObject();
        if (hasher.authenticate(pwAttempt, password, salt)) {
            sqlStmt = String.format("SELECT username, email FROM users WHERE username = '%s';", username);
            pstmt = con.prepareStatement(sqlStmt);
            res = pstmt.executeQuery();
            user.username = res.getString(1);
            user.email = res.getString(2);
        }
        else
            user = null;
        return user;
    }

    public void createAccount() {
        
    }
    
    public void getAcccountInfo() {
        
    }
    
    public SettingsObject getSettings(String username) throws SQLException {
        String sqlStmt;
        sqlStmt = String.format("SELECT length, lowercase, uppercase, digits, specials, extremities, username FROM settings WHERE username = '%s';", username);
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
            toReturn.username = result.getString("username");
        }
        return toReturn;
    }
    
    public void createSettings(String username) throws SQLException {
        String sqlStmt;
        sqlStmt = String.format("INSERT INTO settings (username) VALUES ('%s');", username);
        PreparedStatement pstmt = con.prepareStatement(sqlStmt);
        pstmt.execute();
    }
    
    public void updateSettings(String username, int length, boolean lowercase, 
                               boolean uppercase, boolean digits, boolean specials,
                               boolean extremities) throws SQLException {
        String sqlStmt;
        sqlStmt = String.format("UPDATE settings SET length = %s, lowercase = %x, uppercase = %x, digits = %x, specials = %x, extremities = %x WHERE username = '%s';",
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
