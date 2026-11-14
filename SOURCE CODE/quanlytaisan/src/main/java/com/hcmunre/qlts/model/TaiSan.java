package com.hcmunre.qlts.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "TaiSan")
public class TaiSan implements Serializable {
    @Id
    @Column(name = "MaTaiSan", length = 30)
    private String maTaiSan;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MaLoai", nullable = false)
    private LoaiTaiSan loaiTaiSan;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MaNCC", nullable = false)
    private NhaCungCap nhaCungCap;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MaPhongBan")
    private PhongBan phongBan;

    @Column(name = "TenTaiSan", nullable = false, length = 150)
    private String tenTaiSan;

    @Column(name = "DonViTinh", nullable = false, length = 20)
    private String donViTinh = "Cái";

    @Column(name = "NguyenGia", nullable = false, precision = 18, scale = 2)
    private BigDecimal nguyenGia;

    @Column(name = "GiaTriConLai", nullable = false, precision = 18, scale = 2)
    private BigDecimal giaTriConLai;

    @Column(name = "NgayTiepNhan", nullable = false)
    private LocalDate ngayTiepNhan;

    @Column(name = "TinhTrang", nullable = false, length = 50)
    private String tinhTrang = "Trong kho";

    @Column(name = "ViTri", length = 150)
    private String viTri;

    @Column(name = "NamSanXuat", nullable = false)
    private Integer namSanXuat;

    @Column(name = "SoHoaDon", nullable = false, length = 50)
    private String soHoaDon;

    public TaiSan() {}

    public String getMaTaiSan() { return maTaiSan; }
    public void setMaTaiSan(String maTaiSan) { this.maTaiSan = maTaiSan; }

    public LoaiTaiSan getLoaiTaiSan() { return loaiTaiSan; }
    public void setLoaiTaiSan(LoaiTaiSan loaiTaiSan) { this.loaiTaiSan = loaiTaiSan; }

    public NhaCungCap getNhaCungCap() { return nhaCungCap; }
    public void setNhaCungCap(NhaCungCap nhaCungCap) { this.nhaCungCap = nhaCungCap; }

    public PhongBan getPhongBan() { return phongBan; }
    public void setPhongBan(PhongBan phongBan) { this.phongBan = phongBan; }

    public String getTenTaiSan() { return tenTaiSan; }
    public void setTenTaiSan(String tenTaiSan) { this.tenTaiSan = tenTaiSan; }

    public String getDonViTinh() { return donViTinh; }
    public void setDonViTinh(String donViTinh) { this.donViTinh = donViTinh; }

    public BigDecimal getNguyenGia() { return nguyenGia; }
    public void setNguyenGia(BigDecimal nguyenGia) { this.nguyenGia = nguyenGia; }

    public BigDecimal getGiaTriConLai() { return giaTriConLai; }
    public void setGiaTriConLai(BigDecimal giaTriConLai) { this.giaTriConLai = giaTriConLai; }

    public LocalDate getNgayTiepNhan() { return ngayTiepNhan; }
    public void setNgayTiepNhan(LocalDate ngayTiepNhan) { this.ngayTiepNhan = ngayTiepNhan; }

    public String getTinhTrang() { return tinhTrang; }
    public void setTinhTrang(String tinhTrang) { this.tinhTrang = tinhTrang; }

    public String getViTri() { return viTri; }
    public void setViTri(String viTri) { this.viTri = viTri; }

    public Integer getNamSanXuat() { return namSanXuat; }
    public void setNamSanXuat(Integer namSanXuat) { this.namSanXuat = namSanXuat; }

    public String getSoHoaDon() { return soHoaDon; }
    public void setSoHoaDon(String soHoaDon) { this.soHoaDon = soHoaDon; }
}
