/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package model;

import java.util.List;

public interface Rekomendasi {

    List<Resep> generateRekomendasi(List<Resep> daftarResep);
    void tampilRekomendasi(List<Resep> daftarResep);
}