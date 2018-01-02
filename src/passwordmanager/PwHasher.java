package passwordmanager;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.InvalidKeyException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.NoSuchPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author Nathan
 */
public class PwHasher {
    
    public boolean authenticate(char[] attemptedPw, byte[] hashedPw, byte[] salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] hashedAttemptedPw = getHashedPw(attemptedPw, salt);
        return Arrays.equals(hashedPw, hashedAttemptedPw);
    }
    
    public byte[] getHashedPw(char[] password, byte[] salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        String algorithm = "PBKDF2WithHmacSHA1";
        int derivedKeyLength = 128;
        int iterations = 15000;
        
        KeySpec spec = new PBEKeySpec(password, salt, iterations, derivedKeyLength);
        
        SecretKeyFactory f = SecretKeyFactory.getInstance(algorithm);
        
        return f.generateSecret(spec).getEncoded();
    }
    
    public byte[] getHashedPw(String password, byte[] salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        return getHashedPw(password.toCharArray(), salt);
    }
    
    public byte[] generateDBKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        SecretKey secretKey = keyGen.generateKey();
        byte[] secretKeyBytes = secretKey.getEncoded();
        return secretKeyBytes;
    }
    
    public byte[] generateSalt() throws NoSuchAlgorithmException {        
        SecureRandom rand = SecureRandom.getInstance("SHA1PRNG");
        
        byte[] salt = new byte[8];
        rand.nextBytes(salt);
        
        return salt;
    }
    
    public byte[] encrypt(byte[] encodedKey, byte[] data) 
            throws NoSuchPaddingException, NoSuchAlgorithmException, 
                   InvalidKeyException, IllegalBlockSizeException,
                   BadPaddingException {
        
        SecretKey key = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
        
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedData = Base64.getEncoder().encode(cipher.doFinal(data));
        return encryptedData;
    }
    
    public byte[] decrypt(byte[] encodedKey, byte[] encodedData) 
            throws NoSuchPaddingException, NoSuchAlgorithmException, 
                   InvalidKeyException, IllegalBlockSizeException,
                   BadPaddingException {
        
        SecretKey key = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
        
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        
        byte[] decodedData = Base64.getDecoder().decode(encodedData);
        byte[] data = cipher.doFinal(decodedData);
        
        return data;
    }
}
