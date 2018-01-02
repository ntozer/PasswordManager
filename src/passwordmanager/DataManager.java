package passwordmanager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 *
 * @author Nathan
 */
public class DataManager {
    private static Connection con;
    private PwHasher hasher;
    
    public DataManager() {
        getConnection();
        hasher = new PwHasher();
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
            throws SQLException, NoSuchAlgorithmException, 
                   InvalidKeySpecException, NoSuchPaddingException, 
                   NoSuchAlgorithmException, InvalidKeyException, 
                   IllegalBlockSizeException, BadPaddingException {
        
        //creating a new DB encryption/decryption key
        byte[] dbDataKey = hasher.generateDBKey();
        
        //generating a salt for the DB and the user entered password
        byte[] pwSalt = hasher.generateSalt();
        byte[] dbSalt = hasher.generateSalt();
        
        //creating a key to decrypt the original DB encrypt/decrypt key
        byte[] dbKeyEncryptionKey = hasher.getHashedPw(passwordIn, dbSalt);
        
        //creating a hashed and salted and then encrypted password to store in DB
        byte[] hashedSaltedPw = hasher.getHashedPw(passwordIn, pwSalt);
        byte[] encryptedHashedSaltedPw = hasher.encrypt(dbDataKey, hashedSaltedPw);
        
        //encrypting the DB Data key to store in the database
        byte[] encryptedDBDataKey = hasher.encrypt(dbKeyEncryptionKey, dbDataKey);
        
        PreparedStatement pstmt = con.prepareStatement("INSERT INTO users values(?,?,?,?,?,?)");
        pstmt.setString(1, username);
        pstmt.setBytes(2, encryptedHashedSaltedPw);
        pstmt.setBytes(3, encryptedDBDataKey);
        pstmt.setBytes(4, pwSalt);
        pstmt.setBytes(5, dbSalt);
        pstmt.setString(6, email);
        pstmt.execute();
        
        createSettings(username);
    }
    
    public UserObject verifyLogin(String username, char[] pwAttempt) 
            throws SQLException, NoSuchAlgorithmException, 
                   InvalidKeySpecException, NoSuchPaddingException, 
                   NoSuchAlgorithmException, InvalidKeyException, 
                   IllegalBlockSizeException, BadPaddingException {
        String sqlStmt;
        sqlStmt = String.format("SELECT password, dbKey, passSalt, dbKeySalt FROM users WHERE username = '%s';", username);
        PreparedStatement pstmt = con.prepareStatement(sqlStmt);
        ResultSet res = pstmt.executeQuery();
        byte[] encryptedPassword = res.getBytes("password");
        byte[] encryptedDBKey = res.getBytes("dbKey");
        byte[] pwSalt = res.getBytes("passSalt");
        byte[] dbSalt = res.getBytes("dbKeySalt");
                
        byte[] dbKeyEncryptionKey = hasher.getHashedPw(pwAttempt, dbSalt);
        byte[] dbKey = hasher.decrypt(dbKeyEncryptionKey, encryptedDBKey);
        byte[] password = hasher.decrypt(dbKey, encryptedPassword);
        
        UserObject user = new UserObject();
        if (hasher.authenticate(pwAttempt, password, pwSalt)) {
            sqlStmt = String.format("SELECT username, email FROM users WHERE username = '%s';", username);
            pstmt = con.prepareStatement(sqlStmt);
            res = pstmt.executeQuery();
            user.username = res.getString(1);
            user.email = res.getString(2);
            user.dbKey = dbKey;
        }
        else
            user = null;
        return user;
    }

    public void createAccount(byte[] dbKey, String username, String title, 
                              String accUsername, char[] accPassword, String website) 
            throws SQLException, NoSuchAlgorithmException, 
                   InvalidKeySpecException, NoSuchPaddingException, 
                   NoSuchAlgorithmException, InvalidKeyException, 
                   IllegalBlockSizeException, BadPaddingException {
        byte[] encryptedUsername = hasher.encrypt(dbKey, accUsername.getBytes());
        byte[] encryptedPassword = hasher.encrypt(dbKey, (new String(accPassword)).getBytes());
        
        PreparedStatement pstmt = con.prepareStatement("INSERT INTO accounts values(?,?,?,?,?)");
        pstmt.setString(1, title);
        pstmt.setBytes(2, encryptedUsername);
        pstmt.setBytes(3, encryptedPassword);
        pstmt.setString(4, website);
        pstmt.setString(5, username);
        pstmt.execute();
    }
    
    public AccountObject getAcccountInfo(byte[] dbKey, String username, String title)
            throws SQLException, NoSuchAlgorithmException, 
                   InvalidKeySpecException, NoSuchPaddingException, 
                   NoSuchAlgorithmException, InvalidKeyException, 
                   IllegalBlockSizeException, BadPaddingException {
        String sqlStmt;
        sqlStmt = String.format("SELECT a_username, a_password, website FROM accounts WHERE username = '%s' AND title = '%s';", username, title);
        PreparedStatement pstmt = con.prepareStatement(sqlStmt);
        ResultSet result = pstmt.executeQuery();
        
        byte[] encodedUsername = result.getBytes("a_username");
        byte[] encodedPassword = result.getBytes("a_password");
        String website = result.getString("website");
        
        String accUsername = new String(hasher.decrypt(dbKey, encodedUsername));
        String accPassword = new String(hasher.decrypt(dbKey, encodedPassword));
        
        AccountObject account = new AccountObject();
        account.accPassword = accPassword;
        account.accUsername = accUsername;
        account.title = title;
        account.website = website;
        
        return account;
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
