package DAO;

import Entity.NhanVien;

import java.sql.*;
import java.util.ArrayList;

import ConnectDB.ConnectDB;

public class NhanVien_DAO {
    private ArrayList<NhanVien> dsNV;

    public NhanVien_DAO() {
        dsNV = new ArrayList<>();
    }

    public ArrayList<NhanVien> getDSNV() {
        return dsNV;
    }

    public void setDsNV(ArrayList<NhanVien> dsNV) {
        this.dsNV = dsNV;
    }

    public NhanVien getElement(int index) {
        if (index < 0 || index >= dsNV.size()) return null;
        return dsNV.get(index);
    }

    public int getSize() {
        return dsNV.size();
    }

    public boolean create(NhanVien nv) {
        if (nv == null || nv.getMaNV() == null || nv.getMaNV().isEmpty()) {
            throw new IllegalArgumentException("Nhân viên hoặc mã nhân viên không hợp lệ");
        }

        Connection conn = null;
        PreparedStatement stmtNhanVien = null;
        PreparedStatement stmtTaiKhoan = null;
        boolean success = false;

        try {
            conn = ConnectDB.getConnection();
            if (conn == null) {
                throw new SQLException("Không thể kết nối đến cơ sở dữ liệu");
            }

            conn.setAutoCommit(false);

            String sqlNhanVien = "INSERT INTO NhanVien (maNV, hoTen, soDT, chucVu, ngayVaoLam, caLam, trangThai, matKhau) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            stmtNhanVien = conn.prepareStatement(sqlNhanVien);
            stmtNhanVien.setString(1, nv.getMaNV());
            stmtNhanVien.setString(2, nv.getHoTenNV());
            stmtNhanVien.setString(3, nv.getSoDT());
            stmtNhanVien.setString(4, nv.getChucVu());
            stmtNhanVien.setDate(5, nv.getNgayVaoLam());
            stmtNhanVien.setString(6, nv.getCaLam());
            stmtNhanVien.setString(7, nv.getTrangThai());
            stmtNhanVien.setString(8, nv.getMatKhau());
            int rowsNhanVien = stmtNhanVien.executeUpdate();

            String sqlTaiKhoan = "INSERT INTO TaiKhoan (username, password, vaiTro, maNV) VALUES (?, ?, ?, ?)";
            stmtTaiKhoan = conn.prepareStatement(sqlTaiKhoan);
            stmtTaiKhoan.setString(1, nv.getMaNV());
            stmtTaiKhoan.setString(2, nv.getMatKhau());
            stmtTaiKhoan.setString(3, "NhanVien");
            stmtTaiKhoan.setString(4, nv.getMaNV());
            int rowsTaiKhoan = stmtTaiKhoan.executeUpdate();

            if (rowsNhanVien > 0 && rowsTaiKhoan > 0) {
                conn.commit();
                success = true;
            } else {
                conn.rollback();
            }
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException rollbackEx) {
                System.err.println("Lỗi rollback: " + rollbackEx.getMessage());
            }
            System.err.println("Lỗi khi thêm nhân viên: " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            try {
                if (stmtNhanVien != null) stmtNhanVien.close();
                if (stmtTaiKhoan != null) stmtTaiKhoan.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException closeEx) {
                System.err.println("Lỗi đóng kết nối: " + closeEx.getMessage());
            }
        }

        return success;
    }

    public boolean update(NhanVien nv) {
        if (nv == null || nv.getMaNV() == null || nv.getMaNV().isEmpty()) {
            throw new IllegalArgumentException("Nhân viên hoặc mã nhân viên không hợp lệ");
        }

        String sql = "UPDATE NhanVien SET hoTen = ?, soDT = ?, chucVu = ?, ngayVaoLam = ?, caLam = ?, trangThai = ?, matKhau = ? WHERE maNV = ?";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nv.getHoTenNV());
            stmt.setString(2, nv.getSoDT());
            stmt.setString(3, nv.getChucVu());
            stmt.setDate(4, nv.getNgayVaoLam());
            stmt.setString(5, nv.getCaLam());
            stmt.setString(6, nv.getTrangThai());
            stmt.setString(7, nv.getMatKhau());
            stmt.setString(8, nv.getMaNV());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật nhân viên: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public boolean delete(String maNV) {
        if (maNV == null || maNV.isEmpty()) {
            throw new IllegalArgumentException("Mã nhân viên không hợp lệ");
        }

        Connection conn = null;
        PreparedStatement stmtTaiKhoan = null;
        PreparedStatement stmtNhanVien = null;
        boolean success = false;

        try {
            conn = ConnectDB.getConnection();
            if (conn == null) {
                throw new SQLException("Không thể kết nối đến cơ sở dữ liệu");
            }

            conn.setAutoCommit(false);

            String sqlTaiKhoan = "DELETE FROM TaiKhoan WHERE maNV = ?";
            stmtTaiKhoan = conn.prepareStatement(sqlTaiKhoan);
            stmtTaiKhoan.setString(1, maNV);
            stmtTaiKhoan.executeUpdate();

            String sqlNhanVien = "DELETE FROM NhanVien WHERE maNV = ?";
            stmtNhanVien = conn.prepareStatement(sqlNhanVien);
            stmtNhanVien.setString(1, maNV);
            int rowsNhanVien = stmtNhanVien.executeUpdate();

            if (rowsNhanVien > 0) {
                conn.commit();
                success = true;
            } else {
                conn.rollback();
            }
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException rollbackEx) {
                System.err.println("Lỗi rollback: " + rollbackEx.getMessage());
            }
            System.err.println("Lỗi khi xóa nhân viên: " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            try {
                if (stmtTaiKhoan != null) stmtTaiKhoan.close();
                if (stmtNhanVien != null) stmtNhanVien.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException closeEx) {
                System.err.println("Lỗi đóng kết nối: " + closeEx.getMessage());
            }
        }

        return success;
    }

