package UI;

import javax.swing.*;
import java.awt.*;

public class InvoiceButtonPanel extends JPanel {
    private final JButton btnInOrder;
    private final JButton btnInHoaDon;
    private final JButton btnTachHD;
    private final JButton btnThanhToan;
    private final JButton btnReset;
    private final JButton btnEdit;

    /**
     * Khởi tạo InvoiceButtonPanel.
     */
    public InvoiceButtonPanel() {
        this.btnInOrder = new JButton("In Order");
        this.btnInHoaDon = new JButton("In hóa đơn");
        this.btnTachHD = new JButton("Tách hóa đơn");
        this.btnThanhToan = new JButton("Thanh toán");
        this.btnReset = new JButton("Reset bàn");
        this.btnEdit = new JButton("Chỉnh sửa");

        initializeUI();
    }

    /**
     * Khởi tạo giao diện người dùng.
     */
    private void initializeUI() {
        setLayout(new GridLayout(2, 3, 10, 10));

        btnInOrder.setBackground(Color.ORANGE);
        btnInOrder.setFont(new Font("Roboto", Font.BOLD, 12));
        btnInHoaDon.setBackground(new Color(100, 149, 237));
        btnInHoaDon.setFont(new Font("Roboto", Font.BOLD, 12));
        btnTachHD.setBackground(Color.PINK);
        btnTachHD.setEnabled(false);
        btnTachHD.setFont(new Font("Roboto", Font.BOLD, 12));
        btnThanhToan.setBackground(Color.RED);
        btnThanhToan.setFont(new Font("Roboto", Font.BOLD, 12));
        btnReset.setBackground(Color.GREEN);
        btnReset.setFont(new Font("Roboto", Font.BOLD, 12));
        btnEdit.setBackground(Color.BLUE);
        btnEdit.setFont(new Font("Roboto", Font.BOLD, 12));

        add(btnInOrder);
        add(btnInHoaDon);
        add(btnTachHD);
        add(btnThanhToan);
        add(btnReset);
        add(btnEdit);
    }

    // Getters
    public JButton getBtnInOrder() {
        return btnInOrder;
    }

    public JButton getBtnInHoaDon() {
        return btnInHoaDon;
    }

    public JButton getBtnTachHD() {
        return btnTachHD;
    }

    public JButton getBtnThanhToan() {
        return btnThanhToan;
    }

    public JButton getBtnReset() {
        return btnReset;
    }

    public JButton getBtnEdit() {
        return btnEdit;
    }
}