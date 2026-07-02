package com.laundry.ui;

import com.laundry.ui.laporan.LaporanFrame;
import com.laundry.ui.layanan.LayananTampilFrame;
import com.laundry.ui.pelanggan.PelangganTampilFrame;
import com.laundry.ui.transaksi.TransaksiTampilFrame;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Frame Menu Utama - berisi navigasi ke seluruh modul aplikasi.
 */
public class MainFrame extends JFrame {

    public MainFrame(String namaUser) {
        setTitle("Menu Utama - Aplikasi Laundry");
        setSize(600, 420);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        initComponents(namaUser);
    }

    private void initComponents(String namaUser) {
        JPanel main = new JPanel(new BorderLayout());
        main.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel header = new JLabel("SELAMAT DATANG, " + namaUser.toUpperCase(), SwingConstants.CENTER);
        header.setFont(new Font("SansSerif", Font.BOLD, 18));
        header.setForeground(new Color(30, 90, 170));
        main.add(header, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(2, 3, 15, 15));
        grid.setBorder(new EmptyBorder(30, 10, 30, 10));

        JButton bPelanggan = makeMenuButton("Data Pelanggan");
        JButton bLayanan = makeMenuButton("Data Layanan");
        JButton bTransaksi = makeMenuButton("Transaksi Laundry");
        JButton bLaporan = makeMenuButton("Laporan");
        JButton bLogout = makeMenuButton("Logout");
        JButton bKeluar = makeMenuButton("Keluar Aplikasi");

        grid.add(bPelanggan);
        grid.add(bLayanan);
        grid.add(bTransaksi);
        grid.add(bLaporan);
        grid.add(bLogout);
        grid.add(bKeluar);

        main.add(grid, BorderLayout.CENTER);
        add(main);

        bPelanggan.addActionListener(e -> new PelangganTampilFrame().setVisible(true));
        bLayanan.addActionListener(e -> new LayananTampilFrame().setVisible(true));
        bTransaksi.addActionListener(e -> new TransaksiTampilFrame().setVisible(true));
        bLaporan.addActionListener(e -> new LaporanFrame().setVisible(true));

        bLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin logout?",
                    "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                LoginFrame login = new LoginFrame();
                login.setLocationRelativeTo(null);
                login.setVisible(true);
                this.dispose();
            }
        });

        bKeluar.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin keluar aplikasi?",
                    "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });
    }

    private JButton makeMenuButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.PLAIN, 13));
        b.setFocusPainted(false);
        return b;
    }
}
