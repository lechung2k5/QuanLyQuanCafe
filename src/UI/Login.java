package UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import DAO.NhanVien_DAO;
import Entity.NhanVien;

public class Login extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton showPasswordButton;
    private JButton loginButton;
    private boolean isPasswordVisible = false;

    public Login() {
        setTitle("Đăng Nhập - Quản Lý Quán Cà Phê");
        setSize(900, 540);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        try {
            String fontPath = "Resource/Font/Roboto-Regular.ttf";
            File fontFile = new File(fontPath);
            Font robotoFont = Font.createFont(Font.TRUETYPE_FONT, fontFile).deriveFont(Font.PLAIN, 16);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(robotoFont);

            UIManager.put("Label.font", robotoFont);
            UIManager.put("Button.font", robotoFont);
            UIManager.put("TextField.font", robotoFont);
            UIManager.put("PasswordField.font", robotoFont);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setPreferredSize(new Dimension(450, 500));

        ImageIcon bannerIcon = new ImageIcon("Resource/Img/poster_login.png");
        Image bannerImage = bannerIcon.getImage();
        int bannerWidth = 450;
        int bannerHeight = 500;
        BufferedImage bufferedImage = new BufferedImage(bannerWidth, bannerHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.drawImage(bannerImage, 0, 0, bannerWidth, bannerHeight, null);
        g2d.dispose();

        JLabel bannerLabel = new JLabel(new ImageIcon(bufferedImage), SwingConstants.CENTER);
        leftPanel.add(bannerLabel, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        JLabel titleLabel = new JLabel("CHÚC BẠN CÓ MỘT NGÀY LÀM VIỆC TỐT LÀNH!");
        titleLabel.setFont(new Font("Roboto", Font.BOLD, 14));
        titleLabel.setForeground(Color.decode("#424242"));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("ĐĂNG NHẬP ĐỂ TIẾP TỤC");
        subtitleLabel.setFont(new Font("Roboto", Font.PLAIN, 14));
        subtitleLabel.setForeground(Color.decode("#757575"));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel usernamePanel = new JPanel(new BorderLayout());
        usernamePanel.setMaximumSize(new Dimension(300, 40));
        usernamePanel.setOpaque(false);

        usernameField = new JTextField("");
        usernameField.setForeground(Color.BLACK);
        usernameField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 5));

        ImageIcon avatarIcon = new ImageIcon("Resource/Icon/admin.png");
        Image avatarImage = avatarIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        JLabel avatarLabel = new JLabel(new ImageIcon(avatarImage));
        avatarLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        usernamePanel.add(avatarLabel, BorderLayout.WEST);
        usernamePanel.add(usernameField, BorderLayout.CENTER);
        usernamePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.decode("#E0E0E0")),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        JPanel passwordPanel = new JPanel(new BorderLayout());
        passwordPanel.setMaximumSize(new Dimension(300, 40));
        passwordPanel.setOpaque(false);

        passwordField = new JPasswordField("");
        passwordField.setForeground(Color.BLACK);
        passwordField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 5));

        ImageIcon lockIcon = new ImageIcon("Resource/Icon/pass.png");
        Image lockImage = lockIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        JLabel lockLabel = new JLabel(new ImageIcon(lockImage));
        lockLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        showPasswordButton = new JButton("Show");
        showPasswordButton.setFont(new Font("Roboto", Font.PLAIN, 12));
        showPasswordButton.setForeground(Color.decode("#757575"));
        showPasswordButton.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        showPasswordButton.setFocusPainted(false);
        showPasswordButton.setContentAreaFilled(false);
        showPasswordButton.addActionListener(e -> {
            if (isPasswordVisible) {
                passwordField.setEchoChar('•');
                showPasswordButton.setText("Show");
                isPasswordVisible = false;
            } else {
                passwordField.setEchoChar((char) 0);
                showPasswordButton.setText("Hide");
                isPasswordVisible = true;
            }
        });

        passwordPanel.add(lockLabel, BorderLayout.WEST);
        passwordPanel.add(passwordField, BorderLayout.CENTER);
        passwordPanel.add(showPasswordButton, BorderLayout.EAST);
        passwordPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.decode("#E0E0E0")),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        loginButton = new JButton("Đăng nhập");
        loginButton.setBackground(Color.decode("#0BB783"));
        loginButton.setForeground(Color.WHITE);
        loginButton.setMaximumSize(new Dimension(300, 40));
        loginButton.setFocusPainted(false);
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.addActionListener(e -> performLogin());

        JLabel forgotPasswordLabel = new JLabel("Quên mật khẩu?");
        forgotPasswordLabel.setFont(new Font("Roboto", Font.PLAIN, 12));
        forgotPasswordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        forgotPasswordLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotPasswordLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                ImageIcon dialogIcon = new ImageIcon("Resource/Icon/login.png");
                Image dialogImage = dialogIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
                JLabel dialogLabel = new JLabel(new ImageIcon(dialogImage), SwingConstants.CENTER);
                dialogLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

                JLabel messageLabel = new JLabel("Vui lòng liên hệ quản lý để cấp lại mật khẩu!", SwingConstants.CENTER);
                messageLabel.setFont(new Font("Roboto", Font.BOLD, 14));
                messageLabel.setForeground(Color.BLACK);
                messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

                JButton yesButton = new JButton("Yes");
                yesButton.setBackground(Color.decode("#0BB783"));
                yesButton.setForeground(Color.WHITE);
                yesButton.setFocusPainted(false);
                yesButton.setPreferredSize(new Dimension(100, 30));
                yesButton.setAlignmentX(Component.CENTER_ALIGNMENT);

                JPanel dialogPanel = new JPanel();
                dialogPanel.setBackground(Color.WHITE);
                dialogPanel.setLayout(new BoxLayout(dialogPanel, BoxLayout.Y_AXIS));
                dialogPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

                JPanel iconPanel = new JPanel();
                iconPanel.setBackground(Color.WHITE);
                iconPanel.setLayout(new GridBagLayout());
                iconPanel.add(dialogLabel);

                dialogPanel.add(Box.createVerticalGlue());
                dialogPanel.add(iconPanel);
                dialogPanel.add(Box.createVerticalStrut(10));
                dialogPanel.add(messageLabel);
                dialogPanel.add(Box.createVerticalStrut(20));
                dialogPanel.add(yesButton);
                dialogPanel.add(Box.createVerticalGlue());

                UIManager.put("OptionPane.background", Color.WHITE);
                UIManager.put("Panel.background", Color.WHITE);

                Object[] options = {yesButton};
                JOptionPane.showOptionDialog(
                    Login.this,
                    dialogPanel,
                    "Thông báo",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    options,
                    options[0]
                );
            }
        });

        rightPanel.add(Box.createVerticalGlue());
        rightPanel.add(titleLabel);
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(subtitleLabel);
        rightPanel.add(Box.createVerticalStrut(30));
        rightPanel.add(usernamePanel);
        rightPanel.add(Box.createVerticalStrut(15));
        rightPanel.add(passwordPanel);
        rightPanel.add(Box.createVerticalStrut(20));
        rightPanel.add(loginButton);
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(forgotPasswordLabel);
        rightPanel.add(Box.createVerticalGlue());

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);

        setupKeyBindings();

        setVisible(true);
    }

    private void setupKeyBindings() {
        // Thêm phím tắt Enter cho usernameField
        usernameField.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("ENTER"), "login");
        usernameField.getActionMap().put("login", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });

        // Thêm phím tắt Enter cho passwordField
        passwordField.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("ENTER"), "login");
        passwordField.getActionMap().put("login", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });
    }

    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên đăng nhập và mật khẩu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        NhanVien_DAO nhanVienDAO = new NhanVien_DAO();
        NhanVien nhanVien = nhanVienDAO.authenticate(username, password);
        if (nhanVien != null) {
            dispose();
            new MainApp(nhanVien).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Sai tên đăng nhập hoặc mật khẩu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Login());
    }
}