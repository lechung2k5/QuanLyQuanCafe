package UI;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class HomePanel extends JPanel {
    private Image backgroundImage;
    private RoundedLabel timeLabel; // Sử dụng JLabel tùy chỉnh
    private int imgWidth = 1600;
    private int imgHeight = 850;

    public HomePanel() {
        // Tải ảnh nền
        ImageIcon icon = new ImageIcon("Resource/Img/poster_50nam.jpg");
        backgroundImage = icon.getImage();

        setLayout(null); // Loại bỏ layout để có thể đặt vị trí tự do

        // Tạo nhãn thời gian bo góc
        timeLabel = new RoundedLabel("00:00 | 00/00/0000");
        timeLabel.setFont(new Font("Roboto", Font.BOLD, 35));
        timeLabel.setForeground(Color.WHITE);
        timeLabel.setBounds(105, 750, 350, 60); // Vị trí và kích thước

        add(timeLabel);
        updateTime(); // Cập nhật thời gian ban đầu
        startClock(); // Bắt đầu cập nhật thời gian liên tục
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            int x = (getWidth() - imgWidth) / 2;
            int y = (getHeight() - imgHeight) / 2;
            g.drawImage(backgroundImage, x, y, imgWidth, imgHeight, this);
        }
    }

    private void updateTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm | dd/MM/yyyy");
        Date date = new Date();
        timeLabel.setText(formatter.format(date));
    }

    private void startClock() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> updateTime());
            }
        }, 0, 1000);
    }

    // Lớp JLabel tùy chỉnh để vẽ nền bo góc
    private static class RoundedLabel extends JLabel {
        private static final int ARC_WIDTH = 30; // Độ bo tròn
        private static final int ARC_HEIGHT = 30;

        public RoundedLabel(String text) {
            super(text, SwingConstants.CENTER);
            setOpaque(false); // Không vẽ nền mặc định
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Vẽ hình bo góc màu nền
            g2.setColor(Color.decode("#B70B0E"));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), ARC_WIDTH, ARC_HEIGHT);

            super.paintComponent(g);
            g2.dispose();
        }

        @Override
        protected void paintBorder(Graphics g) {
            // Vẽ viền bo tròn (nếu cần)
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(Color.WHITE); // Màu viền
            g2.setStroke(new BasicStroke(2)); // Độ dày viền
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, ARC_WIDTH, ARC_HEIGHT);

            g2.dispose();
        }
    }
}
