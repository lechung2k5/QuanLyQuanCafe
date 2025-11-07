package UI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

//Lớp để hiển thị cửa sổ chi tiết hóa đơn
class ChiTietHoaDonFrame extends JFrame {
 private JLabel lblMaHoaDon, lblNgay, lblGio, lblNhanVien, lblTongTien;
 private JTable bangSanPham;
 private DefaultTableModel modelBangSanPham;
 private JScrollPane thanhCuonBang;
 private JButton btnIn, btnChinhSua, btnHuy;

 public ChiTietHoaDonFrame(String maHoaDon, String ngay, String gio, String nhanVien, String tongTien, String maKhachHang) {
     setTitle("Chi tiết hóa đơn");
     setSize(600, 400);
     setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
     setLocationRelativeTo(null);
     setLayout(new BorderLayout(10, 10));

     // Panel thông tin hóa đơn
     JPanel panelThongTin = new JPanel(new GridLayout(5, 2, 10, 5));
     panelThongTin.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

     lblMaHoaDon = new JLabel("Mã Hóa đơn: " + maHoaDon);
     lblMaHoaDon.setFont(new Font("Roboto", Font.BOLD, 14));
     panelThongTin.add(lblMaHoaDon);

     JLabel lblKhachHang = new JLabel("Mã khách hàng: " + maKhachHang);
     lblKhachHang.setFont(new Font("Roboto", Font.PLAIN, 14));
     panelThongTin.add(lblKhachHang);

     lblNgay = new JLabel("Ngày: " + ngay);
     lblNgay.setFont(new Font("Roboto", Font.PLAIN, 14));
     panelThongTin.add(lblNgay);

     lblGio = new JLabel("Giờ: " + gio);
     lblGio.setFont(new Font("Roboto", Font.PLAIN, 14));
     panelThongTin.add(lblGio);

     lblNhanVien = new JLabel("Nhân viên: " + nhanVien);
     lblNhanVien.setFont(new Font("Roboto", Font.PLAIN, 14));
     panelThongTin.add(lblNhanVien);

     lblTongTien = new JLabel("Tổng tiền: " + tongTien);
     lblTongTien.setFont(new Font("Roboto", Font.PLAIN, 14));
     panelThongTin.add(lblTongTien);

     add(panelThongTin, BorderLayout.NORTH);

     // Tạo bảng danh sách sản phẩm
     String[] tenCot = {"Tên món", "Size", "Giá", "Số lượng", "Thành tiền"};
     modelBangSanPham = new DefaultTableModel(tenCot, 0) {
         @Override
         public boolean isCellEditable(int row, int column) {
             return false; // Không cho phép chỉnh sửa
         }
     };
     bangSanPham = new JTable(modelBangSanPham);
     bangSanPham.setFont(new Font("Roboto", Font.PLAIN, 14));
     bangSanPham.setRowHeight(30);
     bangSanPham.getTableHeader().setFont(new Font("Roboto", Font.BOLD, 14));
     bangSanPham.getTableHeader().setBackground(Color.decode("#E6F0FF"));
     bangSanPham.getTableHeader().setForeground(Color.decode("#424242"));
     thanhCuonBang = new JScrollPane(bangSanPham);
     add(thanhCuonBang, BorderLayout.CENTER);

     // Thêm dữ liệu mẫu vào bảng danh sách sản phẩm
     themDuLieuMau();

     // Tổng cộng
     JPanel panelTongCong = new JPanel(new FlowLayout(FlowLayout.LEFT));
     JLabel lblTongCong = new JLabel("Tổng cộng: " + tongTien);
     lblTongCong.setFont(new Font("Roboto", Font.BOLD, 14));
     panelTongCong.add(lblTongCong);
     add(panelTongCong, BorderLayout.SOUTH);

     // Panel chứa các nút chức năng
     JPanel panelNut = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
     btnIn = new JButton("In hóa đơn");
     btnIn.setBackground(Color.BLUE);
     btnIn.setForeground(Color.WHITE);
     btnIn.setFont(new Font("Roboto", Font.BOLD, 14));
     btnIn.setPreferredSize(new Dimension(120, 40));
     btnIn.addActionListener(e -> JOptionPane.showMessageDialog(null, "In hóa đơn!"));

     btnChinhSua = new JButton("Chỉnh sửa");
     btnChinhSua.setBackground(Color.YELLOW);
     btnChinhSua.setForeground(Color.BLACK);
     btnChinhSua.setFont(new Font("Roboto", Font.BOLD, 14));
     btnChinhSua.setPreferredSize(new Dimension(120, 40));
     btnChinhSua.addActionListener(e -> JOptionPane.showMessageDialog(null, "Chỉnh sửa hóa đơn!"));

     btnHuy = new JButton("Hủy hóa đơn");
     btnHuy.setBackground(Color.RED);
     btnHuy.setForeground(Color.WHITE);
     btnHuy.setFont(new Font("Roboto", Font.BOLD, 14));
     btnHuy.setPreferredSize(new Dimension(120, 40));
     btnHuy.addActionListener(e -> {
         JOptionPane.showMessageDialog(null, "Hủy hóa đơn!");
         dispose(); // Đóng cửa sổ chi tiết hóa đơn
     });

     panelNut.add(btnIn);
     panelNut.add(btnChinhSua);
     panelNut.add(btnHuy);
     add(panelNut, BorderLayout.SOUTH);
 }

 // Phương thức thêm dữ liệu mẫu vào bảng danh sách sản phẩm
 private void themDuLieuMau() {
     modelBangSanPham.addRow(new Object[]{"Cà phê sữa", "L", "50,000 VNĐ", "2", "100,000 VNĐ"});
     modelBangSanPham.addRow(new Object[]{"Trà đào", "M", "50,000 VNĐ", "1", "50,000 VNĐ"});
 }
}
