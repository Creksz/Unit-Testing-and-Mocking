package com.praktikum.testing.service;

import com.praktikum.testing.model.Anggota;
import com.praktikum.testing.model.Buku;
import com.praktikum.testing.repository.RepositoryBuku;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test Service Perpustakaan")
public class ServicePerpustakaanTest {

    @Mock
    private RepositoryBuku mockRepositoryBuku;

    @Mock
    private KalkulatorDenda mockKalkulatorDenda; // KalkulatorDenda tidak digunakan di tes ini

    private ServicePerpustakaan servicePerpustakaan;
    private Buku bukuTest;
    private Anggota anggotaTest;

    @BeforeEach
    void setUp() {
        // Inisialisasi service dengan mock
        servicePerpustakaan = new ServicePerpustakaan(mockRepositoryBuku, mockKalkulatorDenda);

        // Objek buku standar untuk testing
        bukuTest = new Buku("1234567890", "Pemrograman Java", "John Doe", 5, 150000.0);
        
        // Objek anggota standar untuk testing (Mahasiswa)
        anggotaTest = new Anggota("A001", "John Student", "john@student.ac.id",
                                  "081234567890", Anggota.TipeAnggota.MAHASISWA);
    }

    // --- Test Tambah Buku ---

    @Test
    @DisplayName("Tambah buku berhasil ketika data valid dan buku belum ada")
    void testTambahBukuBerhasil() {
        // Arrange
        // 1. Ketika dicari, kembalikan kosong (buku belum ada)
        when(mockRepositoryBuku.cariByIsbn("1234567890")).thenReturn(Optional.empty());
        // 2. Ketika disimpan, kembalikan true (berhasil disimpan)
        when(mockRepositoryBuku.simpan(bukuTest)).thenReturn(true);

        // Act
        boolean hasil = servicePerpustakaan.tambahBuku(bukuTest);

        // Assert
        assertTrue(hasil, "Harus berhasil menambah buku");
        // Verifikasi bahwa pencarian dan penyimpanan dipanggil tepat 1 kali
        verify(mockRepositoryBuku).cariByIsbn("1234567890");
        verify(mockRepositoryBuku).simpan(bukuTest);
    }

    @Test
    @DisplayName("Tambah buku gagal ketika buku sudah ada")
    void testTambahBukuGagalBukuSudahAda() {
        // Arrange
        // Ketika dicari, kembalikan Optional berisi buku (buku sudah ada)
        when(mockRepositoryBuku.cariByIsbn("1234567890")).thenReturn(Optional.of(bukuTest));

        // Act
        boolean hasil = servicePerpustakaan.tambahBuku(bukuTest);

        // Assert
        assertFalse(hasil, "Tidak boleh berhasil menambah buku yang sudah ada");
        // Verifikasi bahwa simpan() tidak pernah dipanggil
        verify(mockRepositoryBuku, never()).simpan(any(Buku.class));
    }

    @Test
    @DisplayName("Tambah buku gagal ketika data tidak valid")
    void testTambahBukuGagalKetikaDataTidakValid() {
        // Arrange
        // Buat objek buku yang tidak valid (ISBN 3 digit, jumlah negatif, harga negatif)
        Buku bukuTidakValid = new Buku("123", "", "Pengarang", 0, -100.0);

        // Act
        boolean hasil = servicePerpustakaan.tambahBuku(bukuTidakValid);

        // Assert
        assertFalse(hasil, "Tidak boleh menambah buku dengan data tidak valid");
        // Verifikasi bahwa tidak ada interaksi dengan mockRepositoryBuku
        verifyNoInteractions(mockRepositoryBuku);
    }

    // --- Test Hapus Buku ---

    @Test
    @DisplayName("Hapus buku berhasil ketika tidak ada yang dipinjam")
    void testHapusBukuBerhasil() {
        // Arrange
        bukuTest.setJumlahTersedia(5); // Semua salinan tersedia (5 total, 5 tersedia)
        // 1. Ketika dicari, kembalikan Optional berisi buku
        when(mockRepositoryBuku.cariByIsbn("1234567890")).thenReturn(Optional.of(bukuTest));
        // 2. Ketika dihapus, kembalikan true
        when(mockRepositoryBuku.hapus("1234567890")).thenReturn(true);

        // Act
        boolean hasil = servicePerpustakaan.hapusBuku("1234567890");

        // Assert
        assertTrue(hasil, "Harus berhasil menghapus buku");
        verify(mockRepositoryBuku).cariByIsbn("1234567890");
        verify(mockRepositoryBuku).hapus("1234567890");
    }

