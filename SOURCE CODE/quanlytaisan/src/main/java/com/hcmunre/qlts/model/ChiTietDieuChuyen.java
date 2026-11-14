package com.hcmunre.qlts.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "ChiTietDieuChuyen")
public class ChiTietDieuChuyen implements Serializable {
    @EmbeddedId
    private ChiTietDieuChuyenId id = new ChiTietDieuChuyenId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("soPhieuDC")
    @JoinColumn(name = "SoPhieuDC")
    private PhieuDieuChuyen phieuDieuChuyen;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("maTaiSan")
    @JoinColumn(name = "MaTaiSan")
    private TaiSan taiSan;

    @Column(name = "SoLuong", nullable = false)
    private Integer soLuong = 1;

    @Column(name = "TinhTrangKhiChuyen", nullable = false, length = 50)
    private String tinhTrangKhiChuyen;

    @Column(name = "ViTriMoi", nullable = false, length = 150)
    private String viTriMoi;

    public ChiTietDieuChuyen() {}

    public ChiTietDieuChuyenId getId() { return id; }
    public void setId(ChiTietDieuChuyenId id) { this.id = id; }

    public PhieuDieuChuyen getPhieuDieuChuyen() { return phieuDieuChuyen; }
    public void setPhieuDieuChuyen(PhieuDieuChuyen phieuDieuChuyen) { this.phieuDieuChuyen = phieuDieuChuyen; }

    public TaiSan getTaiSan() { return taiSan; }
    public void setTaiSan(TaiSan taiSan) { this.taiSan = taiSan; }

    public Integer getSoLuong() { return soLuong; }
    public void setSoLuong(Integer soLuong) { this.soLuong = soLuong; }

    public String getTinhTrangKhiChuyen() { return tinhTrangKhiChuyen; }
    public void setTinhTrangKhiChuyen(String tinhTrangKhiChuyen) { this.tinhTrangKhiChuyen = tinhTrangKhiChuyen; }

    public String getViTriMoi() { return viTriMoi; }
    public void setViTriMoi(String viTriMoi) { this.viTriMoi = viTriMoi; }
}
