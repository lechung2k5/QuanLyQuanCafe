package UI;

import DAO.*;
import Entity.*;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.List;

public class InvoicePanel extends JPanel implements ActionListener {
    private final String maNV;
    private final OrderListPanel orderListPanel;
    private final OrderPanel orderPanel;
    private final InvoiceForm invoiceForm;
    private final InvoiceButtonPanel buttonPanel;
    private final Map<String, Map<String, String>> tenSPAndSizeToMaSPMap;
    private List<KhuyenMai> danhSachKhuyenMai;
    private String hinhThucThanhToan;
    private final KhachHang_DAO khachHangDAO;

    /**
     * Khởi tạo InvoicePanel với các tham số cần thiết.
     * @param maNV Mã nhân viên
     * @param objects
     * @param orderListPanel Panel danh sách đơn hàng
     * @param orderPanel Panel đơn hàng
     */
    public InvoicePanel(String maNV, Object[][] objects, OrderListPanel orderListPanel, OrderPanel orderPanel) {
        this.maNV = maNV;
        this.orderListPanel = orderListPanel;
        this.orderPanel = orderPanel;
        this.invoiceForm = new InvoiceForm();
        this.buttonPanel = new InvoiceButtonPanel();
        this.tenSPAndSizeToMaSPMap = new HashMap<>();
        this.danhSachKhuyenMai = new ArrayList<>();
        this.hinhThucThanhToan = null;
        this.khachHangDAO = new KhachHang_DAO();

        if (orderListPanel == null) {
            System.err.println("Cảnh báo: orderListPanel là null trong constructor InvoicePanel");
        }

        loadSanPhamData();
        initializeUI();
        loadKhuyenMaiData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Hóa đơn"));
        add(invoiceForm, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setupListeners();

        // Thêm ActionListener cho JComboBox khuyến mãi để cập nhật tổng tiền ngay khi chọn
        invoiceForm.getCbKhuyenMai().addActionListener(e -> {
            updateGrandTotalDisplay();
        });
    }

    private void setupListeners() {
        buttonPanel.getBtnInOrder().addActionListener(this);
        buttonPanel.getBtnInHoaDon().addActionListener(this);
        buttonPanel.getBtnReset().addActionListener(this);
        buttonPanel.getBtnTachHD().addActionListener(this);
        buttonPanel.getBtnThanhToan().addActionListener(this);
        buttonPanel.getBtnEdit().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (o.equals(buttonPanel.getBtnInOrder())) {
            inOrder();
        } else if (o.equals(buttonPanel.getBtnInHoaDon())) {
            inHoaDon();
        } else if (o.equals(buttonPanel.getBtnReset())) {
            resetHoaDon();
        } else if (o.equals(buttonPanel.getBtnTachHD())) {
            tachHoaDon();
        } else if (o.equals(buttonPanel.getBtnThanhToan())) {
            thanhToanHoaDon();
        } else if (o.equals(buttonPanel.getBtnEdit())) {
            updateOrder();
        }
    }

    private void loadSanPhamData() {
        try {
            ProductTable_DAO dao = new ProductTable_DAO();
            Map<String, SanPham> sanPhamMap = dao.getAllSanPhamAsMap();
            for (SanPham sp : sanPhamMap.values()) {
                String tenSP = sp.getTenSP();
                String size = sp.getSize() != null ? sp.getSize() : "";
                String maSP = sp.getMaSP();
                tenSPAndSizeToMaSPMap.computeIfAbsent(tenSP, k -> new HashMap<>()).put(size, maSP);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải danh sách sản phẩm: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void loadKhuyenMaiData() {
        try {
            KhuyenMai_DAO dao = new KhuyenMai_DAO();
            danhSachKhuyenMai = dao.getAllKhuyenMai();
            JComboBox<String> cbKhuyenMai = invoiceForm.getCbKhuyenMai();
            cbKhuyenMai.removeAllItems();
            cbKhuyenMai.addItem("Không có khuyến mãi");
            for (KhuyenMai km : danhSachKhuyenMai) {
                cbKhuyenMai.addItem(km.getTenChuongTrinh());
            }
        } catch (Exception e) {
            JComboBox<String> cbKhuyenMai = invoiceForm.getCbKhuyenMai();
            cbKhuyenMai.removeAllItems();
            cbKhuyenMai.addItem("Không có khuyến mãi");
            JOptionPane.showMessageDialog(this, "Lỗi tải khuyến mãi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private double calculateTotal() {
        double total = 0;
        DefaultTableModel model = invoiceForm.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            try {
                double gia = Double.parseDouble(model.getValueAt(i, 2).toString());
                int soLuong = Integer.parseInt(model.getValueAt(i, 3).toString());
                total += gia * soLuong;
            } catch (NumberFormatException e) {
                System.err.println("Lỗi tính tổng tiền: " + e.getMessage());
            }
        }
        return total;
    }

    double calculateGrandTotal() {
        double thanhTien = calculateTotal();
        JComboBox<String> cbKhuyenMai = invoiceForm.getCbKhuyenMai();
        String selectedPromotion = (String) cbKhuyenMai.getSelectedItem();
        double giamGia = 0;
        if (selectedPromotion != null && !selectedPromotion.equals("Không có khuyến mãi") && danhSachKhuyenMai != null && !danhSachKhuyenMai.isEmpty()) {
            for (KhuyenMai km : danhSachKhuyenMai) {
                if (km.getTenChuongTrinh().equals(selectedPromotion)) {
                    giamGia = km.getGiamGia();
                    break;
                }
            }
        }
        double grandTotal = thanhTien - (thanhTien*(giamGia/100));
        return grandTotal < 0 ? 0 : grandTotal;
    }

    String formatCurrency(double amount) {
        NumberFormat formatter = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        return formatter.format(amount) + " VNĐ";
    }

    private void updateGrandTotalDisplay() {
        invoiceForm.getLblTongCong().setText("Tổng cộng: " + formatCurrency(calculateGrandTotal()));
        invoiceForm.revalidate();
        invoiceForm.repaint();
    }

    private void inOrder() {
        DefaultTableModel model = invoiceForm.getModel();
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Bảng hóa đơn rỗng! Vui lòng thêm món.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String currentTable = invoiceForm.getCurrentTable();
        if (currentTable == null || !Arrays.asList(getTableNames()).contains(currentTable)) {
            JOptionPane.showMessageDialog(this, "Không có bàn nào đang được chọn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String soDienThoai = invoiceForm.getTxtMaKH().getText().trim();
        if (!soDienThoai.isEmpty() && !soDienThoai.matches("\\d{10,11}")) {
            JOptionPane.showMessageDialog(this, "Số điện thoại không hợp lệ! Vui lòng nhập 10-11 chữ số.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String orderId = invoiceForm.getLblMaHoaDon().getText().replace("Mã hóa đơn: ", "");
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        String time = sdf.format(new Date());
        String hinhThuc = invoiceForm.getCbHinhThuc().getSelectedItem() != null ? invoiceForm.getCbHinhThuc().getSelectedItem().toString() : "Tại quán";
        String tongTien = formatCurrency(calculateGrandTotal());
        String trangThai = "Trạng thái: Chưa giao";

        String[] orderData = {
                orderId,
                trangThai,
                "Tổng tiền: " + tongTien,
                "Thời gian: " + time,
                "Hình thức: " + hinhThuc,
                "Số điện thoại: " + (soDienThoai.isEmpty() ? "Không có" : soDienThoai),
                "Số bàn: " + currentTable
        };

        StringBuilder details = new StringBuilder();
        for (int i = 0; i < model.getRowCount(); i++) {
            try {
                String tenMon = model.getValueAt(i, 0).toString();
                String size = model.getValueAt(i, 1).toString();
                String gia = model.getValueAt(i, 2).toString();
                String sl = model.getValueAt(i, 3).toString();
                details.append(tenMon).append(", Size: ").append(size)
                        .append(", SL: ").append(sl).append(", Giá: ").append(gia).append("\n");
            } catch (Exception e) {
                System.err.println("Lỗi tạo chi tiết đơn hàng: " + e.getMessage());
            }
        }

        if (orderListPanel != null) {
            orderListPanel.updateOrder(currentTable, orderData, details.toString());
            JOptionPane.showMessageDialog(this, "Đã cập nhật order cho " + currentTable + "!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Lỗi: OrderListPanel chưa khởi tạo!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }

        if (orderPanel != null) {
            orderPanel.clearOrderTable();
        }

        invoiceForm.setTableReset(false);
        revalidate();
        repaint();
    }

    private void inHoaDon() {
        DefaultTableModel model = invoiceForm.getModel();
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Bảng hóa đơn rỗng! Vui lòng thêm món.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String soDienThoai = invoiceForm.getTxtMaKH().getText().trim();
        if (!soDienThoai.isEmpty() && !soDienThoai.matches("\\d{10,11}")) {
            JOptionPane.showMessageDialog(this, "Số điện thoại không hợp lệ! Vui lòng nhập 10-11 chữ số.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (hinhThucThanhToan == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng thanh toán trước để chọn hình thức thanh toán!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String maKH = null;
        if (!soDienThoai.isEmpty()) {
            maKH = timMaKhachHangTheoSoDienThoai(soDienThoai);
            if (maKH == null) {
                JOptionPane.showMessageDialog(this, "Khách hàng chưa có trong hệ thống, vui lòng tạo khách hàng.", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        String maHD = invoiceForm.getLblMaHoaDon().getText().replace("Mã hóa đơn: ", "");
        double tongTien = calculateGrandTotal();
        Date selectedDate = (Date) invoiceForm.getDateSpinner().getValue();
        LocalDate ngayLap = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        JComboBox<String> cbKhuyenMai = invoiceForm.getCbKhuyenMai();
        int selectedIndex = cbKhuyenMai.getSelectedIndex();
        String maKM = selectedIndex > 0 && !danhSachKhuyenMai.isEmpty() ? danhSachKhuyenMai.get(selectedIndex - 1).getMaKM() : null;
        double vat = 0.10;

        HoaDon hoaDon = new HoaDon(maHD, maNV, maKH, maKM, ngayLap, tongTien, hinhThucThanhToan, vat);

        try {
            HoaDon_DAO hoaDonDao = new HoaDon_DAO();
            boolean hoaDonSaved = hoaDonDao.themHoaDon(hoaDon);
            if (!hoaDonSaved) {
                throw new Exception("Không thể lưu hóa đơn vào bảng HoaDon.");
            }

            for (int i = 0; i < model.getRowCount(); i++) {
                String tenMon = model.getValueAt(i, 0).toString();
                String size = model.getValueAt(i, 1).toString();
                Map<String, String> sizeToMaSP = tenSPAndSizeToMaSPMap.get(tenMon);
                if (sizeToMaSP == null || !sizeToMaSP.containsKey(size)) {
                    System.err.println("Không tìm thấy mã sản phẩm cho món: " + tenMon + " (Size: " + size + ")");
                    continue;
                }
                String maSP = sizeToMaSP.get(size);
                int soLuong = Integer.parseInt(model.getValueAt(i, 3).toString());
                double donGia = Double.parseDouble(model.getValueAt(i, 2).toString());
                double giamGia = 0.0;
                double thanhTien = donGia * soLuong - giamGia;
                ChiTietHoaDon chiTiet = new ChiTietHoaDon(maSP, soLuong, donGia, giamGia, thanhTien);
                ChiTietHoaDon_DAO.themChiTietHoaDon(maHD, chiTiet);
            }

            String soBan = invoiceForm.getCbBan().getSelectedItem().toString();
            String filePath = "E:\\Hoc tap\\HSKJAVA\\BTL_QuanLyCafe\\pdf\\" + maHD + ".pdf";
            createInvoicePDF(filePath, maHD, ngayLap, hinhThucThanhToan, maKH, tongTien, maNV, soBan);

            JOptionPane.showMessageDialog(this, "In hóa đơn thành công: " + formatCurrency(tongTien), "Thành công", JOptionPane.INFORMATION_MESSAGE);

            if (orderListPanel != null) {
                orderListPanel.removeOrder(soBan);
            }
            resetHoaDon();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi lưu hóa đơn: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void thanhToanHoaDon() {
        DefaultTableModel model = invoiceForm.getModel();
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Bảng hóa đơn rỗng! Vui lòng thêm món.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String soDienThoai = invoiceForm.getTxtMaKH().getText().trim();
        if (!soDienThoai.isEmpty() && !soDienThoai.matches("\\d{10,11}")) {
            JOptionPane.showMessageDialog(this, "Số điện thoại không hợp lệ! Vui lòng nhập 10-11 chữ số.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String[] options = {"Tiền mặt", "Chuyển khoản", "Momo", "Quét thẻ"};
        hinhThucThanhToan = (String) JOptionPane.showInputDialog(
                this,
                "Chọn hình thức thanh toán:",
                "Hình thức thanh toán",
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]
        );

        if (hinhThucThanhToan == null) {
            JOptionPane.showMessageDialog(this, "Bạn đã hủy chọn hình thức thanh toán!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this, "Đã chọn hình thức thanh toán: " + hinhThucThanhToan, "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }

    private void createInvoicePDF(String filePath, String maHD, LocalDate ngayLap, String hinhThucThanhToan, String maKH, double tongTien, String maNV, String soBan) throws Exception {
        PdfWriter writer = new PdfWriter(new File(filePath));
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph("JAVA COFFE")
                .setTextAlignment(TextAlignment.CENTER)
                .setBold()
                .setFontSize(16));
        document.add(new Paragraph("H5.1, 12 Nguyen Van Bao, Go Vap, TP.HCM")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(10));
        document.add(new Paragraph("Ma hoa don: " + maHD)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(12));

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd.MM.yyyy");
        String thoiGian = sdf.format(new Date());
        document.add(new Paragraph("Thoi gian: " + thoiGian).setFontSize(10));
        document.add(new Paragraph("Thu ngan: " + maNV).setFontSize(10));
        document.add(new Paragraph("So ban: " + soBan).setFontSize(10));
        document.add(new Paragraph("Khach hang: " + (maKH == null || maKH.isEmpty() ? "Vang lai" : maKH)).setFontSize(10));
        document.add(new Paragraph("\n"));

        float[] columnWidths = {1, 3, 1, 2, 2};
        Table table = new Table(UnitValue.createPercentArray(columnWidths));
        table.addHeaderCell(new Cell().add(new Paragraph("TT").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Ten mon").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("SL").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Đon gia").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Tong tien").setBold()));

        int stt = 1;
        DefaultTableModel model = invoiceForm.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            String tenMon = model.getValueAt(i, 0).toString();
            String size = model.getValueAt(i, 1).toString();
            int soLuong = Integer.parseInt(model.getValueAt(i, 3).toString());
            double donGia = Double.parseDouble(model.getValueAt(i, 2).toString());
            double thanhTien = donGia * soLuong;

            table.addCell(new Cell().add(new Paragraph(String.valueOf(stt++))));
            table.addCell(new Cell().add(new Paragraph(tenMon + "\n(" + size + ")")));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(soLuong))));
            table.addCell(new Cell().add(new Paragraph(formatCurrency(donGia))));
            table.addCell(new Cell().add(new Paragraph(formatCurrency(thanhTien))));
        }
        document.add(table);

        document.add(new Paragraph("Tong so luong: " + model.getRowCount()).setFontSize(10));
        document.add(new Paragraph("Thanh tien: " + formatCurrency(tongTien)).setFontSize(10));
        document.add(new Paragraph("Thanh toan: " + formatCurrency(tongTien)).setFontSize(10));
        

        document.add(new Paragraph("Gia san pham da bao gom VAT 8%.").setFontSize(10));
        document.add(new Paragraph("GUI GTGT chi xuat tai thoi diem").setFontSize(10));
        document.add(new Paragraph("ton. Neu ban can xuat hoa don,").setFontSize(10));
        document.add(new Paragraph("QR code hoac truy cap website").setFontSize(10));
        document.add(new Paragraph("https://javacoffee.com").setFontSize(10));
        document.add(new Paragraph("Moi thac mac xin lien he 02871 087").setFontSize(10));
        document.add(new Paragraph("\n"));


        document.add(new Paragraph("Password WiFi: javacoffeecamon")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(10));

        document.close();
    }

    private void tachHoaDon() {
        JOptionPane.showMessageDialog(this, "Chức năng tách hóa đơn chưa được cài đặt!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void resetHoaDon() {
        String currentTable = invoiceForm.getCurrentTable();
        if (currentTable == null || currentTable.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có bàn nào đang được chọn để reset!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!Arrays.asList(getTableNames()).contains(currentTable)) {
            JOptionPane.showMessageDialog(this, "Tên bàn không hợp lệ: " + currentTable, "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        DefaultTableModel model = invoiceForm.getModel();
        model.setRowCount(0);
        invoiceForm.getOrderDetailsMap().clear();

        if (orderPanel != null) {
            orderPanel.clearOrderTable();
            orderPanel.revalidate();
            orderPanel.repaint();
        }

        invoiceForm.getTxtMaKH().setText("");
        invoiceForm.getCbHinhThuc().setSelectedItem("Tại quán");
        updateCbBan();
        invoiceForm.getCbBan().setSelectedItem(currentTable);
        invoiceForm.getCbKhuyenMai().setSelectedIndex(0);
        invoiceForm.getTxtThanhTien().setText("0");
        invoiceForm.getLblTongCong().setText("Tổng cộng: 0 VNĐ");
        invoiceForm.getDateSpinner().setValue(new Date());
        invoiceForm.getTimeSpinner().setValue(new Date());

        invoiceForm.setCurrentOrderId(generateNewOrderId());

        if (orderListPanel != null) {
            orderListPanel.removeOrder(currentTable);
        }

        invoiceForm.setTableReset(true);
        hinhThucThanhToan = null;
        JOptionPane.showMessageDialog(this, "Đã reset bàn " + currentTable + " thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateOrder() {
        String soDienThoai = invoiceForm.getTxtMaKH().getText().trim();
        if (!soDienThoai.isEmpty() && !soDienThoai.matches("\\d{10,11}")) {
            JOptionPane.showMessageDialog(this, "Số điện thoại không hợp lệ! Vui lòng nhập 10-11 chữ số.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String hinhThuc = invoiceForm.getCbHinhThuc().getSelectedItem().toString();
        String soBan = invoiceForm.getCbBan().getSelectedItem().toString();
        Date time = (Date) invoiceForm.getTimeSpinner().getValue();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        String timeStr = sdf.format(time);
        double tongTien = calculateGrandTotal();

        String[] orderData = {
                invoiceForm.getCurrentOrderId(),
                "Trạng thái: Chưa giao",
                "Tổng tiền: " + formatCurrency(tongTien),
                "Thời gian: " + timeStr,
                "Hình thức: " + hinhThuc,
                "Số điện thoại: " + (soDienThoai.isEmpty() ? "Không có" : soDienThoai),
                "Số bàn: " + soBan
        };

        StringBuilder details = new StringBuilder();
        for (Map.Entry<String, Map<String, Integer>> entry : invoiceForm.getOrderDetailsMap().entrySet()) {
            String tenMon = entry.getKey();
            for (Map.Entry<String, Integer> sizeEntry : entry.getValue().entrySet()) {
                String size = sizeEntry.getKey();
                int sl = sizeEntry.getValue();
                String gia = String.valueOf(invoiceForm.getModel().getValueAt(findRow(tenMon, size), 2));
                details.append(tenMon).append(", Size: ").append(size)
                        .append(", SL: ").append(sl).append(", Giá: ").append(gia).append("\n");
            }
        }

        updateOrCreateOrder(invoiceForm.getCurrentOrderId(), orderData, details.toString(), false);
        JOptionPane.showMessageDialog(this, "Đã cập nhật đơn hàng cho " + soBan, "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }

    private int findRow(String tenMon, String size) {
        DefaultTableModel model = invoiceForm.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            if (model.getValueAt(i, 0).toString().equals(tenMon) && model.getValueAt(i, 1).toString().equals(size)) {
                return i;
            }
        }
        return -1;
    }

    private String[] getTableNames() {
        String[] tableNames = new String[20];
        for (int i = 0; i < 20; i++) {
            tableNames[i] = String.format("Bàn %02d", i + 1);
        }
        return tableNames;
    }

    public void displayOrderDetails(String orderId, String[] orderData, String details) {
        if (orderData == null || orderData.length != 7) {
            JOptionPane.showMessageDialog(this, "Dữ liệu đơn hàng không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String tableFromData = orderData[6].replace("Số bàn: ", "");
        if (tableFromData == null || tableFromData.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không thể xác định bàn từ dữ liệu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        invoiceForm.setCurrentTable(tableFromData);
        invoiceForm.setCurrentOrderId(orderId != null && orderId.matches("HD\\d{3}") ? orderId : generateNewOrderId());

        String soDienThoai = orderData[5].replace("Số điện thoại: ", "");
        invoiceForm.getTxtMaKH().setText(soDienThoai.equals("Không có") ? "" : soDienThoai);
        invoiceForm.getCbHinhThuc().setSelectedItem(orderData[4].replace("Hình thức: ", ""));

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        try {
            String timeStr = orderData[3].replace("Thời gian: ", "").trim();
            if (timeStr.matches("\\d{2}:\\d{2}\\s[AP]M")) {
                Date time = sdf.parse(timeStr);
                invoiceForm.getTimeSpinner().setValue(time);
            } else {
                invoiceForm.getTimeSpinner().setValue(new Date());
            }
        } catch (Exception e) {
            invoiceForm.getTimeSpinner().setValue(new Date());
        }
        invoiceForm.getDateSpinner().setValue(new Date());

        DefaultTableModel model = invoiceForm.getModel();
        model.setRowCount(0);
        invoiceForm.getOrderDetailsMap().clear();

        if (details != null && !details.trim().isEmpty()) {
            String[] detailLines = details.split("\n");
            for (String line : detailLines) {
                if (!line.trim().isEmpty()) {
                    String[] parts = line.split(", ");
                    if (parts.length >= 4) {
                        String tenMon = parts[0];
                        String size = parts[1].replace("Size: ", "");
                        String gia = parts[3].replace("Giá: ", "");
                        String sl = parts[2].replace("SL: ", "");
                        try {
                            double giaValue = Double.parseDouble(gia);
                            int slValue = Integer.parseInt(sl);
                            double total = giaValue * slValue;
                            model.addRow(new Object[]{tenMon, size, giaValue, slValue, total});
                            invoiceForm.getOrderDetailsMap().computeIfAbsent(tenMon, k -> new HashMap<>()).put(size, slValue);
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(this, "Dữ liệu chi tiết đơn hàng không hợp lệ: " + line, "Lỗi", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        }

        if (!invoiceForm.isTableReset() && orderPanel != null) {
            orderPanel.loadOrderForEditing(invoiceForm.getCurrentOrderId(), details);
        } else if (orderPanel != null) {
            orderPanel.clearOrderTable();
        }

        invoiceForm.getTxtThanhTien().setText(String.valueOf(calculateTotal()));
        invoiceForm.getLblTongCong().setText("Tổng cộng: " + formatCurrency(calculateGrandTotal()));
        invoiceForm.getCbKhuyenMai().setSelectedIndex(0);

        invoiceForm.revalidate();
        invoiceForm.repaint();
    }

    public void updateOrCreateOrder(String orderId, String[] orderData, String newDetails, boolean isAppend) {
        if (!invoiceForm.isTableReset() && invoiceForm.getCurrentOrderId() != null && invoiceForm.getCurrentOrderId().matches("HD\\d{3}")) {
            orderId = invoiceForm.getCurrentOrderId();
        } else {
            orderId = generateNewOrderId();
            invoiceForm.getOrderDetailsMap().clear();
        }

        updateOrderInternal(orderId, orderData, newDetails, isAppend);
    }

    private void updateOrderInternal(String orderId, String[] orderData, String newDetails, boolean isAppend) {
        String soDienThoai = orderData[5].replace("Số điện thoại: ", "");
        if (!soDienThoai.equals("Không có") && !soDienThoai.matches("\\d{10,11}")) {
            JOptionPane.showMessageDialog(this, "Số điện thoại không hợp lệ trong dữ liệu đơn hàng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        invoiceForm.setCurrentOrderId(orderId);
        invoiceForm.getTxtMaKH().setText(soDienThoai.equals("Không có") ? "" : soDienThoai);
        invoiceForm.getCbHinhThuc().setSelectedItem(orderData[4].replace("Hình thức: ", ""));
        invoiceForm.setCurrentTable(orderData[6].replace("Số bàn: ", ""));

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        try {
            String timeStr = orderData[3].replace("Thời gian: ", "").trim();
            if (timeStr.matches("\\d{2}:\\d{2}\\s[AP]M")) {
                Date time = sdf.parse(timeStr);
                invoiceForm.getTimeSpinner().setValue(time);
            } else {
                invoiceForm.getTimeSpinner().setValue(new Date());
            }
        } catch (Exception e) {
            invoiceForm.getTimeSpinner().setValue(new Date());
        }
        invoiceForm.getDateSpinner().setValue(new Date());

        DefaultTableModel model = invoiceForm.getModel();
        if (!isAppend) {
            model.setRowCount(0);
            invoiceForm.getOrderDetailsMap().clear();
        }

        if (newDetails != null && !newDetails.trim().isEmpty()) {
            String[] detailLines = newDetails.split("\n");
            for (String line : detailLines) {
                if (!line.trim().isEmpty()) {
                    String[] parts = line.split(", ");
                    if (parts.length >= 4) {
                        String tenMon = parts[0];
                        String size = parts[1].replace("Size: ", "");
                        String gia = parts[3].replace("Giá: ", "");
                        String sl = parts[2].replace("SL: ", "");
                        try {
                            double giaValue = Double.parseDouble(gia);
                            int slValue = Integer.parseInt(sl);
                            double total = giaValue * slValue;

                            Map<String, Integer> sizeMap = invoiceForm.getOrderDetailsMap().computeIfAbsent(tenMon, k -> new HashMap<>());
                            int existingQuantity = sizeMap.getOrDefault(size, 0);
                            sizeMap.put(size, existingQuantity + slValue);

                            boolean updated = false;
                            for (int i = 0; i < model.getRowCount(); i++) {
                                if (model.getValueAt(i, 0).toString().equals(tenMon) && model.getValueAt(i, 1).toString().equals(size)) {
                                    int newQuantity = Integer.parseInt(model.getValueAt(i, 3).toString()) + slValue;
                                    model.setValueAt(newQuantity, i, 3);
                                    model.setValueAt(giaValue * newQuantity, i, 4);
                                    updated = true;
                                    break;
                                }
                            }
                            if (!updated) {
                                model.addRow(new Object[]{tenMon, size, giaValue, slValue, total});
                            }
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(this, "Dữ liệu chi tiết đơn hàng không hợp lệ: " + line, "Lỗi", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        }

        if (orderListPanel != null) {
            String soBan = orderData[6].replace("Số bàn: ", "");
            StringBuilder fullDetails = new StringBuilder();
            for (Map.Entry<String, Map<String, Integer>> entry : invoiceForm.getOrderDetailsMap().entrySet()) {
                String tenMon = entry.getKey();
                for (Map.Entry<String, Integer> sizeEntry : entry.getValue().entrySet()) {
                    String size = sizeEntry.getKey();
                    int sl = sizeEntry.getValue();
                    double giaValue = Double.parseDouble(model.getValueAt(findRow(tenMon, size), 2).toString());
                    fullDetails.append(tenMon).append(", Size: ").append(size)
                            .append(", SL: ").append(sl).append(", Giá: ").append(giaValue).append("\n");
                }
            }
            orderListPanel.updateOrder(soBan, orderData, fullDetails.toString());
        }

        if (!invoiceForm.isTableReset() && orderPanel != null) {
            StringBuilder fullDetails = new StringBuilder();
            for (Map.Entry<String, Map<String, Integer>> entry : invoiceForm.getOrderDetailsMap().entrySet()) {
                String tenMon = entry.getKey();
                for (Map.Entry<String, Integer> sizeEntry : entry.getValue().entrySet()) {
                    String size = sizeEntry.getKey();
                    int sl = sizeEntry.getValue();
                    double giaValue = Double.parseDouble(model.getValueAt(findRow(tenMon, size), 2).toString());
                    fullDetails.append(tenMon).append(", Size: ").append(size)
                            .append(", SL: ").append(sl).append(", Giá: ").append(giaValue).append("\n");
                }
            }
            orderPanel.loadOrderForEditing(orderId, fullDetails.toString());
        }

        invoiceForm.getTxtThanhTien().setText(String.valueOf(calculateTotal()));
        invoiceForm.getLblTongCong().setText("Tổng cộng: " + formatCurrency(calculateGrandTotal()));
        invoiceForm.revalidate();
        invoiceForm.repaint();
    }

    private String generateNewOrderId() {
        if (orderListPanel != null) {
            try {
                return orderListPanel.generateNewOrderId();
            } catch (Exception e) {
                System.err.println("Lỗi tạo mã hóa đơn: " + e.getMessage());
            }
        }
        return String.format("HDXXX");
    }

    private void updateCbBan() {
        JComboBox<String> cbBan = invoiceForm.getCbBan();
        cbBan.removeAllItems();
        for (int i = 1; i <= 20; i++) {
            cbBan.addItem(String.format("Bàn %02d", i));
        }
        if (invoiceForm.getCurrentTable() != null) {
            cbBan.setSelectedItem(invoiceForm.getCurrentTable());
        } else {
            cbBan.setSelectedIndex(0);
        }
    }

    private String timMaKhachHangTheoSoDienThoai(String soDienThoai) {
        try {
            List<KhachHang> danhSachKhachHang = khachHangDAO.getAllKhachHang();
            for (KhachHang khachHang : danhSachKhachHang) {
                if (khachHang.getSoDT() != null && khachHang.getSoDT().equals(soDienThoai)) {
                    return khachHang.getMaKH();
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tìm khách hàng: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return null;
    }

    public String getCurrentTable() {
        return invoiceForm.getCurrentTable();
    }

    public void setCurrentTable(String table) {
        if (!Objects.equals(invoiceForm.getCurrentTable(), table)) {
            invoiceForm.setCurrentTable(table);
            invoiceForm.getModel().setRowCount(0);
            invoiceForm.getOrderDetailsMap().clear();
            invoiceForm.setTableReset(false);
            updateCbBan();
            invoiceForm.getTxtThanhTien().setText("0");
            invoiceForm.getLblTongCong().setText("Tổng cộng: 0 VNĐ");
            invoiceForm.getTxtMaKH().setText("");
            invoiceForm.getCbHinhThuc().setSelectedItem("Tại quán");
            invoiceForm.getDateSpinner().setValue(new Date());
            invoiceForm.getTimeSpinner().setValue(new Date());
            invoiceForm.getCbKhuyenMai().setSelectedIndex(0);
            invoiceForm.setCurrentOrderId(generateNewOrderId());
            revalidate();
            repaint();
        }
    }

    public OrderPanel getOrderPanel() {
        return orderPanel;
    }

    public String getCurrentOrderId() {
        return invoiceForm.getCurrentOrderId();
    }

    public JComboBox<String> getCbHinhThuc() {
        return invoiceForm.getCbHinhThuc();
    }

    public JTextField getTxtMaKH() {
        return invoiceForm.getTxtMaKH();
    }

    public boolean isTableReset() {
        return invoiceForm.isTableReset();
    }
}