package com.hcmunre.qlts.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "PhieuKiemKe")
public class PhieuKiemKe implements Serializable {
    @Id
    @Column(name = "SoPhieuKK", length = 30)
    private String soPhieuKK;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MaPhongBan", nullable = false)
    private PhongBan phongBan;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MaNVChuTri", nullable = false)
    private NguoiDung nvChuTri;

    @Column(name = "NgayKiemKe", nullable = false)
    private LocalDate ngayKiemKe;

    @Column(name = "DotKiemKe", nullable = false, length = 100)
    private String dotKiemKe;

    @Column(name = "TruongBanKiemKe", nullable = false, length = 120)
    private String truongBanKiemKe;

    @Column(name = "GhiChuChung", length = 255)
    private String ghiChuChung;

    @OneToMany(mappedBy = "phieuKiemKe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChiTietKiemKe> chiTietList = new ArrayList<>();

    public PhieuKiemKe() {}

    public String getSoPhieuKK() { return soPhieuKK; }
    public void setSoPhieuKK(String soPhieuKK) { this.soPhieuKK = soPhieuKK; }

    public PhongBan getPhongBan() { return phongBan; }
    public void setPhongBan(PhongBan phongBan) { this.phongBan = phongBan; }

    public NguoiDung getNvChuTri() { return nvChuTri; }
    public void setNvChuTri(NguoiDung nvChuTri) { this.nvChuTri = nvChuTri; }

    public LocalDate getNgayKiemKe() { return ngayKiemKe; }
    public void setNgayKiemKe(LocalDate ngayKiemKe) { this.ngayKiemKe = ngayKiemKe; }

    public String getDotKiemKe() { return dotKiemKe; }
    public void setDotKiemKe(String dotKiemKe) { this.dotKiemKe = dotKiemKe; }

    public String getTruongBanKiemKe() { return truongBanKiemKe; }
    public void setTruongBanKiemKe(String truongBanKiemKe) { this.truongBanKiemKe = truongBanKiemKe; }

    public String getGhiChuChung() { return ghiChuChung; }
    public void setGhiChuChung(String ghiChuChung) { this.ghiChuChung = ghiChuChung; }

    public List<ChiTietKiemKe> getChiTietList() { return chiTietList; }
    public void setChiTietList(List<ChiTietKiemKe> chiTietList) { this.chiTietList = chiTietList; }
}
