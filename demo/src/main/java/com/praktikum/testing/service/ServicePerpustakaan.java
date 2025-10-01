package com.praktikum.testing.service;

import com.praktikum.testing.model.Buku;
import com.praktikum.testing.model.Anggota;
import com.praktikum.testing.repository.RepositoryBuku;
import com.praktikum.testing.util.ValidationUtils;
import java.util.List;
import java.util.Optional;

public class ServicePerpustakaan {

    private final RepositoryBuku repositoryBuku;
    private final KalkulatorDenda KalkulatorDenda;

    public ServicePerpustakaan(RepositoryBuku repositoryBuku, KalkulatorDenda kalkulatorDenda) {
        this.repositoryBuku = repositoryBuku;
        this.KalkulatorDenda = kalkulatorDenda;
    }

    /**
     * Menambahkan buku baru ke sistem.
     */
    public boolean tambahBuku(Buku buku) {
        // 1. Validasi objek Buku
        if (!ValidationUtils.isValidBuku(buku)) {
            return false;
        }

        // 2. Cek apakah buku dengan ISBN yang sama sudah ada
        Optional<Buku> bukuExisting = repositoryBuku.cariByIsbn(buku.getIsbn());
        if (bukuExisting.isPresent()) {
            return false; // Buku sudah ada, tidak bisa ditambahkan sebagai baru
        }

        // 3. Simpan buku baru
        return repositoryBuku.simpan(buku);
    }

    /**
     * Menghapus buku dari sistem.
     */
    public boolean hapusBuku(String isbn) {
        // 1. Validasi ISBN
        if (!ValidationUtils.isValidIsbn(isbn)) {
            return false;
        }

        // 2. Cari buku
        Optional<Buku> bukuOpt = repositoryBuku.cariByIsbn(isbn);
        if (!bukuOpt.isPresent()) {
            return false; // Buku tidak ditemukan
        }
        
        Buku buku = bukuOpt.get();

        // 3. Cek apakah ada salinan yang sedang dipinjam
        // Logika: Jika jumlah tersedia tidak sama dengan jumlah total, berarti ada yang sedang dipinjam
        if (buku.getJumlahTersedia() != buku.getJumlahTotal()) {
            // Tidak bisa dihapus karena ada unit yang sedang dipinjam
            return false; 
        }

        // 4. Hapus buku
        return repositoryBuku.hapus(isbn);
    }

    // --- Metode Pencarian ---

    public Optional<Buku> cariBukuByIsbn(String isbn) {
        if (!ValidationUtils.isValidIsbn(isbn)) {
            return Optional.empty();
        }
        return repositoryBuku.cariByIsbn(isbn);
    }

    public List<Buku> cariBukuByJudul(String judul) {
        return repositoryBuku.cariByJudul(judul);
    }

    public List<Buku> cariBukuByPengarang(String pengarang) {
        return repositoryBuku.cariByPengarang(pengarang);
    }

    /**
     * Mengecek ketersediaan buku berdasarkan ISBN.
     */
    public boolean bukuTersedia(String isbn) {
        Optional<Buku> bukuOpt = repositoryBuku.cariByIsbn(isbn);
        return bukuOpt.isPresent() && bukuOpt.get().isTersedia();
    }

    /**
     * Mendapatkan jumlah stok tersedia untuk Buku tertentu.
     */
    public int getJumlahTersedia(String isbn) {
        // Menggunakan Optional.map dan orElse untuk kemudahan
        return repositoryBuku.cariByIsbn(isbn)
                .map(Buku::getJumlahTersedia)
                .orElse(0); // Kembalikan 0 jika buku tidak ditemukan
    }

    // --- Metode Transaksi Pinjam/Kembali ---
    
    /**
     * Proses peminjaman buku oleh anggota.
     */
    public boolean pinjamBuku(String isbn, Anggota anggota) {
        // 1. Validasi dasar
        if (!ValidationUtils.isValidIsbn(isbn) || !ValidationUtils.isValidAnggota(anggota) || !anggota.isAktif()) {
            return false;
        }

        // 2. Cek apakah anggota masih boleh pinjam (batas pinjaman)
        if (!anggota.bolehPinjamLagi()) {
            return false; 
        }

        // 3. Cek ketersediaan buku
        Optional<Buku> bukuOpt = repositoryBuku.cariByIsbn(isbn);
        if (!bukuOpt.isPresent() || !bukuOpt.get().isTersedia()) {
            return false; 
        }

        Buku buku = bukuOpt.get();
        
        // 4. Update jumlah tersedia (stok berkurang 1)
        int jumlahTersediaBaru = buku.getJumlahTersedia() - 1;
        boolean updateBerhasil = repositoryBuku.updateJumlahTersedia(isbn, jumlahTersediaBaru);

        if (updateBerhasil) {
            // 5. Update data anggota (tambahkan buku ke daftar pinjaman)
            anggota.tambahBukuDipinjam(isbn);
            return true;
        }

        return false;
    }

    /**
     * Proses pengembalian buku oleh anggota.
     */
    public boolean kembalikanBuku(String isbn, Anggota anggota) {
        // 1. Validasi dasar
        if (!ValidationUtils.isValidIsbn(isbn) || anggota == null) {
            return false;
        }

        // 2. Cek apakah anggota memang meminjam buku ini
        if (!anggota.getIdBukuDipinjam().contains(isbn)) {
            return false;
        }

        // 3. Cari buku (untuk mendapatkan jumlah tersedia saat ini)
        Optional<Buku> bukuOpt = repositoryBuku.cariByIsbn(isbn);
        if (!bukuOpt.isPresent()) {
            return false; // Buku tidak ditemukan di repositori (harusnya tidak terjadi jika data konsisten)
        }

        Buku buku = bukuOpt.get();

        // 4. Update jumlah tersedia (stok bertambah 1)
        int jumlahTersediaBaru = buku.getJumlahTersedia() + 1;
        boolean updateBerhasil = repositoryBuku.updateJumlahTersedia(isbn, jumlahTersediaBaru);
        
        if (updateBerhasil) {
            // 5. Update data anggota (hapus buku dari daftar pinjaman)
            anggota.hapusBukuDipinjam(isbn);
            
            // Catatan: Logika pembaruan objek Peminjaman dan perhitungan denda (menggunakan kalkulatorDenda)
            // tidak terlihat di sini, namun harus ditambahkan pada implementasi penuh.
            
            return true;
        }

        return false;
    }
}