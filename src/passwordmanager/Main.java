/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package passwordmanager;

import java.sql.ResultSet;
import java.sql.SQLException;
/**
 *
 * @author Nathan
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SQLiteTest test = new SQLiteTest();
        ResultSet res;
        
        try{
            res = test.displayUsers();
            while(res.next()){
                System.out.println(res.getString("fname") + " " + res.getString("lname"));
            }
        } catch(ClassNotFoundException e) {
            e.printStackTrace();
        } catch(SQLException e) {
            e.printStackTrace();
        }
        
        PwGenerator gen = new PwGenerator();
        
        for (int i = 0; i < 10; i++) {
            System.out.println(gen.generatePassword(16,true,true,true));
        }
        char[] a = new char[10];
        if (a[6] == (char)0) {
            System.out.println("hyuck");
        }
    }
}
