package entity;
public class User {

    private String macAddress;
    private String name;
    private String password;
    private String email;
    private String gender;
    private String cca;

    public User(String macAddress, String name, String password, String email, String gender, String cca) {
        this.macAddress = macAddress;
        this.name = name;
        this.password = password;
        this.email = email;
        this.gender = gender;
        this.cca = cca;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setCca(String cca) {
        this.cca = cca;
    }

    public String getCca() {
        return cca;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getGender() {
        return gender;
    }
    
    public String getYear(){
		int index = email.indexOf("@");
		String year = email.substring((index - 4),index);
		return year;
	}
	
	public String getSchool(){
		int indexBefore = email.indexOf("@");
		int indexAfter = email.indexOf(".", indexBefore + 1);
		String school = email.substring(indexBefore + 1, indexAfter);
		return school;
	}
}
