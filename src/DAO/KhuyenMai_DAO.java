package DAO;

import Entity.KhuyenMai;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import ConnectDB.ConnectDB;

public class KhuyenMai_DAO {
    private Connection conn;

    public KhuyenMai_DAO() throws SQLException {
        conn = ConnectDB.getConnection();
    }

    public List<KhuyenMai> getAllKhuyenMai() throws SQLException {
        List<KhuyenMai> dsKhuyenMai = new ArrayList<>();
        // Temporarily fetch all promotions to test JComboBox display
        String sql = "SELECT * FROM KhuyenMai";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                KhuyenMai km = new KhuyenMai(
                    rs.getString("maKM"),
                    rs.getString("tenChuongTrinh"),
                    rs.getDouble("giamGia"),
                    rs.getDate("ngayBatDau").toLocalDate(),
                    rs.getDate("ngayKetThuc").toLocalDate(),
                    rs.getString("trangThai")
                );
                dsKhuyenMai.add(km);
            }
        } catch (SQLException e) {
            throw new SQLException("Lỗi khi lấy danh sách khuyến mãi: " + e.getMessage());
        }
        return dsKhuyenMai;
    }

    public String getMaxMaKM() throws SQLException {
        String sql = "SELECT MAX(maKM) FROM KhuyenMai";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                String maxMaKM = rs.getString(1);
                if (maxMaKM != null) {
                    return maxMaKM;
                }
            }
        }
        return "KM000"; // Nếu bảng rỗng, trả về mã khởi đầu
    }

    public void themKhuyenMai(KhuyenMai km) throws SQLException {
        String sql = "INSERT INTO KhuyenMai (maKM, tenChuongTrinh, giamGia, ngayBatDau, ngayKetThuc, trangThai) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, km.getMaKM());
            stmt.setString(2, km.getTenChuongTrinh());
            stmt.setDouble(3, km.getGiamGia());
            stmt.setDate(4, java.sql.Date.valueOf(km.getNgayBatDau()));
            stmt.setDate(5, java.sql.Date.valueOf(km.getNgayKetThuc()));
            stmt.setString(6, km.getTrangThai());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Lỗi khi thêm khuyến mãi: " + e.getMessage());
        }
    }

    public void capNhatKhuyenMai(KhuyenMai km) throws SQLException {
        String sql = "UPDATE KhuyenMai SET tenChuongTrinh = ?, giamGia = ?, ngayBatDau = ?, ngayKetThuc = ?, trangThai = ? WHERE maKM = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, km.getTenChuongTrinh());
            stmt.setDouble(2, km.getGiamGia());
            stmt.setDate(3, java.sql.Date.valueOf(km.getNgayBatDau()));
            stmt.setDate(4, java.sql.Date.valueOf(km.getNgayKetThuc()));
            stmt.setString(5, km.getTrangThai());
            stmt.setString(6, km.getMaKM());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Lỗi khi cập nhật khuyến mãi: " + e.getMessage());
        }
    }

    public void xoaKhuyenMai(String maKM) throws SQLException {
        String sql = "DELETE FROM KhuyenMai WHERE maKM = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maKM);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Lỗi khi xóa khuyến mãi: " + e.getMessage());
        }
    }
}