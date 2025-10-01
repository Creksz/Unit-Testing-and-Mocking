package com.praktikum.testing.service;

import com.praktikum.testing.model.Anggota;
import com.praktikum.testing.model.Peminjaman;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

@DisplayName("Test Kalkulator Denda")
public class KalkulatorDendaTest {

    private KalkulatorDenda kalkulatorDenda;
    private Anggota anggotaMahasiswa;
    private Anggota anggotaDosen;
    private Anggota anggotaUmum;

    @BeforeEach
    void setUp() {
        kalkulatorDenda = new KalkulatorDenda();
        anggotaMahasiswa = new Anggota("M001", "John Student", "john@student.ac.id",
                                       "081234567890", Anggota.TipeAnggota.MAHASISWA);
        anggotaDosen = new Anggota("D001", "Alice Lecturer", "alice@univ.ac.id",
                                   "081234567891", Anggota.TipeAnggota.DOSEN);
        anggotaUmum = new Anggota("U001", "Public User", "user@email.com",
                                  "081234567892", Anggota.TipeAnggota.UMUM);
    }

    @Test
    @DisplayName("Tidak ada denda untuk peminjaman yang tidak terlambat")
    void testTidakAdaDendaUntukPeminjamanTidakTerlambat() {
        // Peminjaman tidak terlambat: tanggal kembali setelah tanggal jatuh tempo
        LocalDate tanggalPinjam = LocalDate.now().minusDays(5);
        LocalDate tanggalJatuhTempo = LocalDate.now().plusDays(2); 
        
        Peminjaman peminjaman = new Peminjaman("P001", "M001", "1234567890",
                                               tanggalPinjam, tanggalJatuhTempo);
        
        // Peminjaman belum dikembalikan, hari ini masih sebelum jatuh tempo
        double denda = kalkulatorDenda.hitungDenda(peminjaman, anggotaMahasiswa);
        
        assertEquals(0.0, denda, "Denda harus 0 untuk peminjaman yang tidak terlambat");
    }

    @Test
    @DisplayName("Hitung denda mahasiswa 3 hari terlambat")
    void testHitungDendaMahasiswaTigaHariTerlambat() {
        // Keterlambatan = 3 hari (hari ini - jatuh tempo)
        LocalDate tanggalPinjam = LocalDate.now().minusDays(10); 
        LocalDate tanggalJatuhTempo = LocalDate.now().minusDays(3);
        
        Peminjaman peminjaman = new Peminjaman("P001", "M001", "1234567890",
                                               tanggalPinjam, tanggalJatuhTempo);
        
        double dendaAktual = kalkulatorDenda.hitungDenda(peminjaman, anggotaMahasiswa);
        
        // Sesuai logika: 3 hari terlambat, tapi jika <= 3, denda 0.0
        assertEquals(0.0, dendaAktual, "Denda harus 0.0 karena kebijakan <= 3 hari denda 0");
    }

    @Test
    @DisplayName("Hitung denda dosen 5 hari terlambat")
    void testHitungDendaDosenLimaHariTerlambat() {
        // Keterlambatan = 5 hari
        LocalDate tanggalPinjam = LocalDate.now().minusDays(12);
        LocalDate tanggalJatuhTempo = LocalDate.now().minusDays(5); 
        
        Peminjaman peminjaman = new Peminjaman("P001", "D001", "1234567890",
                                               tanggalPinjam, tanggalJatuhTempo);
        
        double dendaAktual = kalkulatorDenda.hitungDenda(peminjaman, anggotaDosen);
        
        // 5 hari terlambat * 1500.0 (tarif dosen) = 7500.0
        assertEquals(7500.0, dendaAktual, "5 hari * 1500 harus sama dengan 7500");
    }

    @Test
    @DisplayName("Hitung denda umum 10 hari terlambat")
    void testHitungDendaUmumSepuluhHariTerlambat() {
        // Keterlambatan = 10 hari
        LocalDate tanggalPinjam = LocalDate.now().minusDays(15);
        LocalDate tanggalJatuhTempo = LocalDate.now().minusDays(10); 
        
        Peminjaman peminjaman = new Peminjaman("P001", "U001", "1234567890",
                                               tanggalPinjam, tanggalJatuhTempo);
        
        double dendaAktual = kalkulatorDenda.hitungDenda(peminjaman, anggotaUmum);
        
        // 10 hari terlambat * 1500.0 (tarif umum) = 15000.0
        assertEquals(15000.0, dendaAktual, "10 hari * 1500 harus sama dengan 15000");
    }

