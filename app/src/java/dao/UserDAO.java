/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import entity.App;
import entity.User;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.zip.ZipInputStream;

/**
 *
 * @author ASUS-PC
 */
public class UserDAO {

    private ArrayList<User> userList;
    // private ArrayList<String> categories = new ArrayList<>();
    private ArrayList<String> unsuccessful = new ArrayList<>();

    public UserDAO() {
        userList = new ArrayList<>();
    }

    public void insert(ZipInputStream zis, Connection conn) throws IOException, SQLException {
        PreparedStatement stmt = null;
        Scanner sc = new Scanner(zis).useDelimiter(",|\r\n");
        sc.nextLine(); //flush title

        while (sc.hasNext()) {

            //retrieving per row
            int locationId = -1;
            boolean err = false;

            String macAdd = Utility.parseString(sc.next());
            if (macAdd == null) {
                unsuccessful.add("mac add cannot be blank");
                err = true;
            }

            if (!Utility.checkHexadecimal(macAdd)) {
                unsuccessful.add("invalid macAddress");
                err = true;
            }

            String name = Utility.parseString(sc.next());

            if (name == null) {
                unsuccessful.add("name cannot be blank");
                err = true;
            }

            String password = Utility.parseString(sc.next());
            if (password == null) {
                unsuccessful.add("password cannot be blank");
                err = true;
            }

            if (!Utility.checkPassword(password)) {
                unsuccessful.add("invalid password");
                err = true;
            }

            String email = Utility.parseString(sc.next());
            if (email == null) {
                unsuccessful.add("email cannot be blank");
                err = true;
            }
            if (Utility.checkEmail(email)) {
                unsuccessful.add("invalid email");
                err = true;
            }

            String gender = Utility.parseString(sc.next());
            if (gender == null) {
                unsuccessful.add("gender cannot be blank");
                err = true;
            }

            gender.toLowerCase();

            if (!gender.equals("f") || !gender.equals("g")) {
                unsuccessful.add("invalid gender");
                err = true;
            }

            if (!err) {
                //add to list
                User user = new User(macAdd, name, password, email, gender);
                userList.add(user);
                //insert into tables
                String sql = "insert into user (macAdd , name , password , email , gender values(?,?,?,?,?))";
                stmt = conn.prepareStatement(sql);
                stmt.setString(1, "\"" + macAdd + "\"");
                stmt.setString(2, "\"" + name + "\"");
                stmt.setString(3, "\"" + password + "\"");
                stmt.setString(4, "\"" + email + "\"");
                stmt.setString(5, "\"" + gender + "\"");
            }

            //adding to batch
            stmt.addBatch();

        }

        //closing
        if (stmt != null) {
            stmt.executeBatch();
            conn.commit();
            stmt.close();
        }
        if (sc != null) {
            sc.close();
        }
    }
    
    public boolean hasMacAdd(String str){
        for(User u: userList){
            if(u.getMacAddress().equals(str)){
                return true;
            }
        }
        return false;
    }
}
