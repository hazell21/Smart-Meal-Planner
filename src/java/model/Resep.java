package model;

import java.util.ArrayList;
import java.util.List;

public class Resep {
    private String nama;
    private int kalori;
    private String kategori;   
    private String caraMasak;  
    private List<Bahan> daftarBahan; 

    public Resep(String nama, int kalori, String kategori, String caraMasak) {
        this.nama = nama;
        this.kalori = kalori;
        this.kategori = kategori;
        this.caraMasak = caraMasak;
        this.daftarBahan = new ArrayList<>();
    }

    public void tambahBahan(Bahan b) {
        this.daftarBahan.add(b);
    }

    public double hitungTotalHarga() {
        double total = 0;
        if (this.daftarBahan != null) {
            for (Bahan b : this.daftarBahan) {               
                total += b.getHarga(); 
            }
        }
        return total;
    }

    public void tampilResep() {
        System.out.println("Menu Resep: " + nama + " [" + kalori + " kkal]");
    }

    public String getDetail() {
        return "Kategori: " + kategori + " | Langkah: " + caraMasak;
    }

    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }
    public int getKalori() { return kalori; }
    public void setKalori(int kalori) { this.kalori = kalori; }
    public String getKategori() { return kategori; }
    public void setKategori(String kategori) { this.kategori = kategori; }
    public String getCaraMasak() { return caraMasak; }
    public void setCaraMasak(String caraMasak) { this.caraMasak = caraMasak; }
    public List<Bahan> getDaftarBahan() { return daftarBahan; }
}