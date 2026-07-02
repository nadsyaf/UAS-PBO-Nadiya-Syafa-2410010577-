package com.laundry.ui.transaksi;

import com.laundry.db.DBConnection;
import com.laundry.util.Formatter;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

/**
 * Frame Tampil Data Transaksi Laundry.
 * Tabel ini BERELASI dengan tb_pelanggan dan tb_layanan (JOIN query),
 * sesuai ketentuan tugas "tabel yang berelasi".
 */
public class TransaksiTampilFrame extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private JTextField eCari;
    private JButton bTambah, bUbah, bHapus, bCari, bBatal, bTutup;

    public TransaksiTampilFrame() {
        setTitle("Data Transaksi Laundry");
        setSize(900, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initComponents();
        loadData("");
    }

    private void initComponents() {
        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Cari Nama Pelanggan:"));
        eCari = new JTextField(18);
        searchPanel.add(eCari);
        bCari = new JButton("Cari");
        bBatal = new JButton("Reset");
        searchPanel.add(bCari);
        searchPanel.add(bBatal);
        main.add(searchPanel, BorderLayout.NORTH);

        model = new DefaultTableModel(
                new Object[]{"ID", "Pelanggan", "Layanan", "Tgl Masuk", "Berat (Kg)", "Total Harga", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        table = new JTable(model);
        table.setRowHeight(24);
        main.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bTambah = new JButton("Tambah");
        bUbah = new JButton("Ubah");
        bHapus = new JButton("Hapus");
        bTutup = new JButton("Tutup");
        btnPanel.add(bTambah);
        btnPanel.add(bUbah);
        btnPanel.add(bHapus);
        btnPanel.add(bTutup);
        main.add(btnPanel, BorderLayout.SOUTH);

        add(main);
        setListener();
    }

    private void setListener() {
        bTutup.addActionListener(e -> dispose());
        bCari.addActionListener(e -> loadData(eCari.getText().trim()));
        bBatal.addActionListener(e -> {
            eCari.setText("");
            loadData("");
        });

        bTambah.addActionListener(e -> {
            TransaksiTambahFrame f = new TransaksiTambahFrame(this, null);
            f.setLocationRelativeTo(this);
            f.setVisible(true);
        });

        bUbah.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Pilih data yang ingin diubah terlebih dahulu!");
                return;
            }
            int id = (int) model.getValueAt(row, 0);
            TransaksiTambahFrame f = new TransaksiTambahFrame(this, id);
            f.setLocationRelativeTo(this);
            f.setVisible(true);
        });

        bHapus.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Pilih data yang ingin dihapus terlebih dahulu!");
                return;
            }
            int id = (int) model.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Yakin ingin menghapus transaksi #" + id + "?",
                    "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                hapusData(id);
            }
        });
    }

    public void loadData(String keyword) {
        model.setRowCount(0);
        String sql = "SELECT t.id_transaksi, p.nama_pelanggan, l.nama_layanan, "
                + "t.tanggal_masuk, t.berat_kg, t.total_harga, t.status "
                + "FROM tb_transaksi t "
                + "JOIN tb_pelanggan p ON t.id_pelanggan = p.id_pelanggan "
                + "JOIN tb_layanan l ON t.id_layanan = l.id_layanan "
                + "WHERE p.nama_pelanggan LIKE ? "
                + "ORDER BY t.id_transaksi DESC";
        Connection con = DBConnection.getConnection();
        if (con == null) return;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id_transaksi"),
                        rs.getString("nama_pelanggan"),
                        rs.getString("nama_layanan"),
                        rs.getDate("tanggal_masuk"),
                        rs.getDouble("berat_kg"),
                        Formatter.toRupiah(rs.getDouble("total_harga")),
                        rs.getString("status")
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data: " + ex.getMessage());
        }
    }

    private void hapusData(int id) {
        String sql = "DELETE FROM tb_transaksi WHERE id_transaksi = ?";
        Connection con = DBConnection.getConnection();
        if (con == null) return;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data berhasil dihapus.");
            loadData("");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal menghapus data: " + ex.getMessage());
        }
    }
}
