package passwordmanager;

import java.util.Arrays;
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
            DataManager dm = new DataManager();
            String p1 = "i1tq+0lQmC";
            String p2 = "4?OksPDj!P";
            PwHasher hasher = new PwHasher();
            byte[] a = hasher.generateSalt();
            byte[] b = hasher.generateSalt();
            byte[] c = hasher.getHashedPw(p1.toCharArray(), a, b);
            byte[] d = hasher.getHashedPw(p1.toCharArray(), a, b);
            System.out.println(Arrays.equals(c, d));
            
            /*
            boolean toReg = false;
            if (toReg) {
                p1 = gen.generatePassword(10,true,true,true,true,false);
                p2 = gen.generatePassword(10,true,true,true,true,false);
                System.out.println(p1);
                System.out.println(p2);
                //registering user
                dm.registerUser("ntozer", p1.toCharArray(), "ntozer@unb.ca");
                dm.registerUser("asdf", p2.toCharArray(), "asdf@swe.unb.ca");
            }
            //varifying attempting to login
            UserObject user = dm.verifyLogin("ntozer", p1);
            System.out.println(user.username);
            UserObject user2 = dm.verifyLogin("asdf", p2);
            System.out.println(user2.username);
            
            //changing user pw generator settings
            dm.updateSettings(user.username, 16, true, true, true, false, false);
            dm.updateSettings(user2.username, 8, true, true, true, true, true);
            
            //displaying user settings
            SettingsObject settings = dm.getSettings(user.username);
            System.out.println("User: " + settings.username + " PassLength: "
                               + settings.length + " ValidChars: " 
                               + settings.lcase + settings.ucase 
                               + settings.digits + settings.specials 
                               + settings.extremities);
            
            settings = dm.getSettings(user2.username);
            System.out.println("User: " + settings.username + " PassLength: "
                               + settings.length + " ValidChars: " 
                               + settings.lcase + settings.ucase 
                               + settings.digits + settings.specials 
                               + settings.extremities);
*/
            
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        
    }
}
