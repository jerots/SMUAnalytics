/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

/**
 *
 * @author jeremyongts92
 */
/**
 * Admin represents the Administrators of the Application
 */
public class Admin {

    String username;
    String password;

    /**
     * Creates an Admin object with the specified username and password
     *
     * @param username The username of an Admin
     * @param password The password of an Admin
     */
    public Admin(String username, String password) {
        this.username = username;
        this.password = password;
    }
//

    /**
     * Get the username of the Admin
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Get the password of the Admin
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

}
