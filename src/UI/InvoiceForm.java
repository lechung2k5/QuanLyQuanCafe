package UI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class InvoiceForm extends JPanel {
    private String currentTable;
    private String currentOrderId;
    private boolean isTableReset;
    private final JTextField txtMaKH;
    private final JTable table;
    private final DefaultTableModel model;
    private final JComboBox<String> cbKhuyenMai;
    private final JTextField txtThanhTien;
    private final JSpinner dateSpinner;
    private final JSpinner timeSpinner;
    private final JComboBox<String> cbHinhThuc;
    private final JComboBox<String> cbBan;
    private final JLabel lblMaHoaDon;
    private final JLabel lblTongCong;
    private final Map<String, Map<String, Integer>> orderDetailsMap;

    public InvoiceForm() {
        this.currentTable = null;
        this.currentOrderId = String.format("HDXXX");
        this.isTableReset = false;
        this.orderDetailsMap = new HashMap<>();
        this.txtMaKH = new JTextField();
        this.model = new DefaultTableModel(new String[]{"Tên món", "Size", "Giá", "SL", "Tổng tiền"}, 0);
        this.table = new JTable(model);
        this.cbKhuyenMai = new JComboBox<>();
        this.txtThanhTien = new JTextField("0");
        this.dateSpinner = new JSpinner(new SpinnerDateModel());
        this.timeSpinner = new JSpinner(new SpinnerDateModel());
        this.cbHinhThuc = new JComboBox<>(new String[]{"Tại quán", "Mang về"});
        this.cbBan = new JComboBox<>();
        this.lblMaHoaDon = new JLabel("Mã hóa đơn: " + currentOrderId);
        this.lblTongCong = new JLabel("Tổng cộng: 0 VNĐ");

        initializeUI();
    }

    private void initializeUI() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        lblMaHoaDon.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(lblMaHoaDon, gbc);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(400, 200));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        add(scrollPane, gbc);

        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        add(new JLabel("Số điện thoại:"), gbc);

        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 2;
        add(txtMaKH, gbc);

        JPanel panel1 = new JPanel(new GridLayout(1, 3, 5, 5));
        for (int i = 1; i <= 20; i++) {
            cbBan.addItem(String.format("Bàn %02d", i));
        }
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy"));
        timeSpinner.setEditor(new JSpinner.DateEditor(timeSpinner, "hh:mm a"));
        panel1.add(cbBan);
        panel1.add(dateSpinner);
        panel1.add(timeSpinner);
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        add(panel1, gbc);

        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.WEST;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 0;

        gbc.gridy = 5;
        gbc.gridx = 0;
        add(new JLabel("Thành tiền:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        txtThanhTien.setPreferredSize(new Dimension(100, 25));
        add(txtThanhTien, gbc);

        gbc.gridy = 6;
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.WEST;
        add(new JLabel("Khuyến mãi:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        cbKhuyenMai.setPreferredSize(new Dimension(100, 25));
        add(cbKhuyenMai, gbc);

        gbc.gridy = 7;
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.WEST;
        add(new JLabel("Hình thức:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        cbHinhThuc.setPreferredSize(new Dimension(100, 25));
        add(cbHinhThuc, gbc);

        lblTongCong.setForeground(Color.RED);
        lblTongCong.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(lblTongCong, gbc);
    }

    // Getter setter giữ nguyên bên dưới nhé
    // (không cần chỉnh sửa)

    public String getCurrentTable() { return currentTable; }
    public void setCurrentTable(String table) { this.currentTable = table; }
    public String getCurrentOrderId() { return currentOrderId; }
    public void setCurrentOrderId(String orderId) {
        this.currentOrderId = orderId;
        lblMaHoaDon.setText("Mã hóa đơn: " + orderId);
    }
    public boolean isTableReset() { return isTableReset; }
    public void setTableReset(boolean tableReset) { isTableReset = tableReset; }
    public JTextField getTxtMaKH() { return txtMaKH; }
    public JTable getTable() { return table; }
    public DefaultTableModel getModel() { return model; }
    public JComboBox<String> getCbKhuyenMai() { return cbKhuyenMai; }
    public JTextField getTxtThanhTien() { return txtThanhTien; }
    public JSpinner getDateSpinner() { return dateSpinner; }
    public JSpinner getTimeSpinner() { return timeSpinner; }
    public JComboBox<String> getCbHinhThuc() { return cbHinhThuc; }
    public JComboBox<String> getCbBan() { return cbBan; }
    public JLabel getLblMaHoaDon() { return lblMaHoaDon; }
    public JLabel getLblTongCong() { return lblTongCong; }
    public Map<String, Map<String, Integer>> getOrderDetailsMap() { return orderDetailsMap; }
}