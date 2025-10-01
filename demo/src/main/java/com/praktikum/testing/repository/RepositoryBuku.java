package com.praktikum.testing.repository;

import com.praktikum.testing.model.Buku;
import java.util.List;
import java.util.Optional;

public interface RepositoryBuku {

    /**
     * Menyimpan objek Buku baru atau memperbarui yang sudah ada.
     * @param buku objek Buku yang akan disimpan
     * @return true jika operasi berhasil
     */
    boolean simpan(Buku buku);

    /**
     * Mencari Buku berdasarkan ISBN (nomor unik).
     * @param isbn ISBN Buku
     * @return Optional<Buku> jika ditemukan, Optional.empty() jika tidak
     */
    Optional<Buku> cariByIsbn(String isbn);

    /**
     * Mencari Buku berdasarkan judul.
     * @param judul Judul Buku
     * @return List<Buku> yang sesuai
     */
    List<Buku> cariByJudul(String judul);

    /**
     * Mencari Buku berdasarkan pengarang.
     * @param pengarang Nama Pengarang
     * @return List<Buku> yang sesuai
     */
    List<Buku> cariByPengarang(String pengarang);

    /**
     * Menghapus Buku berdasarkan ISBN.
     * @param isbn ISBN Buku yang akan dihapus
     * @return true jika berhasil dihapus
     */
    boolean hapus(String isbn);

    /**
     * Memperbarui jumlah stok tersedia untuk Buku tertentu.
     * @param isbn ISBN Buku
     * @param jumlahTersediaBaru Jumlah stok tersedia yang baru
     * @return true jika operasi pembaruan berhasil
     */
    boolean updateJumlahTersedia(String isbn, int jumlahTersediaBaru);

    /**
     * Mengambil semua objek Buku yang ada.
     * @return List<Buku> semua buku
     */
    List<Buku> cariSemua();
}