package com.praktikum.testing.repository;

import com.praktikum.testing.model.Buku;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import java.util.stream.Collectors; // Diperlukan untuk stream assertion */

@DisplayName("Test Mock Repository Buku - Manual Mock Implementation")
public class MockRepositoryBukuTest {

    private MockRepositoryBuku mockRepository;
    private Buku buku1;
    private Buku buku2;
    private Buku buku3;

    @BeforeEach
    void setUp() {
        // Reset mock repository sebelum setiap test
        mockRepository = new MockRepositoryBuku();

        // Setup konstanta data
        buku1 = new Buku("1234567890", "Pemrograman Java", "John Doe", 5, 150000.0);
        buku2 = new Buku("0987654321", "Algoritma dan Struktur Data", "Jane Smith", 3, 200000.0);
        buku3 = new Buku("1111111111", "Java Advanced", "John Doe", 4, 180000.0);
    }

    // --- Test Simpan ---

    @Test
    @DisplayName("Simpan buku baru - harus berhasil")
    void testSimpanBukuBaru() {
        // Act
        boolean hasil = mockRepository.simpan(buku1);

        // Assert
        assertTrue(hasil, "Harus berhasil menyimpan buku baru");
        assertEquals(1, mockRepository.ukuran(), "Repository harus berisi 1 buku");
        assertTrue(mockRepository.mengandung("1234567890"), "Repository harus mengandung ISBN buku");
    }

    @Test
    @DisplayName("Simpan buku null - harus gagal")
    void testSimpanBukuNull() {
        // Act
        boolean hasil = mockRepository.simpan(null);

        // Assert
        assertFalse(hasil, "Harus gagal menyimpan buku null");
        assertEquals(0, mockRepository.ukuran(), "Repository harus tetap kosong");
    }

    @Test
    @DisplayName("Simpan buku dengan ISBN null - harus gagal")
    void testSimpanBukuDenganIsbnNull() {
        // Arrange
        Buku bukuInvalid = new Buku();
        bukuInvalid.setJudul("Judul Tanpa ISBN");

        // Act
        boolean hasil = mockRepository.simpan(bukuInvalid);

        // Assert
        assertFalse(hasil, "Harus gagal menyimpan buku tanpa ISBN");
        assertEquals(0, mockRepository.ukuran(), "Repository harus tetap kosong");
    }

    @Test
    @DisplayName("Simpan buku duplikat - harus overwrite")
    void testSimpanDuplikatOverwrite() {
        // Arrange
        mockRepository.simpan(buku1); // Simpan buku pertama

        // Buat buku dengan ISBN sama tapi data berbeda
        Buku bukuUpdated = new Buku("1234567890", "Java Programming Updated", "John Doe Updated", 5, 200000.0);

        // Act
        boolean hasil = mockRepository.simpan(bukuUpdated);

        // Assert
        assertTrue(hasil, "Harus berhasil menyimpan (overwrite)");
        assertEquals(1, mockRepository.ukuran(), "Repository harus tetap berisi 1 buku");

        // Verify data terupdate
        Optional<Buku> bukuDiRepository = mockRepository.cariByIsbn("1234567890");
        assertTrue(bukuDiRepository.isPresent());
        assertEquals("Java Programming Updated", bukuDiRepository.get().getJudul());
    }

    // --- Test Cari By ISBN ---

    @Test
    @DisplayName("Cari buku by ISBN - buku ditemukan")
    void testCariByIsbnDitemukan() {
        // Arrange
        mockRepository.simpan(buku1);
        mockRepository.simpan(buku2);

        // Act
        Optional<Buku> hasil = mockRepository.cariByIsbn("0987654321");

        // Assert
        assertTrue(hasil.isPresent(), "Harus menemukan buku");
        assertEquals("Algoritma dan Struktur Data", hasil.get().getJudul());
        assertEquals("Jane Smith", hasil.get().getPengarang());
    }

