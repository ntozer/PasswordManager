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
    }
    
}
