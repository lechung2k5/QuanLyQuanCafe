package UI;

import DAO.KhuyenMai_DAO;
import Entity.KhuyenMai;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class KhuyenMaiPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtMaKM, txtTenChuongTrinh, txtGiamGia, txtSearch;
    private JSpinner spinnerNgayBatDau, spinnerNgayKetThuc;
    private JComboBox<String> cbTrangThai;
    private JButton btnThem, btnSua, btnXoa, btnLamMoi, btnTimKiem;
    private KhuyenMai_DAO khuyenMaiDAO;

    public KhuyenMaiPanel() {
        try {
            khuyenMaiDAO = new KhuyenMai_DAO();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi kết nối cơ sở dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Quản lý khuyến mãi"));
        setBackground(Color.WHITE);

        // Khởi tạo bảng
        String[] columns = {"Mã KM", "Tên chương trình", "Giảm giá (%)", "Ngày bắt đầu", "Ngày kết thúc", "Trạng thái"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(40);
        table.getTableHeader().setBackground(Color.decode("#1E3A8A"));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Roboto", Font.BOLD, 14));

        // Điều chỉnh độ rộng cột
        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(250);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);
        table.getColumnModel().getColumn(4).setPreferredWidth(120);
        table.getColumnModel().getColumn(5).setPreferredWidth(100);

        // Conditional formatting for "Hết hạn" status
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String status = tableModel.getValueAt(row, 5).toString();
                if (status.equals("Hết hạn")) {
                    c.setForeground(Color.RED);
                } else {
                    c.setForeground(Color.BLACK);
                }
                return c;
            }
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                txtMaKM.setText(tableModel.getValueAt(selectedRow, 0).toString());
                txtTenChuongTrinh.setText(tableModel.getValueAt(selectedRow, 1).toString());
                txtGiamGia.setText(tableModel.getValueAt(selectedRow, 2).toString());
                try {
                    spinnerNgayBatDau.setValue(Date.from(LocalDate.parse(tableModel.getValueAt(selectedRow, 3).toString()).atStartOfDay(ZoneId.systemDefault()).toInstant()));
                    spinnerNgayKetThuc.setValue(Date.from(LocalDate.parse(tableModel.getValueAt(selectedRow, 4).toString()).atStartOfDay(ZoneId.systemDefault()).toInstant()));
                } catch (Exception ex) {
                    spinnerNgayBatDau.setValue(new Date());
                    spinnerNgayKetThuc.setValue(new Date());
                }
            }
        });
        JScrollPane scrollPane = new JScrollPane(table);

        // Panel tìm kiếm
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(Color.WHITE);
        JLabel lblSearch = new JLabel("Tìm kiếm:");
        lblSearch.setFont(new Font("Roboto", Font.PLAIN, 14));
        searchPanel.add(lblSearch);
        txtSearch = new JTextField(20);
        txtSearch.setFont(new Font("Roboto", Font.PLAIN, 14));
        txtSearch.setPreferredSize(new Dimension(300, 35));
        searchPanel.add(txtSearch);
        btnTimKiem = new JButton("Tìm kiếm");
        btnTimKiem.setFont(new Font("Roboto", Font.BOLD, 14));
        btnTimKiem.setPreferredSize(new Dimension(120, 35));
        btnTimKiem.setFocusPainted(false);
        btnTimKiem.addActionListener(e -> timKiemKhuyenMai());
        searchPanel.add(btnTimKiem);

        // Panel nhập liệu
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Tên chương trình
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel lblTenChuongTrinh = new JLabel("Tên chương trình:");
        lblTenChuongTrinh.setFont(new Font("Roboto", Font.PLAIN, 14));
        inputPanel.add(lblTenChuongTrinh, gbc);
        gbc.gridx = 1;
        txtTenChuongTrinh = new JTextField(20);
        txtTenChuongTrinh.setFont(new Font("Roboto", Font.PLAIN, 14));
        txtTenChuongTrinh.setPreferredSize(new Dimension(300, 35));
        inputPanel.add(txtTenChuongTrinh, gbc);

        // Mã KM (ẩn đi)
        txtMaKM = new JTextField(15);
        txtMaKM.setEditable(false);
        txtMaKM.setVisible(false);

        // Giảm giá
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel lblGiamGia = new JLabel("KM %:");
        lblGiamGia.setFont(new Font("Roboto", Font.PLAIN, 14));
        inputPanel.add(lblGiamGia, gbc);
        gbc.gridx = 1;
        txtGiamGia = new JTextField(20);
        txtGiamGia.setFont(new Font("Roboto", Font.PLAIN, 14));
        txtGiamGia.setPreferredSize(new Dimension(300, 35));
        inputPanel.add(txtGiamGia, gbc);

        // Thời gian
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel lblThoiGian = new JLabel("Thời gian:");
        lblThoiGian.setFont(new Font("Roboto", Font.PLAIN, 14));
        inputPanel.add(lblThoiGian, gbc);
        gbc.gridx = 1;
        JPanel thoiGianPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        thoiGianPanel.setBackground(Color.WHITE);
        spinnerNgayBatDau = new JSpinner(new SpinnerDateModel());
        spinnerNgayBatDau.setEditor(new JSpinner.DateEditor(spinnerNgayBatDau, "dd/MM/yyyy"));
        spinnerNgayBatDau.setFont(new Font("Roboto", Font.PLAIN, 14));
        spinnerNgayBatDau.setPreferredSize(new Dimension(140, 35));
        thoiGianPanel.add(spinnerNgayBatDau);
        JLabel lblDen = new JLabel("đến");
        lblDen.setFont(new Font("Roboto", Font.PLAIN, 14));
        thoiGianPanel.add(lblDen);
        spinnerNgayKetThuc = new JSpinner(new SpinnerDateModel());
        spinnerNgayKetThuc.setEditor(new JSpinner.DateEditor(spinnerNgayKetThuc, "dd/MM/yyyy"));
        spinnerNgayKetThuc.setFont(new Font("Roboto", Font.PLAIN, 14));
        spinnerNgayKetThuc.setPreferredSize(new Dimension(140, 35));
        thoiGianPanel.add(spinnerNgayKetThuc);
        inputPanel.add(thoiGianPanel, gbc);

        // Trạng thái (ẩn đi, chỉ hiển thị trên bảng)
        cbTrangThai = new JComboBox<>(new String[]{"Đang hoạt động", "Hết hạn", "Chưa bắt đầu"});
        cbTrangThai.setVisible(false); // Ẩn trên giao diện

        // Panel nút chức năng
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
        buttonPanel.setBackground(Color.WHITE);

        btnThem = new JButton("Thêm");
        btnThem.setFont(new Font("Roboto", Font.BOLD, 16));
        btnThem.setBackground(Color.GREEN);
        btnThem.setForeground(Color.WHITE);
        btnThem.setFocusPainted(false);
        btnThem.setPreferredSize(new Dimension(120, 40));
        btnThem.addActionListener(e -> themKhuyenMai());

        btnSua = new JButton("Sửa");
        btnSua.setFont(new Font("Roboto", Font.BOLD, 16));
        btnSua.setBackground(Color.BLUE);
        btnSua.setForeground(Color.WHITE);
        btnSua.setFocusPainted(false);
        btnSua.setPreferredSize(new Dimension(120, 40));
        btnSua.addActionListener(e -> suaKhuyenMai());

        btnLamMoi = new JButton("Lưu");
        btnLamMoi.setFont(new Font("Roboto", Font.BOLD, 16));
        btnLamMoi.setBackground(Color.ORANGE);
        btnLamMoi.setForeground(Color.WHITE);
        btnLamMoi.setFocusPainted(false);
        btnLamMoi.setPreferredSize(new Dimension(120, 40));
        btnLamMoi.addActionListener(e -> lamMoi());

        btnXoa = new JButton("Xóa");
        btnXoa.setFont(new Font("Roboto", Font.BOLD, 16));
        btnXoa.setBackground(Color.RED);
        btnXoa.setForeground(Color.WHITE);
        btnXoa.setFocusPainted(false);
        btnXoa.setPreferredSize(new Dimension(120, 40));
        btnXoa.addActionListener(e -> xoaKhuyenMai());

        buttonPanel.add(btnThem);
        buttonPanel.add(btnSua);
        buttonPanel.add(btnLamMoi);
        buttonPanel.add(btnXoa);

        // Panel tổng hợp
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setBackground(Color.WHITE);
        northPanel.add(searchPanel, BorderLayout.NORTH);
        northPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(northPanel, BorderLayout.NORTH);
        centerPanel.add(inputPanel, BorderLayout.CENTER);
        centerPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);

        // Load dữ liệu ban đầu
        loadDanhSachKhuyenMai();
    }

    private void loadDanhSachKhuyenMai() {
        try {
            List<KhuyenMai> dsKhuyenMai = khuyenMaiDAO.getAllKhuyenMai();
            tableModel.setRowCount(0);
            LocalDate today = LocalDate.now();
            for (KhuyenMai km : dsKhuyenMai) {
                String trangThai = tinhTrangThai(km.getNgayBatDau(), km.getNgayKetThuc(), today);
                tableModel.addRow(new Object[]{
                    km.getMaKM(),
                    km.getTenChuongTrinh(),
                    km.getGiamGia(),
                    km.getNgayBatDau(),
                    km.getNgayKetThuc(),
                    trangThai
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải danh sách khuyến mãi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String tinhTrangThai(LocalDate ngayBatDau, LocalDate ngayKetThuc, LocalDate today) {
        if (today.isBefore(ngayBatDau)) {
            return "Chưa bắt đầu";
        } else if (today.isAfter(ngayKetThuc)) {
            return "Hết hạn";
        } else {
            return "Đang hoạt động";
        }
    }

    private void themKhuyenMai() {
        if (!kiemTraHopLe()) return;

        String maKM;
        try {
            String maxMaKM = khuyenMaiDAO.getMaxMaKM();
            int soThuTu = Integer.parseInt(maxMaKM.substring(2)) + 1;
            maKM = "KM" + String.format("%03d", soThuTu);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi tạo mã khuyến mãi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String tenChuongTrinh = txtTenChuongTrinh.getText();
        double giamGia = Double.parseDouble(txtGiamGia.getText());
        LocalDate ngayBatDau = ((Date) spinnerNgayBatDau.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate ngayKetThuc = ((Date) spinnerNgayKetThuc.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate today = LocalDate.now();
        String trangThai = tinhTrangThai(ngayBatDau, ngayKetThuc, today);

        KhuyenMai km = new KhuyenMai(maKM, tenChuongTrinh, giamGia, ngayBatDau, ngayKetThuc, trangThai);
        try {
            khuyenMaiDAO.themKhuyenMai(km);
            JOptionPane.showMessageDialog(this, "Thêm khuyến mãi thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadDanhSachKhuyenMai();
            lamMoi();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi thêm khuyến mãi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void suaKhuyenMai() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một khuyến mãi để sửa!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!kiemTraHopLe()) return;

        String maKM = txtMaKM.getText();
        String tenChuongTrinh = txtTenChuongTrinh.getText();
        double giamGia = Double.parseDouble(txtGiamGia.getText());
        LocalDate ngayBatDau = ((Date) spinnerNgayBatDau.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate ngayKetThuc = ((Date) spinnerNgayKetThuc.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate today = LocalDate.now();
        String trangThai = tinhTrangThai(ngayBatDau, ngayKetThuc, today);

        KhuyenMai km = new KhuyenMai(maKM, tenChuongTrinh, giamGia, ngayBatDau, ngayKetThuc, trangThai);
        try {
            khuyenMaiDAO.capNhatKhuyenMai(km);
            JOptionPane.showMessageDialog(this, "Cập nhật khuyến mãi thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadDanhSachKhuyenMai();
            lamMoi();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi cập nhật khuyến mãi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xoaKhuyenMai() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một khuyến mãi để xóa!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa khuyến mãi này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            String maKM = tableModel.getValueAt(selectedRow, 0).toString();
            try {
                khuyenMaiDAO.xoaKhuyenMai(maKM);
                JOptionPane.showMessageDialog(this, "Xóa khuyến mãi thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadDanhSachKhuyenMai();
                lamMoi();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Lỗi xóa khuyến mãi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void timKiemKhuyenMai() {
        String keyword = txtSearch.getText().trim().toLowerCase();
        try {
            List<KhuyenMai> dsKhuyenMai = khuyenMaiDAO.getAllKhuyenMai();
            tableModel.setRowCount(0);
            LocalDate today = LocalDate.now();
            for (KhuyenMai km : dsKhuyenMai) {
                if (km.getMaKM().toLowerCase().contains(keyword) || km.getTenChuongTrinh().toLowerCase().contains(keyword)) {
                    String trangThai = tinhTrangThai(km.getNgayBatDau(), km.getNgayKetThuc(), today);
                    tableModel.addRow(new Object[]{
                        km.getMaKM(),
                        km.getTenChuongTrinh(),
                        km.getGiamGia(),
                        km.getNgayBatDau(),
                        km.getNgayKetThuc(),
                        trangThai
                    });
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi tìm kiếm khuyến mãi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void lamMoi() {
        txtMaKM.setText("");
        txtTenChuongTrinh.setText("");
        txtGiamGia.setText("");
        spinnerNgayBatDau.setValue(new Date());
        spinnerNgayKetThuc.setValue(new Date());
        txtSearch.setText("");
        loadDanhSachKhuyenMai();
    }

    private boolean kiemTraHopLe() {
        String tenChuongTrinh = txtTenChuongTrinh.getText().trim();
        if (tenChuongTrinh.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên chương trình không được để trống!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        String giamGiaText = txtGiamGia.getText().trim();
        double giamGia;
        try {
            giamGia = Double.parseDouble(giamGiaText);
            if (giamGia < 0 || giamGia > 100) {
                JOptionPane.showMessageDialog(this, "Giảm giá phải từ 0 đến 100%!", "Lỗi", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Giảm giá phải là một số hợp lệ!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        LocalDate ngayBatDau = ((Date) spinnerNgayBatDau.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate ngayKetThuc = ((Date) spinnerNgayKetThuc.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        if (ngayBatDau.isAfter(ngayKetThuc)) {
            JOptionPane.showMessageDialog(this, "Ngày bắt đầu không được sau ngày kết thúc!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }
}