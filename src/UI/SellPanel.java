package UI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class SellPanel extends JPanel {
    // Biến lưu mã nhân viên, mặc định là "NVJAVA" nếu không cung cấp
    private String maNV;
    // Panel để thêm món vào đơn hàng
    private OrderPanel orderPanel;
    // Panel hiển thị danh sách bàn và trạng thái
    private OrderListPanel orderListPanel;
    // Panel hiển thị chi tiết hóa đơn
    private InvoicePanel invoicePanel;

    /**
     * Khởi tạo SellPanel với mã nhân viên.
     * @param maNV Mã nhân viên
     */
    public SellPanel(String maNV) {
        // Gán mã nhân viên, sử dụng "NVJAVA" nếu maNV là null
        this.maNV = maNV != null ? maNV : "NVJAVA";
        // Sử dụng BorderLayout với khoảng cách 10px giữa các thành phần
        setLayout(new BorderLayout(10, 10));
        // Đặt màu nền trắng cho panel
        setBackground(Color.WHITE);

        // Khởi tạo tạm các panel để tránh null pointer
        orderPanel = new OrderPanel(null); // Tạm khởi tạo để tránh null
        orderListPanel = new OrderListPanel(null);
        // Khởi tạo InvoicePanel với mã nhân viên và các panel liên quan
        invoicePanel = new InvoicePanel(maNV, new Object[0][], orderListPanel, orderPanel);
        // Gán lại orderPanel với invoicePanel để đảm bảo liên kết
        orderPanel = new OrderPanel(invoicePanel);
        // Gán lại orderListPanel với invoicePanel
        orderListPanel = new OrderListPanel(invoicePanel);

        // Đặt kích thước ưu tiên cho các panel
        orderListPanel.setPreferredSize(new Dimension(200, 700));
        invoicePanel.setPreferredSize(new Dimension(300, 700));

        // Thêm các panel vào bố cục BorderLayout
        add(orderListPanel, BorderLayout.WEST); // Danh sách bàn ở phía Tây
        add(orderPanel, BorderLayout.CENTER);   // Panel đặt món ở trung tâm
        add(invoicePanel, BorderLayout.EAST);   // Panel hóa đơn ở phía Đông

        // Thiết lập sự kiện cho nút chuyển hóa đơn
        setupInvoiceUpdateListener();
    }

    /**
     * Thiết lập sự kiện cho nút "Chuyển hóa đơn" để cập nhật đơn hàng.
     */
    private void setupInvoiceUpdateListener() {
        // Thêm hành động khi nhấn nút "Chuyển hóa đơn" trên OrderPanel
        orderPanel.getChuyenHoaDonButton().addActionListener(e -> {
            // Lấy bàn hiện tại từ InvoicePanel
            String currentTable = invoicePanel.getCurrentTable();
            // Kiểm tra xem đã chọn bàn hợp lệ chưa
            if (currentTable == null || !Arrays.asList(OrderListPanel.TABLES).contains(currentTable)) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn bàn trước khi chuyển hóa đơn!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Lấy model của bảng đơn hàng từ OrderPanel
            DefaultTableModel orderModel = (DefaultTableModel) orderPanel.getOrderTable().getModel();
            // Kiểm tra xem bảng đơn hàng có dữ liệu hay không
            if (orderModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng thêm món vào đơn hàng trước khi chuyển hóa đơn!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Lấy dữ liệu đơn hàng từ OrderPanel
            Object[][] orderData = orderPanel.getOrderData();
            // Lấy mã hóa đơn hiện tại từ InvoicePanel
            String orderId = invoicePanel.getCurrentOrderId();
            // Nếu mã hóa đơn không hợp lệ, tạo mã mới từ OrderListPanel
            if (orderId == null || !orderId.matches("HD\\d{3}")) {
                orderId = orderListPanel.generateNewOrderId();
            }

            // Tạo chuỗi chi tiết đơn hàng từ dữ liệu bảng
            StringBuilder details = new StringBuilder();
            for (Object[] row : orderData) {
                details.append(row[0]).append(", Size: ").append(row[1])
                       .append(", SL: ").append(row[3]).append(", Giá: ").append(row[2]).append("\n");
            }

            // Tạo mảng dữ liệu hóa đơn
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
            String time = sdf.format(new Date()); // Lấy thời gian hiện tại
            double grandTotal = invoicePanel.calculateGrandTotal(); // Tính tổng tiền
            String formattedTotal = invoicePanel.formatCurrency(grandTotal); // Định dạng tiền
            String[] orderDataArray = new String[]{
                orderId, // Mã hóa đơn
                "Trạng thái: Chưa giao", // Trạng thái mặc định
                "Tổng tiền: " + formattedTotal, // Tổng tiền
                "Thời gian: " + time, // Thời gian đặt hàng
                "Hình thức: " + invoicePanel.getCbHinhThuc().getSelectedItem().toString(), // Hình thức thanh toán
                "Số điện thoại: " + (invoicePanel.getTxtMaKH().getText().isEmpty() ? "Không có" : invoicePanel.getTxtMaKH().getText()), // Số điện thoại khách hàng
                "Số bàn: " + currentTable // Số bàn
            };

            // Kiểm tra xem có cần thêm sản phẩm vào đơn hàng hiện tại hay tạo mới
            boolean isAppend = !invoicePanel.isTableReset();
            // Cập nhật hoặc tạo đơn hàng trong InvoicePanel
            invoicePanel.updateOrCreateOrder(orderId, orderDataArray, details.toString(), isAppend);
            // Cập nhật đơn hàng trong OrderListPanel
            orderListPanel.updateOrder(currentTable, orderDataArray, details.toString());

            // Xóa bảng đơn hàng sau khi chuyển
            orderPanel.clearOrderTable();

            // Cập nhật giao diện
            revalidate();
            repaint();
            // Ghi log thành công
            System.out.println("SellPanel - Chuyển hóa đơn thành công: " + orderId + " cho bàn " + currentTable);
            // Hiển thị thông báo thành công
            JOptionPane.showMessageDialog(this, "Chuyển hóa đơn thành công cho " + currentTable, "Thành công", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    /**
     * Phương thức main để chạy thử giao diện SellPanel.
     * @param args Đối số dòng lệnh
     */
    public static void main(String[] args) {
        // Chạy giao diện trong luồng sự kiện của Swing
        SwingUtilities.invokeLater(() -> {
            // Tạo cửa sổ chính
            JFrame frame = new JFrame("Quản lý bán hàng");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Thoát khi đóng cửa sổ
            frame.setSize(1200, 700); // Kích thước cửa sổ
            frame.setLocationRelativeTo(null); // Căn giữa màn hình
            // Thêm SellPanel với mã nhân viên mẫu
            frame.add(new SellPanel("NV001"));
            frame.setVisible(true); // Hiển thị cửa sổ
        });
    }
}