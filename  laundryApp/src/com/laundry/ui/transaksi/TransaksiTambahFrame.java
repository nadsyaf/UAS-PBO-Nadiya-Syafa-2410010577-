package com.laundry.ui.transaksi;

import com.laundry.db.DBConnection;
import com.laundry.util.ComboItem;
import com.laundry.util.Formatter;
import java.awt.*;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Frame Tambah/Ubah Data Transaksi.
 * Menampilkan dropdown Pelanggan dan Layanan (relasi antar tabel),
 * serta menghitung otomatis Total Harga = berat_kg x harga_per_kg.
 */
public class TransaksiTambahFrame extends JFrame {

    private final TransaksiTampilFrame parent;
    private final Integer idTransaksi; // null = tambah baru

    private JComboBox<ComboItem> cbPelanggan, cbLayanan;
    private JSpinner sTanggal;
    private JTextField tBerat;
    private JLabel lTotalHarga;
    private JComboBox<String> cbStatus;
    private JButton bSimpan, bBatal;

    public TransaksiTambahFrame(TransaksiTampilFrame parent, Integer idTransaksi) {
        this.parent = parent;
        this.idTransaksi = idTransaksi;

        setTitle(idTransaksi == null ? "Tambah Transaksi Laundry" : "Ubah Transaksi Laundry");
        setSize(430, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        initComponents();
        loadPelanggan();
        loadLayanan();

        if (idTransaksi != null) {
            loadDataById(idTransaksi);
        }
        hitungTotal();
    }

    private void initComponents() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBorder(new EmptyBorder(15, 20, 15, 20));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 5, 6, 5);
        gc.fill = GridBagConstraints.HORIZONTAL;

        int y = 0;

        gc.gridx = 0; gc.gridy = y;
        form.add(new JLabel("Pelanggan"), gc);
        gc.gridx = 1;
        cbPelanggan = new JComboBox<>();
        form.add(cbPelanggan, gc);
        y++;

        gc.gridx = 0; gc.gridy = y;
        form.add(new JLabel("Layanan"), gc);
        gc.gridx = 1;
        cbLayanan = new JComboBox<>();
        cbLayanan.addActionListener(e -> hitungTotal());
        form.add(cbLayanan, gc);
        y++;

        gc.gridx = 0; gc.gridy = y;
        form.add(new JLabel("Tanggal Masuk"), gc);
        gc.gridx = 1;
        SpinnerDateModel dateModel = new SpinnerDateModel(Calendar.getInstance().getTime(), null, null, Calendar.DAY_OF_MONTH);
        sTanggal = new JSpinner(dateModel);
        sTanggal.setEditor(new JSpinner.DateEditor(sTanggal, "yyyy-MM-dd"));
        form.add(sTanggal, gc);
        y++;

        gc.gridx = 0; gc.gridy = y;
        form.add(new JLabel("Berat (Kg)"), gc);
        gc.gridx = 1;
        tBerat = new JTextField(18);
        form.add(tBerat, gc);
        y++;

        gc.gridx = 0; gc.gridy = y;
        form.add(new JLabel("Status"), gc);
        gc.gridx = 1;
        cbStatus = new JComboBox<>(new String[]{"Proses", "Selesai", "Diambil"});
        form.add(cbStatus, gc);
        y++;

        gc.gridx = 0; gc.gridy = y;
        form.add(new JLabel("Total Harga"), gc);
        gc.gridx = 1;
        lTotalHarga = new JLabel("Rp 0");
        lTotalHarga.setFont(new Font("SansSerif", Font.BOLD, 14));
        form.add(lTotalHarga, gc);

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

