package passwordmanager;

/**
 *
 * @author Nathan
 */
public class Main {

    public static void main(String[] args) {
        PwGenerator gen = new PwGenerator();
        PwHasher hash = new PwHasher();
        try {
            char[] password = gen.generatePassword(16, true, true, true, true, false).toCharArray();
            System.out.println(new String(password));
            byte[] bytePass = (new String(password)).getBytes();
            
            byte[] key = hash.generateDBKey();
            byte[] encodedPass = hash.encrypt(key, bytePass);
            byte[] decodedPass = hash.decrypt(key, encodedPass);
            
            System.out.println(new String(decodedPass));
            
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        
        
        try {
            DataManager dm = new DataManager();
            String p1 = "Fh#jI@i0Z-";
            String p2 = "&$n&SB#b7+";            
            
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
            //varifying attempt to login
            UserObject user = dm.verifyLogin("ntozer", p1.toCharArray());
            System.out.println(user.username);
            UserObject user2 = dm.verifyLogin("asdf", p2.toCharArray());
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
            
            //creating user accounts
            dm.createAccount(user.dbKey, user.username, "Facebook", "cornerback44", "abcdef".toCharArray(), "www.facebook.com");
            
            //getting user account
            AccountObject acc = dm.getAcccountInfo(user.dbKey, user.username, "Facebook");
            System.out.println("Title: " + acc.title);
            System.out.println("Username: " + acc.accUsername);
            System.out.println("Password: " + acc.accPassword);
            System.out.println("Website: " + acc.website);
            
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        
    }
}
