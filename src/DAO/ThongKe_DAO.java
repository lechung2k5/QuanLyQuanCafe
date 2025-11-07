package DAO;

import Entity.HoaDon;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

import ConnectDB.ConnectDB;

public class ThongKe_DAO {
    public List<HoaDon> getOrders(String filter, LocalDate selectedDate) {
        List<HoaDon> orders = new ArrayList<>();
        String sql;

        switch (filter) {
            case "Theo ngày":
                sql = "SELECT * FROM HoaDon WHERE CAST(ngayLap AS DATE) = ?";
                break;
            case "Theo tháng":
                sql = "SELECT * FROM HoaDon WHERE MONTH(ngayLap) = ? AND YEAR(ngayLap) = ?";
                break;
            case "Theo năm":
                sql = "SELECT * FROM HoaDon WHERE YEAR(ngayLap) = ?";
                break;
            default:
                sql = "SELECT * FROM HoaDon";
        }

        try (Connection conn = ConnectDB.getConnection()) {
            if (conn == null) {
                System.err.println("Không thể kết nối đến cơ sở dữ liệu!");
                return orders;
            }

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                if (filter.equals("Theo ngày")) {
                    pstmt.setDate(1, java.sql.Date.valueOf(selectedDate != null ? selectedDate : LocalDate.now()));
                } else if (filter.equals("Theo tháng")) {
                    pstmt.setInt(1, selectedDate != null ? selectedDate.getMonthValue() : LocalDate.now().getMonthValue());
                    pstmt.setInt(2, selectedDate != null ? selectedDate.getYear() : LocalDate.now().getYear());
                } else if (filter.equals("Theo năm")) {
                    pstmt.setInt(1, selectedDate != null ? selectedDate.getYear() : LocalDate.now().getYear());
                }

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        HoaDon order = new HoaDon();
                        order.setMaHD(rs.getString("maHD"));
                        order.setMaNV(rs.getString("maNV"));
                        order.setMaKH(rs.getString("maKH"));
                        order.setMaKM(rs.getString("maKM"));
                        java.sql.Date sqlDate = rs.getDate("ngayLap");
                        order.setNgayLap(sqlDate != null ? sqlDate.toLocalDate() : null);
                        order.setTongTien(rs.getDouble("tongTien"));
                        order.setHinhThucThanhToan(rs.getString("hinhThucThanhToan"));
                        order.setVAT(rs.getDouble("VAT"));
                        orders.add(order);
                    }
                }
            }

            System.out.println("Đã lấy được " + orders.size() + " hóa đơn với bộ lọc: " + filter);
            return orders;

        } catch (SQLException e) {
            System.err.println("Lỗi khi truy vấn hóa đơn: " + e.getMessage());
            e.printStackTrace();
            return orders;
        }
    }

    public Map<String, String> getProductNames() {
        Map<String, String> productNames = new HashMap<>();
        String sql = "SELECT maSP, tenSP FROM SanPham";

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                productNames.put(rs.getString("maSP"), rs.getString("tenSP"));
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách sản phẩm: " + e.getMessage());
            e.printStackTrace();
        }

        return productNames;
    }
}