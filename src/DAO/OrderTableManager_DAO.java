package DAO;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.*;
import java.awt.*;

public class OrderTableManager_DAO {
    private JTable orderTable;

    public OrderTableManager_DAO(JTable orderTable) {
        if (orderTable == null) {
            throw new IllegalArgumentException("orderTable must not be null");
        }
        this.orderTable = orderTable;

        String[] orderColumnNames = {"Tên món", "Size", "Giá", "Số lượng", "Tổng cộng", "Hủy"};
        DefaultTableModel model = new DefaultTableModel(new Object[][]{}, orderColumnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3 || column == 5;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 5 ? JButton.class : Object.class;
            }
        };

        orderTable.setModel(model);

        model.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE && e.getColumn() == 3) {
                int row = e.getFirstRow();
                try {
                    int soLuong = Integer.parseInt(model.getValueAt(row, 3).toString());
                    if (soLuong <= 0) {
                        JOptionPane.showMessageDialog(null, "Số lượng phải lớn hơn 0!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        model.setValueAt(1, row, 3);
                        return;
                    }
                    double donGia = parseGia(model.getValueAt(row, 2));
                    double tongCong = donGia * soLuong;
                    model.setValueAt(formatGia(tongCong), row, 4);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Vui lòng nhập số hợp lệ cho số lượng.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    model.setValueAt(1, row, 3);
                }
            }
        });

        TableColumn cancelColumn = orderTable.getColumnModel().getColumn(5);
        cancelColumn.setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
            JButton button = new JButton("Hủy");
            button.setForeground(Color.RED);
            button.setFocusPainted(false);
            return button;
        });

        cancelColumn.setCellEditor(new CancelButtonEditor(new JCheckBox(), orderTable));
    }

    public void addOrUpdateProduct(String tenMon, String size, String giaStr) {
        if (tenMon == null || size == null || giaStr == null) {
            JOptionPane.showMessageDialog(null, "Dữ liệu sản phẩm không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double donGia;
        try {
            donGia = Double.parseDouble(giaStr.replace("đ", "").trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Giá sản phẩm không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        DefaultTableModel model = (DefaultTableModel) orderTable.getModel();
        boolean found = false;

        for (int i = 0; i < model.getRowCount(); i++) {
            if (model.getValueAt(i, 0).equals(tenMon) && model.getValueAt(i, 1).equals(size)) {
                int soLuong = Integer.parseInt(model.getValueAt(i, 3).toString()) + 1;
                model.setValueAt(soLuong, i, 3);
                double tongCong = donGia * soLuong;
                model.setValueAt(formatGia(tongCong), i, 4);
                found = true;
                break;
            }
        }

        if (!found) {
            int soLuong = 1;
            double tongCong = donGia * soLuong;
            model.addRow(new Object[]{tenMon, size, donGia, soLuong, formatGia(tongCong), "Hủy"});
        }
    }

    private double parseGia(Object gia) {
        try {
            return Double.parseDouble(gia.toString().replace("đ", "").trim());
        } catch (NumberFormatException e) {
            System.err.println("Lỗi parse giá: " + e.getMessage());
            return 0;
        }
    }

    private String formatGia(double gia) {
        return String.format("%,.0f", gia).replace(",", ".") + "đ";
    }

    class CancelButtonEditor extends AbstractCellEditor implements TableCellEditor {
        private final JButton button = new JButton("Hủy");
        private final JTable table;

        public CancelButtonEditor(JCheckBox checkBox, JTable table) {
            this.table = table;
            button.setForeground(Color.RED);
            button.setFocusPainted(false);
            button.addActionListener(e -> {
                int row = table.getEditingRow();
                if (row != -1) {
                    int confirm = JOptionPane.showConfirmDialog(
                            null,
                            "Bạn có chắc chắn muốn xóa sản phẩm này khỏi đơn hàng?",
                            "Xác nhận xóa",
                            JOptionPane.YES_NO_OPTION
                    );
                    if (confirm == JOptionPane.YES_OPTION) {
                        ((DefaultTableModel) table.getModel()).removeRow(row);
                    }
                }
                fireEditingStopped();
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return "Hủy";
        }
    }
}