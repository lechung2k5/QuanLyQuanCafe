package UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SidebarPanel extends JPanel {
    private List<JButton> sidebarButtons = new ArrayList<>();
    private JButton activeButton;
    private final Consumer<String> panelSwitcher;
    private final Consumer<JButton> activeButtonSetter;

    // Khởi tạo thanh sidebar với các hàm callback để chuyển panel và cập nhật nút active
    public SidebarPanel(Consumer<String> panelSwitcher, Consumer<JButton> activeButtonSetter) {
        this.panelSwitcher = panelSwitcher;
        this.activeButtonSetter = activeButtonSetter;
        initializeSidebar();
    }

    // Thiết lập giao diện và các nút cho thanh sidebar
    private void initializeSidebar() {
        setLayout(new GridLayout(13, 1));
        setBackground(Color.decode("#FFFFFF"));
        setPreferredSize(new Dimension(245, getHeight()));

        // Create sidebar buttons
        JButton btnHome = createSidebarButton("Trang chủ", "Resource/Icon/home.png", "Resource/Icon/home_white.png", true, "home");
        btnHome.setBackground(Color.decode("#0BB783"));
        btnHome.setForeground(Color.WHITE);
        activeButton = btnHome;
        activeButtonSetter.accept(btnHome);

        addButton(btnHome);
        addButton(createSidebarButton("Bán hàng", "Resource/Icon/cart.png", "Resource/Icon/cart_white.png", false, "sell"));
        addButton(createSidebarButton("Thống kê", "Resource/Icon/dash.png", "Resource/Icon/dash_white.png", false, "thongke"));
        addButton(createSidebarButton("Sản phẩm (Menu)", "Resource/Icon/product.png", "Resource/Icon/product_white.png", false, "product"));
        addButton(createSidebarButton("Hóa đơn", "Resource/Icon/bill.png", "Resource/Icon/bill_white.png", false, "bill"));
        addButton(createSidebarButton("Nhân viên", "Resource/Icon/staff.png", "Resource/Icon/staff_white.png", false, "staff"));
        addButton(createSidebarButton("Khách hàng", "Resource/Icon/customer.png", "Resource/Icon/customer_white.png", false, "khachhang"));
        addButton(createSidebarButton("Khuyến mãi", "Resource/Icon/sale.png", "Resource/Icon/sale_white.png", false, "khuyenmai"));
        addButton(createSidebarButton("Đăng xuất", "Resource/Icon/Sign Out Icon.png", "Resource/Icon/Sign Out Icon_white.png", false, "exit"));
    }

    // Thêm nút vào danh sách và giao diện sidebar
    private void addButton(JButton button) {
        sidebarButtons.add(button);
        add(button);
    }

    // Tạo nút cho sidebar với các thuộc tính và sự kiện
    private JButton createSidebarButton(String text, String defaultIconPath, String whiteIconPath, boolean isFirstButton, String panelName) {
        JButton button = new JButton(text);
        File defaultIconFile = new File(defaultIconPath);
        ImageIcon defaultIcon = null;
        if (defaultIconFile.exists()) {
            defaultIcon = new ImageIcon(defaultIconFile.getAbsolutePath());
            Image scaledDefaultIcon = defaultIcon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
            button.setIcon(new ImageIcon(scaledDefaultIcon));
        } else {
            System.err.println("Không tìm thấy icon mặc định: " + defaultIconPath);
        }

        Font defaultFont = new Font("Roboto", Font.PLAIN, 18);
        button.setFont(defaultFont);
        button.putClientProperty("defaultFont", defaultFont);
        button.putClientProperty("defaultIcon", defaultIcon);

        button.setForeground(Color.decode("#737791"));
        button.setBackground(Color.decode("#FFFFFF"));
        button.setBorder(BorderFactory.createEmptyBorder(isFirstButton ? 5 : 10, 20, 10, 20));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFocusPainted(false);
        button.setIconTextGap(10);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                if (button != activeButton) {
                    button.setBackground(Color.decode("#E6F0FF"));
                }
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                if (button != activeButton) {
                    button.setBackground(Color.decode("#FFFFFF"));
                }
            }
        });

        button.addActionListener(e -> {
            if (activeButton != null && activeButton != button) {
                activeButton.setBackground(Color.decode("#FFFFFF"));
                activeButton.setForeground(Color.decode("#737791"));
                ImageIcon oldDefaultIcon = (ImageIcon) activeButton.getClientProperty("defaultIcon");
                Font oldDefaultFont = (Font) activeButton.getClientProperty("defaultFont");
                if (oldDefaultIcon != null) {
                    Image scaledOldDefaultIcon = oldDefaultIcon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
                    activeButton.setIcon(new ImageIcon(scaledOldDefaultIcon));
                }
                if (oldDefaultFont != null) {
                    activeButton.setFont(oldDefaultFont);
                }
            }

            button.setBackground(Color.decode("#0BB783"));
            button.setForeground(Color.WHITE);
            button.setFont(new Font("Roboto", Font.BOLD, 18));
            File whiteIconFile = new File(whiteIconPath);
            if (whiteIconFile.exists()) {
                ImageIcon whiteIcon = new ImageIcon(whiteIconFile.getAbsolutePath());
                Image scaledWhiteIcon = whiteIcon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
                button.setIcon(new ImageIcon(scaledWhiteIcon));
            } else {
                System.err.println("Không tìm thấy icon trắng: " + whiteIconPath);
            }

            activeButton = button;
            activeButtonSetter.accept(button);

            if (panelName.equals("exit")) {
                int confirm = JOptionPane.showConfirmDialog(
                        null,
                        "Bạn có chắc chắn muốn đăng xuất không?",
                        "Xác nhận thoát",
                        JOptionPane.YES_NO_OPTION
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            } else {
                panelSwitcher.accept(panelName);
            }
        });

        return button;
    }
}