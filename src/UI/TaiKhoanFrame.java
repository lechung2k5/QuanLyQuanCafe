package UI;

import DAO.NhanVien_DAO;
import Entity.NhanVien;

import javax.swing.*;
import java.awt.*;
import java.sql.Date;
import java.text.SimpleDateFormat;

public class TaiKhoanFrame extends JFrame {
    private final NhanVien nhanVien;
    private final NhanVien_DAO nhanVienDao;
    private JTextField txtMaNV, txtHoTen, txtSoDT, txtChucVu, txtNgayVaoLam, txtCaLam, txtTrangThai;
    private JPasswordField txtMatKhauCu, txtMatKhauMoi, txtXacNhanMatKhau;

    public TaiKhoanFrame(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
        this.nhanVienDao = new NhanVien_DAO();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Thông tin tài khoản");
        setSize(500, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitle = new JLabel("Thông tin tài khoản", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(lblTitle, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        mainPanel.add(new JLabel("Mã nhân viên:"), gbc);
        txtMaNV = new JTextField(nhanVien.getMaNV());
        txtMaNV.setEditable(false);
        gbc.gridx = 1;
        mainPanel.add(txtMaNV, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        mainPanel.add(new JLabel("Họ tên:"), gbc);
        txtHoTen = new JTextField(nhanVien.getHoTenNV());
        txtHoTen.setEditable(false);
        gbc.gridx = 1;
        mainPanel.add(txtHoTen, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        mainPanel.add(new JLabel("Số điện thoại:"), gbc);
        txtSoDT = new JTextField(nhanVien.getSoDT());
        txtSoDT.setEditable(true); // Cho phép chỉnh sửa số điện thoại
        gbc.gridx = 1;
        mainPanel.add(txtSoDT, gbc); // Sửa lỗi: Sử dụng gbc thay vì cú pháp sai

        gbc.gridx = 0;
        gbc.gridy++;
        mainPanel.add(new JLabel("Chức vụ:"), gbc);
        txtChucVu = new JTextField(nhanVien.getChucVu());
        txtChucVu.setEditable(false);
        gbc.gridx = 1;
        mainPanel.add(txtChucVu, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        mainPanel.add(new JLabel("Ngày vào làm:"), gbc);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        txtNgayVaoLam = new JTextField(nhanVien.getNgayVaoLam() != null ? sdf.format(nhanVien.getNgayVaoLam()) : "");
        txtNgayVaoLam.setEditable(false);
        gbc.gridx = 1;
        mainPanel.add(txtNgayVaoLam, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        mainPanel.add(new JLabel("Ca làm:"), gbc);
        txtCaLam = new JTextField(nhanVien.getCaLam());
        txtCaLam.setEditable(false);
        gbc.gridx = 1;
        mainPanel.add(txtCaLam, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        mainPanel.add(new JLabel("Trạng thái:"), gbc);
        txtTrangThai = new JTextField(nhanVien.getTrangThai());
        txtTrangThai.setEditable(false);
        gbc.gridx = 1;
        mainPanel.add(txtTrangThai, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        mainPanel.add(new JLabel("Mật khẩu cũ:"), gbc);
        txtMatKhauCu = new JPasswordField();
        gbc.gridx = 1;
        mainPanel.add(txtMatKhauCu, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        mainPanel.add(new JLabel("Mật khẩu mới:"), gbc);
        txtMatKhauMoi = new JPasswordField();
        gbc.gridx = 1;
        mainPanel.add(txtMatKhauMoi, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        mainPanel.add(new JLabel("Xác nhận mật khẩu:"), gbc);
        txtXacNhanMatKhau = new JPasswordField();
        gbc.gridx = 1;
        mainPanel.add(txtXacNhanMatKhau, gbc);

        JButton btnCapNhat = new JButton("Cập nhật thông tin");
        btnCapNhat.setBackground(Color.GREEN);
        btnCapNhat.setForeground(Color.WHITE);
        btnCapNhat.addActionListener(e -> capNhatThongTin());
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(btnCapNhat, gbc);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void capNhatThongTin() {
        String soDT = txtSoDT.getText().trim();
        String matKhauCu = new String(txtMatKhauCu.getPassword()).trim();
        String matKhauMoi = new String(txtMatKhauMoi.getPassword()).trim();
        String xacNhanMatKhau = new String(txtXacNhanMatKhau.getPassword()).trim();

        // Kiểm tra số điện thoại
        if (!soDT.isEmpty() && !soDT.matches("\\d{10,11}")) {
            JOptionPane.showMessageDialog(this, "Số điện thoại không hợp lệ! Vui lòng nhập 10-11 chữ số.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Kiểm tra nếu có nhập mật khẩu
        boolean capNhatMatKhau = !matKhauCu.isEmpty() || !matKhauMoi.isEmpty() || !xacNhanMatKhau.isEmpty();
        if (capNhatMatKhau) {
            // Kiểm tra mật khẩu cũ từ TaiKhoan
            if (!nhanVienDao.kiemTraMatKhau(nhanVien.getMaNV(), matKhauCu)) {
                JOptionPane.showMessageDialog(this, "Mật khẩu cũ không đúng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Kiểm tra mật khẩu mới và xác nhận
            if (!matKhauMoi.equals(xacNhanMatKhau)) {
                JOptionPane.showMessageDialog(this, "Mật khẩu mới và xác nhận không khớp!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Kiểm tra độ dài mật khẩu mới
            if (matKhauMoi.length() < 6) {
                JOptionPane.showMessageDialog(this, "Mật khẩu mới phải có ít nhất 6 ký tự!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // Tạo đối tượng NhanVien mới với thông tin cập nhật
        NhanVien nhanVienCapNhat = new NhanVien(
            nhanVien.getMaNV(),
            nhanVien.getHoTenNV(),
            soDT,
            nhanVien.getChucVu(),
            capNhatMatKhau ? matKhauMoi : nhanVien.getMatKhau(),
            nhanVien.getNgayVaoLam(),
            nhanVien.getCaLam(),
            nhanVien.getTrangThai()
        );

        // Cập nhật thông tin trong bảng NhanVien
        if (nhanVienDao.update(nhanVienCapNhat)) {
            // Nếu cập nhật mật khẩu, đồng bộ với bảng TaiKhoan
            if (capNhatMatKhau && !nhanVienDao.capNhatMatKhau(nhanVien.getMaNV(), matKhauMoi)) {
                JOptionPane.showMessageDialog(this, "Cập nhật mật khẩu trong TaiKhoan thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Cập nhật đối tượng nhanVien
            nhanVien.setSoDT(soDT);
            if (capNhatMatKhau) {
                nhanVien.setMatKhau(matKhauMoi);
            }

            JOptionPane.showMessageDialog(this, "Cập nhật thông tin thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            // Xóa các trường nhập
            txtMatKhauCu.setText("");
            txtMatKhauMoi.setText("");
            txtXacNhanMatKhau.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật thông tin thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}