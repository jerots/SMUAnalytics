/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author ASUS-PC
 */
public final class Utility {

    private static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-DD HH:mm:ss");
    private static ArrayList<String> schoolList;
    private static ArrayList<String> categories;

    static {
        loadSchools();
        loadCategories();
    }

    public static java.sql.Date parseDate(String date) {
        if (date != null && !date.equals("")) {
            try {
                return (java.sql.Date) (df.parse(date));
            } catch (ParseException e) {

            }
        }
        return null;
    }

    public static int parseInt(String str) {
        int num = -1;
        try {
            num = Integer.parseInt(str);

        } catch (NumberFormatException e) {

        }
        return num;
    }

    public static boolean checkHexadecimal(String str) {
        return (str.length() == 40 && str.matches("/[0-9a-fA-F]+/"));
    }

    public static String parseString(String str) {
        str.trim();
        if (str != null && str.length() > 0) {
            return str;
        }
        return null;
    }

    public static boolean checkPassword(String str) {
        return (str.length() > 8 && !str.matches("\\s"));
    }

    public static boolean checkEmail(String str) {
        String[] split = str.split("@");
        String frontEmail = split[0];
        String backEmail = split[1];
        String[] frontParts = frontEmail.split("\\.");
        int year = parseInt(frontParts[frontParts.length - 1]);
        for (String part : frontParts) {
            for (int i = 0; i < part.length(); i++) {
                if (!Character.isLetterOrDigit(part.charAt(i))) {
                    return false;
                }
            }
        }

        if (2011 >= year || 2015 <= year) {
            return false;
        }
        int position = backEmail.indexOf(".");
        return (schoolList.contains(backEmail.substring(0, position)) && backEmail.substring(position).equals(".smu.edu.sg"));
    }
    
     public static boolean checkCategory(String str) {
        return categories.contains(str);
     }

    public static void loadSchools() {
        schoolList = new ArrayList<>();
        schoolList.add("business");
        schoolList.add("accountancy");
        schoolList.add("sis");
        schoolList.add("economics");
        schoolList.add("law");
        schoolList.add("socsc");
    }

    public static void loadCategories() {
        categories = new ArrayList<>();
        categories.add("books");
        categories.add("social");
        categories.add("education");
        categories.add("entertainment");
        categories.add("information");
        categories.add("library");
        categories.add("local");
        categories.add("tools");
        categories.add("fitness");
        categories.add("games");
        categories.add("others");
    }

}
