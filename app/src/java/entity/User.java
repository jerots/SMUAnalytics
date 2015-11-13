package entity;

/**
 * User represents the user of the app
 */
public class User {

    private String macAddress;
    private String name;
    private String password;
    private String email;
    private String gender;
    private String cca;

    /**
     * Creates a User object with the specified macAddress, name, password,
     * email,gender and cca
     *
     * @param macAddress The mac address of the User
     * @param name The name of the User
     * @param password The password of the User
     * @param email The email of the User
     * @param gender The gender of the User
     * @param cca The cca of the User
     */
    public User(String macAddress, String name, String password, String email, String gender, String cca) {
        this.macAddress = macAddress;
        this.name = name;
        this.password = password;
        this.email = email;
        this.gender = gender;
        this.cca = cca;
    }

    /**
     * Set the mac address of the User
     *
     * @param macAddress The mac address of the User
     */
    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    /**
     * Set the name of the User
     *
     * @param name The name of the User
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Set the password of the User
     *
     * @param password The password of the User
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Set the email of the User
     *
     * @param email The email of the User
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Set the gender of the User
     *
     * @param gender The gender of the User
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * Set the cca of the User
     *
     * @param cca The cca of the User
     */
    public void setCca(String cca) {
        this.cca = cca;
    }

    /**
     * Get the cca of the User
     *
     * @return the cca of the User
     */
    public String getCca() {
        return cca;
    }

    /**
     * Get the mac address of the User
     *
     * @return the mac address of the User
     */
    public String getMacAddress() {
        return macAddress;
    }

    /**
     * Get the name of the User
     *
     * @return the name of the User
     */
    public String getName() {
        return name;
    }

    /**
     * Get the password of the User
     *
     * @return the password of the User
     */
    public String getPassword() {
        return password;
    }

    /**
     * Get the email of the User
     *
     * @return the email of the User
     */
    public String getEmail() {
        return email;
    }

    /**
     * Get the gender of the User
     *
     * @return the gender of the User
     */
    public String getGender() {
        return gender;
    }

    /**
     * Get the year of schooling the User
     *
     * @return the year of schooling of the User
     */
    public String getYear() {
        int index = email.indexOf("@");
        String year = email.substring((index - 4), index);
        return year;
    }

    /**
     * Get the school of the User
     *
     * @return the school of the User
     */
    public String getSchool() {
        int indexBefore = email.indexOf("@");
        int indexAfter = email.indexOf(".", indexBefore + 1);
        String school = email.substring(indexBefore + 1, indexAfter);
        return school;
    }
}
