package UI;

import DAO.ChiTietHoaDon_DAO;
import DAO.HoaDon_DAO;
import DAO.ProductTable_DAO;
import Entity.ChiTietHoaDon;
import Entity.HoaDon;
import Entity.SanPham;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class ChiTietHoaDonDialog extends JDialog {
    public ChiTietHoaDonDialog(JFrame parent, String maHD) {
        super(parent, "Chi tiết hóa đơn", true);
        setSize(800, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        // Lấy thông tin hóa đơn
        HoaDon hoaDon = HoaDon_DAO.getHoaDonByMaHD(maHD);
        List<ChiTietHoaDon> chiTietList = ChiTietHoaDon_DAO.getChiTietHoaDon(maHD);

        // Kiểm tra nếu không tìm thấy hóa đơn
        if (hoaDon == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy hóa đơn với mã: " + maHD);
            dispose();
            return;
        }

        // Panel thông tin hóa đơn
        JPanel infoPanel = new JPanel(new GridLayout(0, 1));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 0, 20));

        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        infoPanel.add(new JLabel("Mã Hóa đơn: " + maHD));
        infoPanel.add(new JLabel("Ngày: " + hoaDon.getNgayLap().format(dateFormat) +
                "        Mã khách hàng: " + (hoaDon.getMaKH() != null ? hoaDon.getMaKH() : "Khách lẻ")));
        infoPanel.add(new JLabel("Nhân viên: " + hoaDon.getMaNV()));
        infoPanel.add(new JLabel("Tổng tiền: " + String.format("%,.0fđ", hoaDon.getTongTien())));

        add(infoPanel, BorderLayout.NORTH);

        // Bảng chi tiết sản phẩm
        DefaultTableModel model = createTableModel(chiTietList);
        JTable table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Tổng tiền bên dưới bảng
        JPanel footerPanel = new JPanel(new BorderLayout());
        JLabel lblTong = new JLabel("Tổng cộng: " + String.format("%,.0fđ", hoaDon.getTongTien()));
        lblTong.setFont(new Font("Arial", Font.BOLD, 16));
        lblTong.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));
        footerPanel.add(lblTong, BorderLayout.WEST);

        // Nút hành động
        JPanel buttonPanel = new JPanel();
        JButton btnIn = new JButton("In hóa đơn");
        btnIn.setBackground(Color.BLUE);
        btnIn.setForeground(Color.WHITE);
        btnIn.addActionListener(e -> {
            try {
                String filePath = "E:\\Hoc tap\\HSKJAVA\\BTL_QuanLyCafe\\pdf" + maHD + ".pdf";
               
                exportInvoiceToPDF(filePath, hoaDon, chiTietList);
                JOptionPane.showMessageDialog(this, "Đã xuất hóa đơn tại: " + filePath, "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi khi in hóa đơn: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });


        buttonPanel.add(btnIn);
        footerPanel.add(buttonPanel, BorderLayout.EAST);

        add(footerPanel, BorderLayout.SOUTH);
    }

    private DefaultTableModel createTableModel(List<ChiTietHoaDon> chiTietList) {
        String[] columns = {"Tên món", "Size", "Giá", "Số lượng", "Thành tiền"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        ProductTable_DAO sanPhamDAO = new ProductTable_DAO();
        Map<String, SanPham> sanPhamMap = sanPhamDAO.getAllSanPhamAsMap();

        for (ChiTietHoaDon ct : chiTietList) {
            SanPham sp = sanPhamMap.get(ct.getMaSP());
            String tenSP = (sp != null) ? sp.getTenSP() : "Không tìm thấy";
            String size = (sp != null) ? sp.getSize() : "N/A";

            model.addRow(new Object[]{
                tenSP,
                size,
                String.format("%,.0fđ", ct.getDonGia()),
                ct.getSoLuong(),
                String.format("%,.0fđ", ct.getThanhTien())
            });
        }

        return model;
    }
    private void exportInvoiceToPDF(String filePath, HoaDon hoaDon, List<ChiTietHoaDon> chiTietList) throws Exception {
        com.itextpdf.kernel.pdf.PdfWriter writer = new com.itextpdf.kernel.pdf.PdfWriter(new java.io.File(filePath));
        com.itextpdf.kernel.pdf.PdfDocument pdf = new com.itextpdf.kernel.pdf.PdfDocument(writer);
        com.itextpdf.layout.Document document = new com.itextpdf.layout.Document(pdf);

        document.add(new com.itextpdf.layout.element.Paragraph("JAVA COFFE")
                .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                .setBold()
                .setFontSize(16));
        document.add(new com.itextpdf.layout.element.Paragraph("Mã hóa đơn: " + hoaDon.getMaHD())
                .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));
        document.add(new com.itextpdf.layout.element.Paragraph("Ngày: " + hoaDon.getNgayLap().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
        document.add(new com.itextpdf.layout.element.Paragraph("Nhân viên: " + hoaDon.getMaNV()));
        document.add(new com.itextpdf.layout.element.Paragraph("Khách hàng: " + (hoaDon.getMaKH() != null ? hoaDon.getMaKH() : "Khách lẻ")));
        document.add(new com.itextpdf.layout.element.Paragraph("\n"));

        float[] columnWidths = {3, 1, 2, 1, 2};
        com.itextpdf.layout.element.Table table = new com.itextpdf.layout.element.Table(columnWidths);
        table.addHeaderCell("Tên món");
        table.addHeaderCell("Size");
        table.addHeaderCell("Giá");
        table.addHeaderCell("SL");
        table.addHeaderCell("Thành tiền");

        ProductTable_DAO spDAO = new ProductTable_DAO();
        Map<String, SanPham> spMap = spDAO.getAllSanPhamAsMap();

        for (ChiTietHoaDon ct : chiTietList) {
            SanPham sp = spMap.get(ct.getMaSP());
            String ten = (sp != null) ? sp.getTenSP() : "Không tìm thấy";
            String size = (sp != null) ? sp.getSize() : "N/A";

            table.addCell(ten);
            table.addCell(size);
            table.addCell(String.format("%,.0fđ", ct.getDonGia()));
            table.addCell(String.valueOf(ct.getSoLuong()));
            table.addCell(String.format("%,.0fđ", ct.getThanhTien()));
        }

        document.add(table);
        document.add(new com.itextpdf.layout.element.Paragraph("\nTổng tiền: " + String.format("%,.0fđ", hoaDon.getTongTien()))
                .setBold()
                .setFontSize(12));

        document.close();
    }

}