    public NhanVien timNhanVien(String maNV) {
        for (NhanVien nv : dsNV) {
            if (nv.getMaNV().equalsIgnoreCase(maNV.trim())) {
                return nv;
            }
        }
        return null;
    }

    public ArrayList<NhanVien> getalltbNhanVien() {
        ArrayList<NhanVien> dsnv = new ArrayList<>();
        String sql = "SELECT * FROM NhanVien";

        try (Connection conn = ConnectDB.getConnection();
             Statement statement = conn.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {

            while (rs.next()) {
                NhanVien nv = new NhanVien(
                    rs.getString("maNV"),
                    rs.getString("hoTen"),
                    rs.getString("soDT"),
                    rs.getString("chucVu"),
                    rs.getString("matKhau"),
                    rs.getDate("ngayVaoLam"),
                    rs.getString("caLam"),
                    rs.getString("trangThai")
                );
                dsnv.add(nv);
            }

        } catch (SQLException e) {
            System.err.println("Lỗi truy vấn danh sách nhân viên: " + e.getMessage());
        }

        return dsnv;
    }

    public ArrayList<NhanVien> getNhanVienTheoMaNV(String maNV) {
        ArrayList<NhanVien> dsnv = new ArrayList<>();
        String sql = "SELECT * FROM NhanVien WHERE maNV = ?";

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, maNV);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                NhanVien nv = new NhanVien(
                    rs.getString("maNV"),
                    rs.getString("hoTen"),
                    rs.getString("soDT"),
                    rs.getString("chucVu"),
                    rs.getString("matKhau"),
                    rs.getDate("ngayVaoLam"),
                    rs.getString("caLam"),
                    rs.getString("trangThai")
                );
                dsnv.add(nv);
            }

        } catch (SQLException e) {
            System.err.println("Lỗi truy vấn nhân viên theo mã: " + e.getMessage());
        }

        return dsnv;
    }

    public NhanVien authenticate(String username, String password) {
        String sql = "SELECT n.maNV, n.hoTen, n.soDT, n.chucVu, t.password AS matKhau, n.ngayVaoLam, n.caLam, n.trangThai " +
                     "FROM TaiKhoan t JOIN NhanVien n ON t.maNV = n.maNV " +
                     "WHERE t.username = ? AND t.password = ?";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new NhanVien(
                    rs.getString("maNV"),
                    rs.getString("hoTen"),
                    rs.getString("soDT"),
                    rs.getString("chucVu"),
                    rs.getString("matKhau"), // Lấy từ TaiKhoan.password
                    rs.getDate("ngayVaoLam"),
                    rs.getString("caLam"),
                    rs.getString("trangThai")
                );
            }

        } catch (SQLException e) {
            System.err.println("Lỗi xác thực đăng nhập: " + e.getMessage());
        }

        return null;
    }

    // Thêm phương thức kiểm tra mật khẩu cũ từ TaiKhoan
    public boolean kiemTraMatKhau(String maNV, String matKhau) {
        String sql = "SELECT password FROM TaiKhoan WHERE maNV = ? AND password = ?";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, maNV);
            stmt.setString(2, matKhau);
            ResultSet rs = stmt.executeQuery();

            return rs.next(); // Trả về true nếu tìm thấy bản ghi khớp
        } catch (SQLException e) {
            System.err.println("Lỗi kiểm tra mật khẩu: " + e.getMessage());
            return false;
        }
    }

    public boolean capNhatMatKhau(String maNV, String matKhauMoi) {
        Connection conn = null;
        PreparedStatement stmtNhanVien = null;
        PreparedStatement stmtTaiKhoan = null;
        boolean success = false;

        try {
            conn = ConnectDB.getConnection();
            if (conn == null) {
                throw new SQLException("Không thể kết nối đến cơ sở dữ liệu");
            }

            conn.setAutoCommit(false);

            // Cập nhật mật khẩu trong bảng NhanVien
            String sqlNhanVien = "UPDATE NhanVien SET matKhau = ? WHERE maNV = ?";
            stmtNhanVien = conn.prepareStatement(sqlNhanVien);
            stmtNhanVien.setString(1, matKhauMoi);
            stmtNhanVien.setString(2, maNV);
            int rowsNhanVien = stmtNhanVien.executeUpdate();

            // Cập nhật mật khẩu trong bảng TaiKhoan
            String sqlTaiKhoan = "UPDATE TaiKhoan SET password = ? WHERE maNV = ?";
            stmtTaiKhoan = conn.prepareStatement(sqlTaiKhoan);
            stmtTaiKhoan.setString(1, matKhauMoi);
            stmtTaiKhoan.setString(2, maNV);
            int rowsTaiKhoan = stmtTaiKhoan.executeUpdate();

            if (rowsNhanVien > 0 && rowsTaiKhoan > 0) {
                conn.commit();
                success = true;
            } else {
                conn.rollback();
            }
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException rollbackEx) {
                System.err.println("Lỗi rollback: " + rollbackEx.getMessage());
            }
            System.err.println("Lỗi khi cập nhật mật khẩu: " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            try {
                if (stmtNhanVien != null) stmtNhanVien.close();
                if (stmtTaiKhoan != null) stmtTaiKhoan.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException closeEx) {
                System.err.println("Lỗi đóng kết nối: " + closeEx.getMessage());
            }
        }

        return success;
    }
}