package com.praktikum.testing.service;

import com.praktikum.testing.model.Peminjaman;
import com.praktikum.testing.model.Anggota;

public class KalkulatorDenda {

    // Tarif denda harian per tipe (dalam Rupiah)
    private static final double TARIF_DENDA_MAHASISWA = 1000.0;
    private static final double TARIF_DENDA_DOSEN = 1500.0;
    private static final double TARIF_DENDA_UMUM = 1500.0;

    // Batas maksimal denda
    private static final double DENDA_MAX_MAHASISWA = 50000.0;
    private static final double DENDA_MAX_DOSEN = 75000.0;
    private static final double DENDA_MAX_UMUM = 75000.0;

    /**
     * Menghitung total denda yang harus dibayar untuk peminjaman tertentu.
     * Denda memiliki batas maksimal.
     */
    public static double hitungDenda(Peminjaman peminjaman, Anggota anggota) {
        if (peminjaman == null || anggota == null) {
            throw new IllegalArgumentException("Peminjaman dan Anggota tidak boleh null!");
        }

        // 1. Cek apakah terlambat
        if (!peminjaman.isTerlambat()) {
            return 0.0;
        }

        // 2. Hitung hari terlambat (biasanya hari pertama terlambat sudah dihitung)
        long hariTerlambat = peminjaman.getHariTerlambat();

        // Asumsi: Denda tidak berlaku untuk keterlambatan kurang dari 3 hari (contoh kebijakan)
        // Note: Bagian ini tampaknya spesifik pada kebijakan Anda (if (hariTerlambat <= 3) return 0.0;)
        if (hariTerlambat <= 3) {
            return 0.0;
        }

        // 3. Ambil tarif dan hitung denda kotor
        double tarifHarian = getTarifDendaHarian(anggota.getTipeAnggota());
        double totalDenda = tarifHarian * hariTerlambat;
        double dendaMax = getDendaMaximal(anggota.getTipeAnggota());

        // 4. Batasi denda dengan nilai maksimal
        return Math.min(totalDenda, dendaMax);
    }

    /**
     * Mendapatkan tarif denda harian berdasarkan tipe anggota.
     */
    public static double getTarifDendaHarian(Anggota.TipeAnggota tipeAnggota) {
        if (tipeAnggota == null) {
            throw new IllegalArgumentException("Tipe anggota tidak boleh null!");
        }

        switch (tipeAnggota) {
            case MAHASISWA:
                return TARIF_DENDA_MAHASISWA;
            case DOSEN:
                return TARIF_DENDA_DOSEN;
            case UMUM:
                return TARIF_DENDA_UMUM;
            default:
                throw new IllegalArgumentException("Tipe anggota tidak dikenal: " + tipeAnggota);
        }
    }

    /**
     * Mendapatkan batas maksimal denda berdasarkan tipe anggota.
     */
    public static double getDendaMaximal(Anggota.TipeAnggota tipeAnggota) {
        if (tipeAnggota == null) {
            throw new IllegalArgumentException("Tipe anggota tidak boleh null!");
        }

        switch (tipeAnggota) {
            case MAHASISWA:
                return DENDA_MAX_MAHASISWA;
            case DOSEN:
                return DENDA_MAX_DOSEN;
            case UMUM:
                return DENDA_MAX_UMUM;
            default:
                throw new IllegalArgumentException("Tipe anggota tidak dikenal: " + tipeAnggota);
        }
    }
    
    /**
     * Mengecek apakah peminjaman memiliki potensi denda.
     */
    public static boolean adaDenda(Peminjaman peminjaman) {
        return peminjaman != null && peminjaman.isTerlambat() && peminjaman.getHariTerlambat() > 0;
    }

    /**
     * Mendapatkan deskripsi denda berdasarkan jumlahnya.
     */
    public static String getDeskripsiDenda(double jumlahDenda) {
        if (jumlahDenda <= 0) {
            return "Tidak ada denda";
        } else if (jumlahDenda < 10000) {
            return "Denda ringan";
        } else if (jumlahDenda < 50000) {
            return "Denda sedang";
        } else {
            return "Denda berat";
        }
    }
}