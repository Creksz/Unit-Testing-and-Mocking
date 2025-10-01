package com.praktikum.testing.repository;

import com.praktikum.testing.model.Buku;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Mock implementation dari RepositoryBuku untuk keperluan testing
 * Menggunakan in-memory storage dengan ConcurrentHashMap
 *
 * Catatan: Ini hanya untuk demo! Dalam test yang sebenarnya,
 * sebaiknya gunakan Mockito untuk mocking repository.
 */
public class MockRepositoryBuku implements RepositoryBuku {

    // Map untuk menyimpan buku, key: ISBN (String), value: Buku
    private final Map<String, Buku> bukuMap = new ConcurrentHashMap<>();

    @Override
    public boolean simpan(Buku buku) {
        if (buku == null || buku.getIsbn() == null) {
            return false;
        }

        // Simulasi operasi simpan ke database (put/update)
        bukuMap.put(buku.getIsbn(), buku);
        return true;
    }

    @Override
    public Optional<Buku> cariByIsbn(String isbn) {
        if (isbn == null) {
            return Optional.empty();
        }

        Buku buku = bukuMap.get(isbn);
        // Mengembalikan Optional.ofNullable untuk menangani kasus buku tidak ditemukan
        return Optional.ofNullable(buku);
    }

    @Override
    public List<Buku> cariByJudul(String judul) {
        if (judul == null || judul.trim().isEmpty()) {
            return new ArrayList<>();
        }

        return bukuMap.values().stream()
                .filter(buku -> buku.getJudul().toLowerCase().trim()
                        .contains(judul.toLowerCase().trim()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Buku> cariByPengarang(String pengarang) {
        if (pengarang == null || pengarang.trim().isEmpty()) {
            return new ArrayList<>();
        }

        return bukuMap.values().stream()
                .filter(buku -> buku.getPengarang().toLowerCase().trim()
                        .contains(pengarang.toLowerCase().trim()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean hapus(String isbn) {
        if (isbn == null) {
            return false;
        }

        Buku bukuDihapus = bukuMap.remove(isbn);
        // Jika remove mengembalikan objek (berhasil dihapus), kembalikan true
        return bukuDihapus != null;
    }

    @Override
    public boolean updateJumlahTersedia(String isbn, int jumlahTersediaBaru) {
        if (isbn == null || jumlahTersediaBaru < 0) {
            return false;
        }

        Buku buku = bukuMap.get(isbn);
        if (buku == null) {
            return false;
        }

        // Cek apakah jumlah tersedia baru valid (tidak melebihi jumlah total)
        if (jumlahTersediaBaru > buku.getJumlahTotal()) {
            return false;
        }

        buku.setJumlahTersedia(jumlahTersediaBaru);
        return true;
    }

    @Override
    public List<Buku> cariSemua() {
        // Mengembalikan salinan (ArrayList baru) dari semua nilai dalam Map
        return new ArrayList<>(bukuMap.values());
    }

    // --- Utility methods untuk testing/demo ---

    public void bersihkan() {
        bukuMap.clear();
    }

    public int ukuran() {
        return bukuMap.size();
    }

    public boolean mengandung(String isbn) {
        return bukuMap.containsKey(isbn);
    }
}