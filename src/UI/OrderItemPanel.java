package UI;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;
import java.util.Locale;

public class OrderItemPanel extends JPanel {
    private String[] orderData;
    private String details;
    private InvoicePanel invoicePanel;

    // Khởi tạo panel hiển thị thông tin đơn hàng
    public OrderItemPanel(String[] orderData, String details, InvoicePanel invoicePanel) {
        this.orderData = orderData;
        this.details = details != null ? details : "";
        this.invoicePanel = invoicePanel;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        System.out.println("OrderItemPanel - Created: " + orderData[0] + ", Details: " + this.details);
    }

    // Lấy dữ liệu đơn hàng
    public String[] getOrderData() {
        return orderData;
    }

    // Lấy chi tiết đơn hàng
    public String getDetails() {
        return details;
    }

    // Cập nhật thông tin đơn hàng và tính lại tổng tiền
    public void updateOrder(String[] newOrderData, String newDetails) {
        this.orderData = newOrderData;
        this.details = newDetails;
        double total = calculateTotalFromDetails();
        this.orderData[2] = "Tổng tiền: " + formatCurrency(total);
        System.out.println("OrderItemPanel - Updated order: " + orderData[0] + ", New Details: " + this.details);
    }

    // Tính tổng tiền từ chi tiết đơn hàng
    private double calculateTotalFromDetails() {
        double total = 0;
        if (details != null && !details.trim().isEmpty()) {
            String[] detailLines = details.split("\n");
            for (String line : detailLines) {
                if (!line.trim().isEmpty()) {
                    String[] parts = line.split(", ");
                    if (parts.length >= 4) {
                        String gia = parts[3].replace("Giá: ", "");
                        String sl = parts[2].replace("SL: ", "");
                        try {
                            double giaValue = Double.parseDouble(gia);
                            int slValue = Integer.parseInt(sl);
                            total += giaValue * slValue;
                        } catch (NumberFormatException ex) {
                            System.err.println("Lỗi parse giá hoặc số lượng trong details: " + ex.getMessage());
                        }
                    }
                }
            }
        }
        return total;
    }

    // Định dạng số tiền theo định dạng tiền Việt Nam
    private String formatCurrency(double amount) {
        NumberFormat formatter = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        return formatter.format(amount) + "đ";
    }
}