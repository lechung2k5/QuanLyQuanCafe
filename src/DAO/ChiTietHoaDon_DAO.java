package DAO;

import java.sql.*;
import java.util.ArrayList;
import javax.swing.JOptionPane;

import ConnectDB.ConnectDB;
import Entity.ChiTietHoaDon;

public class ChiTietHoaDon_DAO {
    public static ArrayList<ChiTietHoaDon> getChiTietHoaDon(String maHD) {
        ArrayList<ChiTietHoaDon> list = new ArrayList<>();
        String sql = "SELECT maSP, soLuong, donGia, giamGia, thanhTien FROM ChiTietHoaDon WHERE maHD = ?";

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maHD);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ChiTietHoaDon ct = new ChiTietHoaDon(
                    rs.getString("maSP"),
                    rs.getInt("soLuong"),
                    rs.getDouble("donGia"),
                    rs.getDouble("giamGia"),
                    rs.getDouble("thanhTien")
                );
                list.add(ct);
            }

            if (list.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Không tìm thấy chi tiết hóa đơn cho mã: " + maHD);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi truy vấn chi tiết hóa đơn: " + e.getMessage());
        }

        return list;
    }

    public static void themChiTietHoaDon(String maHD, ChiTietHoaDon chiTiet) throws SQLException {
        String sql = "INSERT INTO ChiTietHoaDon (maHD, maSP, soLuong, donGia, giamGia, thanhTien) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maHD);
            stmt.setString(2, chiTiet.getMaSP());
            stmt.setInt(3, chiTiet.getSoLuong());
            stmt.setDouble(4, chiTiet.getDonGia());
            stmt.setDouble(5, chiTiet.getGiamGia());
            stmt.setDouble(6, chiTiet.getThanhTien());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Lỗi khi thêm chi tiết hóa đơn: " + e.getMessage());
        }
    }
}