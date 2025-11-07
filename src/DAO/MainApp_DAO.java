package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import ConnectDB.ConnectDB;
import Entity.NhanVien;

public class MainApp_DAO {

    public static NhanVien getNhanVienTheoMa(String maNV) {
        Connection conn = ConnectDB.getConnection(); 
        NhanVien nv = null;
        try {
            String sql = "SELECT maNV, hoTen, soDT, chucVu, ngayVaoLam, caLam, trangThai, matKhau FROM NhanVien WHERE maNV = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, maNV);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                nv = new NhanVien(
                        rs.getString("maNV"),
                        rs.getString("hoTen"),
                        rs.getString("soDT"),
                        rs.getString("chucVu"),
                        rs.getString("matKhau"),
                        rs.getDate("ngayVaoLam"), // Sử dụng getDate
                        rs.getString("caLam"),
                        rs.getString("trangThai")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nv;
    }
}

