package Entity;

public class OrderItem {
    private String tenMon;
    private String size;
    private double gia;
    private int soLuong;

    public OrderItem(String tenMon, String size, double gia, int soLuong) {
        this.tenMon = tenMon;
        this.size = size;
        this.gia = gia;
        this.soLuong = soLuong;
    }

    public String getTenMon() { return tenMon; }
    public String getSize() { return size; }
    public double getGia() { return gia; }
    public int getSoLuong() { return soLuong; }
}
