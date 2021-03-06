/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import entity.Admin;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author jeremyongts92
 */
/**
 * AdminDAO handles interactions between Admin and Controllers
 */
public class AdminDAO {

    /**
     * Retrieve the username and password of an admin
     *
     * @param username The unique username that identifies a admin
     * @param password The password of the admin
     * @return Admin object
     */
    public Admin retrieve(String username, String password) {

        String sql = "SELECT * FROM admin WHERE username=? AND password=?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = ConnectionManager.getConnection();
            ps = conn.prepareStatement(sql);

            ps.setString(1, username);
            ps.setString(2, password);

            rs = ps.executeQuery();

            while (rs.next()) {

                return new Admin(rs.getString(1), rs.getString(2));

            }

            ConnectionManager.close(conn, ps);

        } catch (SQLException e) {

        } finally {
            ConnectionManager.close(conn, ps, rs);
        }

        return null;
    }

    /**
     * Retrieve the username and password of an admin
     *
     * @param username The unique username that identifies a admin
     * @return Admin object
     */
    public Admin retrieve(String username) {

        String sql = "SELECT * FROM admin WHERE username=?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = ConnectionManager.getConnection();
            ps = conn.prepareStatement(sql);

            ps.setString(1, username);

            rs = ps.executeQuery();

            while (rs.next()) {

                return new Admin(rs.getString(1), rs.getString(2));

            }

        } catch (SQLException e) {

        } finally {
            ConnectionManager.close(conn, ps, rs);
        }

        return null;
    }

}
