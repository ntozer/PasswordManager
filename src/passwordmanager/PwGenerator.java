package passwordmanager;

import java.util.Random;
/**
 * 
 * @author Nathan
 */
public class PwGenerator {
    public String generatePassword(int lengthIn, boolean lettersIn, boolean digitsIn,
                                   boolean specialsIn) {
        char[] letters;
        if (lettersIn) {
            letters = new char[52];
            for (int i = 0; i < letters.length; i++) {
                if (i < 26) {
                    letters[i] = (char)(i + 65);
                }
                else {
                    letters[i] = (char)(i + 71);
                }
            }
        }
        else {
            letters = new char[0];
        }
        
        char[] digits = new char[10];
        if (digitsIn) {
            digits = new char[10];
            for (int i = 0; i < digits.length; i++) {
                digits[i] = (char)(i + 48);
            }
        }
        else {
            digits = new char[0];
        }
        
        char[] specials;
        if (specialsIn) {
            String specialChars = " !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";
            specials = new char[specialChars.length()];
            for (int i = 0; i < specials.length; i++) {
                specials[i] = specialChars.charAt(i);
            }
        }
        else {
            specials = new char[0];
        }  
        
        char[] validChars = concatenateCharArrs(letters, digits, specials);
        
        Random randomNum = new Random();
        
        char[] genCharArr = new char[lengthIn];
        for (int i = 0; i < genCharArr.length; i++) {
            genCharArr[i] = validChars[randomNum.nextInt(validChars.length)];
        }
        String generatedPass = new String(genCharArr);
        return generatedPass;
    }
    private char[] concatenateCharArrs(char[] a, char[] b, char[] c) {
        char[] concatenated = new char[a.length + b.length + c.length];
        for (int i = 0; i < concatenated.length; i++) {
            if (i < a.length) {
                concatenated[i] = a[i];
            }
            else if (i < a.length + b.length) {
                concatenated[i] = b[i - a.length];
            }
            else {
                concatenated[i] = c[i - a.length - b.length];
            }
        }
        return concatenated;
    }
}
