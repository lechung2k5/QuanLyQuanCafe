package UI;

import DAO.HoaDon_DAO;
import DAO.NhanVien_DAO;
import Entity.HoaDon;
import Entity.NhanVien;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class LichSuHoaDonFrame extends JFrame {
    private JTable bangHoaDon;
    private DefaultTableModel modelBangHoaDon;
    private JScrollPane thanhCuonBang;
    private NhanVien nhanVien;
    private HoaDon_DAO hoaDonDAO = new HoaDon_DAO(); // DAO

    public LichSuHoaDonFrame(String maKhachHang) {
        setTitle("Lịch sử hóa đơn - " + maKhachHang);
        setSize(700, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Khởi tạo bảng
        String[] tenCot = {"Mã hóa đơn", "Ngày", "Nhân viên", "Tổng tiền", "Thanh toán", "Xem chi tiết"};
        modelBangHoaDon = new DefaultTableModel(tenCot, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }
        };

        bangHoaDon = new JTable(modelBangHoaDon);
        bangHoaDon.setFont(new Font("Roboto", Font.PLAIN, 14));
        bangHoaDon.setRowHeight(30);
        bangHoaDon.getTableHeader().setFont(new Font("Roboto", Font.BOLD, 14));
        bangHoaDon.getTableHeader().setBackground(Color.decode("#E6F0FF"));
        bangHoaDon.getTableHeader().setForeground(Color.decode("#424242"));

        bangHoaDon.getColumnModel().getColumn(5).setCellRenderer(new KhachHangPanel.ButtonRenderer());
        bangHoaDon.getColumnModel().getColumn(5).setCellEditor(new ChiTietEditor(new JCheckBox()));

        thanhCuonBang = new JScrollPane(bangHoaDon);
        add(thanhCuonBang, BorderLayout.CENTER);

        taiDuLieuHoaDon(maKhachHang); // Load dữ liệu thật
    }

    // Tải dữ liệu từ DB
    private void taiDuLieuHoaDon(String maKhachHang) {
        List<HoaDon> danhSachHoaDon = hoaDonDAO.getHoaDonByMaKhachHang(maKhachHang);
        modelBangHoaDon.setRowCount(0); // Clear existing rows

        if (danhSachHoaDon.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy hóa đơn nào cho khách hàng này.");
            return;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        for (HoaDon hoaDon : danhSachHoaDon) {
            if (hoaDon.getMaKH() == null) {
                System.out.println("Cảnh báo: Hóa đơn " + hoaDon.getMaHD() + " có maKH null.");
                continue; // Skip invoices with null maKH
            }

            // Lấy mã nhân viên trực tiếp từ HoaDon
            String maNhanVien = hoaDon.getMaNV() != null ? hoaDon.getMaNV() : "Không có";

            // Nếu bạn muốn lấy thêm thông tin từ NhanVienDAO (ví dụ: kiểm tra nhân viên có tồn tại hay không)
            /*
            String maNhanVien = "Không có";
            if (hoaDon.getMaNV() != null) {
                NhanVien nhanVien = nhanVienDAO.getNhanVienByMaNV(hoaDon.getMaNV());
                maNhanVien = nhanVien != null ? nhanVien.getMaNV() : "Không tìm thấy";
            }
            */

            Object[] row = {
                hoaDon.getMaHD(),
                dateFormat.format(java.sql.Date.valueOf(hoaDon.getNgayLap())),
                maNhanVien, // Thêm mã nhân viên vào đây
                currencyFormat.format(hoaDon.getTongTien()),
                hoaDon.getHinhThucThanhToan(),
            };
            modelBangHoaDon.addRow(row);
        }
    }


    // Editor cho nút "Xem chi tiết"
    class ChiTietEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;
        private int row;

        public ChiTietEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.setBackground(Color.decode("#0BB783"));
            button.setForeground(Color.WHITE);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.row = row;
            label = (value == null) ? "Xem" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                String maHoaDon = modelBangHoaDon.getValueAt(row, 0).toString();
                new ChiTietHoaDonDialog(null, maHoaDon).setVisible(true);
            }
            isPushed = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }

        @Override
        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }
}
