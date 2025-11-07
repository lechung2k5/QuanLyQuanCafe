package UI;

import DAO.NhanVien_DAO;
import Entity.NhanVien;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import ConnectDB.ConnectDB;

import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class StaffPanel extends JPanel {
    private static final String[] COLUMN_NAMES = {"Mã NV", "Họ tên", "Số điện thoại", "Chức vụ", "Mật khẩu", "Ngày vào làm", "Ca làm", "Trạng thái"};
    private static final String[] CHUC_VU = {"Nhân viên mở cửa", "Nhân viên phục vụ", "Quản lý"};
    private static final String[] CA_LAM = {"7-13h", "13-22h"};
    private static final String[] TRANG_THAI = {"Đang làm", "Tạm nghỉ", "Nghỉ việc"};

    private NhanVien_DAO nhanVienDAO;
    private DefaultTableModel tableModel;
    private JTable tableNV;
    private JTextField txtMaNV;
    private JTextField txtHoTen;
    private JTextField txtSDT;
    private JTextField txtMK;
    private JTextField txtNgayVaoLam;
    private JTextField txtTim;
    private JComboBox<String> chucVuCombo;
    private JComboBox<String> caLamCombo;
    private JComboBox<String> trangThaiCombo;
    private JButton btnThem;
    private JButton btnXoa;
    private JButton btnSua;
    private JButton btnLuu;
    private JButton btnTim;
    private JButton btnClear;
    private boolean isEditMode;

    public StaffPanel() {
        if (!initializeDatabaseConnection()) {
            JOptionPane.showMessageDialog(null, "Không thể kết nối đến cơ sở dữ liệu. Vui lòng kiểm tra cài đặt SQL Server.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        this.nhanVienDAO = new NhanVien_DAO();
        this.tableModel = new DefaultTableModel(COLUMN_NAMES, 0);
        this.tableNV = new JTable(tableModel);
        this.txtMaNV = new JTextField(15);
        this.txtHoTen = new JTextField(15);
        this.txtSDT = new JTextField(15);
        this.txtMK = new JTextField(15);
        this.txtNgayVaoLam = new JTextField("dd/MM/yyyy", 15);
        this.txtTim = new JTextField(20);
        this.chucVuCombo = new JComboBox<>(CHUC_VU);
        this.caLamCombo = new JComboBox<>(CA_LAM);
        this.trangThaiCombo = new JComboBox<>(TRANG_THAI);
        this.btnThem = new JButton("Thêm");
        this.btnXoa = new JButton("Xóa");
        this.btnSua = new JButton("Sửa");
        this.btnLuu = new JButton("Hủy");
        this.btnTim = new JButton("Tìm");
        this.btnClear = new JButton("Xóa Form");
        this.isEditMode = false;

        initializeUI();
        loadAllNhanVien();
    }

    private boolean initializeDatabaseConnection() {
        try {
            Connection conn = ConnectDB.getConnection();
            return conn != null;
        } catch (Exception e) {
            System.err.println("Không thể lấy kết nối từ ConnectDB: " + e.getMessage());
            return false;
        }
    }

    private boolean checkConnection() {
        try {
            Connection conn = ConnectDB.getConnection();
            if (conn == null || conn.isClosed()) {
                JOptionPane.showMessageDialog(this, "Mất kết nối đến cơ sở dữ liệu. Vui lòng kiểm tra lại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi kết nối cơ sở dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        mainPanel.add(createNorthPanel(), BorderLayout.NORTH);
        mainPanel.add(createTablePanel(), BorderLayout.CENTER);
        mainPanel.add(createSouthPanel(), BorderLayout.SOUTH);

        add(mainPanel);
        setupEventListeners();
    }

    private JPanel createNorthPanel() {
        JPanel northPanel = new JPanel(new BorderLayout());

        JLabel lblTitle = new JLabel("Quản Lý Nhân Viên");
        lblTitle.setForeground(Color.BLUE);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        northPanel.add(lblTitle, BorderLayout.WEST);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        txtTim.setForeground(Color.GRAY);
        txtTim.setText("Nhập mã nhân viên cần tìm");
        txtTim.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (txtTim.getText().equals("Nhập mã nhân viên cần tìm")) {
                    txtTim.setText("");
                    txtTim.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (txtTim.getText().isEmpty()) {
                    txtTim.setForeground(Color.GRAY);
                    txtTim.setText("Nhập mã nhân viên cần tìm");
                }
            }
        });
        btnTim.setBackground(new Color(0, 123, 255));
        btnTim.setForeground(Color.WHITE);
        searchPanel.add(txtTim);
        searchPanel.add(btnTim);

        northPanel.add(searchPanel, BorderLayout.EAST);
        return northPanel;
    }

    private JScrollPane createTablePanel() {
        return new JScrollPane(tableNV);
    }

    private JPanel createSouthPanel() {
        JPanel southPanel = new JPanel(new BorderLayout());
        JPanel formPanel = new JPanel(null);
        formPanel.setPreferredSize(new Dimension(1200, 200));

        int xLabel = 40, xField = 160, yStart = 30, yGap = 50, fieldWidth = 400, labelWidth = 120;

        JLabel lblMaNV = new JLabel("Mã nhân viên:");
        lblMaNV.setBounds(xLabel, yStart, labelWidth, 30);
        formPanel.add(lblMaNV);
        txtMaNV.setBounds(xField, yStart, fieldWidth, 30);
        formPanel.add(txtMaNV);

        JLabel lblHoTen = new JLabel("Họ tên:");
        lblHoTen.setBounds(xLabel + 580, yStart, labelWidth, 30);
        formPanel.add(lblHoTen);
        txtHoTen.setBounds(xField + 580, yStart, fieldWidth, 30);
        formPanel.add(txtHoTen);

        JLabel lblSDT = new JLabel("Số điện thoại:");
        lblSDT.setBounds(xLabel, yStart + yGap, labelWidth, 30);
        formPanel.add(lblSDT);
        txtSDT.setBounds(xField, yStart + yGap, fieldWidth, 30);
        formPanel.add(txtSDT);

        JLabel lblChucVu = new JLabel("Chức vụ:");
        lblChucVu.setBounds(xLabel + 580, yStart + yGap, labelWidth, 30);
        formPanel.add(lblChucVu);
        chucVuCombo.setBounds(xField + 580, yStart + yGap, 200, 30);
        formPanel.add(chucVuCombo);

        JLabel lblMK = new JLabel("Mật khẩu:");
        lblMK.setBounds(xLabel + 860, yStart + yGap, labelWidth, 30);
        formPanel.add(lblMK);
        txtMK.setBounds(xField + 860, yStart + yGap, 200, 30);
        formPanel.add(txtMK);

        JLabel lblNgayVaoLam = new JLabel("Ngày vào làm:");
        lblNgayVaoLam.setBounds(xLabel, yStart + 2 * yGap, labelWidth, 30);
        formPanel.add(lblNgayVaoLam);
        txtNgayVaoLam.setBounds(xField, yStart + 2 * yGap, 200, 30);
        formPanel.add(txtNgayVaoLam);

        JLabel lblCaLam = new JLabel("Ca làm:");
        lblCaLam.setBounds(xLabel + 380, yStart + 2 * yGap, labelWidth, 30);
        formPanel.add(lblCaLam);
        caLamCombo.setBounds(xField + 380, yStart + 2 * yGap, 200, 30);
        formPanel.add(caLamCombo);

        JLabel lblTrangThai = new JLabel("Trạng thái:");
        lblTrangThai.setBounds(xLabel + 680, yStart + 2 * yGap, labelWidth, 30);
        formPanel.add(lblTrangThai);
        trangThaiCombo.setBounds(xField + 680, yStart + 2 * yGap, 200, 30);
        formPanel.add(trangThaiCombo);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        Dimension buttonSize = new Dimension(120, 40);

        btnThem.setBackground(new Color(49, 186, 99));
        btnThem.setForeground(Color.WHITE);
        btnThem.setPreferredSize(buttonSize);

        btnXoa.setBackground(new Color(186, 38, 38));
        btnXoa.setForeground(Color.WHITE);
        btnXoa.setPreferredSize(buttonSize);
        btnXoa.setEnabled(false);

        btnSua.setBackground(new Color(7, 149, 220));
        btnSua.setForeground(Color.WHITE);
        btnSua.setPreferredSize(buttonSize);
        btnSua.setEnabled(false);

        btnLuu.setBackground(new Color(255, 192, 0));
        btnLuu.setForeground(Color.WHITE);
        btnLuu.setPreferredSize(buttonSize);

        btnClear.setBackground(new Color(108, 117, 125));
        btnClear.setForeground(Color.WHITE);
        btnClear.setPreferredSize(buttonSize);

        buttonPanel.add(btnThem);
        buttonPanel.add(btnXoa);
        buttonPanel.add(btnSua);
        buttonPanel.add(btnLuu);
        buttonPanel.add(btnClear);

        southPanel.add(formPanel, BorderLayout.CENTER);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);
        return southPanel;
    }

    private void setupEventListeners() {
        ActionListener buttonListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object o = e.getSource();
                if (o == btnThem) {
                    addNhanVien();
                } else if (o == btnXoa) {
                    deleteNhanVien();
                } else if (o == btnSua) {
                    updateNhanVien();
                } else if (o == btnLuu) {
                    cancelEdit();
                } else if (o == btnTim) {
                    searchNhanVien();
                } else if (o == btnClear) {
                    clearForm();
                }
            }
        };

        btnThem.addActionListener(buttonListener);
        btnXoa.addActionListener(buttonListener);
        btnSua.addActionListener(buttonListener);
        btnLuu.addActionListener(buttonListener);
        btnTim.addActionListener(buttonListener);
        btnClear.addActionListener(buttonListener);

        tableNV.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tableNV.getSelectedRow();
                if (row >= 0) {
                    txtMaNV.setText(tableModel.getValueAt(row, 0).toString());
                    txtHoTen.setText(tableModel.getValueAt(row, 1).toString());
                    txtSDT.setText(tableModel.getValueAt(row, 2).toString());
                    chucVuCombo.setSelectedItem(tableModel.getValueAt(row, 3).toString());
                    txtMK.setText(tableModel.getValueAt(row, 4).toString());
                    // Chuyển Date thành dd/MM/yyyy
                    String ngayVaoLam = tableModel.getValueAt(row, 5).toString();
                    txtNgayVaoLam.setText(ngayVaoLam);
                    caLamCombo.setSelectedItem(tableModel.getValueAt(row, 6).toString());
                    trangThaiCombo.setSelectedItem(tableModel.getValueAt(row, 7).toString());
                    isEditMode = true;
                    btnSua.setEnabled(true);
                    btnXoa.setEnabled(true);
                    txtMaNV.setEditable(false);
                }
            }
        });
    }

    private void loadAllNhanVien() {
        if (!checkConnection()) return;
        try {
            tableModel.setRowCount(0);
            List<NhanVien> list = nhanVienDAO.getalltbNhanVien();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            for (NhanVien nv : list) {
                // Chuyển Date thành String để hiển thị
                String ngayVaoLam = nv.getNgayVaoLam() != null ? sdf.format(nv.getNgayVaoLam()) : "";
                tableModel.addRow(new Object[]{
                        nv.getMaNV(),
                        nv.getHoTenNV(),
                        nv.getSoDT(),
                        nv.getChucVu(),
                        nv.getMatKhau(),
                        ngayVaoLam,
                        nv.getCaLam(),
                        nv.getTrangThai()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu nhân viên: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addNhanVien() {
        if (!checkConnection()) return;
        if (isEditMode) {
            JOptionPane.showMessageDialog(this, "Đang ở chế độ chỉnh sửa. Vui lòng hủy trước khi thêm mới!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (validData()) {
            try {
                NhanVien nv = createNhanVien();
                if (nhanVienDAO.create(nv)) {
                    JOptionPane.showMessageDialog(this, "Thêm nhân viên và tài khoản thành công", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    loadAllNhanVien();
                    clearForm();
                } else {
                    JOptionPane.showMessageDialog(this, "Thêm nhân viên thất bại", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi thêm nhân viên: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteNhanVien() {
        if (!checkConnection()) return;
        int row = tableNV.getSelectedRow();
        if (row >= 0) {
            String maNV = tableModel.getValueAt(row, 0).toString();
            int confirm = JOptionPane.showConfirmDialog(this, "Xóa nhân viên " + maNV + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    if (nhanVienDAO.delete(maNV)) {
                        JOptionPane.showMessageDialog(this, "Xóa nhân viên và tài khoản thành công", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                        loadAllNhanVien();
                        clearForm();
                        isEditMode = false;
                        btnSua.setEnabled(false);
                        btnXoa.setEnabled(false);
                        txtMaNV.setEditable(true);
                    } else {
                        JOptionPane.showMessageDialog(this, "Xóa nhân viên thất bại", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Lỗi khi xóa nhân viên: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên để xóa", "Lỗi", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void updateNhanVien() {
        if (!checkConnection()) return;
        if (isEditMode && validData()) {
            try {
                NhanVien updatedNV = createNhanVien();
                int confirm = JOptionPane.showConfirmDialog(this, "Cập nhật thông tin nhân viên?", "Xác nhận", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    if (nhanVienDAO.update(updatedNV)) {
                        JOptionPane.showMessageDialog(this, "Chỉnh sửa nhân viên thành công", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                        loadAllNhanVien();
                        clearForm();
                        isEditMode = false;
                        btnSua.setEnabled(false);
                        btnXoa.setEnabled(false);
                        txtMaNV.setEditable(true);
                    } else {
                        JOptionPane.showMessageDialog(this, "Chỉnh sửa nhân viên thất bại", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi chỉnh sửa nhân viên: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên để chỉnh sửa", "Lỗi", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void cancelEdit() {
        if (isEditMode) {
            clearForm();
            isEditMode = false;
            btnSua.setEnabled(false);
            btnXoa.setEnabled(false);
            txtMaNV.setEditable(true);
            JOptionPane.showMessageDialog(this, "Đã hủy chỉnh sửa", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void searchNhanVien() {
        if (!checkConnection()) return;
        String maNV = txtTim.getText().trim();
        if (maNV.isEmpty() || maNV.equals("Nhập mã nhân viên cần tìm")) {
            loadAllNhanVien();
        } else {
            try {
                ArrayList<NhanVien> list = nhanVienDAO.getNhanVienTheoMaNV(maNV);
                tableModel.setRowCount(0);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                if (list.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Không tìm thấy nhân viên với mã " + maNV, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    loadAllNhanVien();
                } else {
                    for (NhanVien nv : list) {
                        String ngayVaoLam = nv.getNgayVaoLam() != null ? sdf.format(nv.getNgayVaoLam()) : "";
                        tableModel.addRow(new Object[]{
                                nv.getMaNV(),
                                nv.getHoTenNV(),
                                nv.getSoDT(),
                                nv.getChucVu(),
                                nv.getMatKhau(),
                                ngayVaoLam,
                                nv.getCaLam(),
                                nv.getTrangThai()
                        });
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi tìm kiếm nhân viên: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean validData() {
        String maNV = txtMaNV.getText().trim();
        String hoTen = txtHoTen.getText().trim();
        String sdt = txtSDT.getText().trim();
        String mk = txtMK.getText().trim();
        String ngayVaoLam = txtNgayVaoLam.getText().trim();

        if (maNV.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã nhân viên không được rỗng", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Kiểm tra trùng mã nhân viên khi thêm mới
        if (!isEditMode) {
            try {
                for (NhanVien nv : nhanVienDAO.getalltbNhanVien()) {
                    if (nv.getMaNV().equals(maNV)) {
                        JOptionPane.showMessageDialog(this, "Mã nhân viên đã tồn tại!", "Lỗi", JOptionPane.WARNING_MESSAGE);
                        return false;
                    }
                }
                // Kiểm tra trùng username trong TaiKhoan
                String sql = "SELECT COUNT(*) FROM TaiKhoan WHERE username = ?";
                try (Connection conn = ConnectDB.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, maNV);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next() && rs.getInt(1) > 0) {
                        JOptionPane.showMessageDialog(this, "Username (mã nhân viên) đã tồn tại trong bảng TaiKhoan!", "Lỗi", JOptionPane.WARNING_MESSAGE);
                        return false;
                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi kiểm tra mã nhân viên: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        if (!hoTen.matches("^[A-Z][\\p{L}]*(\\s[A-Z][\\p{L}]*)+")) {
            JOptionPane.showMessageDialog(this, "Họ tên phải bắt đầu bằng chữ hoa và chứa ít nhất 2 từ", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (!mk.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$!%*?&])[A-Za-z\\d@#$!%*?&]{8,}$")) {
            JOptionPane.showMessageDialog(this, "Mật khẩu tối thiểu 8 ký tự, có chữ hoa, chữ thường, số, ký tự đặc biệt", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (!sdt.matches("^0\\d{9}$")) {
            JOptionPane.showMessageDialog(this, "Số điện thoại phải bắt đầu bằng 0 và có 10 chữ số", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false);
        try {
            sdf.parse(ngayVaoLam);
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Ngày vào làm phải có định dạng dd/MM/yyyy", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }

    private NhanVien createNhanVien() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false);
        Date ngayVaoLam = null;
        try {
            java.util.Date parsedDate = sdf.parse(txtNgayVaoLam.getText().trim());
            ngayVaoLam = new Date(parsedDate.getTime());
        } catch (ParseException e) {
            // Đã kiểm tra trong validData, nên không cần xử lý thêm
        }

        return new NhanVien(
                txtMaNV.getText().trim(),
                txtHoTen.getText().trim(),
                txtSDT.getText().trim(),
                chucVuCombo.getSelectedItem().toString(),
                txtMK.getText().trim(),
                ngayVaoLam,
                caLamCombo.getSelectedItem().toString(),
                trangThaiCombo.getSelectedItem().toString()
        );
    }

    private void clearForm() {
        txtMaNV.setText("");
        txtHoTen.setText("");
        txtSDT.setText("");
        txtMK.setText("");
        txtNgayVaoLam.setText("dd/MM/yyyy");
        chucVuCombo.setSelectedIndex(0);
        caLamCombo.setSelectedIndex(0);
        trangThaiCombo.setSelectedIndex(0);
        tableNV.clearSelection();
        isEditMode = false;
        btnSua.setEnabled(false);
        btnXoa.setEnabled(false);
        txtMaNV.setEditable(true);
    }

    public JTable getTableNV() {
        return tableNV;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Quản lý nhân viên");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1400, 700);
            frame.setLocationRelativeTo(null);
            frame.add(new StaffPanel());
            frame.setVisible(true);
        });
    }
}