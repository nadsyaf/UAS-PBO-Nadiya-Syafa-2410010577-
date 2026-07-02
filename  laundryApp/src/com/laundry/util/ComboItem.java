package com.laundry.util;

/**
 * Wrapper sederhana untuk item JComboBox yang menyimpan id dan label,
 * serta data tambahan (opsional) seperti harga.
 */
public class ComboItem {
    private final int id;
    private final String label;
    private final double extraValue; // contoh: harga_per_kg untuk layanan

    public ComboItem(int id, String label) {
        this(id, label, 0);
    }

    public ComboItem(int id, String label, double extraValue) {
        this.id = id;
        this.label = label;
        this.extraValue = extraValue;
    }

    public int getId() {
        return id;
    }

    public double getExtraValue() {
        return extraValue;
    }

    @Override
    public String toString() {
        return label;
    }
}
