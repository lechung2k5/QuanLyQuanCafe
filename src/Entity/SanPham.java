package Entity;

import javax.swing.JCheckBox;

public class SanPham {
    private String maSP;
    private String tenSP;
    private String loaiSP;
    private double donGia;
    private String size;
    private int soLuongTon;
    private String moTa;
    private JCheckBox chon; // Cột "Chọn" để chọn sản phẩm

    // ✅ Constructor đầy đủ
    public SanPham(String maSP, String tenSP, String loaiSP, double donGia, String size, int soLuongTon, String moTa) {
        this.maSP = maSP;
        this.tenSP = tenSP;
        this.loaiSP = loaiSP;
        this.donGia = donGia;
        this.size = size;
        this.soLuongTon = soLuongTon;
        this.moTa = moTa;
        this.chon = new JCheckBox();
    }

    // ✅ Constructor rút gọn để hiển thị bảng
    public SanPham(String tenSP, String size, double donGia) {
        this.tenSP = tenSP;
        this.size = size;
        this.donGia = donGia;
        this.chon = new JCheckBox();
    }

    // ✅ Dùng để hiển thị dữ liệu trong JTable
    public Object[] toRow() {
        return new Object[] { tenSP, size, String.format("%,.0fđ", donGia), chon };
    }

    // 🔽 Getter và Setter đầy đủ
    public String getMaSP() {
        return maSP;
    }

    public void setMaSP(String maSP) {
        this.maSP = maSP;
    }

    public String getTenSP() {
        return tenSP;
    }

    public void setTenSP(String tenSP) {
        this.tenSP = tenSP;
    }

    public String getLoaiSP() {
        return loaiSP;
    }

    public void setLoaiSP(String loaiSP) {
        this.loaiSP = loaiSP;
    }

    public double getDonGia() {
        return donGia;
    }

    public void setDonGia(double donGia) {
        this.donGia = donGia;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public int getSoLuongTon() {
        return soLuongTon;
    }

    public void setSoLuongTon(int soLuongTon) {
        this.soLuongTon = soLuongTon;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public JCheckBox getChon() {
        return chon;
    }

    public void setChon(JCheckBox chon) {
        this.chon = chon;
    }

    @Override
    public String toString() {
        return "SanPham{" +
                "maSP='" + maSP + '\'' +
                ", tenSP='" + tenSP + '\'' +
                ", loaiSP='" + loaiSP + '\'' +
                ", size='" + size + '\'' +
                ", donGia=" + donGia +
                ", soLuongTon=" + soLuongTon +
                ", moTa='" + moTa + '\'' +
                '}';
    }
}
