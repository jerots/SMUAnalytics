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
import java.util.HashMap;

/**
 *
 * @author ASUS-PC
 */
public final class Utility {

    private static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private static ArrayList<String> schoolList;
    private static ArrayList<String> categories;
    private static ArrayList<String> years;
    private static ArrayList<String> genders;

    static {
        loadSchools();
        loadCategories();
        loadYears();
        loadGenders();
    }

    public static Date parseDate(String date) {
        date = parseString(date);
        if (date != null && date.length() > 0) {
            try {
                return df.parse(date);
            } catch (ParseException e) {
            }
        }
        return null;
    }

    public static String formatDate(Date date) {
        if (date != null) {
            String format = df.format(date);
            if (checkDate(format)) {
                return format;
            }
        }
        return null;
    }

    public static Date parseOnlyDate(String date) {
        date = parseString(date);
        if (date != null && date.length() > 0) {
            try {
                return sdf.parse(date);
            } catch (ParseException e) {
            }
        }
        return null;
    }

    public static String formatOnlyDate(Date date) {
        if (date != null) {
            String format = sdf.format(date);
            if (checkOnlyDate(format)) {
                return format;
            }
        }
        return null;
    }

    public static boolean checkDate(String str) {
        return str.matches("((((19[7-9]\\d)|([2-9]\\d{3}))-(0[13578]|1[02])-31)|(((19[7-9]\\d)|([2-9]\\d{3}))-(0[13456789]|1[012])-(0[1-9]|[12]\\d|30))|"
                + "(((19[7-9]\\d)|([2-9]\\d{3}))-02-(0[1-9]|1\\d|2[0-8]))|((19([79][26])|(8[048]))|([2-9]\\d(([13579][26])|([24680][048])))-02-29))"
                + "\\s(([0-1]\\d)|(2[0-3])):([0-5]\\d):([0-5]\\d)");
    }

    public static boolean checkOnlyDate(String str) {
        return str.matches("((((19[7-9]\\d)|([2-9]\\d{3}))-(0[13578]|1[02])-31)|(((19[7-9]\\d)|([2-9]\\d{3}))-(0[13456789]|1[012])-(0[1-9]|[12]\\d|30))|"
                + "(((19[7-9]\\d)|([2-9]\\d{3}))-02-(0[1-9]|1\\d|2[0-8]))|((19([79][26])|(8[048]))|([2-9]\\d(([13579][26])|([24680][048])))-02-29)).*");
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
        return (str.length() == 40 && str.matches("[0-9a-fA-F]+"));
    }

    public static String parseString(String str) {
        if (str != null && str.length() > 0) {
            return str.trim();
        }
        return null;
    }

    public static boolean checkPassword(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return str.length() >= 8;
    }

    public static boolean checkEmail(String str) {
        if (str != null) {
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
            if (2011 > year || 2015 < year) {
                return false;
            }
            int position = backEmail.indexOf(".");
            return (schoolList.contains(backEmail.substring(0, position)) && backEmail.substring(position).equals(".smu.edu.sg"));
        }
        return false;
    }

    public static boolean checkCategory(String chk) {
        String check = parseString(chk);
        if (check != null) {
            return categories.contains(chk);
        }
        return false;
    }

    public static ArrayList<String> retrieveSchools() {
        return schoolList;
    }

    public static ArrayList<String> retrieveCategories() {
        return categories;
    }

    public static HashMap<String,Long> compareSchools(HashMap<String,Long> schools) {
        HashMap<String,Long> missing = new HashMap<>();
        for (String s : schoolList) {
            if (!schools.containsKey(s)) {
                schools.put(s, (long) 0);
            }
        }
        return missing;
    }

    public static boolean checkSchools(String school) {
        String sch = parseString(school);
        if (sch != null) {
            return schoolList.contains(sch);
        }
        return false;
    }

    public static String getSchool(String email) {
        return email.substring(email.indexOf("@") + 1, email.indexOf(".", email.indexOf("@")));
    }

    public static long secondsBetweenDates(Date startDate, Date endDate) {
        return (endDate.getTime() - startDate.getTime()) / 1000;
    }

    public static long daysBetweenDates(Date startDate, Date endDate) {
        return (endDate.getTime() - startDate.getTime()) / 1000 / 60 / 60 / 24 + 1;
    }

    /* ??????
     */
    public static Date getNextHour(Date startDate) {

        return new Date(startDate.getTime() + (1000 * 60 * 60));
    }

    //*ALERT* THERE IS A DUPLICATE!
    public static ArrayList<String> getSchoolList() {
        return schoolList;
    }

    public static long getEndTime(long time) {
        return (time / (60 * 60 * 24 * 1000) + 1) * (60 * 60 * 24 * 1000);
    }

    public static void loadSchools() {
        schoolList = new ArrayList<>();
        schoolList.add("accountancy");
        schoolList.add("business");
        schoolList.add("economics");
        schoolList.add("law");
        schoolList.add("sis");
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
    
    public static void loadYears(){
        years = new ArrayList<String>();
        years.add("2011");
        years.add("2012");
        years.add("2013");
        years.add("2014");
        years.add("2015");
    }
    
    public static void loadGenders(){
        genders = new ArrayList<String>();
        genders.add("f");
        genders.add("m");
    }

    public static ArrayList<String> getYears() {
        return years;
    }

    public static ArrayList<String> getGenders() {
        return genders;
    }

}
