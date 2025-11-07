package DAO;

import java.sql.*;
import java.util.ArrayList;

import ConnectDB.ConnectDB;
import Entity.LoaiSP;

public class LoaiSP_DAO {

    // Sử dụng ConnectDB để lấy kết nối
    private Connection getConnection() throws SQLException {
        return ConnectDB.getConnection();
    }

    // Lấy maLoai dựa trên tenLoai
    public String getMaLoaiByTenSP(String tenLoai) {
        String query = "SELECT maLoai FROM LoaiSP WHERE tenLoai = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, tenLoai);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("maLoai");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Lỗi truy vấn maLoai: " + e.getMessage());
        }
        return null;
    }

    // Lấy tất cả LoaiSP
    public ArrayList<LoaiSP> getalltbLoaiSP() {
        ArrayList<LoaiSP> listSP = new ArrayList<>();
        String query = "SELECT maLoai, tenLoai FROM LoaiSP"; // Sửa tenSP thành tenLoai
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                LoaiSP loaiSP = new LoaiSP(rs.getString("maLoai"), rs.getString("tenLoai"));
                listSP.add(loaiSP);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Lỗi truy vấn danh sách LoaiSP: " + e.getMessage());
        }
        return listSP;
    }
}