    @Test
    @DisplayName("Cari buku by ISBN - buku tidak ditemukan")
    void testCariByIsbnTidakDitemukan() {
        // Arrange
        mockRepository.simpan(buku1);

        // Act
        Optional<Buku> hasil = mockRepository.cariByIsbn("9999999999");

        // Assert
        assertFalse(hasil.isPresent(), "Harus mengembalikan Optional.empty()");
    }

    @Test
    @DisplayName("Cari buku by ISBN null - harus empty")
    void testCariByIsbnNull() {
        // Act
        Optional<Buku> hasil = mockRepository.cariByIsbn(null);

        // Assert
        assertFalse(hasil.isPresent(), "Harus mengembalikan Optional.empty() untuk ISBN null");
    }

    // --- Test Cari By Judul ---

    @Test
    @DisplayName("Cari buku by judul case insensitive")
    void testCariByJudulCaseInsensitive() {
        // Arrange
        mockRepository.simpan(buku1); // "Pemrograman Java"
        mockRepository.simpan(buku2); // "Algoritma dan Struktur Data"
        mockRepository.simpan(buku3); // "Java Advanced"

        // Act
        List<Buku> hasil1 = mockRepository.cariByJudul("Java");
        List<Buku> hasil2 = mockRepository.cariByJudul("java");
        List<Buku> hasil3 = mockRepository.cariByJudul("JAV");

        // Assert
        assertEquals(2, hasil1.size(), "Harus menemukan 2 buku dengan kata 'Java' (case insensitive)");
        assertEquals(2, hasil2.size(), "Harus menemukan 2 buku dengan kata 'java'");
        assertEquals(2, hasil3.size(), "Harus menemukan 2 buku dengan kata 'JAV'");

        // Verify judul yang ditemukan
        assertTrue(hasil1.stream().anyMatch(b -> b.getJudul().equals("Pemrograman Java")));
        assertTrue(hasil1.stream().anyMatch(b -> b.getJudul().equals("Java Advanced")));
    }

    @Test
    @DisplayName("Cari buku by judul partial match")
    void testCariByJudulPartialMatch() {
        // Arrange
        mockRepository.simpan(buku1); 
        mockRepository.simpan(buku2); 
        mockRepository.simpan(buku3);

        // Act
        List<Buku> hasil = mockRepository.cariByJudul("Algoritma");

        // Assert
        assertEquals(1, hasil.size());
        assertEquals("Algoritma dan Struktur Data", hasil.get(0).getJudul());
    }

    @Test
    @DisplayName("Cari buku by judul kosong - harus empty list")
    void testCariByJudulKosong() {
        // Arrange
        mockRepository.simpan(buku1);

        // Act
        List<Buku> hasil1 = mockRepository.cariByJudul("");
        List<Buku> hasil2 = mockRepository.cariByJudul("   ");
        List<Buku> hasil3 = mockRepository.cariByJudul(null);

        // Assert
        assertTrue(hasil1.isEmpty(), "Harus empty untuk string kosong");
        assertTrue(hasil2.isEmpty(), "Harus empty untuk whitespace");
        assertTrue(hasil3.isEmpty(), "Harus empty untuk null");
    }

    // --- Test Cari By Pengarang ---

    @Test
    @DisplayName("Cari buku by pengarang")
    void testCariByPengarang() {
        // Arrange
        mockRepository.simpan(buku1); // John Doe
        mockRepository.simpan(buku2); // Jane Smith
        mockRepository.simpan(buku3); // John Doe

        // Act
        List<Buku> hasil = mockRepository.cariByPengarang("John Doe");

        // Assert
        assertEquals(2, hasil.size(), "Harus menemukan 2 buku oleh John Doe");
        // Verify bahwa kedua buku yang ditemukan memang oleh John Doe
        assertTrue(hasil.stream().allMatch(b -> b.getPengarang().equals("John Doe")));
    }

    // --- Test Hapus ---

