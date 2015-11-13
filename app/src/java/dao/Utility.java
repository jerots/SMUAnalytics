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
/**
 * Utility handles interactions between DAOs and Controllers that involves
 * validation
 */
public final class Utility {

    private static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private static ArrayList<String> schoolList;
    private static ArrayList<String> categories;
    private static ArrayList<String> years;
    private static ArrayList<String> genders;

    /* Loads the list of Schools and Categories
     */
    static {
        loadSchools();
        loadCategories();
        loadYears();
        loadGenders();
    }

    /**
     * Converts a date with type "String" into a Date object
     *
     * @param Date in String for formatting
     * @return Date object in "yyyy-MM-dd HH:mm:ss"
     */
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

    /**
     * Format a date into DateFormat "yyyy-MM-dd HH:mm:ss"
     *
     * @param Date object for formatting
     * @return String object of the input date in "yyyy-MM-dd HH:mm:ss"
     */
    public static String formatDate(Date date) {
        if (date != null) {
            String format = df.format(date);
            if (checkDate(format)) {
                return format;
            }
        }
        return null;
    }

    /**
     * Format a date into DateFormat "yyyy-MM-dd"
     *
     * @param Date in String for formatting
     * @return Date object in "yyyy-MM-dd"
     */
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

    /**
     * Format a date into DateFormat "yyyy-MM-dd"
     *
     * @param Date object for formatting
     * @return String object of input Date in "yyyy-MM-dd"
     */
    public static String formatOnlyDate(Date date) {
        if (date != null) {
            String format = sdf.format(date);
            if (checkOnlyDate(format)) {
                return format;
            }
        }
        return null;
    }

    /**
     * Validate that date is in the correct format "yyyy-MM-dd HH:mm:ss"
     *
     * @param Date in String for validation
     * @return true if date is valid, false if otherwise
     */
    public static boolean checkDate(String str) {
        return str.matches("((((19[7-9]\\d)|([2-9]\\d{3}))-(0[13578]|1[02])-31)|(((19[7-9]\\d)|([2-9]\\d{3}))-(0[13456789]|1[012])-(0[1-9]|[12]\\d|30))|"
                + "(((19[7-9]\\d)|([2-9]\\d{3}))-02-(0[1-9]|1\\d|2[0-8]))|((19([79][26])|(8[048]))|([2-9]\\d(([13579][26])|([24680][048])))-02-29))"
                + "\\s(([0-1]\\d)|(2[0-3])):([0-5]\\d):([0-5]\\d)");
    }

    /**
     * Validate that date is in the correct format "yyyy-MM-dd"
     *
     * @param Date in String for validation
     * @return true if date is valid, false if otherwise
     */
    public static boolean checkOnlyDate(String str) {
        return str.matches("((((19[7-9]\\d)|([2-9]\\d{3}))-(0[13578]|1[02])-31)|(((19[7-9]\\d)|([2-9]\\d{3}))-(0[13456789]|1[012])-(0[1-9]|[12]\\d|30))|"
                + "(((19[7-9]\\d)|([2-9]\\d{3}))-02-(0[1-9]|1\\d|2[0-8]))|((19([79][26])|(8[048]))|([2-9]\\d(([13579][26])|([24680][048])))-02-29)).*");
    }

    /* Validate that number is an Integer
     * @param Integer in String for validation
     * @return The number in Integer if number is an Integer, -1 if otherwise
     */
    public static int parseInt(String str) {
        int num = -1;
        try {
            num = Integer.parseInt(str);

        } catch (NumberFormatException e) {

        }
        return num;
    }

    /* Validate that String input is a Hexadecimal
     *
     * @return true if String is a Hexadecimal, false if otherwise
     */
    public static boolean checkHexadecimal(String str) {
        return (str.length() == 40 && str.matches("[0-9a-fA-F]+"));
    }

    /* Validate that String input is a valid String
     * @param String for validation
     * @return String is a valid String, null if otherwise
     */
    public static String parseString(String str) {
        if (str != null && str.length() > 0) {
            return str.trim();
        }
        return null;
    }

    /* Validate that input is a valid password
     * @param password in String for validation
     * @return true if input is a valid password, false if otherwise
     */
    public static boolean checkPassword(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return str.length() >= 8;
    }

