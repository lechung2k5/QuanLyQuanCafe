package UI;

import DAO.ChiTietHoaDon_DAO;
import DAO.ThongKe_DAO;
import Entity.HoaDon;
import Entity.ChiTietHoaDon;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class ThongKePanel extends JPanel {
    private JTable ordersTable;
    private JTable topItemsTable;
    private JComboBox<String> ordersFilter;
    private JComboBox<String> topItemsFilter;
    private JComboBox<String> revenueFilter;
    private JPanel revenueBarChartPanel;
    private JPanel orderLineChartPanel;
    private ThongKe_DAO thongKeDAO;

    public ThongKePanel() {
        setLayout(new BorderLayout());
        setBackground(Color.decode("#F7FAFC"));

        thongKeDAO = new ThongKe_DAO();

        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        mainSplitPane.setDividerSize(0);
        mainSplitPane.setResizeWeight(0.5);
        mainSplitPane.setBackground(Color.decode("#F7FAFC"));

        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setBackground(Color.decode("#F7FAFC"));

        JSplitPane topSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        topSplitPane.setDividerSize(0);
        topSplitPane.setResizeWeight(0.5);
        topSplitPane.setBackground(Color.decode("#F7FAFC"));

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(Color.decode("#F7FAFC"));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 10));

        JPanel topItemsHeader = new JPanel(new BorderLayout());
        topItemsHeader.setBackground(Color.WHITE);
        topItemsHeader.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel topItemsTitle = new JLabel("Món bán chạy");
        topItemsTitle.setFont(new Font("Roboto", Font.BOLD, 18));
        topItemsTitle.setForeground(Color.decode("#1A3C34"));
        topItemsHeader.add(topItemsTitle, BorderLayout.WEST);

        topItemsFilter = new JComboBox<>(new String[]{"Theo ngày", "Theo tháng", "Theo năm"});
        topItemsFilter.setFont(new Font("Roboto", Font.PLAIN, 14));
        topItemsFilter.setPreferredSize(new Dimension(150, 30));
        topItemsHeader.add(topItemsFilter, BorderLayout.EAST);

        leftPanel.add(topItemsHeader, BorderLayout.NORTH);

        JPanel topItemsPanel = new JPanel(new BorderLayout());
        topItemsPanel.setBackground(Color.WHITE);
        topItemsPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        String[] itemsColumnNames = {"#", "Tên món", "Số lượng đã bán", "Doanh thu"};
        Object[][] itemsData = {};
        topItemsTable = new JTable(new DefaultTableModel(itemsData, itemsColumnNames));
        topItemsTable.setFont(new Font("Roboto", Font.PLAIN, 14));
        topItemsTable.setRowHeight(40);
        JScrollPane topItemsScrollPane = new JScrollPane(topItemsTable);
        topItemsPanel.add(topItemsScrollPane, BorderLayout.CENTER);

        leftPanel.add(topItemsPanel, BorderLayout.CENTER);
        topSplitPane.setLeftComponent(leftPanel);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(Color.decode("#F7FAFC"));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 20));

        JPanel ordersHeader = new JPanel(new BorderLayout());
        ordersHeader.setBackground(Color.WHITE);
        ordersHeader.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel ordersTitle = new JLabel("Số lượng hóa đơn");
        ordersTitle.setFont(new Font("Roboto", Font.BOLD, 18));
        ordersTitle.setForeground(Color.decode("#1A3C34"));
        ordersHeader.add(ordersTitle, BorderLayout.WEST);

        ordersFilter = new JComboBox<>(new String[]{"Theo ngày", "Theo tháng", "Theo năm"});
        ordersFilter.setFont(new Font("Roboto", Font.PLAIN, 14));
        ordersFilter.setPreferredSize(new Dimension(150, 30));
        ordersHeader.add(ordersFilter, BorderLayout.EAST);

        rightPanel.add(ordersHeader, BorderLayout.NORTH);

        JPanel ordersPanel = new JPanel(new BorderLayout());
        ordersPanel.setBackground(Color.WHITE);
        ordersPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        String[] columnNames = {"Mã hóa đơn", "Thời gian bán", "Tổng tiền", ""};
        Object[][] data = {};
        ordersTable = new JTable(new DefaultTableModel(data, columnNames));
        ordersTable.setFont(new Font("Roboto", Font.PLAIN, 14));
        ordersTable.setRowHeight(40);

        ordersTable.getColumnModel().getColumn(3).setCellRenderer(new TableCellRenderer() {
            private final JButton button = new JButton("Xem chi tiết");

            {
                button.setOpaque(true);
                button.setFont(new Font("Roboto", Font.PLAIN, 14));
                button.setForeground(Color.WHITE);
                button.setBackground(Color.decode("#34D399"));
                button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            }

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                return button;
            }
        });

        ordersTable.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            private final JButton button = new JButton();
            private String label;
            private boolean isPushed;

            {
                button.setOpaque(true);
                button.setText("Xem chi tiết");
                button.setFont(new Font("Roboto", Font.PLAIN, 14));
                button.setForeground(Color.WHITE);
                button.setBackground(Color.decode("#34D399"));
                button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                button.addActionListener(e -> fireEditingStopped());
            }

            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                label = (String) table.getValueAt(row, 0);
                button.setText("Xem chi tiết");
                isPushed = true;
                return button;
            }

            @Override
            public Object getCellEditorValue() {
                if (isPushed) {
                    new ChiTietHoaDonDialog((JFrame) SwingUtilities.getWindowAncestor(ThongKePanel.this), label).setVisible(true);
                }
                isPushed = false;
                return "Xem chi tiết";
            }

            @Override
            public boolean stopCellEditing() {
                isPushed = false;
                return super.stopCellEditing();
            }
        });

        JScrollPane ordersScrollPane = new JScrollPane(ordersTable);
        ordersPanel.add(ordersScrollPane, BorderLayout.CENTER);

        rightPanel.add(ordersPanel, BorderLayout.CENTER);
        topSplitPane.setRightComponent(rightPanel);

        topSection.add(topSplitPane, BorderLayout.CENTER);
        mainSplitPane.setTopComponent(topSection);

        JPanel bottomSection = new JPanel(new BorderLayout());
        bottomSection.setBackground(Color.decode("#F7FAFC"));
        bottomSection.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

        JPanel revenueHeader = new JPanel(new BorderLayout());
        revenueHeader.setBackground(Color.WHITE);
        revenueHeader.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel revenueTitle = new JLabel("Doanh thu");
        revenueTitle.setFont(new Font("Roboto", Font.BOLD, 18));
        revenueTitle.setForeground(Color.decode("#1A3C34"));
        revenueHeader.add(revenueTitle, BorderLayout.WEST);

        revenueFilter = new JComboBox<>(new String[]{"Theo ngày", "Theo tháng", "Theo năm"});
        revenueFilter.setFont(new Font("Roboto", Font.PLAIN, 14));
        revenueFilter.setPreferredSize(new Dimension(150, 30));
        revenueHeader.add(revenueFilter, BorderLayout.EAST);

        bottomSection.add(revenueHeader, BorderLayout.NORTH);

        JSplitPane chartSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        chartSplitPane.setDividerSize(0);
        chartSplitPane.setResizeWeight(0.5);
        chartSplitPane.setBackground(Color.decode("#F7FAFC"));

        revenueBarChartPanel = new JPanel(new BorderLayout()) {
            private String tooltipText = null;
            private Point tooltipPoint = null;

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int width = getWidth();
                int height = getHeight();
                int margin = 30;
                int barWidth = 30;

                g2d.setColor(Color.BLACK);
                g2d.drawLine(margin, margin, margin, height - margin);
                g2d.drawLine(margin, height - margin, width - margin, height - margin);

                Map<String, Map<String, Double>> revenueData = getRevenueByTimeSlot();
                List<String> timeSlots = new ArrayList<>(revenueData.keySet());
                if (timeSlots.isEmpty()) {
                    g2d.setColor(Color.BLACK);
                    g2d.drawString("Không có dữ liệu để hiển thị", width / 2 - 50, height / 2);
                    return;
                }

                double maxValue = revenueData.values().stream()
                        .flatMap(slot -> slot.values().stream())
                        .max(Double::compare).orElse(1.0);

                int barSpacing = timeSlots.size() > 1 ? (width - 2 * margin) / (timeSlots.size()) : width - 2 * margin;

                for (int i = 0; i < timeSlots.size(); i++) {
                    String slot = timeSlots.get(i);
                    Map<String, Double> slotData = revenueData.get(slot);
                    double guestRevenue = slotData.getOrDefault("Khách lẻ", 0.0);
                    double memberRevenue = slotData.getOrDefault("Khách thành viên", 0.0);

                    int x = margin + i * barSpacing;

                    int guestBarHeight = maxValue == 0 ? 0 : (int) (guestRevenue / maxValue * (height - 2 * margin));
                    g2d.setColor(Color.decode("#60A5FA"));
                    g2d.fillRect(x, height - margin - guestBarHeight, barWidth, guestBarHeight);

                    int memberBarHeight = maxValue == 0 ? 0 : (int) (memberRevenue / maxValue * (height - 2 * margin));
                    g2d.setColor(Color.decode("#34D399"));
                    g2d.fillRect(x + barWidth, height - margin - memberBarHeight, barWidth, memberBarHeight);

                    g2d.setColor(Color.BLACK);
                    g2d.drawString(slot, x, height - margin + 15);
                }

                g2d.setColor(Color.decode("#60A5FA"));
                g2d.fillRect(width - 100, margin, 10, 10);
                g2d.setColor(Color.BLACK);
                g2d.drawString("Khách lẻ", width - 80, margin + 10);

                g2d.setColor(Color.decode("#34D399"));
                g2d.fillRect(width - 100, margin + 20, 10, 10);
                g2d.setColor(Color.BLACK);
                g2d.drawString("Khách thành viên", width - 80, margin + 30);

                if (tooltipText != null && tooltipPoint != null) {
                    g2d.setFont(new Font("Roboto", Font.PLAIN, 12));
                    FontMetrics fm = g2d.getFontMetrics();
                    int textWidth = fm.stringWidth(tooltipText);
                    int textHeight = fm.getHeight();
                    int padding = 5;
                    int boxWidth = textWidth + 2 * padding;
                    int boxHeight = textHeight + 2 * padding;

                    int x = tooltipPoint.x + 10;
                    int y = tooltipPoint.y - boxHeight - 10;
                    if (x + boxWidth > width) x = tooltipPoint.x - boxWidth - 10;
                    if (y < 0) y = tooltipPoint.y + 10;

                    g2d.setColor(new Color(0, 0, 0, 200));
                    g2d.fillRect(x, y, boxWidth, boxHeight);
                    g2d.setColor(Color.WHITE);
                    g2d.drawString(tooltipText, x + padding, y + textHeight);
                }
            }

            {
                addMouseMotionListener(new MouseMotionAdapter() {
                    @Override
                    public void mouseMoved(MouseEvent e) {
                        int width = getWidth();
                        int height = getHeight();
                        int margin = 30;
                        int barWidth = 30;

                        Map<String, Map<String, Double>> revenueData = getRevenueByTimeSlot();
                        List<String> timeSlots = new ArrayList<>(revenueData.keySet());
                        if (timeSlots.isEmpty()) {
                            tooltipText = null;
                            repaint();
                            return;
                        }

                        int barSpacing = timeSlots.size() > 1 ? (width - 2 * margin) / (timeSlots.size()) : width - 2 * margin;

                        tooltipText = null;
                        tooltipPoint = null;

                        for (int i = 0; i < timeSlots.size(); i++) {
                            String slot = timeSlots.get(i);
                            Map<String, Double> slotData = revenueData.get(slot);
                            double guestRevenue = slotData.getOrDefault("Khách lẻ", 0.0);
                            double memberRevenue = slotData.getOrDefault("Khách thành viên", 0.0);

                            int x = margin + i * barSpacing;
                            double maxValue = revenueData.values().stream()
                                    .flatMap(slotMap -> slotMap.values().stream())
                                    .max(Double::compare).orElse(1.0);
                            int guestBarHeight = maxValue == 0 ? 0 : (int) (guestRevenue / maxValue * (height - 2 * margin));
                            int memberBarHeight = maxValue == 0 ? 0 : (int) (memberRevenue / maxValue * (height - 2 * margin));

                            Rectangle guestBar = new Rectangle(x, height - margin - guestBarHeight, barWidth, guestBarHeight);
                            Rectangle memberBar = new Rectangle(x + barWidth, height - margin - memberBarHeight, barWidth, memberBarHeight);

                            if (guestBar.contains(e.getPoint())) {
                                tooltipText = String.format("Khách lẻ: %,.0f VNĐ", guestRevenue);
                                tooltipPoint = e.getPoint();
                            } else if (memberBar.contains(e.getPoint())) {
                                tooltipText = String.format("Khách thành viên: %,.0f VNĐ", memberRevenue);
                                tooltipPoint = e.getPoint();
                            }
                        }
                        repaint();
                    }
                });
            }
        };
        revenueBarChartPanel.setBackground(Color.WHITE);
        revenueBarChartPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        chartSplitPane.setLeftComponent(revenueBarChartPanel);

        orderLineChartPanel = new JPanel(new BorderLayout()) {
            private String tooltipText = null;
            private Point tooltipPoint = null;

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int width = getWidth();
                int height = getHeight();
                int margin = 30;

                g2d.setColor(Color.BLACK);
                g2d.drawLine(margin, margin, margin, height - margin);
                g2d.drawLine(margin, height - margin, width - margin, height - margin);

                Map<String, Map<String, Integer>> orderData = getOrderCountByTimeSlot();
                List<String> timeSlots = new ArrayList<>(orderData.keySet());
                if (timeSlots.isEmpty()) {
                    g2d.setColor(Color.BLACK);
                    g2d.drawString("Không có dữ liệu để hiển thị", width / 2 - 50, height / 2);
                    return;
                }

                int maxValue = orderData.values().stream()
                        .flatMap(slot -> slot.values().stream())
                        .max(Integer::compare).orElse(1);

                int pointSpacing = timeSlots.size() > 1 ? (width - 2 * margin) / (timeSlots.size() - 1) : width - 2 * margin;

                g2d.setColor(Color.decode("#60A5FA"));
                int[] mangVeValues = new int[timeSlots.size()];
                for (int i = 0; i < timeSlots.size(); i++) {
                    mangVeValues[i] = orderData.get(timeSlots.get(i)).getOrDefault("Mang về", 0);
                }
                for (int i = 0; i < timeSlots.size() - 1; i++) {
                    int x1 = margin + i * pointSpacing;
                    int y1 = height - margin - (int) ((double) mangVeValues[i] / maxValue * (height - 2 * margin));
                    int x2 = margin + (i + 1) * pointSpacing;
                    int y2 = height - margin - (int) ((double) mangVeValues[i + 1] / maxValue * (height - 2 * margin));
                    g2d.drawLine(x1, y1, x2, y2);
                    g2d.fillOval(x1 - 3, y1 - 3, 6, 6);
                    if (i == timeSlots.size() - 2) {
                        g2d.fillOval(x2 - 3, y2 - 3, 6, 6);
                    }
                }

                g2d.setColor(Color.decode("#34D399"));
                int[] taiChoValues = new int[timeSlots.size()];
                for (int i = 0; i < timeSlots.size(); i++) {
                    taiChoValues[i] = orderData.get(timeSlots.get(i)).getOrDefault("Tại chỗ", 0);
                }
                for (int i = 0; i < timeSlots.size() - 1; i++) {
                    int x1 = margin + i * pointSpacing;
                    int y1 = height - margin - (int) ((double) taiChoValues[i] / maxValue * (height - 2 * margin));
                    int x2 = margin + (i + 1) * pointSpacing;
                    int y2 = height - margin - (int) ((double) taiChoValues[i + 1] / maxValue * (height - 2 * margin));
                    g2d.drawLine(x1, y1, x2, y2);
                    g2d.fillOval(x1 - 3, y1 - 3, 6, 6);
                    if (i == timeSlots.size() - 2) {
                        g2d.fillOval(x2 - 3, y2 - 3, 6, 6);
                    }
                }

                g2d.setColor(Color.BLACK);
                for (int i = 0; i < timeSlots.size(); i++) {
                    int x = margin + i * pointSpacing;
                    g2d.drawString(timeSlots.get(i), x - 20, height - margin + 15);
                }

                g2d.setColor(Color.decode("#60A5FA"));
                g2d.drawLine(width - 100, margin, width - 80, margin);
                g2d.setColor(Color.BLACK);
                g2d.drawString("Mang về", width - 60, margin + 5);

                g2d.setColor(Color.decode("#34D399"));
                g2d.drawLine(width - 100, margin + 20, width - 80, margin + 20);
                g2d.setColor(Color.BLACK);
                g2d.drawString("Tại chỗ", width - 60, margin + 25);

                g2d.setFont(new Font("Roboto", Font.PLAIN, 14));
                g2d.drawString("Số đơn hàng theo thời gian", margin, margin - 10);

                if (tooltipText != null && tooltipPoint != null) {
                    g2d.setFont(new Font("Roboto", Font.PLAIN, 12));
                    FontMetrics fm = g2d.getFontMetrics();
                    int textWidth = fm.stringWidth(tooltipText);
                    int textHeight = fm.getHeight();
                    int padding = 5;
                    int boxWidth = textWidth + 2 * padding;
                    int boxHeight = textHeight + 2 * padding;

                    int x = tooltipPoint.x + 10;
                    int y = tooltipPoint.y - boxHeight - 10;
                    if (x + boxWidth > width) x = tooltipPoint.x - boxWidth - 10;
                    if (y < 0) y = tooltipPoint.y + 10;

                    g2d.setColor(new Color(0, 0, 0, 200));
                    g2d.fillRect(x, y, boxWidth, boxHeight);
                    g2d.setColor(Color.WHITE);
                    g2d.drawString(tooltipText, x + padding, y + textHeight);
                }
            }

            {
                addMouseMotionListener(new MouseMotionAdapter() {
                    @Override
                    public void mouseMoved(MouseEvent e) {
                        int width = getWidth();
                        int height = getHeight();
                        int margin = 30;

                        Map<String, Map<String, Integer>> orderData = getOrderCountByTimeSlot();
                        List<String> timeSlots = new ArrayList<>(orderData.keySet());
                        if (timeSlots.isEmpty()) {
                            tooltipText = null;
                            repaint();
                            return;
                        }

                        int maxValue = orderData.values().stream()
                                .flatMap(slot -> slot.values().stream())
                                .max(Integer::compare).orElse(1);

                        int pointSpacing = timeSlots.size() > 1 ? (width - 2 * margin) / (timeSlots.size() - 1) : width - 2 * margin;

                        tooltipText = null;
                        tooltipPoint = null;

                        for (int i = 0; i < timeSlots.size(); i++) {
                            String slot = timeSlots.get(i);
                            int mangVe = orderData.get(slot).getOrDefault("Mang về", 0);
                            int taiCho = orderData.get(slot).getOrDefault("Tại chỗ", 0);

                            int x = margin + i * pointSpacing;
                            int mangVeY = height - margin - (int) ((double) mangVe / maxValue * (height - 2 * margin));
                            int taiChoY = height - margin - (int) ((double) taiCho / maxValue * (height - 2 * margin));

                            Rectangle mangVePoint = new Rectangle(x - 5, mangVeY - 5, 10, 10);
                            Rectangle taiChoPoint = new Rectangle(x - 5, taiChoY - 5, 10, 10);

                            if (mangVePoint.contains(e.getPoint())) {
                                tooltipText = String.format("Mang về: %d đơn", mangVe);
                                tooltipPoint = e.getPoint();
                            } else if (taiChoPoint.contains(e.getPoint())) {
                                tooltipText = String.format("Tại chỗ: %d đơn", taiCho);
                                tooltipPoint = e.getPoint();
                            }
                        }
                        repaint();
                    }
                });
            }
        };
        orderLineChartPanel.setBackground(Color.WHITE);
        orderLineChartPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        chartSplitPane.setRightComponent(orderLineChartPanel);

        bottomSection.add(chartSplitPane, BorderLayout.CENTER);
        mainSplitPane.setBottomComponent(bottomSection);

        add(mainSplitPane, BorderLayout.CENTER);

        ordersFilter.addActionListener(e -> updateOrdersTable());
        topItemsFilter.addActionListener(e -> updateTopItemsTable());
        revenueFilter.addActionListener(e -> {
            revenueBarChartPanel.repaint();
            orderLineChartPanel.repaint();
        });

        updateOrdersTable();
        updateTopItemsTable();
    }

    private void updateOrdersTable() {
        String filter = (String) ordersFilter.getSelectedItem();
        List<HoaDon> orders = thongKeDAO.getOrders(filter, null);
        DefaultTableModel model = (DefaultTableModel) ordersTable.getModel();
        model.setRowCount(0);

        if (orders.isEmpty()) {
            System.out.println("Không có hóa đơn nào để hiển thị trong bảng.");
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        for (HoaDon order : orders) {
            LocalDate date = order.getNgayLap();
            String formattedDate = date != null ? date.format(formatter) : "Không có ngày";
            model.addRow(new Object[]{
                order.getMaHD(),
                formattedDate,
                String.format("%,.0f VNĐ", order.getTongTien()),
                "Xem chi tiết"
            });
        }
        System.out.println("Đã hiển thị " + orders.size() + " hóa đơn trong bảng.");
    }

    private void updateTopItemsTable() {
        String filter = (String) topItemsFilter.getSelectedItem();
        List<HoaDon> orders = thongKeDAO.getOrders(filter, null);
        Map<String, Integer> quantityMap = new HashMap<>();
        Map<String, Double> revenueMap = new HashMap<>();
        Map<String, String> productNames = new HashMap<>();

        productNames.putAll(thongKeDAO.getProductNames());

        for (HoaDon order : orders) {
            List<ChiTietHoaDon> chiTietList = ChiTietHoaDon_DAO.getChiTietHoaDon(order.getMaHD());
            for (ChiTietHoaDon ct : chiTietList) {
                quantityMap.merge(ct.getMaSP(), ct.getSoLuong(), Integer::sum);
                revenueMap.merge(ct.getMaSP(), ct.getThanhTien(), Double::sum);
            }
        }

        List<Map.Entry<String, Integer>> sortedItems = new ArrayList<>(quantityMap.entrySet());
        sortedItems.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        DefaultTableModel model = (DefaultTableModel) topItemsTable.getModel();
        model.setRowCount(0);

        if (sortedItems.isEmpty()) {
            System.out.println("Không có sản phẩm nào để hiển thị trong bảng món bán chạy.");
            return;
        }

        int rank = 1;
        for (Map.Entry<String, Integer> entry : sortedItems) {
            String maSP = entry.getKey();
            String tenSP = productNames.getOrDefault(maSP, "Không tìm thấy");
            int soLuong = entry.getValue();
            double doanhThu = revenueMap.getOrDefault(maSP, 0.0);
            model.addRow(new Object[]{
                rank++,
                tenSP,
                soLuong,
                String.format("%,.0f VNĐ", doanhThu)
            });
        }
        System.out.println("Đã hiển thị " + sortedItems.size() + " sản phẩm trong bảng món bán chạy.");
    }

    private Map<String, Map<String, Double>> getRevenueByTimeSlot() {
        String filter = (String) revenueFilter.getSelectedItem();
        List<HoaDon> orders = thongKeDAO.getOrders(filter, null);
        Map<String, Map<String, Double>> revenueByTimeSlot = new TreeMap<>();

        for (HoaDon order : orders) {
            if (order.getNgayLap() == null) {
                continue;
            }
            String timeSlot = getRevenueTimeSlot(order.getNgayLap(), filter);
            String customerType = order.getMaKH() == null ? "Khách lẻ" : "Khách thành viên";
            revenueByTimeSlot.computeIfAbsent(timeSlot, k -> new HashMap<>())
                    .merge(customerType, order.getTongTien(), Double::sum);
        }

        return revenueByTimeSlot;
    }

    private String getRevenueTimeSlot(LocalDate date, String filter) {
        if (date == null) {
            return "Không có ngày";
        }
        if ("Theo ngày".equals(filter)) {
            LocalDate today = LocalDate.now();
            if (date.equals(today)) {
                return "Hôm nay";
            } else if (date.equals(today.minusDays(1))) {
                return "Hôm qua";
            } else if (date.equals(today.minusDays(2))) {
                return "Hôm kia";
            } else {
                return date.format(DateTimeFormatter.ofPattern("dd/MM"));
            }
        } else if ("Theo tháng".equals(filter)) {
            int day = date.getDayOfMonth();
            if (day <= 10) {
                return "1-10";
            } else if (day <= 20) {
                return "11-20";
            } else {
                return "21-31";
            }
        } else {
            int month = date.getMonthValue();
            if (month <= 4) {
                return "Quý 1";
            } else if (month <= 8) {
                return "Quý 2";
            } else {
                return "Quý 3";
            }
        }
    }

    private Map<String, Map<String, Integer>> getOrderCountByTimeSlot() {
        String filter = (String) revenueFilter.getSelectedItem();
        List<HoaDon> orders = thongKeDAO.getOrders(filter, null);
        Map<String, Map<String, Integer>> orderCountByTimeSlot = new TreeMap<>();

        // Định nghĩa thứ tự các mốc thời gian
        List<String> timeSlotsOrder;
        if ("Theo ngày".equals(filter)) {
            timeSlotsOrder = Arrays.asList("Hôm kia", "Hôm qua", "Hôm nay");
        } else if ("Theo tháng".equals(filter)) {
            timeSlotsOrder = Arrays.asList("1-10", "11-20", "21-31");
        } else {
            timeSlotsOrder = Arrays.asList("Quý 1", "Quý 2", "Quý 3");
        }

        // Khởi tạo map với các mốc thời gian
        for (String slot : timeSlotsOrder) {
            orderCountByTimeSlot.put(slot, new HashMap<>());
            orderCountByTimeSlot.get(slot).put("Mang về", 0);
            orderCountByTimeSlot.get(slot).put("Tại chỗ", 0);
        }

        for (HoaDon order : orders) {
            if (order.getNgayLap() == null) {
                continue;
            }
            String timeSlot = getOrderTimeSlot(order.getNgayLap(), filter);
            String orderType = order.getHinhThucThanhToan() != null && order.getHinhThucThanhToan().equalsIgnoreCase("Tiền Mặt") ? "Tại chỗ" : "Mang về";
            if (orderCountByTimeSlot.containsKey(timeSlot)) {
                orderCountByTimeSlot.get(timeSlot).merge(orderType, 1, Integer::sum);
            }
        }

        // Chỉ giữ các mốc thời gian được định nghĩa
        Map<String, Map<String, Integer>> filteredOrderCount = new TreeMap<>();
        for (String slot : timeSlotsOrder) {
            filteredOrderCount.put(slot, orderCountByTimeSlot.get(slot));
        }

        return filteredOrderCount;
    }

    private String getOrderTimeSlot(LocalDate date, String filter) {
        if (date == null) {
            return "Không có ngày";
        }
        if ("Theo ngày".equals(filter)) {
            LocalDate today = LocalDate.now();
            if (date.equals(today)) {
                return "Hôm nay";
            } else if (date.equals(today.minusDays(1))) {
                return "Hôm qua";
            } else if (date.equals(today.minusDays(2))) {
                return "Hôm kia";
            } else {
                return date.format(DateTimeFormatter.ofPattern("dd/MM"));
            }
        } else if ("Theo tháng".equals(filter)) {
            int day = date.getDayOfMonth();
            if (day <= 10) {
                return "1-10";
            } else if (day <= 20) {
                return "11-20";
            } else {
                return "21-31";
            }
        } else {
            int month = date.getMonthValue();
            if (month <= 4) {
                return "Quý 1";
            } else if (month <= 8) {
                return "Quý 2";
            } else {
                return "Quý 3";
            }
        }
    }
}