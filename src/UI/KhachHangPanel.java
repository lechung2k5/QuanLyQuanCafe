package UI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import DAO.KhachHang_DAO;
import DAO.HoaDon_DAO;
import Entity.KhachHang;
import Entity.HoaDon;

public class KhachHangPanel extends JPanel {
    // Khai báo các thành phần giao diện
    private JLabel lblTieuDe;
    private JComboBox<String> cbLocLoaiKhachHang;
    private JTextField txtTimKiem;
    private JButton btnTimKiem;
    private JTable bangKhachHang;
    private DefaultTableModel modelBang;
    private JScrollPane thanhCuonBang;
    private JTextField txtMaKhachHang;
    private JTextField txtHoTen;
    private JTextField txtSoDienThoai;
    private JTextField txtEmail;
    private JTextField txtDiaChi;
    private JTextField txtNgayDangKy;
    private JButton btnThem;
    private JButton btnXoa;
    private JButton btnSua;
    private JButton btnLuu;
    private List<Object[]> originalData; // Lưu trữ dữ liệu gốc để lọc
    private KhachHang_DAO khachHangDAO; // DAO để truy cập dữ liệu khách hàng
    private HoaDon_DAO hoaDonDAO; // DAO để truy cập dữ liệu hóa đơn

    public KhachHangPanel() {
        // Khởi tạo DAO
        khachHangDAO = new KhachHang_DAO();
        hoaDonDAO = new HoaDon_DAO();

        // Khởi tạo danh sách lưu dữ liệu gốc
        originalData = new ArrayList<>();

        // Thiết lập layout chính cho panel
        setLayout(new BorderLayout());
        setBackground(Color.decode("#F5F5F5"));

        // Tạo panel tiêu đề và bộ lọc
        JPanel panelTieuDe = new JPanel(new BorderLayout());
        panelTieuDe.setBackground(Color.decode("#F5F5F5"));

        // Tiêu đề "Danh sách khách hàng" và bộ lọc
        JPanel panelTieuDeVaLoc = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelTieuDeVaLoc.setBackground(Color.decode("#F5F5F5"));
        lblTieuDe = new JLabel("Danh sách khách hàng");
        lblTieuDe.setFont(new Font("Roboto", Font.BOLD, 20));
        lblTieuDe.setForeground(Color.decode("#424242"));
        panelTieuDeVaLoc.add(lblTieuDe);

        // Bộ lọc loại khách hàng
        String[] loaiKhachHang = {"VIP, Thành viên...", "VIP", "Thành viên"};
        cbLocLoaiKhachHang = new JComboBox<>(loaiKhachHang);
        cbLocLoaiKhachHang.setFont(new Font("Roboto", Font.PLAIN, 14));
        cbLocLoaiKhachHang.setPreferredSize(new Dimension(150, 30));
        panelTieuDeVaLoc.add(cbLocLoaiKhachHang);

        panelTieuDe.add(panelTieuDeVaLoc, BorderLayout.WEST);

        // Trường tìm kiếm và nút tìm kiếm
        JPanel panelTimKiem = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelTimKiem.setBackground(Color.decode("#F5F5F5"));
        txtTimKiem = new JTextField("Nhập SĐT khách hàng cần tìm");
        txtTimKiem.setFont(new Font("Roboto", Font.PLAIN, 14));
        txtTimKiem.setForeground(Color.GRAY);
        txtTimKiem.setPreferredSize(new Dimension(200, 30));
        txtTimKiem.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (txtTimKiem.getText().equals("Nhập SĐT khách hàng cần tìm")) {
                    txtTimKiem.setText("");
                    txtTimKiem.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (txtTimKiem.getText().isEmpty()) {
                    txtTimKiem.setText("Nhập SĐT khách hàng cần tìm");
                    txtTimKiem.setForeground(Color.GRAY);
                }
            }
        });
        panelTimKiem.add(txtTimKiem);

        // Nút tìm kiếm
        btnTimKiem = new JButton("Tìm");
        btnTimKiem.setBackground(Color.decode("#2196F3"));
        btnTimKiem.setForeground(Color.WHITE);
        btnTimKiem.setFont(new Font("Roboto", Font.BOLD, 14));
        btnTimKiem.setPreferredSize(new Dimension(80, 30));
        panelTimKiem.add(btnTimKiem);

        panelTieuDe.add(panelTimKiem, BorderLayout.EAST);
        add(panelTieuDe, BorderLayout.NORTH);

        // Tạo bảng danh sách khách hàng
        String[] tenCot = {"Mã khách hàng", "Họ tên", "Số điện thoại", "Địa chỉ", "Email", "Ngày đăng ký", "Loại khách hàng", "Tổng tiền hóa đơn", "Xem lịch sử hóa đơn"};
        modelBang = new DefaultTableModel(tenCot, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 8;
            }
        };
        bangKhachHang = new JTable(modelBang);
        bangKhachHang.setFont(new Font("Roboto", Font.PLAIN, 14));
        bangKhachHang.setRowHeight(30);
        bangKhachHang.getTableHeader().setFont(new Font("Roboto", Font.BOLD, 14));
        bangKhachHang.getTableHeader().setBackground(Color.decode("#E6F0FF"));
        bangKhachHang.getTableHeader().setForeground(Color.decode("#424242"));
        bangKhachHang.getColumnModel().getColumn(8).setCellRenderer(new ButtonRenderer());
        bangKhachHang.getColumnModel().getColumn(8).setCellEditor(new ButtonEditor(new JCheckBox()));
        thanhCuonBang = new JScrollPane(bangKhachHang);
        add(thanhCuonBang, BorderLayout.CENTER);

        // Tạo panel chứa các trường nhập liệu và nút
        JPanel panelNhapLieu = new JPanel(new GridLayout(3, 2, 20, 10));
        panelNhapLieu.setBackground(Color.decode("#F5F5F5"));
        panelNhapLieu.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Hàng 1: Mã khách hàng và Họ tên
        JLabel lblMaKhachHang = new JLabel("Mã khách hàng:");
        lblMaKhachHang.setFont(new Font("Roboto", Font.PLAIN, 16));
        panelNhapLieu.add(lblMaKhachHang);
        txtMaKhachHang = new JTextField();
        txtMaKhachHang.setFont(new Font("Roboto", Font.PLAIN, 16));
        panelNhapLieu.add(txtMaKhachHang);

        JLabel lblHoTen = new JLabel("Họ tên:");
        lblHoTen.setFont(new Font("Roboto", Font.PLAIN, 16));
        panelNhapLieu.add(lblHoTen);
        txtHoTen = new JTextField();
        txtHoTen.setFont(new Font("Roboto", Font.PLAIN, 16));
        panelNhapLieu.add(txtHoTen);

        // Hàng 2: Số điện thoại và Email
        JLabel lblSoDienThoai = new JLabel("Số điện thoại:");
        lblSoDienThoai.setFont(new Font("Roboto", Font.PLAIN, 16));
        panelNhapLieu.add(lblSoDienThoai);
        txtSoDienThoai = new JTextField();
        txtSoDienThoai.setFont(new Font("Roboto", Font.PLAIN, 16));
        panelNhapLieu.add(txtSoDienThoai);

        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setFont(new Font("Roboto", Font.PLAIN, 16));
        panelNhapLieu.add(lblEmail);
        txtEmail = new JTextField();
        txtEmail.setFont(new Font("Roboto", Font.PLAIN, 16));
        panelNhapLieu.add(txtEmail);

        // Hàng 3: Ngày đăng ký và Địa chỉ
        JLabel lblNgayDangKy = new JLabel("Ngày đăng ký:");
        lblNgayDangKy.setFont(new Font("Roboto", Font.PLAIN, 16));
        panelNhapLieu.add(lblNgayDangKy);
        txtNgayDangKy = new JTextField();
        txtNgayDangKy.setFont(new Font("Roboto", Font.PLAIN, 16));
        txtNgayDangKy.setForeground(Color.GRAY);
        panelNhapLieu.add(txtNgayDangKy);

        JLabel lblDiaChi = new JLabel("Địa chỉ:");
        lblDiaChi.setFont(new Font("Roboto", Font.PLAIN, 16));
        panelNhapLieu.add(lblDiaChi);
        txtDiaChi = new JTextField();
        txtDiaChi.setFont(new Font("Roboto", Font.PLAIN, 16));
        panelNhapLieu.add(txtDiaChi);

        // Tạo panel chứa các nút chức năng
        JPanel panelNut = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panelNut.setBackground(Color.decode("#F5F5F5"));

        btnThem = new JButton("Thêm");
        btnThem.setBackground(Color.decode("#0BB783"));
        btnThem.setForeground(Color.WHITE);
        btnThem.setFont(new Font("Roboto", Font.BOLD, 16));
        btnThem.setPreferredSize(new Dimension(120, 40));

        btnXoa = new JButton("Xóa");
        btnXoa.setBackground(Color.RED);
        btnXoa.setForeground(Color.WHITE);
        btnXoa.setFont(new Font("Roboto", Font.BOLD, 16));
        btnXoa.setPreferredSize(new Dimension(120, 40));

        btnSua = new JButton("Sửa");
        btnSua.setBackground(Color.BLUE);
        btnSua.setForeground(Color.WHITE);
        btnSua.setFont(new Font("Roboto", Font.BOLD, 16));
        btnSua.setPreferredSize(new Dimension(120, 40));

        btnLuu = new JButton("Lưu");
        btnLuu.setBackground(Color.YELLOW);
        btnLuu.setForeground(Color.BLACK);
        btnLuu.setFont(new Font("Roboto", Font.BOLD, 16));
        btnLuu.setPreferredSize(new Dimension(120, 40));

        panelNut.add(btnThem);
        panelNut.add(btnXoa);
        panelNut.add(btnSua);
        panelNut.add(btnLuu);

        // Tạo panel tổng hợp để chứa panel nhập liệu và panel nút
        JPanel panelTongHop = new JPanel(new BorderLayout());
        panelTongHop.setBackground(Color.decode("#F5F5F5"));
        panelTongHop.add(panelNhapLieu, BorderLayout.CENTER);
        panelTongHop.add(panelNut, BorderLayout.SOUTH);
        add(panelTongHop, BorderLayout.SOUTH);

        // Tải dữ liệu từ cơ sở dữ liệu
        taiDuLieuTuCSDL();

        // Thêm sự kiện cho các nút và JComboBox
        themSuKienChoNut();
        themSuKienChoComboBox();
    }

    // Phương thức tính tổng tiền hóa đơn của một khách hàng
    private double tinhTongTienHoaDon(String maKhachHang) {
        List<HoaDon> danhSachHoaDon = hoaDonDAO.getHoaDonByMaKhachHang(maKhachHang);
        double tongTien = 0.0;
        for (HoaDon hoaDon : danhSachHoaDon) {
            tongTien += hoaDon.getTongTien();
        }
        return tongTien;
    }

    // Phương thức tải dữ liệu từ cơ sở dữ liệu
    private void taiDuLieuTuCSDL() {
        List<KhachHang> danhSachKhachHang = khachHangDAO.getAllKhachHang();
        modelBang.setRowCount(0);
        originalData.clear();

        for (KhachHang khachHang : danhSachKhachHang) {
            String loaiKhachHang = khachHang.getDiemTichLuy() >= 30 ? "VIP" : "Thành viên";
            double tongTienHoaDon = tinhTongTienHoaDon(khachHang.getMaKH());
            String tongTienHoaDonFormatted = String.format("%,.0f VNĐ", tongTienHoaDon);

            Object[] row = {
                khachHang.getMaKH(),
                khachHang.getHoTenKH(),
                khachHang.getSoDT(),
                khachHang.getDiaChi(),
                khachHang.getEmail(),
                khachHang.getNgayDangKy(),
                loaiKhachHang,
                tongTienHoaDonFormatted,
                "Xem"
            };
            modelBang.addRow(row);
            originalData.add(row);
        }
    }

    // Phương thức làm mới dữ liệu
    public void lamMoiDuLieu() {
        taiDuLieuTuCSDL();
    }

    // Phương thức thêm sự kiện cho JComboBox
    private void themSuKienChoComboBox() {
        cbLocLoaiKhachHang.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String loaiKhachHang = cbLocLoaiKhachHang.getSelectedItem().toString();
                locDanhSachKhachHang(loaiKhachHang);
            }
        });
    }

    // Phương thức lọc danh sách khách hàng
    private void locDanhSachKhachHang(String loaiKhachHang) {
        modelBang.setRowCount(0);
        if (loaiKhachHang.equals("VIP, Thành viên...")) {
            for (Object[] row : originalData) {
                modelBang.addRow(row);
            }
        } else {
            for (Object[] row : originalData) {
                if (row[6].toString().equals(loaiKhachHang)) {
                    modelBang.addRow(row);
                }
            }
        }
    }

    // Phương thức tìm kiếm khách hàng theo số điện thoại
    private void timKiemKhachHang(String soDienThoai) {
        modelBang.setRowCount(0);
        if (soDienThoai.isEmpty() || soDienThoai.equals("Nhập SĐT khách hàng cần tìm")) {
            for (Object[] row : originalData) {
                modelBang.addRow(row);
            }
        } else {
            for (Object[] row : originalData) {
                if (row[2].toString().contains(soDienThoai)) {
                    modelBang.addRow(row);
                }
            }
        }
    }

    // Phương thức thêm sự kiện cho các nút
    private void themSuKienChoNut() {
        // Sự kiện nút Tìm kiếm
        btnTimKiem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String soDienThoai = txtTimKiem.getText().trim();
                timKiemKhachHang(soDienThoai);
            }
        });

        // Sự kiện nút Thêm
        btnThem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String maKhachHang = txtMaKhachHang.getText().trim();
                String hoTen = txtHoTen.getText().trim();
                String soDienThoai = txtSoDienThoai.getText().trim();
                String email = txtEmail.getText().trim();
                String diaChi = txtDiaChi.getText().trim();
                String ngayDangKy = txtNgayDangKy.getText().trim();

                // Kiểm tra tất cả các trường không được để trống
                if (maKhachHang.isEmpty() || hoTen.isEmpty() || soDienThoai.isEmpty() || 
                    email.isEmpty() || diaChi.isEmpty() || ngayDangKy.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Vui lòng nhập đầy đủ tất cả các trường!");
                    return;
                }

                // Kiểm tra mã khách hàng: KH + 3 số (ví dụ: KH123)
                if (!maKhachHang.matches("^KH\\d{3}$")) {
                    JOptionPane.showMessageDialog(null, "Mã khách hàng phải bắt đầu bằng 'KH' và theo sau là 3 chữ số!");
                    return;
                }

                // Kiểm tra họ tên: Mỗi từ bắt đầu bằng chữ hoa, hỗ trợ tiếng Việt
                if (!hoTen.matches("^[A-ZÀ-ỹ][a-zà-ỹ]*(?:\\s[A-ZÀ-ỹ][a-zà-ỹ]*)*$")) {
                    JOptionPane.showMessageDialog(null, "Họ tên phải viết hoa chữ cái đầu mỗi từ!");
                    return;
                }

                // Kiểm tra số điện thoại: 10 chữ số
                if (!soDienThoai.matches("^\\d{10}$")) {
                    JOptionPane.showMessageDialog(null, "Số điện thoại phải gồm 10 chữ số!");
                    return;
                }
                
                // Kiểm tra email: Phải có đuôi @gmail.com
                if (!email.matches("^[\\w.-]+@gmail\\.com$")) {
                    JOptionPane.showMessageDialog(null, "Email phải có định dạng hợp lệ và có đuôi @gmail.com!");
                    return;
                }

                // Tạo đối tượng khách hàng
                KhachHang khachHang = new KhachHang(maKhachHang, hoTen, soDienThoai, diaChi, email, ngayDangKy, 0);

                // Thêm vào cơ sở dữ liệu và kiểm tra kết quả
                if (khachHangDAO.addKhachHang(khachHang)) {
                    // Chỉ thêm vào bảng giao diện nếu thêm vào cơ sở dữ liệu thành công
                    Object[] newRow = {maKhachHang, hoTen, soDienThoai, diaChi, email, ngayDangKy, "Thành viên", "0 VND", "Xem"};
                    modelBang.addRow(newRow);
                    originalData.add(newRow);
                    xoaNoiDungCacTruong();
                }
            }
        });
        // Sự kiện nút Xóa
        btnXoa.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int dongDaChon = bangKhachHang.getSelectedRow();
                if (dongDaChon != -1) {
                    String maKhachHang = modelBang.getValueAt(dongDaChon, 0).toString();
                    khachHangDAO.deleteKhachHang(maKhachHang);

                    // Xóa khỏi bảng giao diện và originalData
                    originalData.removeIf(row -> row[0].toString().equals(maKhachHang));
                    modelBang.removeRow(dongDaChon);
                    xoaNoiDungCacTruong();
                } else {
                    JOptionPane.showMessageDialog(null, "Vui lòng chọn một khách hàng để xóa!");
                }
            }
        });

        // Sự kiện nút Sửa
        btnSua.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int dongDaChon = bangKhachHang.getSelectedRow();
                if (dongDaChon != -1) {
                    String maKhachHang = txtMaKhachHang.getText();
                    String hoTen = txtHoTen.getText();
                    String soDienThoai = txtSoDienThoai.getText();
                    String email = txtEmail.getText();
                    String diaChi = txtDiaChi.getText();
                    String ngayDangKy = txtNgayDangKy.getText();

                    // Lấy điểm tích lũy hiện tại
                    int diemTichLuy = 0;
                    for (Object[] row : originalData) {
                        if (row[0].toString().equals(maKhachHang)) {
                            diemTichLuy = Integer.parseInt(row[6].toString().equals("VIP") ? "30" : "0");
                            break;
                        }
                    }

                    KhachHang khachHang = new KhachHang(maKhachHang, hoTen, soDienThoai, diaChi, email, ngayDangKy, diemTichLuy);
                    khachHangDAO.updateKhachHang(khachHang);

                    // Cập nhật originalData
                    for (Object[] row : originalData) {
                        if (row[0].toString().equals(maKhachHang)) {
                            row[1] = hoTen;
                            row[2] = soDienThoai;
                            row[3] = diaChi;
                            row[4] = email;
                            row[5] = ngayDangKy;
                            row[6] = diemTichLuy >= 30 ? "VIP" : "Thành viên";
                            row[7] = String.format("%,.0f VND", tinhTongTienHoaDon(maKhachHang));
                            break;
                        }
                    }

                    // Cập nhật bảng giao diện
                    modelBang.setValueAt(hoTen, dongDaChon, 1);
                    modelBang.setValueAt(soDienThoai, dongDaChon, 2);
                    modelBang.setValueAt(diaChi, dongDaChon, 3);
                    modelBang.setValueAt(email, dongDaChon, 4);
                    modelBang.setValueAt(ngayDangKy, dongDaChon, 5);
                    modelBang.setValueAt(diemTichLuy >= 30 ? "VIP" : "Thành viên", dongDaChon, 6);
                    modelBang.setValueAt(String.format("%,.0f VND", tinhTongTienHoaDon(maKhachHang)), dongDaChon, 7);
                } else {
                    JOptionPane.showMessageDialog(null, "Vui lòng chọn một khách hàng để sửa!");
                }
            }
        });

        // Sự kiện nút Lưu
        btnLuu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Đã lưu thông tin khách hàng!");
            }
        });

        // Sự kiện khi chọn một dòng trong bảng
        bangKhachHang.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int dongDaChon = bangKhachHang.getSelectedRow();
                if (dongDaChon != -1) {
                    txtMaKhachHang.setText(modelBang.getValueAt(dongDaChon, 0).toString());
                    txtHoTen.setText(modelBang.getValueAt(dongDaChon, 1).toString());
                    txtSoDienThoai.setText(modelBang.getValueAt(dongDaChon, 2).toString());
                    txtDiaChi.setText(modelBang.getValueAt(dongDaChon, 3).toString());
                    txtEmail.setText(modelBang.getValueAt(dongDaChon, 4).toString());
                    txtNgayDangKy.setText(modelBang.getValueAt(dongDaChon, 5).toString());
                }
            }
        });
    }

    // Phương thức xóa nội dung các trường nhập liệu
    private void xoaNoiDungCacTruong() {
        txtMaKhachHang.setText("");
        txtHoTen.setText("");
        txtSoDienThoai.setText("");
        txtEmail.setText("");
        txtDiaChi.setText("");
        txtNgayDangKy.setText("");
        txtNgayDangKy.setForeground(Color.GRAY);
    }

    // Renderer để hiển thị nút "Xem" trong bảng
    static class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "Xem" : value.toString());
            setBackground(Color.LIGHT_GRAY);
            return this;
        }
    }

    // Editor để xử lý sự kiện khi nhấn nút "Xem"
    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;
        private int row;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.row = row;
            label = (value == null) ? "Xem" : value.toString();
            button.setText(label);
            button.setBackground(Color.LIGHT_GRAY);
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                String maKhachHang = modelBang.getValueAt(row, 0).toString();
                new LichSuHoaDonFrame(maKhachHang).setVisible(true);
            }
            isPushed = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }

        @Override
        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }
}