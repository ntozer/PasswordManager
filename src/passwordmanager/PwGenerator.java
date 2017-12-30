package passwordmanager;

import java.util.Random;
/**
 * 
 * @author Nathan
 */
public class PwGenerator {
    
    private char[] initUppercase(boolean include) {
        char[] uppercases;
        if (include) {
            uppercases = new char[26];
            for (int i = 0; i < uppercases.length; i++) {
                uppercases[i] = (char)(i + 65);
            }
        }
        else {
            uppercases = new char[0];
        }
        return uppercases;
    }
    
    private char[] initLowercase(boolean include) {
        char[] lowercases;
        if (include) {
            lowercases = new char[26];
            for (int i = 0; i < lowercases.length; i++) {
                lowercases[i] = (char)(i + 97);
            }
        }
        else {
            lowercases = new char[0];
        }
        return lowercases;
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
            String specialChars = "!#$%&*+-=?@^_";
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
    
    private char[] initExtremities(boolean include) {
        char[] extremities;
        if (include) {
            String extremityChars = " /\\()[]{};:'\"<>,.`~|";
            extremities = new char[extremityChars.length()];
            for (int i = 0; i < extremities.length; i++) {
                extremities[i] = extremityChars.charAt(i);
            }
        }
        else {
            extremities = new char[0];
        }
        return extremities;
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
    
    public String generatePassword(int lengthIn, boolean containUppers, 
                                   boolean containLowers, boolean containDigits,
                                   boolean containSpecials, boolean containExtremities) {
        char[] uppers = initUppercase(containUppers);
        char[] lowers = initLowercase(containLowers);
        char[] digits = initDigits(containDigits);
        char[] specials = initSpecials(containSpecials);
        char[] extremities = initExtremities(containExtremities);
        
        char[] validChars = concatenateCharArrs(uppers, lowers, digits, specials, extremities);
        
        Random randomNum = new Random();
        
        char[] genCharArr = new char[lengthIn];
        for (int i = 0; i < genCharArr.length; i++) {
            genCharArr[i] = validChars[randomNum.nextInt(validChars.length)];
        }
        String generatedPass = new String(genCharArr);
        return generatedPass;
    }
}
