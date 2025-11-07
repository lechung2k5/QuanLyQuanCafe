package DAO;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import Entity.KhachHang;

public class KhachHang_DAO {
    private Connection conn;

    // Constructor để khởi tạo kết nối với Windows Authentication
    public KhachHang_DAO() {
        try {
            String url = "jdbc:sqlserver://localhost:1433;databaseName=QuanLyQuanCafe;integratedSecurity=true;encrypt=true;trustServerCertificate=true";
            conn = DriverManager.getConnection(url);
            System.out.println("Kết nối thành công đến SQL Server!");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Lỗi kết nối SQL Server: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Lỗi kết nối: " + e.getMessage());
        }
    }

    // Phương thức lấy tất cả dữ liệu từ bảng KhachHang
    public List<KhachHang> getAllKhachHang() {
        List<KhachHang> data = new ArrayList<>();
        String query = "SELECT maKH, hoTenKH, soDT, diaChi, email, ngayDangKy, diemTichLuy FROM KhachHang";
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            int rowCount = 0;
            while (rs.next()) {
                rowCount++;
                // Định dạng ngày từ DATE sang chuỗi dd/MM/yyyy
                String ngayDangKy = dateFormat.format(rs.getDate("ngayDangKy"));
                KhachHang khachHang = new KhachHang(
                    rs.getString("maKH"),
                    rs.getString("hoTenKH"),
                    rs.getString("soDT"),
                    rs.getString("diaChi"),
                    rs.getString("email"),
                    ngayDangKy,
                    rs.getInt("diemTichLuy")
                );
                data.add(khachHang);
            }
            System.out.println("Số lượng khách hàng tìm thấy: " + rowCount);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi truy vấn: " + e.getMessage() +
                "\nVui lòng kiểm tra dữ liệu hoặc kết nối.");
        }

        return data;
    }

 // Phương thức thêm một khách hàng mới, trả về true nếu thành công
    public boolean addKhachHang(KhachHang khachHang) {
        // Kiểm tra trùng mã khách hàng
        if (isMaKhachHangExists(khachHang.getMaKH())) {
            JOptionPane.showMessageDialog(null, "Mã khách hàng đã tồn tại! Vui lòng nhập mã khác.");
            return false;
        }

        // Kiểm tra trùng số điện thoại
        if (isSoDienThoaiExists(khachHang.getSoDT())) {
            JOptionPane.showMessageDialog(null, "Số điện thoại đã tồn tại! Vui lòng nhập số khác.");
            return false;
        }

        String query = "INSERT INTO KhachHang (maKH, hoTenKH, soDT, email, diaChi, ngayDangKy, diemTichLuy) VALUES (?, ?, ?, ?, ?, ?, ?)";
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, khachHang.getMaKH());
            pstmt.setString(2, khachHang.getHoTenKH());
            pstmt.setString(3, khachHang.getSoDT());
            pstmt.setString(4, khachHang.getEmail());
            pstmt.setString(5, khachHang.getDiaChi());
            // Chuyển chuỗi ngày thành java.sql.Date
            java.util.Date parsedDate = dateFormat.parse(khachHang.getNgayDangKy());
            pstmt.setDate(6, new java.sql.Date(parsedDate.getTime()));
            pstmt.setInt(7, khachHang.getDiemTichLuy());
            pstmt.executeUpdate();
            System.out.println("Thêm khách hàng thành công: " + khachHang.getMaKH());
            return true;
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi thêm khách hàng. Vui lòng kiểm tra lại dữ liệu.");
            return false;
        } catch (ParseException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Định dạng ngày không hợp lệ! Vui lòng nhập theo dd/MM/yyyy.");
            return false;
        }
    }

    // Phương thức kiểm tra mã khách hàng tồn tại
    private boolean isMaKhachHangExists(String maKH) {
        String query = "SELECT COUNT(*) FROM KhachHang WHERE maKH = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, maKH);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    // Kiểm tra số điện thoại đã tồn tại
    public boolean isSoDienThoaiExists(String soDT) {
        String query = "SELECT COUNT(*) FROM KhachHang WHERE soDT = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, soDT);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Phương thức xóa một khách hàng
    public void deleteKhachHang(String maKH) {
        String query = "DELETE FROM KhachHang WHERE maKH = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, maKH);
            pstmt.executeUpdate();
            System.out.println("Xóa khách hàng thành công: " + maKH);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi xóa khách hàng: " + e.getMessage());
        }
    }

    // Phương thức sửa thông tin khách hàng
    public void updateKhachHang(KhachHang khachHang) {
        String query = "UPDATE KhachHang SET hoTenKH = ?, soDT = ?, email = ?, diaChi = ?, ngayDangKy = ?, diemTichLuy = ? WHERE maKH = ?";
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, khachHang.getHoTenKH());
            pstmt.setString(2, khachHang.getSoDT());
            pstmt.setString(3, khachHang.getEmail());
            pstmt.setString(4, khachHang.getDiaChi());
            // Chuyển chuỗi ngày thành java.sql.Date
            java.util.Date parsedDate = dateFormat.parse(khachHang.getNgayDangKy());
            pstmt.setDate(5, new java.sql.Date(parsedDate.getTime()));
            pstmt.setInt(6, khachHang.getDiemTichLuy());
            pstmt.setString(7, khachHang.getMaKH());
            pstmt.executeUpdate();
            System.out.println("Cập nhật khách hàng thành công: " + khachHang.getMaKH());
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi cập nhật khách hàng: " + e.getMessage());
        }
    }

    // Đóng kết nối khi không sử dụng nữa
    public void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("Đã đóng kết nối SQL Server!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
