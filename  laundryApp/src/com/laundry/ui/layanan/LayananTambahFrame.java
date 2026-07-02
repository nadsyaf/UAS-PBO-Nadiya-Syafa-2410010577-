package com.laundry.ui.layanan;

import com.laundry.db.DBConnection;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Frame Tambah/Ubah Data Layanan.
 * Jika idLayanan == null maka mode TAMBAH, selain itu mode UBAH.
 */
public class LayananTambahFrame extends JFrame {

    private final LayananTampilFrame parent;
    private final Integer idLayanan;

    private JTextField tNama, tHarga, tEstimasi;
    private JButton bSimpan, bBatal;

    public LayananTambahFrame(LayananTampilFrame parent, Integer idLayanan) {
        this.parent = parent;
        this.idLayanan = idLayanan;

        setTitle(idLayanan == null ? "Tambah Data Layanan" : "Ubah Data Layanan");
        setSize(400, 280);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        initComponents();

        if (idLayanan != null) {
            loadDataById(idLayanan);
        }
    }

    private void initComponents() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBorder(new EmptyBorder(15, 20, 15, 20));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 5, 6, 5);
        gc.fill = GridBagConstraints.HORIZONTAL;

        gc.gridx = 0; gc.gridy = 0;
        form.add(new JLabel("Nama Layanan"), gc);
        gc.gridx = 1;
        tNama = new JTextField(18);
        form.add(tNama, gc);

        gc.gridx = 0; gc.gridy = 1;
        form.add(new JLabel("Harga per Kg (Rp)"), gc);
        gc.gridx = 1;
        tHarga = new JTextField(18);
        form.add(tHarga, gc);

        gc.gridx = 0; gc.gridy = 2;
        form.add(new JLabel("Estimasi Selesai (hari)"), gc);
        gc.gridx = 1;
        tEstimasi = new JTextField(18);
        form.add(tEstimasi, gc);

        main.add(form, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bSimpan = new JButton("Simpan");
        bBatal = new JButton("Batal");
        btnPanel.add(bSimpan);
        btnPanel.add(bBatal);
        main.add(btnPanel, BorderLayout.SOUTH);

        add(main);

        bSimpan.addActionListener(e -> simpanData());
        bBatal.addActionListener(e -> dispose());
    }

    private void loadDataById(int id) {
        String sql = "SELECT * FROM tb_layanan WHERE id_layanan = ?";
        Connection con = DBConnection.getConnection();
        if (con == null) return;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                tNama.setText(rs.getString("nama_layanan"));
                tHarga.setText(String.valueOf(rs.getDouble("harga_per_kg")));
                tEstimasi.setText(String.valueOf(rs.getInt("estimasi_hari")));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data: " + ex.getMessage());
        }
    }

    private void simpanData() {
        String nama = tNama.getText().trim();
        String hargaStr = tHarga.getText().trim();
        String estimasiStr = tEstimasi.getText().trim();

        if (nama.isEmpty() || hargaStr.isEmpty() || estimasiStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field wajib diisi!");
            return;
        }

        double harga;
        int estimasi;
        try {
            harga = Double.parseDouble(hargaStr);
            estimasi = Integer.parseInt(estimasiStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Harga dan estimasi harus berupa angka!");
            return;
        }

        Connection con = DBConnection.getConnection();
        if (con == null) return;

        String sql;
        if (idLayanan == null) {
            sql = "INSERT INTO tb_layanan (nama_layanan, harga_per_kg, estimasi_hari) VALUES (?, ?, ?)";
        } else {
            sql = "UPDATE tb_layanan SET nama_layanan = ?, harga_per_kg = ?, estimasi_hari = ? WHERE id_layanan = ?";
        }

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nama);
            ps.setDouble(2, harga);
            ps.setInt(3, estimasi);
            if (idLayanan != null) {
                ps.setInt(4, idLayanan);
            }
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data berhasil disimpan.");
            parent.loadData("");
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan data: " + ex.getMessage());
        }
    }
}