        // Update total setiap kali berat diketik
        tBerat.addCaretListener(e -> hitungTotal());
    }

    private void loadPelanggan() {
        cbPelanggan.removeAllItems();
        String sql = "SELECT id_pelanggan, nama_pelanggan FROM tb_pelanggan ORDER BY nama_pelanggan";
        Connection con = DBConnection.getConnection();
        if (con == null) return;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                cbPelanggan.addItem(new ComboItem(rs.getInt("id_pelanggan"), rs.getString("nama_pelanggan")));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data pelanggan: " + ex.getMessage());
        }
    }

    private void loadLayanan() {
        cbLayanan.removeAllItems();
        String sql = "SELECT id_layanan, nama_layanan, harga_per_kg FROM tb_layanan ORDER BY nama_layanan";
        Connection con = DBConnection.getConnection();
        if (con == null) return;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String label = rs.getString("nama_layanan") + " (" + Formatter.toRupiah(rs.getDouble("harga_per_kg")) + "/kg)";
                cbLayanan.addItem(new ComboItem(rs.getInt("id_layanan"), label, rs.getDouble("harga_per_kg")));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data layanan: " + ex.getMessage());
        }
    }

    private void hitungTotal() {
        try {
            double berat = tBerat.getText().trim().isEmpty() ? 0 : Double.parseDouble(tBerat.getText().trim());
            ComboItem layanan = (ComboItem) cbLayanan.getSelectedItem();
            double harga = layanan != null ? layanan.getExtraValue() : 0;
            double total = berat * harga;
            lTotalHarga.setText(Formatter.toRupiah(total));
        } catch (NumberFormatException ex) {
            lTotalHarga.setText("Rp 0");
        }
    }

    private void loadDataById(int id) {
        String sql = "SELECT * FROM tb_transaksi WHERE id_transaksi = ?";
        Connection con = DBConnection.getConnection();
        if (con == null) return;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                selectComboById(cbPelanggan, rs.getInt("id_pelanggan"));
                selectComboById(cbLayanan, rs.getInt("id_layanan"));
                sTanggal.setValue(rs.getDate("tanggal_masuk"));
                tBerat.setText(String.valueOf(rs.getDouble("berat_kg")));
                cbStatus.setSelectedItem(rs.getString("status"));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data: " + ex.getMessage());
        }
    }

    private void selectComboById(JComboBox<ComboItem> combo, int id) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i).getId() == id) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private void simpanData() {
        ComboItem pelanggan = (ComboItem) cbPelanggan.getSelectedItem();
        ComboItem layanan = (ComboItem) cbLayanan.getSelectedItem();
        String beratStr = tBerat.getText().trim();

        if (pelanggan == null || layanan == null || beratStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field wajib diisi! (Tambahkan data pelanggan/layanan jika kosong)");
            return;
        }

        double berat;
        try {
            berat = Double.parseDouble(beratStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Berat harus berupa angka!");
            return;
        }

        double totalHarga = berat * layanan.getExtraValue();
        java.util.Date tglMasukUtil = (java.util.Date) sTanggal.getValue();
        Date tglMasuk = new Date(tglMasukUtil.getTime());
        String status = (String) cbStatus.getSelectedItem();

        Connection con = DBConnection.getConnection();
        if (con == null) return;

        String sql;
        if (idTransaksi == null) {
            sql = "INSERT INTO tb_transaksi (id_pelanggan, id_layanan, tanggal_masuk, berat_kg, total_harga, status) "
                    + "VALUES (?, ?, ?, ?, ?, ?)";
        } else {
            sql = "UPDATE tb_transaksi SET id_pelanggan = ?, id_layanan = ?, tanggal_masuk = ?, "
                    + "berat_kg = ?, total_harga = ?, status = ? WHERE id_transaksi = ?";
        }

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, pelanggan.getId());
            ps.setInt(2, layanan.getId());
            ps.setDate(3, tglMasuk);
            ps.setDouble(4, berat);
            ps.setDouble(5, totalHarga);
            ps.setString(6, status);
            if (idTransaksi != null) {
                ps.setInt(7, idTransaksi);
            }
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data transaksi berhasil disimpan.");
            parent.loadData("");
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan data: " + ex.getMessage());
        }
    }
}
