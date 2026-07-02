package com.laundry.ui.layanan;

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
 * Frame Tampil Data Layanan Laundry (jenis layanan & harga per kg).
 * Tabel ini merupakan TAMBAHAN di luar ketentuan minimum modul.
 */
public class LayananTampilFrame extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private JTextField eCari;
    private JButton bTambah, bUbah, bHapus, bCari, bBatal, bTutup;

    public LayananTampilFrame() {
        setTitle("Data Layanan Laundry");
        setSize(650, 420);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initComponents();
        loadData("");
    }

    private void initComponents() {
        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Cari Nama Layanan:"));
        eCari = new JTextField(18);
        searchPanel.add(eCari);
        bCari = new JButton("Cari");
        bBatal = new JButton("Reset");
        searchPanel.add(bCari);
        searchPanel.add(bBatal);
        main.add(searchPanel, BorderLayout.NORTH);

        model = new DefaultTableModel(
                new Object[]{"ID", "Nama Layanan", "Harga/Kg", "Estimasi (Hari)"}, 0) {
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
            LayananTambahFrame f = new LayananTambahFrame(this, null);
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
            LayananTambahFrame f = new LayananTambahFrame(this, id);
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
            String nama = model.getValueAt(row, 1).toString();
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Yakin ingin menghapus layanan \"" + nama + "\"?",
                    "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                hapusData(id);
            }
        });
    }

    public void loadData(String keyword) {
        model.setRowCount(0);
        String sql = "SELECT * FROM tb_layanan WHERE nama_layanan LIKE ? ORDER BY id_layanan";
        Connection con = DBConnection.getConnection();
        if (con == null) return;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id_layanan"),
                        rs.getString("nama_layanan"),
                        Formatter.toRupiah(rs.getDouble("harga_per_kg")),
                        rs.getInt("estimasi_hari")
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data: " + ex.getMessage());
        }
    }

    private void hapusData(int id) {
        String sql = "DELETE FROM tb_layanan WHERE id_layanan = ?";
        Connection con = DBConnection.getConnection();
        if (con == null) return;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data berhasil dihapus.");
            loadData("");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal menghapus data (mungkin masih terpakai di transaksi): "
                    + ex.getMessage());
        }
    }
}
