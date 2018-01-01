package passwordmanager;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 *
 * @author Nathan
 */
public class PwHasher {
    
    public boolean authenticate(String attemptedPw, byte[] encryptedPw, byte[] salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] encryptedAttemptedPw = getEncryptedPw(attemptedPw, salt);
        return Arrays.equals(encryptedPw, encryptedAttemptedPw);
    }
    
    public byte[] getEncryptedPw(char[] password, byte[] salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        String algorithm = "PBKDF2WithHmacSHA1";
        int derivedKeyLength = 128;
        int iterations = 15000;
        
        KeySpec spec = new PBEKeySpec(password, salt, iterations, derivedKeyLength);
        
        SecretKeyFactory f = SecretKeyFactory.getInstance(algorithm);
        
        return f.generateSecret(spec).getEncoded();
    }
    
    public byte[] getEncryptedPw(String password, byte[] salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        return getEncryptedPw(password.toCharArray(), salt);
    }
    
    public byte[] generateSalt() throws NoSuchAlgorithmException {        
        SecureRandom rand = SecureRandom.getInstance("SHA1PRNG");
        
        byte[] salt = new byte[8];
        rand.nextBytes(salt);
        
        return salt;
    }
    
    public byte[] getHashedPw(char[] password, byte[] dbKeySalt, byte[] pwSalt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        
        byte[] firstHash = getEncryptedPw(password, dbKeySalt);
        byte[] secondHash = getEncryptedPw((new String(firstHash)).toCharArray(), pwSalt);
        return secondHash;
    }
}
