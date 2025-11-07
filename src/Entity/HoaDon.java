package Entity;

import java.sql.Timestamp;
import java.time.LocalDate;

public class HoaDon {
    private String maHD;
    private String maNV;
    private String maKH;
    private String maKM;
    private LocalDate ngayLap;
    private double tongTien;
    private String hinhThucThanhToan;
    private double vat;

    // Constructor đầy đủ
    public HoaDon(String maHD, String maNV, String maKH, String maKM,
                  LocalDate ngayLap, double tongTien,
                  String hinhThucThanhToan, double vat) {
        this.maHD = maHD;
        this.maNV = maNV;
        this.maKH = maKH;
        this.maKM = maKM;
        this.ngayLap = ngayLap;
        this.tongTien = tongTien;
        this.hinhThucThanhToan = hinhThucThanhToan;
        this.vat = vat;
    }

    // Constructor mặc định
    public HoaDon() {
    }

    // Getters
    public String getMaHD() {
        return maHD;
    }

    public String getMaNV() {
        return maNV;
    }

    public String getMaKH() {
        return maKH;
    }

    public String getMaKM() {
        return maKM;
    }

    public LocalDate getNgayLap() {
        return ngayLap;
    }

    public double getTongTien() {
        return tongTien;
    }

    public String getHinhThucThanhToan() {
        return hinhThucThanhToan;
    }

    public double getVAT() {
        return vat;
    }

    // Setters
    public void setMaHD(String maHD) {
        this.maHD = maHD;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public void setMaKH(String maKH) {
        this.maKH = maKH;
    }

    public void setMaKM(String maKM) {
        this.maKM = maKM;
    }

    public void setNgayLap(LocalDate ngayLap) {
        this.ngayLap = ngayLap;
    }

    public void setTongTien(double tongTien) {
        this.tongTien = tongTien;
    }

    public void setHinhThucThanhToan(String hinhThucThanhToan) {
        this.hinhThucThanhToan = hinhThucThanhToan;
    }

    public void setVAT(double vat) {
        this.vat = vat;
    }
}