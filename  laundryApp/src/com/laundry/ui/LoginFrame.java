package com.laundry.ui;

import com.laundry.db.DBConnection;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Frame Login - memvalidasi username & password terhadap tabel tb_users.
 */
public class LoginFrame extends JFrame {

    private JTextField tUsername;
    private JPasswordField tPassword;
    private JButton bLogin, bKeluar;

    public LoginFrame() {
        setTitle("Login - Aplikasi Laundry");
        setSize(420, 320);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        initComponents();
    }

    private void initComponents() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBorder(new EmptyBorder(20, 30, 20, 30));

        JLabel title = new JLabel("APLIKASI LAUNDRY", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(new Color(30, 90, 170));

        JLabel subtitle = new JLabel("Silakan login untuk melanjutkan", SwingConstants.CENTER);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.add(title);
        titlePanel.add(subtitle);
        main.add(titlePanel, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8, 5, 8, 5);
        gc.fill = GridBagConstraints.HORIZONTAL;

        gc.gridx = 0; gc.gridy = 0;
        form.add(new JLabel("Username"), gc);
        gc.gridx = 1;
        tUsername = new JTextField(15);
        form.add(tUsername, gc);

        gc.gridx = 0; gc.gridy = 1;
        form.add(new JLabel("Password"), gc);
        gc.gridx = 1;
        tPassword = new JPasswordField(15);
        form.add(tPassword, gc);

        main.add(form, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bLogin = new JButton("Login");
        bKeluar = new JButton("Keluar");
        btnPanel.add(bLogin);
        btnPanel.add(bKeluar);
        main.add(btnPanel, BorderLayout.SOUTH);

        add(main);

        bLogin.addActionListener(e -> doLogin());
        bKeluar.addActionListener(e -> System.exit(0));

        // Enter key triggers login
        tPassword.addActionListener(e -> doLogin());
    }

    private void doLogin() {
        String username = tUsername.getText().trim();
        String password = new String(tPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username dan password wajib diisi!",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "SELECT * FROM tb_users WHERE username = ? AND password = ?";
        Connection con = DBConnection.getConnection();
        if (con == null) return;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String namaLengkap = rs.getString("nama_lengkap");
                JOptionPane.showMessageDialog(this, "Login berhasil, selamat datang " + namaLengkap + "!",
                        "Sukses", JOptionPane.INFORMATION_MESSAGE);
                MainFrame mainFrame = new MainFrame(namaLengkap);
                mainFrame.setLocationRelativeTo(null);
                mainFrame.setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Username atau password salah!",
                        "Login Gagal", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