    /* Validate that input is in the correct Email Address format
     * @param email address in String for validation
     * @return true if Email Address is in the correct format, false if otherwise
     */
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

    /* Validate that input is within the list of valid Categories
     * @param Category in String for validation
     * @return true if category is valid, false if otherwise
     */
    public static boolean checkCategory(String chk) {
        String check = parseString(chk);
        if (check != null) {
            return categories.contains(chk);
        }
        return false;
    }

    /**
     * Retrieve an ArrayList of all valid schools
     *
     * @return an ArrayList of all valid schools
     */
    public static ArrayList<String> retrieveSchools() {
        return schoolList;
    }

    /**
     * Retrieve an ArrayList of all valid categories
     *
     * @return an ArrayList of all valid categories
     */
    public static ArrayList<String> retrieveCategories() {
        return categories;
    }

    /**
     * Retrieve an ArrayList of all schools that are not in the input list of
     * schools
     *
     * @param an ArrayList of all schools that exist in the list of schools
     * @return an ArrayList of all schools that are not in the input list of
     * schools
     */
    public static HashMap<String, Long> compareSchools(HashMap<String, Long> schools) {
        HashMap<String, Long> missing = new HashMap<>();
        for (String s : schoolList) {
            if (!schools.containsKey(s)) {
                schools.put(s, (long) 0);
            }
        }
        return missing;
    }

    /* Validate that school is within the list of valid Schools
     * @school School of a user
     * @return true if school is valid, false if otherwise
     */
    public static boolean checkSchools(String school) {
        String sch = parseString(school);
        if (sch != null) {
            return schoolList.contains(sch);
        }
        return false;
    }

    /* Retrieve that school of a user from the given email address
     * @param email Email Address of a user
     * @return School from the given email address
     */
    public static String getSchool(String email) {
        return email.substring(email.indexOf("@") + 1, email.indexOf(".", email.indexOf("@")));
    }

    /* Calculates the number of seconds between two given dates
     * @param startDate The start of the period of interest
     *  @param endDate The end of the period of interest
     * @return the number of seconds between two given dates
     */
    public static long secondsBetweenDates(Date startDate, Date endDate) {
        return (endDate.getTime() - startDate.getTime()) / 1000;
    }

    /* Calculates the number of days between two given dates
     * @param startDate The start of the period of interest
     * @param endDate The end of the period of interest
     * @return the number of days between two given dates
     */
    public static long daysBetweenDates(Date startDate, Date endDate) {
        return (endDate.getTime() - startDate.getTime()) / 1000 / 60 / 60 / 24 + 1;
    }

    /* Get the next hour of the date in time
     * @param startDate The start of the period of interest
     * @Date the Date corresponding to it
     */
    public static Date getNextHour(Date startDate) {

        return new Date(startDate.getTime() + (1000 * 60 * 60));
    }

    //*ALERT* THERE IS A DUPLICATE!
    public static ArrayList<String> getSchoolList() {
        return schoolList;
    }

    /**
     * Calculates the time in milliseconds after 24 hours
     *
     * @param the time of the date for conversion
     * @result the time of the input date after 24 hours
     */
    public static long getEndTime(long time) {
        return (time / (60 * 60 * 24 * 1000) + 1) * (60 * 60 * 24 * 1000);
    }

    /**
     * Loads all valid Schools to an ArrayList
     *
     */
    public static void loadSchools() {
        schoolList = new ArrayList<>();
        schoolList.add("accountancy");
        schoolList.add("business");
        schoolList.add("economics");
        schoolList.add("law");
        schoolList.add("sis");
        schoolList.add("socsc");
    }

    /**
     * Loads all categories to an ArrayList
     *
     */
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

        /**
     * Loads all years to an ArrayList
     *
     */
    public static void loadYears() {
        years = new ArrayList<String>();
        years.add("2011");
        years.add("2012");
        years.add("2013");
        years.add("2014");
        years.add("2015");
    }

        /**
     * Loads all genders to an ArrayList
     *
     */
    public static void loadGenders() {
        genders = new ArrayList<String>();
        genders.add("f");
        genders.add("m");
    }
    /**
     * Get an arraylist of available years
     *
     */
    public static ArrayList<String> getYears() {
        return years;
    }

    /**
     * Get an arraylist of available gender
     *
     */
    public static ArrayList<String> getGenders() {
        return genders;
    }

}
