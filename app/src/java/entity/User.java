package entity;
public class User {

    private String macAddress;
    private String name;
    private String password;
    private String email;
    private String gender;

    public User(String macAddress, String name, String password, String email, String gender) {
        this.macAddress = macAddress;
        this.name = name;
        this.password = password;
        this.email = email;
        this.gender = gender;
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
