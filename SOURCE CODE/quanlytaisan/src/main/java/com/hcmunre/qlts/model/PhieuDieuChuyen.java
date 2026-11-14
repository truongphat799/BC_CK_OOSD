package com.hcmunre.qlts.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "PhieuDieuChuyen")
public class PhieuDieuChuyen implements Serializable {
    @Id
    @Column(name = "SoPhieuDC", length = 30)
    private String soPhieuDC;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MaNVLap", nullable = false)
    private NguoiDung nvLap;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MaPhongBanChuyen", nullable = false)
    private PhongBan phongBanChuyen;

    @Column(name = "NgayDieuChuyen", nullable = false)
    private LocalDate ngayDieuChuyen;

    @Column(name = "LyDoDieuChuyen", nullable = false, length = 255)
    private String lyDoDieuChuyen;

    @Column(name = "GhiChu", length = 255)
    private String ghiChu;

    @OneToMany(mappedBy = "phieuDieuChuyen", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChiTietDieuChuyen> chiTietList = new ArrayList<>();

    public PhieuDieuChuyen() {}

    public String getSoPhieuDC() { return soPhieuDC; }
    public void setSoPhieuDC(String soPhieuDC) { this.soPhieuDC = soPhieuDC; }

    public NguoiDung getNvLap() { return nvLap; }
    public void setNvLap(NguoiDung nvLap) { this.nvLap = nvLap; }

    public PhongBan getPhongBanChuyen() { return phongBanChuyen; }
    public void setPhongBanChuyen(PhongBan phongBanChuyen) { this.phongBanChuyen = phongBanChuyen; }

    public LocalDate getNgayDieuChuyen() { return ngayDieuChuyen; }
    public void setNgayDieuChuyen(LocalDate ngayDieuChuyen) { this.ngayDieuChuyen = ngayDieuChuyen; }

    public String getLyDoDieuChuyen() { return lyDoDieuChuyen; }
    public void setLyDoDieuChuyen(String lyDoDieuChuyen) { this.lyDoDieuChuyen = lyDoDieuChuyen; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }

    public List<ChiTietDieuChuyen> getChiTietList() { return chiTietList; }
    public void setChiTietList(List<ChiTietDieuChuyen> chiTietList) { this.chiTietList = chiTietList; }
}
