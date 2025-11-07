package UI;

import DAO.ProductTable_DAO;
import DAO.ProductTableManager_DAO;
import DAO.OrderTableManager_DAO;
import Entity.SanPham;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderPanel extends JPanel {
    private InvoicePanel invoicePanel; // Không final để có thể cập nhật
    private String currentOrderId;
    private final JTable productTable;
    private final JTable orderTable;
    private final ProductTable_DAO productDAO;
    private final ProductTableManager_DAO productTableManager;
    private final OrderTableManager_DAO orderTableManager;
    private final Map<String, String> categoryMapping;
    private JButton btnChuyenHoaDon;
    private JTextField searchField;

    /**
     * Khởi tạo OrderPanel.
     * @param invoicePanel Panel hóa đơn
     */
    public OrderPanel(InvoicePanel invoicePanel) {
        this.invoicePanel = invoicePanel;
        this.currentOrderId = null;
        this.productDAO = new ProductTable_DAO();
        this.categoryMapping = new HashMap<>();
        this.productTable = new JTable();
        this.orderTable = new JTable();
        this.orderTableManager = new OrderTableManager_DAO(orderTable);
        this.productTableManager = new ProductTableManager_DAO(productTable, orderTableManager);

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Order"));

        initializeCategoryMapping();
        initializeUI();
        loadProductsByCategory("Cà phê");
    }

    /**
     * Cập nhật InvoicePanel.
     * @param invoicePanel Panel hóa đơn mới
     */
    public void setInvoicePanel(InvoicePanel invoicePanel) {
        this.invoicePanel = invoicePanel;
    }

    /**
     * Khởi tạo giao diện người dùng.
     */
    private void initializeUI() {
        JPanel categoryPanel = createCategoryPanel();
        add(categoryPanel, BorderLayout.NORTH);

        JPanel contentPanel = createContentPanel();
        add(contentPanel, BorderLayout.CENTER);

        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Khởi tạo ánh xạ danh mục sản phẩm.
     */
    private void initializeCategoryMapping() {
        categoryMapping.put("Cà phê", "CaPhe");
        categoryMapping.put("Trà", "Tra");
        categoryMapping.put("Sữa chua", "SuaChua");
        categoryMapping.put("Đồ ăn vặt", "DoAnVat");
        categoryMapping.put("Sinh tố", "SinhTo");
        categoryMapping.put("Trà sữa", "TraSua");
        categoryMapping.put("Đá xay", "DaXay");
        categoryMapping.put("Soda", "Soda");
        categoryMapping.put("Nước ép", "NuocEp");
    }

    /**
     * Tạo panel chứa các nút danh mục và thanh tìm kiếm.
     * @return JPanel chứa các nút danh mục và thanh tìm kiếm
     */
    private JPanel createCategoryPanel() {
        JPanel categoryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        categoryPanel.setBackground(Color.WHITE);

        // Thêm thanh tìm kiếm
        searchField = new JTextField(15);
        searchField.setToolTipText("Tìm kiếm sản phẩm...");
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String searchText = searchField.getText().trim();
                if (searchText.isEmpty()) {
                    loadProductsByCategory("Cà phê"); // Mặc định hiển thị danh mục "Cà phê"
                } else {
                    searchProducts(searchText);
                }
            }
        });
        categoryPanel.add(new JLabel("Tìm kiếm:"));
        categoryPanel.add(searchField);

        // Thêm các nút danh mục
        String[] categories = {"Cà phê", "Trà", "Sữa chua", "Đồ ăn vặt", "Sinh tố", "Trà sữa", "Đá xay", "Soda", "Nước ép"};
        for (String category : categories) {
            JButton button = createCategoryButton(category);
            categoryPanel.add(button);
        }

        return categoryPanel;
    }

    /**
     * Tạo nút danh mục.
     * @param categoryName Tên danh mục
     * @return JButton cho danh mục
     */
    private JButton createCategoryButton(String categoryName) {
        JButton button = new JButton(categoryName);
        button.setBackground(Color.decode("#1E3A8A"));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.addActionListener(e -> {
            loadProductsByCategory(categoryName);
            searchField.setText(""); // Xóa nội dung thanh tìm kiếm khi chọn danh mục
        });
        return button;
    }

    /**
     * Tạo panel chứa bảng sản phẩm và bảng đơn hàng.
     * @return JPanel chứa các bảng
     */
    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        productTable.setRowHeight(30);
        orderTable.setRowHeight(30);

        JScrollPane productScrollPane = new JScrollPane(productTable);
        JScrollPane orderScrollPane = new JScrollPane(orderTable);

        contentPanel.add(productScrollPane);
        contentPanel.add(orderScrollPane);

        return contentPanel;
    }

    /**
     * Tạo panel chứa các nút chức năng.
     * @return JPanel chứa các nút
     */
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        JButton btnGopOrder = new JButton("Gộp order");
        btnGopOrder.setBackground(Color.decode("#7C3AED"));
        btnGopOrder.setForeground(Color.WHITE);
        btnGopOrder.setFocusPainted(false);
        btnGopOrder.addActionListener(e -> JOptionPane.showMessageDialog(this, "Chức năng gộp order chưa được cài đặt!", "Thông báo", JOptionPane.INFORMATION_MESSAGE));

        btnChuyenHoaDon = new JButton("Chuyển hóa đơn");
        btnChuyenHoaDon.setBackground(Color.decode("#10B981"));
        btnChuyenHoaDon.setForeground(Color.WHITE);
        btnChuyenHoaDon.setFocusPainted(false);
        btnChuyenHoaDon.addActionListener(e -> chuyenHoaDon());

        buttonPanel.add(btnGopOrder);
        buttonPanel.add(btnChuyenHoaDon);

        return buttonPanel;
    }

    /**
     * Tải sản phẩm theo danh mục.
     * @param categoryName Tên danh mục
     */
    private void loadProductsByCategory(String categoryName) {
        String categoryCode = categoryMapping.get(categoryName);
        if (categoryCode != null) {
            try {
                List<SanPham> products = productDAO.getProductsByCategory(categoryCode);
                productTableManager.updateTable(products);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi tải sản phẩm: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Danh mục không hợp lệ: " + categoryName, "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Tìm kiếm sản phẩm theo tên.
     * @param searchText Chuỗi tìm kiếm
     */
    private void searchProducts(String searchText) {
        try {
            List<SanPham> allProducts = productDAO.getProductsByName(searchText);
            productTableManager.updateTable(allProducts);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tìm kiếm sản phẩm: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Lấy dữ liệu từ orderTable.
     * @return Mảng dữ liệu đơn hàng
     */
    public Object[][] getOrderData() {
        DefaultTableModel model = (DefaultTableModel) orderTable.getModel();
        int rowCount = model.getRowCount();
        Object[][] data = new Object[rowCount][5];
        for (int i = 0; i < rowCount; i++) {
            data[i][0] = model.getValueAt(i, 0); // Tên món
            data[i][1] = model.getValueAt(i, 1); // Size
            data[i][2] = model.getValueAt(i, 2); // Giá
            data[i][3] = model.getValueAt(i, 3); // Số lượng
            data[i][4] = model.getValueAt(i, 4); // Tổng cộng
        }
        return data;
    }

    /**
     * Xóa bảng orderTable.
     */
    public void clearOrderTable() {
        DefaultTableModel model = (DefaultTableModel) orderTable.getModel();
        model.setRowCount(0);
        System.out.println("OrderPanel - Cleared orderTable, row count: " + model.getRowCount());
        revalidate();
        repaint();
    }

  
    /**
     * Chuyển hóa đơn sang InvoicePanel.
     */
    private void chuyenHoaDon() {
        if (orderTable.getRowCount() == 0) {
           
            return;
        }

        if (invoicePanel == null) {
            JOptionPane.showMessageDialog(this, "Lỗi: InvoicePanel chưa khởi tạo!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String soBan = invoicePanel.getCurrentTable();
        if (soBan == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn bàn trước khi chuyển hóa đơn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Lấy dữ liệu từ orderTable
        Object[][] orderData = getOrderData();
        StringBuilder newDetails = new StringBuilder();
        double tongTien = 0.0;
        for (Object[] row : orderData) {
            String tenMon = row[0].toString();
            String size = row[1].toString();
            String gia = row[2].toString();
            String sl = row[3].toString();
            try {
                double giaValue = Double.parseDouble(gia);
                int slValue = Integer.parseInt(sl);
                tongTien += giaValue * slValue;
                newDetails.append(tenMon).append(", Size: ").append(size)
                        .append(", SL: ").append(sl).append(", Giá: ").append(gia).append("\n");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Dữ liệu đơn hàng không hợp lệ: " + tenMon, "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // Lấy thông tin đơn hàng hiện tại từ InvoicePanel
        String orderId = invoicePanel.getCurrentOrderId();
        String soDienThoai = invoicePanel.getTxtMaKH().getText().trim().isEmpty() ? "Không có" : invoicePanel.getTxtMaKH().getText().trim();
        String hinhThuc = invoicePanel.getCbHinhThuc().getSelectedItem().toString();
        
        // Tính tổng tiền bao gồm cả các món hiện có trong InvoicePanel
        double currentTotal = invoicePanel.calculateGrandTotal();
        tongTien += currentTotal; // Cộng tổng tiền của các món mới vào tổng tiền hiện tại

        String[] orderInfo = {
                orderId,
                "Trạng thái: Chưa giao",
                "Tổng tiền: " + invoicePanel.formatCurrency(tongTien),
                "Thời gian: " + new SimpleDateFormat("hh:mm a").format(new Date()),
                "Hình thức: " + hinhThuc,
                "Số điện thoại: " + soDienThoai,
                "Số bàn: " + soBan
        };

        // Cập nhật đơn hàng trong InvoicePanel, giữ nguyên các món hiện có và thêm các món mới
        try {
            invoicePanel.updateOrCreateOrder(orderId, orderInfo, newDetails.toString(), true);
            JOptionPane.showMessageDialog(this, "Đã cập nhật hóa đơn cho " + soBan, "Thành công", JOptionPane.INFORMATION_MESSAGE);
            clearOrderTable(); // Xóa orderTable sau khi chuyển
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi cập nhật hóa đơn: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Tải đơn hàng để chỉnh sửa.
     * @param orderId Mã đơn hàng
     * @param details Chi tiết đơn hàng
     */
    public void loadOrderForEditing(String orderId, String details) {
        this.currentOrderId = orderId;
        clearOrderTable();
        if (details != null && !details.trim().isEmpty()) {
            String[] detailLines = details.split("\n");
            for (String line : detailLines) {
                if (!line.trim().isEmpty()) {
                    String[] parts = line.split(", ");
                    if (parts.length >= 4) {
                        String tenMon = parts[0];
                        String size = parts[1].replace("Size: ", "");
                        String gia = parts[3].replace("Giá: ", "");
                        try {
                            orderTableManager.addOrUpdateProduct(tenMon, size, gia);
                            System.out.println("OrderPanel - Added product: " + tenMon + ", Size: " + size);
                        } catch (Exception e) {
                            System.err.println("Lỗi thêm sản phẩm vào orderTable: " + e.getMessage());
                        }
                    }
                }
            }
        }
        revalidate();
        repaint();
        System.out.println("OrderPanel - Loaded order for editing: " + orderId + ", Row count: " + ((DefaultTableModel) orderTable.getModel()).getRowCount());
    }

    /**
     * Lấy bảng orderTable.
     * @return JTable orderTable
     */
    public JTable getOrderTable() {
        return orderTable;
    }

    /**
     * Lấy nút chuyển hóa đơn.
     * @return JButton chuyển hóa đơn
     */
    public JButton getChuyenHoaDonButton() {
        return btnChuyenHoaDon;
    }

    /**
     * Lấy mã đơn hàng hiện tại.
     * @return Mã đơn hàng
     */
    public String getCurrentOrderId() {
        return currentOrderId;
    }

    /**
     * Thiết lập mã đơn hàng hiện tại.
     * @param orderId Mã đơn hàng
     */
    public void setCurrentOrderId(String orderId) {
        this.currentOrderId = orderId;
    }
}