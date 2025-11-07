package DAO;

import Entity.SanPham;
import javax.swing.JOptionPane;

import ConnectDB.ConnectDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductTable_DAO {

    private Connection getConnection() throws SQLException {
        return ConnectDB.getConnection();
    }

    // Thêm sản phẩm mới
    public boolean create(SanPham sp) {
        if (sp == null || sp.getMaSP() == null || sp.getMaSP().isEmpty()) {
            throw new IllegalArgumentException("Sản phẩm hoặc mã sản phẩm không hợp lệ");
        }

        String query = "INSERT INTO SanPham (maSP, tenSP, loaiSP, size, donGia, soLuongTon, moTa) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, sp.getMaSP());
            stmt.setString(2, sp.getTenSP());
            stmt.setString(3, sp.getLoaiSP());
            stmt.setString(4, sp.getSize());
            stmt.setDouble(5, sp.getDonGia());
            stmt.setInt(6, sp.getSoLuongTon());
            stmt.setString(7, sp.getMoTa());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Lỗi khi thêm sản phẩm: " + e.getMessage());
            return false;
        }
    }

    // Cập nhật sản phẩm
    public boolean update(SanPham sp) {
        if (sp == null || sp.getMaSP() == null || sp.getMaSP().isEmpty()) {
            throw new IllegalArgumentException("Sản phẩm hoặc mã sản phẩm không hợp lệ");
        }

        String query = "UPDATE SanPham SET tenSP = ?, loaiSP = ?, size = ?, donGia = ?, soLuongTon = ?, moTa = ? WHERE maSP = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, sp.getTenSP());
            stmt.setString(2, sp.getLoaiSP());
            stmt.setString(3, sp.getSize());
            stmt.setDouble(4, sp.getDonGia());
            stmt.setInt(5, sp.getSoLuongTon());
            stmt.setString(6, sp.getMoTa());
            stmt.setString(7, sp.getMaSP());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Lỗi khi cập nhật sản phẩm: " + e.getMessage());
            return false;
        }
    }

    // Xóa sản phẩm
    public boolean delete(String maSP) {
        if (maSP == null || maSP.isEmpty()) {
            throw new IllegalArgumentException("Mã sản phẩm không hợp lệ");
        }

        String query = "DELETE FROM SanPham WHERE maSP = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, maSP);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Lỗi khi xóa sản phẩm: " + e.getMessage());
            return false;
        }
    }

    // Lấy sản phẩm theo MaSP
    public SanPham getSanPhamByMaSP(String maSP) {
        String query = "SELECT maSP, tenSP, size, donGia, loaiSP, soLuongTon, moTa FROM SanPham WHERE maSP = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, maSP);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new SanPham(
                    rs.getString("maSP"),
                    rs.getString("tenSP"),
                    rs.getString("loaiSP"),
                    rs.getDouble("donGia"),
                    rs.getString("size"),
                    rs.getInt("soLuongTon"),
                    rs.getString("moTa")
                );
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Lỗi truy vấn sản phẩm: " + e.getMessage());
        }
        return null;
    }

    // Lấy tất cả sản phẩm dưới dạng Map
    public Map<String, SanPham> getAllSanPhamAsMap() {
        Map<String, SanPham> sanPhamMap = new HashMap<>();
        String query = "SELECT maSP, tenSP, size, donGia, loaiSP, soLuongTon, moTa FROM SanPham";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                SanPham sp = new SanPham(
                    rs.getString("maSP"),
                    rs.getString("tenSP"),
                    rs.getString("loaiSP"),
                    rs.getDouble("donGia"),
                    rs.getString("size"),
                    rs.getInt("soLuongTon"),
                    rs.getString("moTa")
                );
                sanPhamMap.put(sp.getMaSP(), sp);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Lỗi truy vấn tất cả sản phẩm: " + e.getMessage());
        }
        return sanPhamMap;
    }

    // Lấy sản phẩm theo loại sản phẩm (loaiSP)
    public List<SanPham> getProductsByCategory(String category) {
        List<SanPham> data = new ArrayList<>();
        String query = "SELECT maSP, tenSP, size, donGia, loaiSP, soLuongTon, moTa FROM SanPham WHERE loaiSP = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, category);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                SanPham product = new SanPham(
                    rs.getString("maSP"),
                    rs.getString("tenSP"),
                    rs.getString("loaiSP"),
                    rs.getDouble("donGia"),
                    rs.getString("size"),
                    rs.getInt("soLuongTon"),
                    rs.getString("moTa")
                );
                data.add(product);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Lỗi truy vấn sản phẩm theo danh mục: " + e.getMessage());
        }
        return data;
    }

    // Lấy sản phẩm theo tên (tìm kiếm gần đúng)
    public List<SanPham> getProductsByName(String searchText) {
        List<SanPham> data = new ArrayList<>();
        String query = "SELECT maSP, tenSP, size, donGia, loaiSP, soLuongTon, moTa FROM SanPham WHERE tenSP LIKE ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, "%" + searchText + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                SanPham product = new SanPham(
                    rs.getString("maSP"),
                    rs.getString("tenSP"),
                    rs.getString("loaiSP"),
                    rs.getDouble("donGia"),
                    rs.getString("size"),
                    rs.getInt("soLuongTon"),
                    rs.getString("moTa")
                );
                data.add(product);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Lỗi tìm kiếm sản phẩm: " + e.getMessage());
        }
        return data;
    }
}