package UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import Entity.NhanVien;

public class MainApp extends JFrame {
    private CardLayout cardLayout; // Layout để chuyển đổi giữa các panel
    private JPanel mainPanel; // Panel chính chứa các panel con
    private JButton activeButton; // Nút đang được chọn trên Sidebar
    private final NhanVien nhanVien; // Thông tin nhân viên đăng nhập
    private volatile boolean isRefreshing = false; // Cờ ngăn chặn làm mới đồng thời
    private long lastRefreshTime = 0; // Thời gian làm mới lần cuối
    private static final long DEBOUNCE_MS = 1000; // Thời gian chờ tối thiểu giữa các lần làm mới (1000ms)

    // Constructor khởi tạo MainApp với thông tin nhân viên
    public MainApp(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
        initializeUI();
        setupKeyBindings();
    }

    // Khởi tạo giao diện người dùng
    private void initializeUI() {
        setTitle("Quản Lý Quán Cà Phê - Trang Chủ");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Toàn màn hình
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Căn giữa cửa sổ
        setLayout(new BorderLayout());

        loadCustomFont(); // Tải font tùy chỉnh

        // Khởi tạo thanh menu
        JMenuBar menuBar = new JMenuBar();
        JMenu menuTaiKhoan = new JMenu("Tài khoản");
        JMenu menuHeThong = new JMenu("Hệ thống");

        // Thêm mục "Xem thông tin tài khoản" vào menu Tài khoản
        JMenuItem itemXemTaiKhoan = new JMenuItem("Xem thông tin tài khoản");
        itemXemTaiKhoan.addActionListener(e -> new TaiKhoanFrame(nhanVien).setVisible(true));
        menuTaiKhoan.add(itemXemTaiKhoan);

        // Thêm mục "Đăng xuất" vào menu Tài khoản
        JMenuItem itemDangXuat = new JMenuItem("Đăng xuất");
        itemDangXuat.addActionListener(e -> dangXuat());
        menuTaiKhoan.add(itemDangXuat);

        // Thêm mục "Xem phím tắt" vào menu Hệ thống
        JMenuItem itemPhimTat = new JMenuItem("Xem phím tắt");
        itemPhimTat.addActionListener(e -> hienThiPhimTat());
        menuHeThong.add(itemPhimTat);

        // Thêm mục "Giới thiệu" vào menu Hệ thống
        JMenuItem itemGioiThieu = new JMenuItem("Giới thiệu");
        itemGioiThieu.addActionListener(e -> hienThiGioiThieu());
        menuHeThong.add(itemGioiThieu);

        menuBar.add(menuTaiKhoan);
        menuBar.add(menuHeThong);
        setJMenuBar(menuBar);

        // Khởi tạo các thành phần giao diện
        SidebarPanel sidebar = new SidebarPanel(this::showPanel, this::setActiveButton);
        HeaderPanel header = new HeaderPanel(nhanVien);
        initializeMainPanel();

        // Thêm các thành phần vào frame
        add(sidebar, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    // Hiển thị danh sách phím tắt
    private void hienThiPhimTat() {
        String message = """
            Danh sách phím tắt:
            - F5: Làm mới giao diện
            - Ctrl + 1: Trang chủ
            - Ctrl + 2: Bán hàng
            - Ctrl + 3: Hóa đơn
            - Ctrl + 4: Sản phẩm
            - Ctrl + 5: Thống kê
            - Ctrl + 6: Khách hàng
            - Ctrl + 7: Nhân viên
            - Ctrl + 8: Khuyến mãi
            - Ctrl + L: Đăng xuất
            - Ctrl + T: Xem thông tin tài khoản
            """;
        JOptionPane.showMessageDialog(this, message, "Danh sách phím tắt", JOptionPane.INFORMATION_MESSAGE);
    }

    // Hiển thị thông tin giới thiệu
    private void hienThiGioiThieu() {
        String message = """
            TRƯỜNG ĐẠI HỌC CÔNG NGHIỆP THÀNH PHỐ HỒ CHÍ MINH
            KHOA CÔNG NGHỆ THÔNG TIN

            MÔN HỌC: LẬP TRÌNH HƯỚNG SỰ KIỆN VỚI CÔNG NGHỆ JAVA

            ĐỀ TÀI: “QUẢN LÝ BÁN HÀNG TẠI QUÁN COFFEE”

            Giảng viên hướng dẫn: Trần Thị Anh Thi
            Nhóm thực hiện: Nhóm 9
            Lớp: 422000379101 – DHKTPM19ATT

            DANH SÁCH THÀNH VIÊN:
            STT  Họ và tên                  MSSV       Vai trò
            1    Lê Công Chung             23637071   Nhóm trưởng
            2    Lê Viết Cao Sơn           23637311   Thành viên
            3    Lê Hoài Phước Mãi         23633841   Thành viên
            4    Nguyễn Trần Dân Quân      23637841   Thành viên
            """;
        JOptionPane.showMessageDialog(this, message, "Giới thiệu", JOptionPane.INFORMATION_MESSAGE);
    }

    // Xử lý đăng xuất
    private void dangXuat() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc muốn đăng xuất?", 
            "Xác nhận đăng xuất", 
            JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            SwingUtilities.invokeLater(() -> new Login().setVisible(true));
        }
    }