    @Test
    @DisplayName("Test Denda tidak boleh melebihi batas maksimal")
    void testDendaTidakMelebihiBatasMaksimal() {
        // Peminjaman sangat terlambat (100 hari)
        LocalDate tanggalPinjam = LocalDate.now().minusDays(107);
        LocalDate tanggalJatuhTempo = LocalDate.now().minusDays(100); 
        
        Peminjaman peminjaman = new Peminjaman("P001", "M001", "1234567890",
                                               tanggalPinjam, tanggalJatuhTempo);
        
        double dendaAktual = kalkulatorDenda.hitungDenda(peminjaman, anggotaMahasiswa);
        
        // Denda kotor: 100 hari * 1000 = 100000.0
        // Batas maksimal mahasiswa: 50000.0
        assertEquals(50000.0, dendaAktual, "Denda tidak boleh melebihi batas maksimal mahasiswa (50000.0)");
    }

    @Test
    @DisplayName("Exception untuk parameter null")
    void testExceptionParameterNull() {
        // Peminjaman null
        assertThrows(IllegalArgumentException.class, 
                     () -> kalkulatorDenda.hitungDenda(null, anggotaMahasiswa), 
                     "Harus throw exception untuk peminjaman null");
        
        Peminjaman peminjaman = new Peminjaman("P001", "M001", "1234567890",
                                               LocalDate.now(), LocalDate.now().plusDays(1));
        
        // Anggota null
        assertThrows(IllegalArgumentException.class, 
                     () -> kalkulatorDenda.hitungDenda(peminjaman, null), 
                     "Harus throw exception untuk anggota null");
    }

    @Test
    @DisplayName("Cek tarif denda harian sesuai tipe anggota")
    void testGetTarifDendaHarian() {
        assertEquals(1000.0, KalkulatorDenda.getTarifDendaHarian(Anggota.TipeAnggota.MAHASISWA));
        assertEquals(1500.0, KalkulatorDenda.getTarifDendaHarian(Anggota.TipeAnggota.DOSEN));
        assertEquals(1500.0, KalkulatorDenda.getTarifDendaHarian(Anggota.TipeAnggota.UMUM));

        assertThrows(IllegalArgumentException.class, 
                     () -> KalkulatorDenda.getTarifDendaHarian(null));
    }

    @Test
    @DisplayName("Cek denda maksimal sesuai tipe anggota")
    void testGetDendaMaximal() {
        assertEquals(50000.0, KalkulatorDenda.getDendaMaximal(Anggota.TipeAnggota.MAHASISWA));
        assertEquals(75000.0, KalkulatorDenda.getDendaMaximal(Anggota.TipeAnggota.DOSEN));
        assertEquals(75000.0, KalkulatorDenda.getDendaMaximal(Anggota.TipeAnggota.UMUM));

        assertThrows(IllegalArgumentException.class, 
                     () -> KalkulatorDenda.getDendaMaximal(null));
    }

    @Test
    @DisplayName("Cek ada denda")
    void testAdaDenda() {
        // Peminjaman terlambat
        LocalDate tanggalJatuhTempoTerlambat = LocalDate.now().minusDays(5);
        Peminjaman peminjamanTerlambat = new Peminjaman("P001", "M001", "1234567890",
                                                        LocalDate.now().minusDays(10), 
                                                        tanggalJatuhTempoTerlambat);
        assertTrue(KalkulatorDenda.adaDenda(peminjamanTerlambat));

        // Peminjaman tidak terlambat
        Peminjaman peminjamanTidakTerlambat = new Peminjaman("P002", "M001", "1234567890",
                                                             LocalDate.now().minusDays(5), 
                                                             LocalDate.now().plusDays(5));
        assertFalse(KalkulatorDenda.adaDenda(peminjamanTidakTerlambat));

        // Peminjaman null
        assertFalse(KalkulatorDenda.adaDenda(null));
    }

    @Test
    @DisplayName("Deskripsi denda sesuai jumlah")
    void testDeskripsiDenda() {
        assertEquals("Tidak ada denda", KalkulatorDenda.getDeskripsiDenda(0.0));
        assertEquals("Denda ringan", KalkulatorDenda.getDeskripsiDenda(9000.0));
        assertEquals("Denda sedang", KalkulatorDenda.getDeskripsiDenda(25000.0));
        assertEquals("Denda berat", KalkulatorDenda.getDeskripsiDenda(75000.0));
    }
}