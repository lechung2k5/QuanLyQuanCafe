package UI;

import DAO.LoaiSP_DAO;
import DAO.ProductTable_DAO;
import Entity.LoaiSP;
import Entity.SanPham;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import ConnectDB.ConnectDB;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProductPanel extends JPanel {
    private static final String[] COLUMN_NAMES = {"Mã SP", "Tên Món", "Size", "Giá"};
    private static final String[] SIZES = {"L", "M", "XL"};
    private static final String[] CATEGORIES = {
            "Sữa Chua", "Trà", "Cà Phê", "Đồ Ăn Vặt", "Đá Xay",
            "Trà Sữa", "Sinh Tố", "Soda", "Nước Ép"
    };
    private static final String[] CATEGORY_CODES = {
            "SuaChua", "Tra", "CaPhe", "DoAnVat", "DaXay",
            "TraSua", "SinhTo", "SoDa", "NuocEp"
    };

    private ProductTable_DAO productDAO;
    private LoaiSP_DAO categoryDAO;
    private DefaultTableModel tableModel;
    private JTable productTable;
    private JTextField txtMaSP;
    private JTextField txtTenSP;
    private JTextField txtGia;
    private JComboBox<String> sizeCombo;
    private JComboBox<String> categoryCombo;
    private JButton btnThem;
    private JButton btnXoa;
    private JButton btnChinhSua;
    private JButton btnHuy;
    private JButton btnXoaTrang;
    private boolean isEditMode;

    /**
     * Khởi tạo ProductPanel.
     */
    public ProductPanel() {
        if (!initializeDatabaseConnection()) {
            JOptionPane.showMessageDialog(null, "Không thể kết nối đến cơ sở dữ liệu. Vui lòng kiểm tra cài đặt SQL Server.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        this.productDAO = new ProductTable_DAO();
        this.categoryDAO = new LoaiSP_DAO();
        this.tableModel = new DefaultTableModel(COLUMN_NAMES, 0);
        this.productTable = new JTable(tableModel);
        this.txtMaSP = new JTextField(15);
        this.txtTenSP = new JTextField(15);
        this.txtGia = new JTextField(15);
        this.sizeCombo = new JComboBox<>(SIZES);
        this.categoryCombo = new JComboBox<>();
        this.isEditMode = false;
		this.btnThem = new JButton();

        initializeUI();
        loadCategories();
        loadAllProducts();
    }

    /**
     * Khởi tạo kết nối cơ sở dữ liệu.
     * @return true nếu kết nối thành công, false nếu thất bại
     */
    private boolean initializeDatabaseConnection() {
        try {
            Connection conn = ConnectDB.getConnection();
            return conn != null;
        } catch (Exception e) {
            System.err.println("Không thể lấy kết nối từ ConnectDB: " + e.getMessage());
            return false;
        }
    }

    /**
     * Kiểm tra kết nối cơ sở dữ liệu.
     * @return true nếu kết nối hợp lệ, false nếu không
     */
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

    /**
     * Khởi tạo giao diện người dùng.
     */
    private void initializeUI() {
        setLayout(new BorderLayout());
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        mainPanel.add(createNorthPanel(), BorderLayout.NORTH);
        mainPanel.add(createTablePanel(), BorderLayout.CENTER);
        mainPanel.add(createFormPanel(), BorderLayout.SOUTH);

        add(mainPanel);
        setupEventListeners();
    }

    /**
     * Tạo panel phía trên chứa tiêu đề và các nút danh mục.
     * @return JPanel chứa tiêu đề và nút danh mục
     */
    private JPanel createNorthPanel() {
        JPanel northPanel = new JPanel(new BorderLayout());

        JLabel lblTitle = new JLabel("Quản Lý Sản Phẩm");
        lblTitle.setForeground(Color.BLUE);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        northPanel.add(lblTitle, BorderLayout.NORTH);

        JPanel buttonGridPanel = new JPanel(new GridLayout(1, CATEGORIES.length, 10, 10));
        buttonGridPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        for (int i = 0; i < CATEGORIES.length; i++) {
            JButton button = createCategoryButton(CATEGORIES[i], CATEGORY_CODES[i]);
            buttonGridPanel.add(button);
        }

        northPanel.add(buttonGridPanel, BorderLayout.CENTER);
        return northPanel;
    }

    /**
     * Tạo panel chứa bảng sản phẩm.
     * @return JScrollPane chứa bảng sản phẩm
     */
    private JScrollPane createTablePanel() {
        productTable.getColumnModel().getColumn(2).setMaxWidth(100);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        productTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        productTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);

        return new JScrollPane(productTable);
    }

    /**
     * Tạo panel chứa form nhập liệu và các nút chức năng.
     * @return JPanel chứa form và nút
     */
    private JPanel createFormPanel() {
        JPanel southPanel = new JPanel(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Labels
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Mã Sản Phẩm:"), gbc);
        gbc.gridx = 1;
        formPanel.add(new JLabel("Tên Món:"), gbc);
        gbc.gridx = 2;
        formPanel.add(new JLabel("Size:"), gbc);
        gbc.gridx = 3;
        formPanel.add(new JLabel("Giá:"), gbc);
        gbc.gridx = 4;
        formPanel.add(new JLabel("Loại Sản Phẩm:"), gbc);

        // Input Fields
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(txtMaSP, gbc);

        gbc.gridx = 1;
        formPanel.add(txtTenSP, gbc);

        gbc.gridx = 2;
        sizeCombo.setPreferredSize(new Dimension(100, 30));
        formPanel.add(sizeCombo, gbc);

        gbc.gridx = 3;
        formPanel.add(txtGia, gbc);

        gbc.gridx = 4;
        categoryCombo.setEditable(false);
        formPanel.add(categoryCombo, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        Dimension buttonSize = new Dimension(120, 40);

        btnThem = new JButton("Thêm");
        btnThem.setBackground(new Color(49, 186, 99));
        btnThem.setForeground(Color.WHITE);
        btnThem.setPreferredSize(buttonSize);

        btnXoa = new JButton("Xóa");
        btnXoa.setBackground(new Color(186, 38, 38));
        btnXoa.setForeground(Color.WHITE);
        btnXoa.setPreferredSize(buttonSize);
        btnXoa.setEnabled(false);

        btnChinhSua = new JButton("Sửa");
        btnChinhSua.setBackground(new Color(7, 149, 220));
        btnChinhSua.setForeground(Color.WHITE);
        btnChinhSua.setPreferredSize(buttonSize);
        btnChinhSua.setEnabled(false);

        btnHuy = new JButton("Hủy");
        btnHuy.setBackground(new Color(255, 192, 0));
        btnHuy.setForeground(Color.WHITE);
        btnHuy.setPreferredSize(buttonSize);

        btnXoaTrang = new JButton("Xóa Form");
        btnXoaTrang.setBackground(new Color(108, 117, 125));
        btnXoaTrang.setForeground(Color.WHITE);
        btnXoaTrang.setPreferredSize(buttonSize);

        buttonPanel.add(btnThem);
        buttonPanel.add(btnXoa);
        buttonPanel.add(btnChinhSua);
        buttonPanel.add(btnHuy);
        buttonPanel.add(btnXoaTrang);

        southPanel.add(formPanel, BorderLayout.CENTER);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);
        return southPanel;
    }

    /**
     * Tạo nút danh mục.
     * @param text Tên danh mục
     * @param categoryCode Mã danh mục
     * @return JButton cho danh mục
     */
    private JButton createCategoryButton(String text, String categoryCode) {
        JButton button = new JButton(text);
        button.setForeground(Color.BLUE);
        button.setPreferredSize(new Dimension(120, 40));
        button.setBackground(Color.WHITE);
        button.setBorder(BorderFactory.createLineBorder(Color.BLUE));
        button.addActionListener(e -> loadProductsByCategory(categoryCode));
        return button;
    }

    /**
     * Thiết lập các sự kiện cho bảng và nút.
     */
    private void setupEventListeners() {
        productTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!checkConnection()) return;
                int row = productTable.getSelectedRow();
                if (row >= 0) {
                    try {
                        String productCode = tableModel.getValueAt(row, 0).toString();
                        SanPham product = productDAO.getSanPhamByMaSP(productCode);
                        if (product != null) {
                            txtMaSP.setText(product.getMaSP());
                            txtTenSP.setText(product.getTenSP());
                            sizeCombo.setSelectedItem(product.getSize());
                            txtGia.setText(String.format("%.0f", product.getDonGia()));
                            String categoryName = categoryDAO.getalltbLoaiSP().stream()
                                    .filter(lsp -> lsp.getMaLoai().equals(product.getLoaiSP()))
                                    .findFirst()
                                    .map(LoaiSP::getTenSP)
                                    .orElse("");
                            categoryCombo.setSelectedItem(categoryName);
                            isEditMode = true;
                            btnChinhSua.setEnabled(true);
                            btnXoa.setEnabled(true);
                            txtMaSP.setEditable(false);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(ProductPanel.this, "Lỗi khi tải thông tin sản phẩm: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        btnThem.addActionListener(e -> addProduct());
        btnXoa.addActionListener(e -> deleteProduct());
        btnChinhSua.addActionListener(e -> updateProduct());
        btnHuy.addActionListener(e -> cancelEdit());
        btnXoaTrang.addActionListener(e -> clearForm());
    }

    /**
     * Tải danh sách loại sản phẩm vào combo box.
     */
    private void loadCategories() {
        if (!checkConnection()) return;
        try {
            ArrayList<LoaiSP> categories = categoryDAO.getalltbLoaiSP();
            if (categories.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Danh sách loại sản phẩm rỗng. Vui lòng thêm loại sản phẩm trước!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            } else {
                categoryCombo.removeAllItems();
                for (LoaiSP category : categories) {
                    categoryCombo.addItem(category.getTenSP());
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải danh sách loại sản phẩm: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Tải tất cả sản phẩm vào bảng.
     */
    private void loadAllProducts() {
        if (!checkConnection()) return;
        try {
            tableModel.setRowCount(0);
            Map<String, SanPham> productMap = productDAO.getAllSanPhamAsMap();
            for (SanPham product : productMap.values()) {
                tableModel.addRow(new Object[]{
                        product.getMaSP(),
                        product.getTenSP(),
                        product.getSize(),
                        String.format("%,.0f", product.getDonGia())
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu sản phẩm: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Tải sản phẩm theo danh mục.
     * @param categoryCode Mã danh mục
     */
    private void loadProductsByCategory(String categoryCode) {
        if (!checkConnection()) return;
        try {
            tableModel.setRowCount(0);
            List<SanPham> products = productDAO.getProductsByCategory(categoryCode);
            for (SanPham product : products) {
                tableModel.addRow(new Object[]{
                        product.getMaSP(),
                        product.getTenSP(),
                        product.getSize(),
                        String.format("%,.0f", product.getDonGia())
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải sản phẩm theo danh mục: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Thêm sản phẩm mới.
     */
    private void addProduct() {
        if (!checkConnection()) return;
        if (isEditMode) {
            JOptionPane.showMessageDialog(this, "Đang ở chế độ chỉnh sửa. Vui lòng lưu hoặc hủy trước khi thêm mới!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (validData()) {
            try {
                SanPham product = createSanPham();
                if (productDAO.create(product)) {
                    JOptionPane.showMessageDialog(this, "Thêm sản phẩm thành công", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    loadAllProducts();
                    clearForm();
                } else {
                    JOptionPane.showMessageDialog(this, "Thêm sản phẩm thất bại", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi thêm sản phẩm: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Xóa sản phẩm.
     */
    private void deleteProduct() {
        if (!checkConnection()) return;
        int row = productTable.getSelectedRow();
        if (row >= 0) {
            String productCode = tableModel.getValueAt(row, 0).toString();
            int confirm = JOptionPane.showConfirmDialog(this, "Xóa sản phẩm " + productCode + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    if (productDAO.delete(productCode)) {
                        JOptionPane.showMessageDialog(this, "Xóa sản phẩm thành công", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                        loadAllProducts();
                        clearForm();
                        isEditMode = false;
                        btnXoa.setEnabled(false);
                        btnChinhSua.setEnabled(false);
                        txtMaSP.setEditable(true);
                    } else {
                        JOptionPane.showMessageDialog(this, "Xóa sản phẩm thất bại", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Lỗi khi xóa sản phẩm: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một sản phẩm để xóa", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Cập nhật sản phẩm.
     */
    private void updateProduct() {
        if (!checkConnection()) return;
        if (isEditMode && validUpdateData()) {
            try {
                SanPham product = createSanPham();
                if (productDAO.update(product)) {
                    JOptionPane.showMessageDialog(this, "Cập nhật sản phẩm thành công", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    loadAllProducts();
                    clearForm();
                    isEditMode = false;
                    btnXoa.setEnabled(false);
                    btnChinhSua.setEnabled(false);
                    txtMaSP.setEditable(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Cập nhật sản phẩm thất bại", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật sản phẩm: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Hủy chế độ chỉnh sửa.
     */
    private void cancelEdit() {
        if (isEditMode) {
            clearForm();
            JOptionPane.showMessageDialog(this, "Đã hủy chỉnh sửa", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Kiểm tra dữ liệu đầu vào khi thêm sản phẩm.
     * @return true nếu dữ liệu hợp lệ, false nếu không
     */
    private boolean validData() {
        String productCode = txtMaSP.getText().trim();
        String productName = txtTenSP.getText().trim();
        String priceStr = txtGia.getText().trim();
        Object selectedCategory = categoryCombo.getSelectedItem();

        if (productCode.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã sản phẩm không được rỗng", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (productDAO.getSanPhamByMaSP(productCode) != null) {
            JOptionPane.showMessageDialog(this, "Mã sản phẩm đã tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (productName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên món không được rỗng", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (!productName.matches("^[A-Z][\\p{L}]*(\\s[A-Za-z][\\p{L}]*)+")) {
            JOptionPane.showMessageDialog(this, "Tên món phải bắt đầu bằng chữ hoa và chỉ chứa chữ cái hoặc khoảng trắng", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (priceStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Giá không được rỗng", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        try {
            double price = Double.parseDouble(priceStr);
            if (price <= 0) {
                JOptionPane.showMessageDialog(this, "Giá phải lớn hơn 0", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Giá phải là số hợp lệ", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (selectedCategory == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn loại sản phẩm", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        String categoryName = selectedCategory.toString();
        String categoryCode = categoryDAO.getMaLoaiByTenSP(categoryName);
        if (categoryCode == null) {
            JOptionPane.showMessageDialog(this, "Loại sản phẩm '" + categoryName + "' không tồn tại", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    /**
     * Kiểm tra dữ liệu đầu vào khi cập nhật sản phẩm.
     * @return true nếu dữ liệu hợp lệ, false nếu không
     */
    private boolean validUpdateData() {
        String productCode = txtMaSP.getText().trim();
        String productName = txtTenSP.getText().trim();
        String priceStr = txtGia.getText().trim();
        Object selectedCategory = categoryCombo.getSelectedItem();

        if (productCode.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã sản phẩm không được rỗng", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (productName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên món không được rỗng", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (!productName.matches("^[A-Z][a-zA-Z\\s]*$")) {
            JOptionPane.showMessageDialog(this, "Tên món phải bắt đầu bằng chữ hoa và chỉ chứa chữ cái hoặc khoảng trắng", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (priceStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Giá không được rỗng", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        try {
            double price = Double.parseDouble(priceStr);
            if (price <= 0) {
                JOptionPane.showMessageDialog(this, "Giá phải lớn hơn 0", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Giá phải là số hợp lệ", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (selectedCategory == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn loại sản phẩm", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        String categoryName = selectedCategory.toString();
        String categoryCode = categoryDAO.getMaLoaiByTenSP(categoryName);
        if (categoryCode == null) {
            JOptionPane.showMessageDialog(this, "Loại sản phẩm '" + categoryName + "' không tồn tại", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    /**
     * Tạo đối tượng SanPham từ dữ liệu form.
     * @return SanPham mới
     */
    private SanPham createSanPham() {
        String productCode = txtMaSP.getText().trim();
        String productName = txtTenSP.getText().trim();
        String size = sizeCombo.getSelectedItem().toString();
        double price = Double.parseDouble(txtGia.getText().trim());
        String categoryName = categoryCombo.getSelectedItem().toString();
        String categoryCode = categoryDAO.getMaLoaiByTenSP(categoryName);
        return new SanPham(productCode, productName, categoryCode, price, size, 0, "");
    }

    /**
     * Xóa dữ liệu trên form.
     */
    private void clearForm() {
        txtMaSP.setText("");
        txtTenSP.setText("");
        txtGia.setText("");
        sizeCombo.setSelectedIndex(0);
        if (categoryCombo.getItemCount() > 0) {
            categoryCombo.setSelectedIndex(0);
        }
        isEditMode = false;
        btnXoa.setEnabled(false);
        btnChinhSua.setEnabled(false);
        txtMaSP.setEditable(true);
        productTable.clearSelection();
    }

    /**
     * Lấy bảng sản phẩm.
     * @return JTable chứa danh sách sản phẩm
     */
    public JTable getProductTable() {
        return productTable;
    }

    /**
     * Lấy model của bảng sản phẩm.
     * @return DefaultTableModel của bảng
     */
    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    /**
     * Main method để chạy thử giao diện.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Quản lý sản phẩm");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1400, 700);
            frame.setLocationRelativeTo(null);
            frame.add(new ProductPanel());
            frame.setVisible(true);
        });
    }
}