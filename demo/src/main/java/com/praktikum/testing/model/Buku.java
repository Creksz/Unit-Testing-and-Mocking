package com.praktikum.testing.model;

import java.util.Objects;

public class Buku {
    private String isbn;
    private String judul;
    private String pengarang;
    private int jumlahTotal;
    private int jumlahTersedia;
    private double harga;

    public Buku() {
    }

    public Buku(String isbn, String judul, String pengarang, int jumlahTotal, double harga) {
        this.isbn = isbn;
        this.judul = judul;
        this.pengarang = pengarang;
        this.jumlahTotal = jumlahTotal;
        // Pada konstruktor, jumlahTersedia biasanya diinisialisasi sama dengan jumlahTotal
        this.jumlahTersedia = jumlahTotal; 
        this.harga = harga;
    }

    // Getters dan Setters
    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getPengarang() {
        return pengarang;
    }

    public void setPengarang(String pengarang) {
        this.pengarang = pengarang;
    }

    public int getJumlahTotal() {
        return jumlahTotal;
    }

    public void setJumlahTotal(int jumlahTotal) {
        this.jumlahTotal = jumlahTotal;
    }

    public int getJumlahTersedia() {
        return jumlahTersedia;
    }

    public void setJumlahTersedia(int jumlahTersedia) {
        this.jumlahTersedia = jumlahTersedia;
    }

    public double getHarga() {
        return harga;
    }

    public void setHarga(double harga) {
        this.harga = harga;
    }

    /**
     * Mengecek apakah buku masih tersedia (stok > 0).
     */
    public boolean isTersedia() {
        return jumlahTersedia > 0;
    }

    @Override
    public boolean equals(Object o) {
        // Baris 80: 'if (this == o) return true;' - Cek apakah objek sama
        if (this == o) return true;
        // Baris 81: 'if (o == null || getClass() != o.getClass()) return false;' - Cek null dan kelas
        if (o == null || getClass() != o.getClass()) return false;
        
        // Baris 82: 'Buku buku = (Buku) o;' - Lakukan casting
        Buku buku = (Buku) o;
        
        // Baris 83: 'return Objects.equals(isbn, buku.isbn);' - Membandingkan ISBN
        return Objects.equals(isbn, buku.isbn);
    }

    @Override
    public int hashCode() {
        // Baris 88: 'return Objects.hash(isbn);' - Menggunakan ISBN sebagai identifikasi unik
        return Objects.hash(isbn);
    }

    @Override
    public String toString() {
        // Perbaikan dilakukan di sini. Menggunakan format standar Java untuk keterbacaan.
        return "Buku{" +
                "isbn='" + isbn + '\'' +
                ", judul='" + judul + '\'' +
                ", pengarang='" + pengarang + '\'' +
                ", jumlahTotal=" + jumlahTotal +
                ", jumlahTersedia=" + jumlahTersedia +
                ", harga=" + harga +
                '}';
    }
}