package controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.Bahan;
import model.DatabaseConnection;
import model.Mahasiswa;
import model.MealPlanner;
import model.RecipeAPIService;
import model.Resep;
import model.RekomendasiMakanan;

@WebServlet(name = "MealPlannerServlet", urlPatterns = {"/MealPlannerServlet"})
public class MealPlannerServlet extends HttpServlet {

    private List<Bahan> ambilBahanGlobalDariDB() {
        List<Bahan> list = new ArrayList<>();
        String sql = "SELECT * FROM bahan";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(new Bahan(rs.getString("nama"), rs.getDouble("harga"), rs.getString("satuan"), 1.0));
            }
        } catch (SQLException e) {
            System.err.println("Gagal mengambil master bahan: " + e.getMessage());
        }
        return list;
    }

    private List<Resep> ambilResepDariDB() {
        List<Resep> bankResep = new ArrayList<>();
        String sqlResep = "SELECT * FROM resep";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmtResep = conn.prepareStatement(sqlResep);
             ResultSet rsResep = stmtResep.executeQuery()) {
            while (rsResep.next()) {
                int idResep = rsResep.getInt("id_resep");
                Resep r = new Resep(rsResep.getString("nama"), rsResep.getInt("kalori"), rsResep.getString("kategori"), rsResep.getString("cara_masak"));
                String sqlBahan = "SELECT b.nama, b.harga, b.satuan, dr.jumlah FROM detail_resep dr JOIN bahan b ON dr.id_bahan = b.id_bahan WHERE dr.id_resep = ?";
                try (PreparedStatement stmtBahan = conn.prepareStatement(sqlBahan)) {
                    stmtBahan.setInt(1, idResep);
                    try (ResultSet rsBahan = stmtBahan.executeQuery()) {
                        while (rsBahan.next()) {
                            r.tambahBahan(new Bahan(rsBahan.getString("nama"), rsBahan.getDouble("harga"), rsBahan.getString("satuan"), rsBahan.getDouble("jumlah")));
                        }
                    }
                }
                bankResep.add(r);
            }
        } catch (SQLException e) {
            System.err.println("Gagal mengambil data resep: " + e.getMessage());
        }
        return bankResep;
    }

    private void muatJadwalDariDB(String nim, MealPlanner planner) {
        String sql = "SELECT j.hari, j.waktu_makan, r.nama AS nama_resep FROM jadwal j JOIN resep r ON j.id_resep = r.id_resep WHERE j.nim = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nim);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Resep resep = planner.cariResep(rs.getString("nama_resep"));
                    if (resep != null) planner.tambahJadwal(rs.getString("hari"), rs.getString("waktu_makan"), resep);
                }
            }
        } catch (SQLException e) {
            System.err.println("Gagal muat jadwal: " + e.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        HttpSession session = request.getSession();
        if ("logout".equalsIgnoreCase(action)) {
            session.invalidate();
            response.sendRedirect("login.jsp");
            return;
        }
        response.sendRedirect("dashboard.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        HttpSession session = request.getSession();
        MealPlanner planner = (MealPlanner) session.getAttribute("planner");
        Mahasiswa mhs = (Mahasiswa) session.getAttribute("mahasiswa");

        if ("login".equalsIgnoreCase(action)) {
            String email = request.getParameter("email");
            String password = request.getParameter("password");
            if (email != null && !email.isEmpty() && password != null && !password.isEmpty()) {
                try (Connection conn = DatabaseConnection.getConnection()) {
                    String sql = "SELECT * FROM mahasiswa WHERE email = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                        stmt.setString(1, email);
                        try (ResultSet rs = stmt.executeQuery()) {
                            if (rs.next()) {
                                mhs = new Mahasiswa(rs.getString("nama"), rs.getString("email"), rs.getString("nim"), rs.getDouble("budget"));
                            } else {
                                String namaDinamis = email.split("@")[0];
                                namaDinamis = namaDinamis.substring(0, 1).toUpperCase() + namaDinamis.substring(1);
                                String nimDinamis = "1030124" + String.valueOf(System.currentTimeMillis()).substring(10);
                                String insertSql = "INSERT INTO mahasiswa (nim, nama, email, budget) VALUES (?, ?, ?, ?)";
                                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                                    insertStmt.setString(1, nimDinamis);
                                    insertStmt.setString(2, namaDinamis);
                                    insertStmt.setString(3, email);
                                    insertStmt.setDouble(4, 1500000.00);
                                    insertStmt.executeUpdate();
                                }
                                mhs = new Mahasiswa(namaDinamis, email, nimDinamis, 1500000.00);
                            }
                        }
                    }
                } catch (SQLException e) { e.printStackTrace(); }

                if (mhs != null) {
                    MealPlanner newPlanner = new MealPlanner(mhs.getNama());
                    session.setAttribute("mahasiswa", mhs);
                    session.setAttribute("planner", newPlanner);
                    session.setAttribute("daftarBahanGlobal", ambilBahanGlobalDariDB());
                    List<Resep> bankResep = ambilResepDariDB();
                    for (Resep r : bankResep) newPlanner.tambahResep(r);
                    muatJadwalDariDB(mhs.getNim(), newPlanner);
                    response.sendRedirect("dashboard.jsp");
                } else { response.sendRedirect("login.jsp?error=1"); }
            } else { response.sendRedirect("login.jsp?error=1"); }

        } else if ("addJadwal".equalsIgnoreCase(action)) {
            if (planner != null && mhs != null) {
                String hari = request.getParameter("hari");
                String waktuMakan = request.getParameter("waktuMakan");
                Resep resepDipilih = planner.cariResep(request.getParameter("namaResep"));
                if (resepDipilih != null) {
                    if (mhs.kurangiBudget(resepDipilih.hitungTotalHarga())) {
                        try (Connection conn = DatabaseConnection.getConnection()) {
                            conn.setAutoCommit(false);
                            int idResepFix = 0;
                            try (PreparedStatement stmtId = conn.prepareStatement("SELECT id_resep FROM resep WHERE nama = ?")) {
                                stmtId.setString(1, resepDipilih.getNama());
                                try (ResultSet rsId = stmtId.executeQuery()) { if (rsId.next()) idResepFix = rsId.getInt("id_resep"); }
                            }
                            try (PreparedStatement stmtInsert = conn.prepareStatement("INSERT INTO jadwal (nim, hari, waktu_makan, id_resep) VALUES (?, ?, ?, ?)")) {
                                stmtInsert.setString(1, mhs.getNim());
                                stmtInsert.setString(2, hari);
                                stmtInsert.setString(3, waktuMakan);
                                stmtInsert.setInt(4, idResepFix);
                                stmtInsert.executeUpdate();
                            }
                            try (PreparedStatement stmtUpdate = conn.prepareStatement("UPDATE mahasiswa SET budget = ? WHERE nim = ?")) {
                                stmtUpdate.setDouble(1, mhs.getBudget());
                                stmtUpdate.setString(2, mhs.getNim());
                                stmtUpdate.executeUpdate();
                            }
                            conn.commit();
                            planner.tambahJadwal(hari, waktuMakan, resepDipilih);
                            session.setAttribute("successMessage", "Menu berhasil dijadwalkan!");
                        } catch (SQLException e) { session.setAttribute("errorMessage", "Gagal transaksi: " + e.getMessage()); }
                    } else { session.setAttribute("errorMessage", "Anggaran tidak mencukupi!"); }
                }
            }
            response.sendRedirect("dashboard.jsp");

        } else if ("updateBudget".equalsIgnoreCase(action)) {
            double budgetBaru = Double.parseDouble(request.getParameter("budgetBaru"));
            if (mhs != null) {
                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement stmt = conn.prepareStatement("UPDATE mahasiswa SET budget = ? WHERE nim = ?")) {
                    stmt.setDouble(1, budgetBaru);
                    stmt.setString(2, mhs.getNim());
                    stmt.executeUpdate();
                    mhs.setBudget(budgetBaru);
                    session.setAttribute("successMessage", "Anggaran dompet diperbarui!");
                } catch (SQLException e) { session.setAttribute("errorMessage", "Gagal update budget: " + e.getMessage()); }
            }
            response.sendRedirect("dashboard.jsp");

        } else if ("addBahanMaster".equalsIgnoreCase(action)) {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("INSERT INTO bahan (nama, harga, satuan) VALUES (?, ?, ?)")) {
                stmt.setString(1, request.getParameter("namaBahanMaster"));
                stmt.setDouble(2, Double.parseDouble(request.getParameter("hargaBahanMaster")));
                stmt.setString(3, request.getParameter("satuanBahanMaster"));
                stmt.executeUpdate();
                session.setAttribute("daftarBahanGlobal", ambilBahanGlobalDariDB());
                session.setAttribute("successMessage", "Bahan mentah baru sukses didaftarkan!");
            } catch (SQLException e) { session.setAttribute("errorMessage", "Gagal simpan bahan: " + e.getMessage()); }
            response.sendRedirect("dashboard.jsp");

        } else if ("editBahanMaster".equalsIgnoreCase(action)) {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("UPDATE bahan SET nama = ?, harga = ?, satuan = ? WHERE nama = ?")) {
                stmt.setString(1, request.getParameter("namaBahanBaru"));
                stmt.setDouble(2, Double.parseDouble(request.getParameter("hargaBahanBaru")));
                stmt.setString(3, request.getParameter("satuanBahanBaru"));
                stmt.setString(4, request.getParameter("namaBahanLama"));
                stmt.executeUpdate();
                session.setAttribute("daftarBahanGlobal", ambilBahanGlobalDariDB());
                if (planner != null) {
                    planner.getDaftarResep().clear();
                    for (Resep r : ambilResepDariDB()) planner.tambahResep(r);
                }
                session.setAttribute("successMessage", "Atribut bahan baku berhasil di-edit!");
            } catch (SQLException e) { session.setAttribute("errorMessage", "Gagal edit bahan: " + e.getMessage()); }
            response.sendRedirect("dashboard.jsp");

            } else if ("rekomendasi".equalsIgnoreCase(action)) {
            double budgetMaks = Double.parseDouble(request.getParameter("budgetMaksRekomendasi"));
            
           RecipeAPIService apiService = new RecipeAPIService();
            List<Resep> hasilRekomendasiAPI = apiService.getRekomendasiDariAPI(budgetMaks);
            
            session.setAttribute("hasilRekomendasi", hasilRekomendasiAPI);
            session.setAttribute("targetBudgetRek", budgetMaks);
            session.setAttribute("successMessage", "Rekomendasi berbasis Live Web API berhasil dimuat!");
            
            response.sendRedirect("dashboard.jsp");

            } else if ("addResepBaru".equalsIgnoreCase(action)) {
            String[] namaBahanArr = request.getParameterValues("namaBahan[]");
            String[] jumlahBahanArr = request.getParameterValues("jumlahBahan[]");
            
            try (Connection conn = DatabaseConnection.getConnection()) {
                conn.setAutoCommit(false);
                int idResepGen = 0;
                try (PreparedStatement stmtResep = conn.prepareStatement("INSERT INTO resep (nama, kalori, kategori, cara_masak) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                    stmtResep.setString(1, request.getParameter("namaResep"));
                    stmtResep.setInt(2, Integer.parseInt(request.getParameter("calori")));
                    stmtResep.setString(3, request.getParameter("kategori"));
                    stmtResep.setString(4, request.getParameter("caraMasak"));
                    stmtResep.executeUpdate();
                    try (ResultSet gk = stmtResep.getGeneratedKeys()) { if (gk.next()) idResepGen = gk.getInt(1); }
                }
                if (idResepGen > 0 && namaBahanArr != null) {
                    for (int i = 0; i < namaBahanArr.length; i++) {
                        int idBahanFix = 0;
                        try (PreparedStatement stmtCek = conn.prepareStatement("SELECT id_bahan FROM bahan WHERE nama = ?")) {
                            stmtCek.setString(1, namaBahanArr[i]);
                            try (ResultSet rs = stmtCek.executeQuery()) { if (rs.next()) idBahanFix = rs.getInt("id_bahan"); }
                        }
                        if (idBahanFix > 0) {
                            try (PreparedStatement stmtJ = conn.prepareStatement("INSERT INTO detail_resep (id_resep, id_bahan, jumlah) VALUES (?, ?, ?)")) {
                                stmtJ.setInt(1, idResepGen);
                                stmtJ.setInt(2, idBahanFix);
                                stmtJ.setDouble(3, Double.parseDouble(jumlahBahanArr[i]));
                                stmtJ.executeUpdate();
                            }
                        }
                    }
                }
                conn.commit();
                if (planner != null) {
                    planner.getDaftarResep().clear();
                    for (Resep r : ambilResepDariDB()) planner.tambahResep(r);
                }
                session.setAttribute("successMessage", "Menu resep baru berhasil disimpan!");
            } catch (SQLException e) { session.setAttribute("errorMessage", "Gagal buat resep: " + e.getMessage()); }
            response.sendRedirect("dashboard.jsp");
        }
    }
}