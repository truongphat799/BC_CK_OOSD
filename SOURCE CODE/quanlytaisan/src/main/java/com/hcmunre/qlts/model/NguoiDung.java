package com.hcmunre.qlts.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "NguoiDung")
public class NguoiDung implements Serializable {
    @Id
    @Column(name = "MaNguoiDung", length = 20)
    private String maNguoiDung;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MaVaiTro", nullable = false)
    private VaiTro vaiTro;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MaPhongBan", nullable = false)
    private PhongBan phongBan;

    @Column(name = "HoTen", nullable = false, length = 120)
    private String hoTen;

    @Column(name = "TenDangNhap", nullable = false, unique = true, length = 50)
    private String tenDangNhap;

    @Column(name = "MatKhau", nullable = false, length = 255)
    private String matKhau;

    @Column(name = "Email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "SoDienThoai", length = 20)
    private String soDienThoai;

    @Column(name = "TrangThai", nullable = false, length = 30)
    private String trangThai = "Đang hoạt động";

    public NguoiDung() {}

    public String getMaNguoiDung() { return maNguoiDung; }
    public void setMaNguoiDung(String maNguoiDung) { this.maNguoiDung = maNguoiDung; }

    public VaiTro getVaiTro() { return vaiTro; }
    public void setVaiTro(VaiTro vaiTro) { this.vaiTro = vaiTro; }

    public PhongBan getPhongBan() { return phongBan; }
    public void setPhongBan(PhongBan phongBan) { this.phongBan = phongBan; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public String getTenDangNhap() { return tenDangNhap; }
    public void setTenDangNhap(String tenDangNhap) { this.tenDangNhap = tenDangNhap; }

    public String getMatKhau() { return matKhau; }
    public void setMatKhau(String matKhau) { this.matKhau = matKhau; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
}
