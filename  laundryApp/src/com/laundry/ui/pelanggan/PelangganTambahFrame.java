package com.laundry.ui.pelanggan;

import com.laundry.db.DBConnection;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Frame Tambah/Ubah Data Pelanggan.
 * Jika idPelanggan == null maka mode TAMBAH, selain itu mode UBAH.
 */
public class PelangganTambahFrame extends JFrame {

    private final PelangganTampilFrame parent;
    private final Integer idPelanggan; // null = tambah baru

    private JTextField tNama, tAlamat, tHp;
    private JButton bSimpan, bBatal;

    public PelangganTambahFrame(PelangganTampilFrame parent, Integer idPelanggan) {
        this.parent = parent;
        this.idPelanggan = idPelanggan;

        setTitle(idPelanggan == null ? "Tambah Data Pelanggan" : "Ubah Data Pelanggan");
        setSize(400, 280);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        initComponents();

        if (idPelanggan != null) {
            loadDataById(idPelanggan);
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
        form.add(new JLabel("Nama Pelanggan"), gc);
        gc.gridx = 1;
        tNama = new JTextField(18);
        form.add(tNama, gc);

        gc.gridx = 0; gc.gridy = 1;
        form.add(new JLabel("Alamat"), gc);
        gc.gridx = 1;
        tAlamat = new JTextField(18);
        form.add(tAlamat, gc);

        gc.gridx = 0; gc.gridy = 2;
        form.add(new JLabel("No. HP"), gc);
        gc.gridx = 1;
        tHp = new JTextField(18);
        form.add(tHp, gc);

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
        String sql = "SELECT * FROM tb_pelanggan WHERE id_pelanggan = ?";
        Connection con = DBConnection.getConnection();
        if (con == null) return;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                tNama.setText(rs.getString("nama_pelanggan"));
                tAlamat.setText(rs.getString("alamat"));
                tHp.setText(rs.getString("no_hp"));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data: " + ex.getMessage());
        }
    }

    private void simpanData() {
        String nama = tNama.getText().trim();
        String alamat = tAlamat.getText().trim();
        String hp = tHp.getText().trim();

        if (nama.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama pelanggan wajib diisi!");
            return;
        }

        Connection con = DBConnection.getConnection();
        if (con == null) return;

        String sql;
        if (idPelanggan == null) {
            sql = "INSERT INTO tb_pelanggan (nama_pelanggan, alamat, no_hp) VALUES (?, ?, ?)";
        } else {
            sql = "UPDATE tb_pelanggan SET nama_pelanggan = ?, alamat = ?, no_hp = ? WHERE id_pelanggan = ?";
        }

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nama);
            ps.setString(2, alamat);
            ps.setString(3, hp);
            if (idPelanggan != null) {
                ps.setInt(4, idPelanggan);
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
