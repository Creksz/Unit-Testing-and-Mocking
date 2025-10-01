package com.praktikum.testing.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
// Tidak ada di kode asli, tapi sebaiknya diimport jika menggunakan equals/hashCode standar

public class Peminjaman {
    private String idPeminjaman;
    private String idAnggota;
    private String isbnBuku;
    private LocalDate tanggalPinjam;
    private LocalDate tanggalJatuhTempo;
    private LocalDate tanggalKembali; // tanggalKembali bisa null jika belum dikembalikan
    private boolean sudahDikembalikan;

    public Peminjaman() {
    }

    public Peminjaman(String idPeminjaman, String idAnggota, String isbnBuku, 
                      LocalDate tanggalPinjam, LocalDate tanggalJatuhTempo) {
        this.idPeminjaman = idPeminjaman;
        this.idAnggota = idAnggota;
        this.isbnBuku = isbnBuku;
        this.tanggalPinjam = tanggalPinjam;
        this.tanggalJatuhTempo = tanggalJatuhTempo;
        this.sudahDikembalikan = false;
    }

    // Getters dan Setters
    public String getIdPeminjaman() {
        return idPeminjaman;
    }

    public void setIdPeminjaman(String idPeminjaman) {
        this.idPeminjaman = idPeminjaman;
    }

    public String getIdAnggota() {
        return idAnggota;
    }

    public void setIdAnggota(String idAnggota) {
        this.idAnggota = idAnggota;
    }

    public String getIsbnBuku() {
        return isbnBuku;
    }

    public void setIsbnBuku(String isbnBuku) {
        this.isbnBuku = isbnBuku;
    }

    public LocalDate getTanggalPinjam() {
        return tanggalPinjam;
    }

    public void setTanggalPinjam(LocalDate tanggalPinjam) {
        this.tanggalPinjam = tanggalPinjam;
    }

    public LocalDate getTanggalJatuhTempo() {
        return tanggalJatuhTempo;
    }

    public void setTanggalJatuhTempo(LocalDate tanggalJatuhTempo) {
        this.tanggalJatuhTempo = tanggalJatuhTempo;
    }

    public LocalDate getTanggalKembali() {
        return tanggalKembali;
    }

    public void setTanggalKembali(LocalDate tanggalKembali) {
        this.tanggalKembali = tanggalKembali;
    }

    public boolean isSudahDikembalikan() {
        return sudahDikembalikan;
    }

    public void setSudahDikembalikan(boolean sudahDikembalikan) {
        this.sudahDikembalikan = sudahDikembalikan;
    }

    /**
     * Mengecek apakah peminjaman terhitung terlambat.
     */
    public boolean isTerlambat() {
        if (sudahDikembalikan) {
            // Jika sudah dikembalikan, cek apakah tanggalKembali > tanggalJatuhTempo
            return tanggalKembali.isAfter(tanggalJatuhTempo);
        }
        // Jika belum dikembalikan, cek apakah hari ini > tanggalJatuhTempo
        return LocalDate.now().isAfter(tanggalJatuhTempo);
    }

    /**
     * Menghitung jumlah hari keterlambatan.
     */
    public long getHariTerlambat() {
        if (sudahDikembalikan) {
            // Jika sudah dikembalikan: hitung selisih jika tanggalKembali > tanggalJatuhTempo, jika tidak 0
            return tanggalKembali.isAfter(tanggalJatuhTempo)
                    ? ChronoUnit.DAYS.between(tanggalJatuhTempo, tanggalKembali) : 0;
        }

        // Jika belum dikembalikan: hitung selisih jika hari ini > tanggalJatuhTempo, jika tidak 0
        LocalDate hari_ini = LocalDate.now();
        return hari_ini.isAfter(tanggalJatuhTempo)
                ? ChronoUnit.DAYS.between(tanggalJatuhTempo, hari_ini) : 0;
    }

    /**
     * Menghitung total durasi peminjaman (sampai dikembalikan atau sampai hari ini).
     */
    public long getDurasiPeminjaman() {
        // Tentukan tanggal akhir: jika sudah dikembalikan gunakan tanggalKembali, jika tidak gunakan hari ini
        LocalDate tanggalAkhir = sudahDikembalikan ? tanggalKembali : LocalDate.now();
        
        // Hitung selisih antara tanggalPinjam dan tanggalAkhir
        return ChronoUnit.DAYS.between(tanggalPinjam, tanggalAkhir);
    }
    
    // Anda mungkin ingin menambahkan implementasi equals dan hashCode
    /*
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Peminjaman that = (Peminjaman) o;
        return Objects.equals(idPeminjaman, that.idPeminjaman);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idPeminjaman);
    }
    */
}