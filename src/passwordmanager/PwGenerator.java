package passwordmanager;

import java.util.Random;
/**
 * 
 * @author Nathan
 */
public class PwGenerator {
    
    private char[] initLetters(boolean include) {
        char[] letters;
        if (include) {
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
        return letters;
    }
    
    private char[] initDigits(boolean include) {
        char[] digits;
        if (include) {
            digits = new char[10];
            for (int i = 0; i < digits.length; i++) {
                digits[i] = (char)(i + 48);
            }
        }
        else {
            digits = new char[0];
        }
        return digits;
    }
    
    private char[] initSpecials(boolean include) {
        char[] specials;
        if (include) {
            String specialChars = " !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";
            specials = new char[specialChars.length()];
            for (int i = 0; i < specials.length; i++) {
                specials[i] = specialChars.charAt(i);
            }
        }
        else {
            specials = new char[0];
        }
        return specials;
    }
    
    private char[] concatenateCharArrs(char[]...args) {
        int totalLength = 0;
        for (char[] arg : args) {
            totalLength += arg.length;
        }
        char[] concatenated = new char[totalLength];
        
        int currentIndex = 0;
        for (char[] arg : args) {
            for (int i = 0; i < arg.length; i++) {
                concatenated[currentIndex] = arg[i];
                currentIndex += 1;
            }
        }
        return concatenated;
    }
    
    public String generatePassword(int lengthIn, boolean containLetters, boolean containDigits,
                                   boolean containSpecials) {
        char[] letters = initLetters(containLetters);
        char[] digits = initDigits(containDigits);
        char[] specials = initSpecials(containSpecials);
        
        char[] validChars = concatenateCharArrs(letters, digits, specials);
        
        Random randomNum = new Random();
        
        char[] genCharArr = new char[lengthIn];
        for (int i = 0; i < genCharArr.length; i++) {
            genCharArr[i] = validChars[randomNum.nextInt(validChars.length)];
        }
        String generatedPass = new String(genCharArr);
        return generatedPass;
    }
}