    @Test
    @DisplayName("Hapus buku gagal ketika ada yang dipinjam")
    void testHapusBukuGagalAdaYangDipinjam() {
        // Arrange
        bukuTest.setJumlahTersedia(3); // Ada 2 yang dipinjam (5 total - 3 tersedia = 2 dipinjam)
        when(mockRepositoryBuku.cariByIsbn("1234567890")).thenReturn(Optional.of(bukuTest));

        // Act
        boolean hasil = servicePerpustakaan.hapusBuku("1234567890");

        // Assert
        assertFalse(hasil, "Tidak boleh menghapus buku yang sedang dipinjam");
        // Verifikasi bahwa hapus tidak pernah dipanggil
        verify(mockRepositoryBuku).cariByIsbn("1234567890");
        verify(mockRepositoryBuku, never()).hapus(anyString());
    }
    
    // --- Test Cari Buku ---

    @Test
    @DisplayName("Cari buku by ISBN berhasil")
    void testCariBukuByIsbnBerhasil() {
        // Arrange
        when(mockRepositoryBuku.cariByIsbn("1234567890")).thenReturn(Optional.of(bukuTest));

        // Act
        Optional<Buku> hasil = servicePerpustakaan.cariBukuByIsbn("1234567890");

        // Assert
        assertTrue(hasil.isPresent(), "Harus menemukan buku");
        assertEquals("Pemrograman Java", hasil.get().getJudul());
        verify(mockRepositoryBuku).cariByIsbn("1234567890");
    }

    @Test
    @DisplayName("Cari buku by judul berhasil")
    void testCariBukuByJudulBerhasil() {
        // Arrange
        List<Buku> daftarBuku = Arrays.asList(bukuTest);
        when(mockRepositoryBuku.cariByJudul("Pemrograman Java")).thenReturn(daftarBuku);

        // Act
        List<Buku> hasil = servicePerpustakaan.cariBukuByJudul("Pemrograman Java");

        // Assert
        assertEquals(1, hasil.size());
        assertEquals("Pemrograman Java", hasil.get(0).getJudul());
        verify(mockRepositoryBuku).cariByJudul("Pemrograman Java");
    }

    // --- Test Pinjam Buku ---

    @Test
    @DisplayName("Pinjam buku berhasil ketika semua kondisi terpenuhi")
    void testPinjamBukuBerhasil() {
        // Arrange
        bukuTest.setJumlahTersedia(3); // Stok tersedia: 3
        // 1. Ketika dicari, kembalikan Optional berisi buku (tersedia)
        when(mockRepositoryBuku.cariByIsbn("1234567890")).thenReturn(Optional.of(bukuTest));
        // 2. Ketika diupdate stok, kembalikan true
        // Stok baru harus 3 - 1 = 2
        when(mockRepositoryBuku.updateJumlahTersedia("1234567890", 2)).thenReturn(true);
        
        // Atur anggota agar bisa pinjam (saat ini 0 pinjaman, batas 3)
        anggotaTest.getIdBukuDipinjam().clear();

        // Act
        boolean hasil = servicePerpustakaan.pinjamBuku("1234567890", anggotaTest);

        // Assert
        assertTrue(hasil, "Harus berhasil meminjam buku");
        // Cek bahwa buku telah ditambahkan ke daftar pinjaman anggota
        assertTrue(anggotaTest.getIdBukuDipinjam().contains("1234567890")); 
        // Verifikasi pembaruan stok dipanggil dengan nilai yang benar
        verify(mockRepositoryBuku).updateJumlahTersedia("1234567890", 2); 
        verify(mockRepositoryBuku).cariByIsbn("1234567890");
    }

    @Test
    @DisplayName("Pinjam buku gagal ketika buku tidak tersedia")
    void testPinjamBukuGagalKetikaTidakTersedia() {
        // Arrange
        bukuTest.setJumlahTersedia(0); // Stok tersedia: 0
        // Ketika dicari, kembalikan Optional berisi buku (tapi tidak tersedia)
        when(mockRepositoryBuku.cariByIsbn("1234567890")).thenReturn(Optional.of(bukuTest));

        // Act
        boolean hasil = servicePerpustakaan.pinjamBuku("1234567890", anggotaTest);

        // Assert
        assertFalse(hasil, "Tidak boleh meminjam buku yang tidak tersedia");
        // Cek bahwa daftar pinjaman anggota tidak bertambah
        assertFalse(anggotaTest.getIdBukuDipinjam().contains("1234567890"));
        // Verifikasi bahwa update stok tidak pernah dipanggil
        verify(mockRepositoryBuku, never()).updateJumlahTersedia(anyString(), anyInt()); 
        verify(mockRepositoryBuku).cariByIsbn("1234567890");
    }

    @Test
    @DisplayName("Pinjam buku gagal ketika anggota tidak aktif")
    void testPinjamBukuGagalAnggotaTidakAktif() {
        // Arrange
        anggotaTest.setAktif(false);

        // Act
        boolean hasil = servicePerpustakaan.pinjamBuku("1234567890", anggotaTest);

        // Assert
        assertFalse(hasil, "Anggota tidak aktif tidak boleh meminjam buku");
        // Verifikasi tidak ada interaksi dengan mockRepositoryBuku
        verifyNoInteractions(mockRepositoryBuku); 
    }

