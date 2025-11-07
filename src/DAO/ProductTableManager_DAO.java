package DAO;

import Entity.SanPham;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class ProductTableManager_DAO {
    private JTable productTable;
    private OrderTableManager_DAO orderTableManager;

    public ProductTableManager_DAO(JTable productTable, OrderTableManager_DAO orderTableManager) {
        if (productTable == null || orderTableManager == null) {
            throw new IllegalArgumentException("productTable and orderTableManager must not be null");
        }
        this.productTable = productTable;
        this.orderTableManager = orderTableManager;

        // Khởi tạo bảng sản phẩm
        String[] productColumnNames = {"Tên SP", "Size", "Đơn giá", "Chọn"};
        productTable.setModel(new DefaultTableModel(new Object[][]{}, productColumnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3; // Chỉ cột "Chọn" có thể chỉnh sửa
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 3 ? JButton.class : Object.class;
            }
        });
    }

    public void updateTable(List<SanPham> data) {
        Object[][] tableData = new Object[data.size()][4];
        for (int i = 0; i < data.size(); i++) {
            SanPham product = data.get(i);
            tableData[i] = new Object[]{
                product.getTenSP(),
                product.getSize(),
                product.getDonGia(),
                "Chọn" // Dữ liệu cho cột nút là chuỗi, không phải JButton
            };
        }

        // Cập nhật mô hình dữ liệu bảng
        productTable.setModel(new DefaultTableModel(tableData, new String[]{"Tên SP", "Size", "Đơn giá", "Chọn"}) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 3 ? String.class : Object.class;
            }
        });

        // Cột "Chọn" xử lý Renderer và Editor
        TableColumn chonColumn = productTable.getColumnModel().getColumn(3);
        chonColumn.setCellRenderer(new ButtonRenderer());
        chonColumn.setCellEditor(new ButtonEditor(new JCheckBox(), productTable, orderTableManager));
    }

    // === Renderer: Hiển thị nút ===
    static class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            setText("Chọn");
            setBackground(Color.decode("#4CAF50")); // Màu xanh lá
            setForeground(Color.WHITE);
            return this;
        }
    }

    // === Editor: Xử lý khi nhấn nút ===
    static class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private boolean isPushed;
        private JTable table;
        private OrderTableManager_DAO orderTableManager;

        public ButtonEditor(JCheckBox checkBox, JTable table, OrderTableManager_DAO orderTableManager) {
            super(checkBox);
            this.table = table;
            this.orderTableManager = orderTableManager;

            button = new JButton("Chọn");
            button.setOpaque(true);
            button.setBackground(Color.decode("#4CAF50"));
            button.setForeground(Color.WHITE);

            button.addActionListener(e -> {
                fireEditingStopped(); // Dừng edit để không bị lỗi

                int row = table.getSelectedRow();
                if (row >= 0) {
                    String tenSP = table.getValueAt(row, 0).toString();
                    String size = table.getValueAt(row, 1).toString();
                    String gia = table.getValueAt(row, 2).toString();

                    orderTableManager.addOrUpdateProduct(tenSP, size, gia);
                    
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return "Chọn";
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }
}
