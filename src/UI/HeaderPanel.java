package UI;

import Entity.NhanVien;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class HeaderPanel extends JPanel {
    private final NhanVien nhanVien;

    // Khởi tạo header với thông tin nhân viên
    public HeaderPanel(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
        initializeHeader();
    }

    // Thiết lập giao diện cho header, bao gồm logo và thông tin người dùng
    private void initializeHeader() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(getWidth(), 80));
        setLayout(new BorderLayout());

        // Logo panel
        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.setBackground(Color.WHITE);
        logoPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 2, 20));

        ImageIcon logoIcon = new ImageIcon("Resource/Img/logo.png");
        Image logoImage = logoIcon.getImage();
        double ratio = (double) logoImage.getWidth(null) / logoImage.getHeight(null);
        int scaledWidth = 180;
        int scaledHeight = (int) (scaledWidth / ratio);

        BufferedImage bufferedImage = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.drawImage(logoImage, 0, 0, scaledWidth, scaledHeight, null);
        g2d.dispose();

        JLabel logoLabel = new JLabel(new ImageIcon(bufferedImage));
        logoPanel.add(logoLabel, BorderLayout.CENTER);
        logoPanel.setPreferredSize(new Dimension(220, 80));
        add(logoPanel, BorderLayout.WEST);

        // User info panel
        JPanel userInfoPanel = new JPanel();
        userInfoPanel.setLayout(new BoxLayout(userInfoPanel, BoxLayout.Y_AXIS));
        userInfoPanel.setBackground(Color.WHITE);
        userInfoPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        if (nhanVien != null) {
            JLabel userNameLabel = new JLabel(nhanVien.getHoTenNV());
            userNameLabel.setFont(new Font("Arial", Font.BOLD, 14));
            userNameLabel.setForeground(Color.decode("#424242"));

            JLabel userRoleLabel = new JLabel(nhanVien.getChucVu());
            userRoleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            userRoleLabel.setForeground(Color.decode("#757575"));

            userInfoPanel.add(userNameLabel);
            userInfoPanel.add(userRoleLabel);
        } else {
            JLabel errorLabel = new JLabel("Lỗi: Không tìm thấy thông tin nhân viên");
            errorLabel.setFont(new Font("Arial", Font.BOLD, 14));
            errorLabel.setForeground(Color.RED);
            userInfoPanel.add(errorLabel);
        }

        // User avatar
        ImageIcon avatarIcon = new ImageIcon("Resource/Icon/avatar.png");
        Image avatarImage = avatarIcon.getImage().getScaledInstance(36, 36, Image.SCALE_SMOOTH);
        JLabel userAvatar = new JLabel(new ImageIcon(avatarImage)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setClip(new java.awt.geom.Ellipse2D.Float(0, 0, getWidth(), getHeight()));
                super.paintComponent(g2);
                g2.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(36, 36);
            }

            @Override
            public boolean isOpaque() {
                return false;
            }
        };
        userAvatar.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 0));

        // User panel
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        userPanel.setBackground(Color.WHITE);
        userPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 20));
        userPanel.add(userInfoPanel);
        userPanel.add(userAvatar);
        add(userPanel, BorderLayout.EAST);
    }
}