    @Test
    @DisplayName("Hapus buku yang ada - harus berhasil")
    void testHapusBukuYangAda() {
        // Arrange
        mockRepository.simpan(buku1); // 1234567890
        mockRepository.simpan(buku2); // 0987654321
        assertEquals(2, mockRepository.ukuran());

        // Act
        boolean hasil = mockRepository.hapus("1234567890");

        // Assert
        assertTrue(hasil, "Harus berhasil menghapus");
        assertEquals(1, mockRepository.ukuran(), "Harus tersisa 1 buku");
        assertFalse(mockRepository.mengandung("1234567890"), "Buku1 sudah terhapus");
        assertTrue(mockRepository.mengandung("0987654321"), "Buku2 harus masih ada");
    }

    @Test
    @DisplayName("Hapus buku yang tidak ada - harus gagal")
    void testHapusBukuYangTidakAda() {
        // Arrange
        mockRepository.simpan(buku1); 

        // Act
        boolean hasil = mockRepository.hapus("9999999999");

        // Assert
        assertFalse(hasil, "Harus gagal menghapus buku yang tidak ada");
        assertEquals(1, mockRepository.ukuran(), "Repository harus tetap berisi 1 buku");
    }

    @Test
    @DisplayName("Hapus dengan ISBN null - harus gagal")
    void testHapusDenganIsbnNull() {
        // Act
        boolean hasil = mockRepository.hapus(null);

        // Assert
        assertFalse(hasil, "Harus gagal untuk ISBN null");
    }

    // --- Test Update Jumlah Tersedia ---

    @Test
    @DisplayName("Update jumlah tersedia - valid update")
    void testUpdateJumlahTersediaValid() {
        // Arrange
        mockRepository.simpan(buku1); // JumlahTotal = 5, JumlahTersedia = 5

        // Act
        // Set jumlah tersedia menjadi 3 (turun)
        boolean hasil = mockRepository.updateJumlahTersedia("1234567890", 3);

        // Assert
        assertTrue(hasil, "Harus berhasil update");
        Optional<Buku> bukuUpdated = mockRepository.cariByIsbn("1234567890");
        assertTrue(bukuUpdated.isPresent());
        assertEquals(3, bukuUpdated.get().getJumlahTersedia(), "Jumlah tersedia harus 3");
        assertEquals(5, bukuUpdated.get().getJumlahTotal(), "Jumlah total harus tetap sama");
    }

    @Test
    @DisplayName("Update jumlah tersedia - melebihi jumlah total harus gagal")
    void testUpdateJumlahTersediaMelebihiTotal() {
        // Arrange
        mockRepository.simpan(buku1); // JumlahTotal = 5

        // Act
        // Coba set jumlah tersedia > jumlah total (10 > 5)
        boolean hasil = mockRepository.updateJumlahTersedia("1234567890", 10);

        // Assert
        assertFalse(hasil, "Harus gagal update karena melebihi jumlah total");
        Optional<Buku> bukuUpdated = mockRepository.cariByIsbn("1234567890");
        assertEquals(5, bukuUpdated.get().getJumlahTersedia(), "Jumlah tersedia tidak boleh berubah");
    }
    
    @Test
    @DisplayName("Update jumlah tersedia - jumlah negatif harus gagal")
    void testUpdateJumlahTersediaNegatif() {
        // Arrange
        mockRepository.simpan(buku1); 

        // Act
        boolean hasil = mockRepository.updateJumlahTersedia("1234567890", -1);

        // Assert
        assertFalse(hasil, "Harus gagal untuk jumlah negatif");
    }

    @Test
    @DisplayName("Update jumlah tersedia - buku tidak ditemukan harus gagal")
    void testUpdateJumlahTersediaBukuTidakDitemukan() {
        // Act
        boolean hasil = mockRepository.updateJumlahTersedia("9999999999", 5);

        // Assert
        assertFalse(hasil, "Harus gagal untuk buku yang tidak ada");
    }

