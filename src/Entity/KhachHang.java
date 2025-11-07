package Entity;

public class KhachHang {
    private String maKH;
    private String hoTenKH;
    private String soDT;
    private String diaChi;
    private String email;
    private String ngayDangKy;
    private int diemTichLuy;

    public KhachHang(String maKH, String hoTenKH, String soDT, String diaChi, String email, String ngayDangKy, int diemTichLuy) {
        this.maKH = maKH;
        this.hoTenKH = hoTenKH;
        this.soDT = soDT;
        this.diaChi = diaChi;
        this.email = email;
        this.ngayDangKy = ngayDangKy;
        this.diemTichLuy = diemTichLuy;
    }

    // Getters và Setters
    public String getMaKH() {
        return maKH;
    }

    public void setMaKH(String maKH) {
        this.maKH = maKH;
    }

    public String getHoTenKH() {
        return hoTenKH;
    }

    public void setHoTenKH(String hoTenKH) {
        this.hoTenKH = hoTenKH;
    }

    public String getSoDT() {
        return soDT;
    }

    public void setSoDT(String soDT) {
        this.soDT = soDT;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNgayDangKy() {
        return ngayDangKy;
    }

    public void setNgayDangKy(String ngayDangKy) {
        this.ngayDangKy = ngayDangKy;
    }

    public int getDiemTichLuy() {
        return diemTichLuy;
    }

    public void setDiemTichLuy(int diemTichLuy) {
        this.diemTichLuy = diemTichLuy;
    }
}
