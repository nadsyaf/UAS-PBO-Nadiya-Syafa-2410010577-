CREATE DATABASE IF NOT EXISTS laundry_db;
USE laundry_db;

CREATE TABLE IF NOT EXISTS tb_users (
    id_user INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(50) NOT NULL,
    nama_lengkap VARCHAR(100) NOT NULL,
    level VARCHAR(20) NOT NULL DEFAULT 'Admin'
);

INSERT INTO tb_users (username, password, nama_lengkap, level)
VALUES ('admin', 'admin123', 'Administrator', 'Admin');

CREATE TABLE IF NOT EXISTS tb_pelanggan (
    id_pelanggan INT AUTO_INCREMENT PRIMARY KEY,
    nama_pelanggan VARCHAR(100) NOT NULL,
    alamat VARCHAR(200),
    no_hp VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS tb_layanan (
    id_layanan INT AUTO_INCREMENT PRIMARY KEY,
    nama_layanan VARCHAR(100) NOT NULL,
    harga_per_kg DECIMAL(10,2) NOT NULL,
    estimasi_hari INT NOT NULL DEFAULT 1
);

INSERT INTO tb_layanan (nama_layanan, harga_per_kg, estimasi_hari) VALUES
('Cuci Reguler', 5000, 3),
('Cuci Express (1 Hari)', 8000, 1),
('Cuci Setrika Saja', 4000, 2),
('Cuci Selimut/Bed Cover', 10000, 3);

CREATE TABLE IF NOT EXISTS tb_transaksi (
    id_transaksi INT AUTO_INCREMENT PRIMARY KEY,
    id_pelanggan INT NOT NULL,
    id_layanan INT NOT NULL,
    tanggal_masuk DATE NOT NULL,
    berat_kg DECIMAL(6,2) NOT NULL,
    total_harga DECIMAL(12,2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'Proses',
    CONSTRAINT fk_transaksi_pelanggan FOREIGN KEY (id_pelanggan)
        REFERENCES tb_pelanggan(id_pelanggan) ON DELETE CASCADE,
    CONSTRAINT fk_transaksi_layanan FOREIGN KEY (id_layanan)
        REFERENCES tb_layanan(id_layanan) ON DELETE CASCADE
);