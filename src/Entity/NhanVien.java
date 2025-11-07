package Entity;

import java.sql.Date;

public class NhanVien {
	private String maNV, hoTenNV , soDT, chucVu, matKhau,  CaLam, TrangThai;
	private Date ngayVaoLam;
	public NhanVien(String maNV, String hoTenNV, String soDT, String chucVu, String matKhau , Date ngayVaoLam, String caLam,
			String trangThai) {
		super();
		this.maNV = maNV;
		this.hoTenNV = hoTenNV;
		this.soDT = soDT;
		this.chucVu = chucVu;
		this.matKhau = matKhau;
		CaLam = caLam;
		TrangThai = trangThai;
		this.ngayVaoLam = ngayVaoLam;
	}
	public String getMaNV() {
		return maNV;
	}
	public void setMaNV(String maNV) {
		this.maNV = maNV;
	}
	public String getHoTenNV() {
		return hoTenNV;
	}
	public void setHoTenNV(String hoTenNV) {
		this.hoTenNV = hoTenNV;
	}
	public String getSoDT() {
		return soDT;
	}
	public void setSoDT(String soDT) {
		this.soDT = soDT;
	}
	public String getChucVu() {
		return chucVu;
	}
	public void setChucVu(String chucVu) {
		this.chucVu = chucVu;
	}
	public String getMatKhau() {
		return matKhau;
	}
	public void setMatKhau(String matKhau) {
		this.matKhau = matKhau;
	}
	public String getCaLam() {
		return CaLam;
	}
	public void setCaLam(String caLam) {
		CaLam = caLam;
	}
	public String getTrangThai() {
		return TrangThai;
	}
	public void setTrangThai(String trangThai) {
		TrangThai = trangThai;
	}
	public Date getNgayVaoLam() {
		return ngayVaoLam;
	}
	public void setNgayVaoLam(Date ngayVaoLam) {
		this.ngayVaoLam = ngayVaoLam;
	}
	@Override
	public String toString() {
		return "NhanVien [maNV=" + maNV + ", hoTenNV=" + hoTenNV + ", soDT=" + soDT + ", chucVu=" + chucVu
				+ ", matKhau=" + matKhau + ", CaLam=" + CaLam + ", TrangThai=" + TrangThai + ", ngayVaoLam="
				+ ngayVaoLam + "]";
	}
	
	

	
}
