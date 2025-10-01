package com.praktikum.testing.util;

import com.praktikum.testing.model.Buku;
import com.praktikum.testing.model.Anggota;

public class ValidationUtils {

    /**
     * Validasi email sederhana.
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        // Validasi sederhana: karakter alfanumerik, titik, atau dash, diikuti @,
        // diikuti alfanumerik, titik, diikuti 2-4 karakter huruf.
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,4}$";
        return email.matches(emailRegex);
    }

    /**
     * Validasi nomor telepon (Format Indonesia).
     */
    public static boolean isValidNomorTelepon(String telepon) {
        if (telepon == null || telepon.trim().isEmpty()) {
            return false;
        }

        // Hapus semua spasi dan tanda hubung
        String teleponBersih = telepon.replaceAll("[\\s\\-]", "");

        // Telepon Indonesia harus dimulai dengan 08 atau +628 dan memiliki 10-13 digit
        // Pola: dimulai 08 atau +628, diikuti 8-11 digit, total 10-13 digit.
        return teleponBersih.matches("(08|\\+628)[0-9]{8,11}$");
    }

    /**
     * Validasi ISBN sederhana (10 atau 13 digit).
     */
    public static boolean isValidIsbn(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            return false;
        }

        // Hapus tanda hubung dan spasi
        String isbnBersih = isbn.replaceAll("[\\s\\-]", "");

        // ISBN harus 10 atau 13 digit dan hanya terdiri dari angka
        return isbnBersih.matches("[0-9]{10}") || isbnBersih.matches("[0-9]{13}");
    }

    /**
     * Validasi objek Buku.
     */
    public static boolean isValidBuku(Buku buku) {
        if (buku == null) {
            return false;
        }

        return isValidIsbn(buku.getIsbn()) &&
               isValidString(buku.getJudul()) &&
               isValidString(buku.getPengarang()) &&
               buku.getJumlahTotal() >= 0 &&
               buku.getJumlahTersedia() >= 0 &&
               buku.getJumlahTersedia() <= buku.getJumlahTotal() &&
               isAngkaNonNegatif(buku.getHarga()); // Menggunakan method isAngkaNonNegatif
    }

    /**
     * Validasi objek Anggota.
     */
    public static boolean isValidAnggota(Anggota anggota) {
        if (anggota == null) {
            return false;
        }

        return isValidString(anggota.getIdAnggota()) &&
               isValidString(anggota.getNama()) &&
               isValidEmail(anggota.getEmail()) &&
               isValidNomorTelepon(anggota.getTelepon()) &&
               anggota.getTipeAnggota() != null;
    }

    /**
     * Validasi String (tidak null dan tidak kosong setelah trim).
     */
    public static boolean isValidString(String str) {
        return str != null && !str.trim().isEmpty();
    }

    /**
     * Validasi angka positif (angka > 0).
     */
    public static boolean isAngkaPositif(double angka) {
        return angka > 0;
    }

    /**
     * Validasi angka non-negatif (angka >= 0).
     */
    public static boolean isAngkaNonNegatif(double angka) {
        return angka >= 0;
    }
}