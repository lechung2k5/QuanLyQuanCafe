package UI;

import javax.swing.*;

import DAO.HoaDon_DAO;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderListPanel extends JPanel {
    // Panel chứa danh sách các nút bàn
    private JPanel orderListPanel;
    // Panel hóa đơn để hiển thị chi tiết đơn hàng
    private InvoicePanel invoicePanel;
    // Đường dẫn tệp lưu trữ đơn hàng
    private static final String ORDER_FILE = "data/orders.txt";
    // Mảng chứa danh sách tên bàn (Bàn 01 đến Bàn 20)
    static final String[] TABLES = new String[20];
    static {
        for (int i = 0; i < 20; i++) {
            TABLES[i] = String.format("Bàn %02d", i + 1);
        }
    }
    // Bản đồ lưu trữ các nút bàn, ánh xạ tên bàn với JButton
    private Map<String, JButton> tableButtons;

    /**
     * Khởi tạo OrderListPanel.
     * @param invoicePanel Panel hóa đơn để liên kết
     */
    public OrderListPanel(InvoicePanel invoicePanel) {
        // Gán InvoicePanel
        this.invoicePanel = invoicePanel;
        // Khởi tạo bản đồ lưu trữ nút bàn
        this.tableButtons = new HashMap<>();
        // Sử dụng BorderLayout cho panel
        setLayout(new BorderLayout());
        // Đặt màu nền trắng
        setBackground(Color.WHITE);
        // Thêm viền với tiêu đề "Danh sách bàn"
        setBorder(BorderFactory.createTitledBorder("Danh sách bàn"));

        // Khởi tạo tệp orders.txt
        initializeOrderFile();

        // Tạo panel chứa danh sách nút bàn với GridLayout
        orderListPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        orderListPanel.setBackground(Color.WHITE);
        orderListPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Tạo thanh cuộn cho danh sách bàn
        JScrollPane scrollPane = new JScrollPane(orderListPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(300, 500));
        // Thêm thanh cuộn vào trung tâm của panel
        add(scrollPane, BorderLayout.CENTER);

        // Khởi tạo các nút bàn
        initializeTableButtons();

        // Cập nhật giao diện
        revalidate();
        repaint();
    }

    /**
     * Khởi tạo các nút bàn và thiết lập trạng thái dựa trên orders.txt.
     */
    private void initializeTableButtons() {
        // Đọc dữ liệu từ orders.txt
        List<String> orderLines = readOrderFile();
        // Bản đồ lưu trạng thái bàn (có đơn hàng hay không)
        Map<String, Boolean> tableStatus = new HashMap<>();
        for (String table : TABLES) {
            tableStatus.put(table, false); // Mặc định bàn trống
        }
        // Kiểm tra trạng thái bàn từ orders.txt
        for (String line : orderLines) {
            if (line.startsWith("#Bàn")) {
                String tableName = line.substring(1);
                if (Arrays.asList(TABLES).contains(tableName)) {
                    tableStatus.put(tableName, true); // Bàn có đơn hàng
                }
            }
        }

        // Tạo nút cho từng bàn
        for (String table : TABLES) {
            JButton tableButton = new JButton(table);
            tableButton.setFont(new Font("Arial", Font.BOLD, 14));
            tableButton.setForeground(Color.WHITE);
            tableButton.setFocusPainted(false);
            tableButton.setPreferredSize(new Dimension(150, 50));
            tableButton.setOpaque(true);

            // Đặt màu dựa trên trạng thái bàn
            boolean isOccupied = tableStatus.getOrDefault(table, false);
            tableButton.setBackground(isOccupied ? Color.decode("#FF9800") : Color.decode("#4CAF50"));
            Color hoverColor = isOccupied ? Color.decode("#FFB300") : Color.decode("#66BB6A");

            // Thêm hiệu ứng hover khi di chuột
            tableButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    tableButton.setBackground(hoverColor);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    tableButton.setBackground(isOccupied ? Color.decode("#FF9800") : Color.decode("#4CAF50"));
                }
            });

            // Thêm sự kiện nhấp chuột để hiển thị đơn hàng
            tableButton.addActionListener(e -> displayTableOrder(table));
            // Lưu nút bàn vào bản đồ
            tableButtons.put(table, tableButton);
            // Thêm nút vào panel
            orderListPanel.add(tableButton);
        }
    }

    /**
     * Hiển thị chi tiết đơn hàng của một bàn.
     * @param tableName Tên bàn
     */
    private void displayTableOrder(String tableName) {
        // Kiểm tra tên bàn hợp lệ
        if (!Arrays.asList(TABLES).contains(tableName)) {
            JOptionPane.showMessageDialog(this, "Tên bàn không hợp lệ: " + tableName, "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Ghi log hiển thị bàn
        System.out.println("OrderListPanel - Displaying table: " + tableName);

        // Kiểm tra InvoicePanel có được khởi tạo không
        if (invoicePanel != null) {
            invoicePanel.setCurrentTable(tableName);
            System.out.println("OrderListPanel - Set current table: " + tableName);
        } else {
            JOptionPane.showMessageDialog(this, "Lỗi: InvoicePanel chưa được khởi tạo!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Đọc dữ liệu từ orders.txt
        List<String> orderLines = readOrderFile();
        String[] orderData = null;
        List<String> detailsList = new ArrayList<>();
        String orderId = null;

        // Tìm đơn hàng của bàn
        for (int i = orderLines.size() - 1; i >= 0; i--) {
            String line = orderLines.get(i);
            if (line.equals("#" + tableName)) {
                orderData = new String[7];
                i++;
                int j = 0;
                // Đọc dữ liệu đơn hàng
                while (j < 7 && i < orderLines.size() && !orderLines.get(i).startsWith("#")) {
                    String dataLine = orderLines.get(i);
                    if (dataLine.startsWith("orderId=")) {
                        orderData[0] = dataLine.replace("orderId=", "");
                        orderId = dataLine.replace("orderId=", "");
                        j++;
                    } else if (dataLine.startsWith("trangThai=")) {
                        orderData[1] = "Trạng thái: " + dataLine.replace("trangThai=", "");
                        j++;
                    } else if (dataLine.startsWith("tongTien=")) {
                        orderData[2] = "Tổng tiền: " + dataLine.replace("tongTien=", "");
                        j++;
                    } else if (dataLine.startsWith("thoiGian=")) {
                        orderData[3] = "Thời gian: " + dataLine.replace("thoiGian=", "");
                        j++;
                    } else if (dataLine.startsWith("hinhThuc=")) {
                        orderData[4] = "Hình thức: " + dataLine.replace("hinhThuc=", "");
                        j++;
                    } else if (dataLine.startsWith("soDienThoai=")) {
                        orderData[5] = "Số điện thoại: " + dataLine.replace("soDienThoai=", "");
                        j++;
                    } else if (dataLine.startsWith("soBan=")) {
                        orderData[6] = "Số bàn: " + dataLine.replace("soBan=", "");
                        j++;
                    }
                    i++;
                }
                // Đọc chi tiết đơn hàng
                while (i < orderLines.size() && !orderLines.get(i).startsWith("#")) {
                    String detailLine = orderLines.get(i);
                    if (detailLine.startsWith("details=")) {
                        detailsList.add(detailLine.replace("details=", ""));
                    }
                    i++;
                }
                break;
            }
        }

        // Kết hợp các chi tiết đơn hàng thành chuỗi
        String details = String.join("\n", detailsList);

        // Nếu không có đơn hàng hoặc bàn đã được reset
        if (orderData == null || orderData[0] == null || invoicePanel.isTableReset()) {
            if (!invoicePanel.isTableReset() && invoicePanel.getCurrentOrderId() != null) {
                // Sử dụng mã hóa đơn hiện tại nếu bàn chưa reset
                orderId = invoicePanel.getCurrentOrderId();
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
                String time = sdf.format(new Date());
                orderData = new String[]{
                    orderId,
                    "Trạng thái: Chưa giao",
                    "Tổng tiền: 0 VNĐ",
                    "Thời gian: " + time,
                    "Hình thức: Tại quán",
                    "Số điện thoại: Không có",
                    "Số bàn: " + tableName
                };
                details = "";
                System.out.println("OrderListPanel - Using existing orderId " + orderId + " for table " + tableName);
            } else {
                // Tạo đơn hàng mới
                orderId = generateNewOrderId();
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
                String time = sdf.format(new Date());
                orderData = new String[]{
                    orderId,
                    "Trạng thái: Chưa giao",
                    "Tổng tiền: 0 VNĐ",
                    "Thời gian: " + time,
                    "Hình thức: Tại quán",
                    "Số điện thoại: Không có",
                    "Số bàn: " + tableName
                };
                details = "";
                updateOrderInFile(tableName, orderData, details);
                System.out.println("OrderListPanel - Created new order for " + tableName + ": " + Arrays.toString(orderData));
            }
        } else {
            // Ghi log đơn hàng đã tìm thấy
            System.out.println("OrderListPanel - Found order for " + tableName + ": " + Arrays.toString(orderData));
            System.out.println("OrderListPanel - Details: " + details);
        }

        // Kiểm tra dữ liệu đơn hàng có đầy đủ không
        for (int i = 0; i < orderData.length; i++) {
            if (orderData[i] == null) {
                JOptionPane.showMessageDialog(this, "Dữ liệu đơn hàng không đầy đủ cho bàn: " + tableName, "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // Hiển thị chi tiết đơn hàng trên InvoicePanel
        if (invoicePanel != null) {
            invoicePanel.displayOrderDetails(orderId, orderData, details);
        } else {
            JOptionPane.showMessageDialog(this, "Lỗi: InvoicePanel chưa được khởi tạo!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Tạo mã hóa đơn mới dựa trên cơ sở dữ liệu.
     * @return Mã hóa đơn mới (HDxxx)
     */
    public String generateNewOrderId() {
        // Khởi tạo DAO để truy vấn cơ sở dữ liệu
        HoaDon_DAO hoaDonDao = new HoaDon_DAO();
        // Lấy mã hóa đơn lớn nhất từ cơ sở dữ liệu
        String latestId = hoaDonDao.getLatestInvoiceId();

        int idNum = 0;
        if (latestId != null && latestId.matches("HD\\d{3}")) {
            try {
                // Chuyển đổi mã hóa đơn thành số
                idNum = Integer.parseInt(latestId.replace("HD", ""));
            } catch (NumberFormatException e) {
                System.err.println("Lỗi parse mã hóa đơn: " + latestId);
            }
        }

        // Tạo mã hóa đơn mới
        return String.format("HD%03d", idNum + 1);
    }

    /**
     * Cập nhật đơn hàng cho một bàn.
     * @param tableName Tên bàn
     * @param orderData Dữ liệu đơn hàng
     * @param details Chi tiết đơn hàng
     */
    public void updateOrder(String tableName, String[] orderData, String details) {
        // Kiểm tra tên bàn hợp lệ
        if (!Arrays.asList(TABLES).contains(tableName)) {
            JOptionPane.showMessageDialog(this, "Tên bàn không hợp lệ: " + tableName, "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Cập nhật đơn hàng trong tệp
        updateOrderInFile(tableName, orderData, details);
        // Cập nhật màu nút bàn thành vàng/cam (có đơn hàng)
        JButton tableButton = tableButtons.get(tableName);
        if (tableButton != null) {
            tableButton.setBackground(Color.decode("#FF9800"));
            // Thêm hiệu ứng hover
            tableButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    tableButton.setBackground(Color.decode("#FFB300"));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    tableButton.setBackground(Color.decode("#FF9800"));
                }
            });
        }
        // Ghi log cập nhật đơn hàng
        System.out.println("OrderListPanel - Updated order for table: " + tableName);
        System.out.println("OrderListPanel - Updated orderData: " + Arrays.toString(orderData));
        System.out.println("OrderListPanel - Updated details: " + details);
        // Cập nhật giao diện
        revalidate();
        repaint();
    }

    /**
     * Xóa đơn hàng của một bàn.
     * @param tableName Tên bàn
     */
    public void removeOrder(String tableName) {
        // Kiểm tra tên bàn hợp lệ
        if (!Arrays.asList(TABLES).contains(tableName)) {
            JOptionPane.showMessageDialog(this, "Tên bàn không hợp lệ: " + tableName, "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Đọc dữ liệu từ orders.txt
        List<String> orderLines = readOrderFile();
        List<String> newLines = new ArrayList<>();
        boolean skip = false;

        // Lọc bỏ dữ liệu của bàn được xóa
        for (String line : orderLines) {
            if (line.equals("#" + tableName)) {
                skip = true;
            } else if (skip && line.startsWith("#Bàn")) {
                skip = false;
                newLines.add(line);
            } else if (!skip) {
                newLines.add(line);
            }
        }

        // Ghi lại tệp với dữ liệu mới
        writeOrderFile(newLines);
        // Reset màu nút bàn về xanh lá (bàn trống)
        JButton tableButton = tableButtons.get(tableName);
        if (tableButton != null) {
            tableButton.setBackground(Color.decode("#4CAF50"));
            // Thêm hiệu ứng hover
            tableButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    tableButton.setBackground(Color.decode("#66BB6A"));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    tableButton.setBackground(Color.decode("#4CAF50"));
                }
            });
        }
        // Ghi log xóa đơn hàng
        System.out.println("OrderListPanel - Removed order for table: " + tableName);
    }

    /**
     * Đọc dữ liệu từ tệp orders.txt.
     * @return Danh sách các dòng trong tệp
     */
    private List<String> readOrderFile() {
        List<String> lines = new ArrayList<>();
        File file = new File(ORDER_FILE);
        // Kiểm tra tệp có tồn tại không
        if (!file.exists()) {
            System.out.println("OrderListPanel - File " + ORDER_FILE + " không tồn tại, trả về danh sách rỗng");
            return lines;
        }

        // Đọc từng dòng từ tệp
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            System.err.println("Lỗi đọc file: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Lỗi đọc file đơn hàng: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        return lines;
    }

    /**
     * Ghi dữ liệu vào tệp orders.txt.
     * @param lines Danh sách các dòng để ghi
     */
    private void writeOrderFile(List<String> lines) {
        File file = new File(ORDER_FILE);
        // Ghi từng dòng vào tệp
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
            System.out.println("OrderListPanel - Successfully wrote to file: " + ORDER_FILE);
        } catch (IOException e) {
            System.err.println("Lỗi ghi file: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Lỗi ghi file đơn hàng: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Cập nhật đơn hàng trong tệp orders.txt.
     * @param tableName Tên bàn
     * @param orderData Dữ liệu đơn hàng
     * @param details Chi tiết đơn hàng
     */
    private void updateOrderInFile(String tableName, String[] orderData, String details) {
        // Ghi log bắt đầu cập nhật tệp
        System.out.println("OrderListPanel - Cập nhật tệp với dữ liệu cho bàn: " + tableName);

        // Kiểm tra tên bàn hợp lệ
        if (!Arrays.asList(TABLES).contains(tableName)) {
            System.err.println("Tên bàn không hợp lệ: " + tableName);
            return;
        }

        // Kiểm tra mã hóa đơn hợp lệ
        String orderId = orderData[0];
        if (orderId == null || !orderId.matches("HD\\d{3}")) {
            System.err.println("Mã hóa đơn không hợp lệ: " + orderId);
            return;
        }

        // Đọc dữ liệu hiện tại từ orders.txt
        List<String> orderLines = readOrderFile();
        List<String> newLines = new ArrayList<>();
        boolean foundTable = false;

        // Xóa dữ liệu cũ của bàn
        for (int i = 0; i < orderLines.size(); i++) {
            String line = orderLines.get(i);
            if (line.equals("#" + tableName)) {
                foundTable = true;
                while (i < orderLines.size() && !orderLines.get(i).startsWith("#Bàn")) {
                    i++;
                }
                if (i < orderLines.size()) {
                    newLines.add(orderLines.get(i));
                }
            } else {
                newLines.add(line);
            }
        }

        // Thêm dữ liệu mới cho bàn
        newLines.add("#" + tableName);
        newLines.add("orderId=" + orderData[0]);
        newLines.add("trangThai=" + orderData[1].replace("Trạng thái: ", ""));
        newLines.add("tongTien=" + orderData[2].replace("Tổng tiền: ", ""));
        newLines.add("thoiGian=" + orderData[3].replace("Thời gian: ", ""));
        newLines.add("hinhThuc=" + orderData[4].replace("Hình thức: ", ""));
        newLines.add("soDienThoai=" + orderData[5].replace("Số điện thoại: ", ""));
        newLines.add("soBan=" + orderData[6].replace("Số bàn: ", ""));
        // Thêm chi tiết đơn hàng nếu có
        if (details != null && !details.trim().isEmpty()) {
            for (String detailLine : details.split("\n")) {
                if (!detailLine.trim().isEmpty()) {
                    newLines.add("details=" + detailLine);
                }
            }
        }

        // Ghi log dữ liệu mới
        System.out.println("OrderListPanel - Dòng mới để ghi cho bàn " + tableName + ": " + newLines);
        // Ghi dữ liệu vào tệp
        writeOrderFile(newLines);
        System.out.println("OrderListPanel - Tệp đã được cập nhật cho bàn: " + tableName);
    }

    /**
     * Khởi tạo tệp orders.txt nếu chưa tồn tại.
     */
    private void initializeOrderFile() {
        File file = new File(ORDER_FILE);
        try {
            // Tạo thư mục cha nếu chưa tồn tại
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            // Tạo tệp mới hoặc xóa nội dung cũ
            file.createNewFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write("");
            }
            System.out.println("OrderListPanel - Đã reset file orders.txt tại: " + ORDER_FILE);
        } catch (IOException e) {
            System.err.println("Lỗi reset file orders.txt: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Lỗi reset file đơn hàng: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}