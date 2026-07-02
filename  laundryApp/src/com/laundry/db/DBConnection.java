package com.laundry.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 * Kelas untuk mengelola koneksi ke database MySQL.
 * Ubah nilai URL, USER, PASS sesuai konfigurasi MySQL masing-masing.
 */
public class DBConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/laundry_db?useSSL=false&serverTimezone=Asia/Jakarta";
    private static final String USER = "root";
    private static final String PASS = "";

    private static Connection conn = null;

    /**
     * Mengambil koneksi database. Jika koneksi belum ada / sudah tertutup,
     * maka akan dibuat koneksi baru.
     */
    public static Connection getConnection() {
        try {
            if (conn == null || conn.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                conn = DriverManager.getConnection(URL, USER, PASS);
            }
        } catch (ClassNotFoundException | SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    "Koneksi database gagal!\n" + ex.getMessage(),
                    "Error Koneksi",
                    JOptionPane.ERROR_MESSAGE);
            System.out.println(ex.toString());
        }
        return conn;
    }

    public static void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
    }
}
