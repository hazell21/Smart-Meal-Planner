/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.util.ArrayList;
import java.util.List;


public class RekomendasiMakanan implements Rekomendasi {

    private double  budgetHarian;       
    private List<String> bahanTersedia; 

    public RekomendasiMakanan(double budgetHarian) {
        this.budgetHarian  = budgetHarian;
        this.bahanTersedia = new ArrayList<>();
    }

    public RekomendasiMakanan(double budgetHarian, List<String> bahanTersedia) {
        this.budgetHarian  = budgetHarian;
        this.bahanTersedia = bahanTersedia;
    }

    @Override
    public List<Resep> generateRekomendasi(List<Resep> daftarResep) {
        List<Resep> hasil = new ArrayList<>();
        for (Resep r : daftarResep) {
            if (r.hitungTotalHarga() <= budgetHarian) {
                hasil.add(r);
            }
        }
        return hasil;
    }

    @Override
    public void tampilRekomendasi(List<Resep> daftarResep) {
        List<Resep> rekomendasi = generateRekomendasi(daftarResep);
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║     REKOMENDASI MENU HARI INI        ║");
        System.out.printf ("║  Budget harian : Rp %,.0f%-13s║%n", budgetHarian, "");
        System.out.println("╠══════════════════════════════════════╣");

        if (rekomendasi.isEmpty()) {
            System.out.println("║  Tidak ada resep sesuai budget.      ║");
        } else {
            for (Resep r : rekomendasi) {
                System.out.printf("║  %-20s Rp %,6.0f  ║%n",
                        r.getNama(), r.hitungTotalHarga());
            }
        }
        System.out.println("╚══════════════════════════════════════╝");
    }

    public List<Resep> rekomendasiBerdasarkanBahan(List<Resep> daftarResep) {
        List<Resep> hasil = new ArrayList<>();
        for (Resep r : daftarResep) {
            boolean bisaDibuat = true;
            for (Bahan b : r.getDaftarBahan()) {
                boolean adaBahan = false;
                for (String namaBahan : bahanTersedia) {
                    if (namaBahan.equalsIgnoreCase(b.getNama())) {
                        adaBahan = true;
                        break;
                    }
                }
                if (!adaBahan) {
                    bisaDibuat = false;
                    break;
                }
            }
            if (bisaDibuat) {
                hasil.add(r);
            }
        }
        return hasil;
    }

    public double getBudgetHarian() {
        return budgetHarian;
    }

    public void setBudgetHarian(double budgetHarian) {
        this.budgetHarian = budgetHarian;
    }

    public List<String> getBahanTersedia() {
        return bahanTersedia;
    }

    public void setBahanTersedia(List<String> bahanTersedia) {
        this.bahanTersedia = bahanTersedia;
    }

    public void tambahBahanTersedia(String namaBahan) {
        this.bahanTersedia.add(namaBahan);
    }

    @Override
    public String toString() {
        return "RekomendasiMakanan{budgetHarian=Rp" + budgetHarian + "}";
    }
}
