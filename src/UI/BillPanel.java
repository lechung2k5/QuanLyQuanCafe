package UI;

import DAO.HoaDon_DAO;
import Entity.HoaDon;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BillPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtTimKiem;
    private HoaDon_DAO hoaDonDao; // Khởi tạo với tham chiếu BillPanel

    public BillPanel() {
        setLayout(new BorderLayout());

        // Khởi tạo HoaDon_DAO với tham chiếu đến BillPanel
        hoaDonDao = new HoaDon_DAO(this);

        // Panel tiêu đề và tìm kiếm
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Tiêu đề
        JLabel lblTitle = new JLabel("Danh sách hóa đơn", SwingConstants.LEFT);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        topPanel.add(lblTitle, BorderLayout.WEST);

        // Panel tìm kiếm
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel lblTimKiem = new JLabel("Tìm kiếm");
        txtTimKiem = new JTextField(15);
        JButton btnTimKiem = new JButton("Tìm kiếm");
        btnTimKiem.setBackground(Color.GREEN);
        btnTimKiem.setForeground(Color.WHITE);
        searchPanel.add(lblTimKiem);
        searchPanel.add(txtTimKiem);
        searchPanel.add(btnTimKiem);
        topPanel.add(searchPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Bảng hóa đơn
        String[] columns = {
            "Mã hóa đơn", "Ngày", "Hình thức thanh toán", "Mã nhân viên", "Tổng tiền", "Xem chi tiết"
        };

        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Chỉ cột "Xem chi tiết" có thể click
            }
        };

        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Nút "Xem chi tiết"
        table.getColumn("Xem chi tiết").setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
            JButton button = new JButton("Xem");
            button.setForeground(Color.WHITE);
            button.setBackground(new Color(0, 153, 102));
            return button;
        });

        table.getColumn("Xem chi tiết").setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            final JButton button = new JButton("Xem");

            {
                button.setForeground(Color.WHITE);
                button.setBackground(new Color(0, 153, 102));
                button.addActionListener(e -> {
                    int selectedRow = table.getSelectedRow();
                    if (selectedRow == -1) {
                        JOptionPane.showMessageDialog(null, "Vui lòng chọn một hóa đơn để xem chi tiết!");
                        return;
                    }
                    String maHD = table.getValueAt(selectedRow, 0).toString();
                    new ChiTietHoaDonDialog(null, maHD).setVisible(true);
                });
            }

            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                return button;
            }

            @Override
            public Object getCellEditorValue() {
                return "Xem";
            }
        });

        // Sự kiện tìm kiếm
        btnTimKiem.addActionListener(e -> {
            String maHD = txtTimKiem.getText().trim();
            if (maHD.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập mã hóa đơn để tìm kiếm!");
                return;
            }
            loadDataToTable(maHD);
        });

        // Tải dữ liệu ban đầu
        loadDataToTable();
    }

    private void loadDataToTable() {
        List<HoaDon> danhSachHoaDon = hoaDonDao.getAllHoaDon();
        loadTableData(danhSachHoaDon);
    }

    private void loadDataToTable(String maHD) {
        List<HoaDon> danhSachHoaDon = hoaDonDao.timKiemHoaDonTheoMa(maHD);
        loadTableData(danhSachHoaDon);
    }

    private void loadTableData(List<HoaDon> danhSachHoaDon) {
        model.setRowCount(0); // Xóa dữ liệu cũ
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (HoaDon hd : danhSachHoaDon) {
            String formattedDate = hd.getNgayLap() != null ? hd.getNgayLap().format(formatter) : "";

            model.addRow(new Object[]{
                hd.getMaHD(),
                formattedDate,
                hd.getHinhThucThanhToan(),
                hd.getMaNV(),
                String.format("%,.0f VND", hd.getTongTien()),
                "Xem"
            });
        }
    }

    public void refreshHoaDonList() {
        model.setRowCount(0); // Xóa dữ liệu cũ trong bảng
        List<HoaDon> danhSachHoaDon = hoaDonDao.getAllHoaDon();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (HoaDon hd : danhSachHoaDon) {
            String formattedDate = hd.getNgayLap() != null ? hd.getNgayLap().format(formatter) : "";
            model.addRow(new Object[]{
                hd.getMaHD(),
                formattedDate,
                hd.getHinhThucThanhToan(),
                hd.getMaNV(),
                String.format("%,.0f VND", hd.getTongTien()),
                "Xem"
            });
        }
        table.revalidate();
        table.repaint();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Danh sách hóa đơn");
        frame.setSize(900, 600);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new BillPanel());
        frame.setVisible(true);
    }
}