    // --- Test Cari Semua ---

    @Test
    @DisplayName("Cari semua buku - repository kosong")
    void testCariSemuaRepositoryKosong() {
        // Act
        List<Buku> hasil = mockRepository.cariSemua();

        // Assert
        assertTrue(hasil.isEmpty(), "Harus empty list untuk repository kosong");
    }

    @Test
    @DisplayName("Cari semua buku - repository berisi beberapa buku")
    void testCariSemuaRepositoryBerisi() {
        // Arrange
        mockRepository.simpan(buku1);
        mockRepository.simpan(buku2);
        mockRepository.simpan(buku3);

        // Act
        List<Buku> hasil = mockRepository.cariSemua();

        // Assert
        assertEquals(3, hasil.size(), "Harus mengembalikan semua buku");
        // Cek apakah list mengandung semua buku
        assertTrue(hasil.contains(buku1));
        assertTrue(hasil.contains(buku2));
        assertTrue(hasil.contains(buku3));
    }

    // --- Test Integrasi (Flow Peminjaman) ---

    @Test
    @DisplayName("Integration test - simulasi flow peminjaman buku")
    void testIntegrationFlowPeminjaman() {
        // 1. Tambah buku ke repository
        mockRepository.simpan(buku1); // Stok awal: 5
        assertEquals(1, mockRepository.ukuran());

        // 2. Cek ketersediaan awal
        Optional<Buku> bukuAwal = mockRepository.cariByIsbn("1234567890");
        assertTrue(bukuAwal.isPresent());
        assertEquals(5, bukuAwal.get().getJumlahTersedia());

        // 3. Pinjam buku (kurangi jumlah tersedia: 5 -> 4)
        boolean updateBerhasil1 = mockRepository.updateJumlahTersedia("1234567890", 4);
        assertTrue(updateBerhasil1);

        // 4. Verifikasi perubahan
        Optional<Buku> bukuSetelahPinjam = mockRepository.cariByIsbn("1234567890");
        assertTrue(bukuSetelahPinjam.isPresent());
        assertEquals(4, bukuSetelahPinjam.get().getJumlahTersedia());

        // 5. Kembalikan buku (tambah jumlah tersedia: 4 -> 5)
        boolean updateBerhasil2 = mockRepository.updateJumlahTersedia("1234567890", 5);
        assertTrue(updateBerhasil2);

        // 6. Verifikasi kembali ke semula
        Optional<Buku> bukuSetelahKembali = mockRepository.cariByIsbn("1234567890");
        assertTrue(bukuSetelahKembali.isPresent());
        assertEquals(5, bukuSetelahKembali.get().getJumlahTersedia());
    }

    // --- Test Utility Methods ---
    
    @Test
    @DisplayName("Test utility methods - ukuran dan mengandung")
    void testUtilityMethods() {
        // Test awal: repository kosong
        assertEquals(0, mockRepository.ukuran());
        assertFalse(mockRepository.mengandung("1234567890"));

        // Tambah buku pertama
        mockRepository.simpan(buku1);
        assertEquals(1, mockRepository.ukuran());
        assertTrue(mockRepository.mengandung("1234567890"));
        assertFalse(mockRepository.mengandung("9999999999"));

        // Tambah buku kedua
        mockRepository.simpan(buku2);
        assertEquals(2, mockRepository.ukuran());
        assertTrue(mockRepository.mengandung("0987654321"));
    }
    
    @Test
    @DisplayName("Bersihkan repository - harus kosong")
    void testBersihkanRepository() {
        // Arrange
        mockRepository.simpan(buku1);
        mockRepository.simpan(buku2);
        assertEquals(2, mockRepository.ukuran());

        // Act
        mockRepository.bersihkan();

        // Assert
        assertEquals(0, mockRepository.ukuran(), "Repository harus kosong setelah dibersihkan");
    }
}