    // Cài đặt các phím tắt
    private void setupKeyBindings() {
        // Lấy InputMap và ActionMap của mainPanel
        InputMap inputMap = mainPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = mainPanel.getActionMap();

        // Phím tắt F5 để làm mới giao diện
        inputMap.put(KeyStroke.getKeyStroke("F5"), "refresh");
        actionMap.put("refresh", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long currentTime = System.currentTimeMillis();
                // Kiểm tra nếu đang làm mới hoặc chưa đủ thời gian chờ
                if (isRefreshing || (currentTime - lastRefreshTime < DEBOUNCE_MS)) {
                    System.out.println("Bỏ qua làm mới: đang xử lý hoặc quá sớm");
                    return; // Bỏ qua nếu nhấn F5 quá nhanh
                }
                isRefreshing = true; // Đặt cờ đang làm mới
                System.out.println("Bắt đầu xử lý phím F5");
                try {
                    refreshMainApp(); // Thực hiện làm mới
                    // Hiển thị thông báo modal để ngăn chồng lấn
                    JOptionPane.showMessageDialog(MainApp.this, "Giao diện đã được làm mới!", 
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    System.err.println("Lỗi khi làm mới: " + ex.getMessage());
                    ex.printStackTrace();
                } finally {
                    lastRefreshTime = currentTime; // Cập nhật thời gian làm mới
                    isRefreshing = false; // Xóa cờ sau khi hoàn thành
                    System.out.println("Hoàn thành làm mới giao diện");
                }
            }
        });

