package DAO;

import Entity.HoaDon;
import UI.BillPanel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

import ConnectDB.ConnectDB;

public class HoaDon_DAO {

    // Lưu tham chiếu đến BillPanel để làm mới giao diện
    private BillPanel billPanel;

    // Constructor với tham chiếu BillPanel
    public HoaDon_DAO(BillPanel billPanel) {
        this.billPanel = billPanel;
    }

    // Constructor mặc định (cho các trường hợp không cần BillPanel)
    public HoaDon_DAO() {
        this.billPanel = null;
    }

    // Hàm refresh để làm mới danh sách hóa đơn trên giao diện
    public void refresh() {
        if (billPanel != null) {
            billPanel.refreshHoaDonList();
        }
    }

    public List<HoaDon> getAllHoaDon() {
        List<HoaDon> danhSachHoaDon = new ArrayList<>();
        // Sắp xếp theo maHD giảm dần để hiển thị hóa đơn mới nhất trước
        String sql = "SELECT maHD, maNV, maKH, maKM, ngayLap, tongTien, hinhThucThanhToan, VAT FROM HoaDon ORDER BY maHD DESC";

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                HoaDon hoaDon = new HoaDon(
                    rs.getString("maHD"),
                    rs.getString("maNV"),
                    rs.getString("maKH"),
                    rs.getString("maKM"),
                    rs.getDate("ngayLap").toLocalDate(),
                    rs.getDouble("tongTien"),
                    rs.getString("hinhThucThanhToan"),
                    rs.getDouble("VAT")
                );
                danhSachHoaDon.add(hoaDon);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi truy vấn danh sách hóa đơn: " + e.getMessage());
        }

        return danhSachHoaDon;
    }

    public boolean themHoaDon(HoaDon hd) {
        String sql = "INSERT INTO HoaDon (maHD, maNV, maKH, maKM, ngayLap, tongTien, hinhThucThanhToan, VAT) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        try {
            conn = ConnectDB.getConnection();
            conn.setAutoCommit(false); // Tắt autocommit để kiểm soát giao dịch thủ công
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, hd.getMaHD());
                stmt.setString(2, hd.getMaNV());
                stmt.setString(3, hd.getMaKH());
                stmt.setString(4, hd.getMaKM());
                stmt.setDate(5, java.sql.Date.valueOf(hd.getNgayLap()));
                stmt.setDouble(6, hd.getTongTien());
                stmt.setString(7, hd.getHinhThucThanhToan());
                stmt.setDouble(8, hd.getVAT());
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    conn.commit(); // Commit giao dịch
                    refresh(); // Làm mới giao diện ngay sau khi thêm hóa đơn
                    return true;
                } else {
                    conn.rollback(); // Rollback nếu không thêm được
                    return false;
                }
            }
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback nếu có lỗi
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi thêm hóa đơn: " + e.getMessage());
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Khôi phục chế độ autocommit
                    conn.close();
                } catch (SQLException closeEx) {
                    closeEx.printStackTrace();
                }
            }
        }
    }

    public List<HoaDon> timKiemHoaDonTheoMa(String maHD) {
        List<HoaDon> danhSachHoaDon = new ArrayList<>();
        String sql = "SELECT maHD, maNV, maKH, maKM, ngayLap, tongTien, hinhThucThanhToan, VAT FROM HoaDon WHERE maHD LIKE ? ORDER BY maHD DESC";

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + maHD + "%"); // Tìm kiếm gần đúng
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                HoaDon hd = new HoaDon(
                    rs.getString("maHD"),
                    rs.getString("maNV"),
                    rs.getString("maKH"),
                    rs.getString("maKM"),
                    rs.getDate("ngayLap").toLocalDate(),
                    rs.getDouble("tongTien"),
                    rs.getString("hinhThucThanhToan"),
                    rs.getDouble("VAT")
                );
                danhSachHoaDon.add(hd);
            }

            if (danhSachHoaDon.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Không tìm thấy hóa đơn với mã: " + maHD);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi truy vấn hóa đơn: " + e.getMessage());
        }

        return danhSachHoaDon;
    }

    public static HoaDon getHoaDonByMaHD(String maHD) {
        HoaDon hoaDon = null;
        String sql = "SELECT maHD, maNV, maKH, maKM, ngayLap, tongTien, hinhThucThanhToan, VAT FROM HoaDon WHERE maHD = ?";

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maHD);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    hoaDon = new HoaDon(
                        rs.getString("maHD"),
                        rs.getString("maNV"),
                        rs.getString("maKH"),
                        rs.getString("maKM"),
                        rs.getDate("ngayLap").toLocalDate(),
                        rs.getDouble("tongTien"),
                        rs.getString("hinhThucThanhToan"),
                        rs.getDouble("VAT")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi truy vấn hóa đơn: " + e.getMessage());
        }

        return hoaDon;
    }

    public List<HoaDon> getHoaDonByMaKhachHang(String maKhachHang) {
        List<HoaDon> danhSachHoaDon = new ArrayList<>();
        String sql = "SELECT maHD, maNV, maKH, maKM, ngayLap, tongTien, hinhThucThanhToan, VAT FROM HoaDon WHERE maKH = ? ORDER BY maHD DESC";

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maKhachHang);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                HoaDon hd = new HoaDon(
                    rs.getString("maHD"),
                    rs.getString("maNV"),
                    rs.getString("maKH"),
                    rs.getString("maKM"),
                    rs.getDate("ngayLap").toLocalDate(),
                    rs.getDouble("tongTien"),
                    rs.getString("hinhThucThanhToan"),
                    rs.getDouble("VAT")
                );
                danhSachHoaDon.add(hd);
            }

            if (danhSachHoaDon.isEmpty()) {
                System.out.println("Không tìm thấy hóa đơn nào cho khách hàng: " + maKhachHang);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi truy vấn hóa đơn theo mã khách hàng: " + e.getMessage());
        }

        return danhSachHoaDon;
    }

    public String getLatestInvoiceId() {
        String latestId = "HD000"; // Giá trị mặc định nếu không có hóa đơn
        String sql = "SELECT MAX(maHD) FROM HoaDon";

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                String maxId = rs.getString(1); // Lấy mã hóa đơn lớn nhất
                if (maxId != null && maxId.matches("HD\\d{3}")) {
                    latestId = maxId;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi truy vấn mã hóa đơn lớn nhất: " + e.getMessage());
        }

        return latestId;
    }
}