    @Test
    @DisplayName("Pinjam buku gagal ketika batas pinjam tercapai")
    void testPinjamBukuGagalBatasPinjamTercapai() {
        // Arrange
        // Mahasiswa sudah pinjam 3 buku (batas maksimal)
        anggotaTest.tambahBukuDipinjam("1111111111");
        anggotaTest.tambahBukuDipinjam("2222222222");
        anggotaTest.tambahBukuDipinjam("3333333333");
        
        // Act
        boolean hasil = servicePerpustakaan.pinjamBuku("1234567890", anggotaTest);

        // Assert
        assertFalse(hasil, "Tidak boleh meminjam ketika batas pinjam tercapai");
        // Verifikasi tidak ada interaksi dengan mockRepositoryBuku
        verifyNoInteractions(mockRepositoryBuku);
    }

    // --- Test Kembalikan Buku ---

    @Test
    @DisplayName("Kembalikan buku berhasil")
    void testKembalikanBukuBerhasil() {
        // Arrange
        bukuTest.setJumlahTersedia(2); // Stok tersedia saat ini: 2
        // Anggota sudah meminjam buku ini
        anggotaTest.tambahBukuDipinjam("1234567890");
        
        // 1. Ketika dicari, kembalikan Optional berisi buku
        when(mockRepositoryBuku.cariByIsbn("1234567890")).thenReturn(Optional.of(bukuTest));
        // 2. Ketika diupdate stok, kembalikan true
        // Stok baru harus 2 + 1 = 3
        when(mockRepositoryBuku.updateJumlahTersedia("1234567890", 3)).thenReturn(true);
        
        // Act
        boolean hasil = servicePerpustakaan.kembalikanBuku("1234567890", anggotaTest);

        // Assert
        assertTrue(hasil, "Harus berhasil mengembalikan buku");
        // Cek bahwa buku telah dihapus dari daftar pinjaman anggota
        assertFalse(anggotaTest.getIdBukuDipinjam().contains("1234567890")); 
        // Verifikasi pembaruan stok dipanggil dengan nilai yang benar
        verify(mockRepositoryBuku).updateJumlahTersedia("1234567890", 3);
        verify(mockRepositoryBuku).cariByIsbn("1234567890");
    }

    @Test
    @DisplayName("Kembalikan buku gagal ketika anggota tidak meminjam buku tersebut")
    void testKembalikanBukuGagalTidakMeminjam() {
        // Arrange
        anggotaTest.getIdBukuDipinjam().clear(); // Anggota tidak meminjam buku apapun

        // Act
        boolean hasil = servicePerpustakaan.kembalikanBuku("1234567890", anggotaTest);

        // Assert
        assertFalse(hasil, "Tidak boleh mengembalikan buku yang tidak dipinjam");
        // Verifikasi tidak ada interaksi dengan mockRepositoryBuku
        verifyNoInteractions(mockRepositoryBuku); 
    }
    
    // --- Test Ketersediaan dan Jumlah Stok ---

    @Test
    @DisplayName("Cek ketersediaan buku")
    void testBukuTersedia() {
        // Arrange
        // 1. Buku tersedia
        bukuTest.setJumlahTersedia(1);
        when(mockRepositoryBuku.cariByIsbn("1234567890")).thenReturn(Optional.of(bukuTest));
        // Act & Assert
        assertTrue(servicePerpustakaan.bukuTersedia("1234567890"));

        // 2. Buku tidak tersedia (stok 0)
        bukuTest.setJumlahTersedia(0);
        // Mocking ulang agar cariByIsbn berikutnya mengembalikan objek yang sama dengan stok 0
        when(mockRepositoryBuku.cariByIsbn("1234567890")).thenReturn(Optional.of(bukuTest));
        // Act & Assert
        assertFalse(servicePerpustakaan.bukuTersedia("1234567890"));
    }

    @Test
    @DisplayName("Get jumlah tersedia")
    void testGetJumlahTersedia() {
        // Arrange
        bukuTest.setJumlahTersedia(3);
        when(mockRepositoryBuku.cariByIsbn("1234567890")).thenReturn(Optional.of(bukuTest));
        
        // Act
        int jumlah = servicePerpustakaan.getJumlahTersedia("1234567890");
        
        // Assert
        assertEquals(3, jumlah);
        verify(mockRepositoryBuku).cariByIsbn("1234567890");
    }
    
    @Test
    @DisplayName("Get jumlah tersedia untuk buku yang tidak ada")
    void testGetJumlahTersediaBukuTidakAda() {
        // Arrange
        when(mockRepositoryBuku.cariByIsbn("9999999999")).thenReturn(Optional.empty());

        // Act
        int jumlah = servicePerpustakaan.getJumlahTersedia("9999999999");

        // Assert
        assertEquals(0, jumlah);
        verify(mockRepositoryBuku).cariByIsbn("9999999999");
    }
}