        // Phím tắt điều hướng các panel
        String[] panels = {"home", "sell", "bill", "product", "thongke", "khachhang", "staff", "khuyenmai"};
        String[] panelNames = {"Trang chủ", "Bán hàng", "Hóa đơn", "Sản phẩm", "Thống kê", "Khách hàng", "Nhân viên", "Khuyến mãi"};
        for (int i = 0; i < panels.length; i++) {
            final String panel = panels[i];
            final String panelName = panelNames[i];
            inputMap.put(KeyStroke.getKeyStroke("control " + (i + 1)), "show" + panel);
            actionMap.put("show" + panel, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    showPanel(panel);
                    if (!panel.equals("staff") || (nhanVien != null && "Quản lý".equals(nhanVien.getChucVu()))) {
                        JOptionPane.showMessageDialog(MainApp.this, "Đã chuyển đến: " + panelName, 
                            "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            });
        }

        // Phím tắt đăng xuất
        inputMap.put(KeyStroke.getKeyStroke("control L"), "logout");
        actionMap.put("logout", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dangXuat();
            }
        });

        // Phím tắt xem thông tin tài khoản
        inputMap.put(KeyStroke.getKeyStroke("control T"), "viewAccount");
        actionMap.put("viewAccount", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new TaiKhoanFrame(nhanVien).setVisible(true);
                JOptionPane.showMessageDialog(MainApp.this, "Đã mở thông tin tài khoản!", 
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }

    // Làm mới giao diện
    private void refreshMainApp() {
        SwingUtilities.invokeLater(() -> {
            System.out.println("Bắt đầu làm mới giao diện");
            try {
                // Xóa mainPanel cũ
                if (mainPanel != null) {
                    remove(mainPanel);
                    mainPanel.removeAll(); // Xóa toàn bộ nội dung panel
                    mainPanel = null; // Giải phóng tham chiếu
                }

                // Khởi tạo lại mainPanel
                initializeMainPanel();
                add(mainPanel, BorderLayout.CENTER);
                
                // Gắn lại phím tắt cho mainPanel mới
                setupKeyBindings();
                
                revalidate(); // Cập nhật layout
                repaint(); // Vẽ lại giao diện
                cardLayout.show(mainPanel, "home"); // Hiển thị panel Trang chủ
                System.out.println("Đã làm mới giao diện, chuyển về Trang chủ");
            } catch (Exception ex) {
                System.err.println("Lỗi trong refreshMainApp: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
    }

    // Tải font tùy chỉnh
    private void loadCustomFont() {
        try {
            // Kiểm tra file font tồn tại
            String fontPath = "Resource/Font/Roboto-Regular.ttf";
            File fontFile = new File(fontPath);
            if (!fontFile.exists()) {
                System.err.println("File font không tồn tại: " + fontPath);
                // Sử dụng font mặc định của hệ thống
                UIManager.put("Label.font", new Font("Arial", Font.PLAIN, 18));
                UIManager.put("Button.font", new Font("Arial", Font.PLAIN, 18));
                return;
            }
            // Tải font từ file
            Font robotoFont = Font.createFont(Font.TRUETYPE_FONT, fontFile).deriveFont(Font.PLAIN, 18);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(robotoFont);
            // Áp dụng font cho giao diện
            UIManager.put("Label.font", robotoFont);
            UIManager.put("Button.font", robotoFont);
            System.out.println("Đã tải font thành công: " + fontPath);
        } catch (FontFormatException e) {
            System.err.println("Lỗi định dạng font: " + e.getMessage());
            e.printStackTrace();
            // Sử dụng font mặc định
            UIManager.put("Label.font", new Font("Arial", Font.PLAIN, 18));
            UIManager.put("Button.font", new Font("Arial", Font.PLAIN, 18));
        } catch (IOException e) {
            System.err.println("Lỗi đọc file font: " + e.getMessage());
            e.printStackTrace();
            // Sử dụng font mặc định
            UIManager.put("Label.font", new Font("Arial", Font.PLAIN, 18));
            UIManager.put("Button.font", new Font("Arial", Font.PLAIN, 18));
        }
    }

    // Khởi tạo mainPanel và thêm các panel con
    private void initializeMainPanel() {
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(Color.decode("#F5F5F5"));

        mainPanel.add(new HomePanel(), "home");
        mainPanel.add(new SellPanel(nhanVien != null ? nhanVien.getMaNV() : "Unknown"), "sell");
        mainPanel.add(new BillPanel(), "bill");
        mainPanel.add(new ProductPanel(), "product");
        mainPanel.add(new ThongKePanel(), "thongke");
        mainPanel.add(new KhachHangPanel(), "khachhang");
        mainPanel.add(new StaffPanel(), "staff");
        mainPanel.add(new KhuyenMaiPanel(), "khuyenmai");
    }

    // Chuyển đổi giữa các panel
    private void showPanel(String panelName) {
        if ("staff".equals(panelName)) {
            if (nhanVien == null || !"Quản lý".equals(nhanVien.getChucVu())) {
                JOptionPane.showMessageDialog(this, 
                    "Bạn không có quyền truy cập trang Nhân viên!", 
                    "Lỗi truy cập", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        cardLayout.show(mainPanel, panelName);
    }

    // Cập nhật nút đang được chọn trên Sidebar
    private void setActiveButton(JButton button) {
        this.activeButton = button;
    }

    // Phương thức main để chạy ứng dụng
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Login());
    }
}