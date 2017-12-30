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
        PwGenerator gen = new PwGenerator();
        for (int i = 0; i < 5; i++) {
            System.out.println(gen.generatePassword(4,false,false,true,false,false));
        }
        try {
            String p1 = gen.generatePassword(10,true,true,true,true,false);
            String p2 = gen.generatePassword(10,true,true,true,true,false);
            DataManager dm = new DataManager();
            dm.registerUser("ntozer", p1, "ntozer@unb.ca");
            dm.registerUser("asdf", p2, "asdf@swe.unb.ca");
            
            if (dm.verifyLogin("ntozer", p1)) {
                System.out.println("Logged in as ntozer");
            }
            if (dm.verifyLogin("ntozer", p2)) {
                System.out.println("Logged in as ntozer");
            }
            if (dm.verifyLogin("asdf", p2)) {
                System.out.println("Logged in as asdf");
            }
            
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        
    }
}
