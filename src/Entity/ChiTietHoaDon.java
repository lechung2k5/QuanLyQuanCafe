package Entity;

public class ChiTietHoaDon {
    private String maSP;
    private int soLuong;
    private double donGia;
    private double giamGia;
    private double thanhTien;

    // Constructor
    public ChiTietHoaDon(String maSP, int soLuong, double donGia, double giamGia, double thanhTien) {
        this.maSP = maSP;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.giamGia = giamGia;
        this.thanhTien = thanhTien;
    }

    // Getters
    public String getMaSP() {
        return maSP;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public double getDonGia() {
        return donGia;
    }

    public double getGiamGia() {
        return giamGia;
    }

    public double getThanhTien() {
        return thanhTien;
    }
}