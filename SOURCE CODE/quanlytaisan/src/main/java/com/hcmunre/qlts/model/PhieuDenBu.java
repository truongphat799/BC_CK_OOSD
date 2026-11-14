package com.hcmunre.qlts.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "PhieuDenBu")
public class PhieuDenBu implements Serializable {
    @Id
    @Column(name = "SoPhieuDB", length = 30)
    private String soPhieuDB;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "SoPhieuBT", nullable = false)
    private PhieuBaoTri phieuBaoTri;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MaNVBoiThuong", nullable = false)
    private NguoiDung nvBoiThuong;

    @Column(name = "SoTienDenBu", nullable = false, precision = 18, scale = 2)
    private BigDecimal soTienDenBu;

    @Column(name = "LyDoDenBu", nullable = false, length = 255)
    private String lyDoDenBu;

    @Column(name = "HinhThucXuLy", nullable = false, length = 100)
    private String hinhThucXuLy = "Khấu trừ lương";

    @Column(name = "HanChotBoiThuong", nullable = false)
    private LocalDate hanChotBoiThuong;

    @Column(name = "TrangThai", nullable = false, length = 30)
    private String trangThai = "Chưa bồi thường";

    public PhieuDenBu() {}

    public String getSoPhieuDB() { return soPhieuDB; }
    public void setSoPhieuDB(String soPhieuDB) { this.soPhieuDB = soPhieuDB; }

    public PhieuBaoTri getPhieuBaoTri() { return phieuBaoTri; }
    public void setPhieuBaoTri(PhieuBaoTri phieuBaoTri) { this.phieuBaoTri = phieuBaoTri; }

    public NguoiDung getNvBoiThuong() { return nvBoiThuong; }
    public void setNvBoiThuong(NguoiDung nvBoiThuong) { this.nvBoiThuong = nvBoiThuong; }

    public BigDecimal getSoTienDenBu() { return soTienDenBu; }
    public void setSoTienDenBu(BigDecimal soTienDenBu) { this.soTienDenBu = soTienDenBu; }

    public String getLyDoDenBu() { return lyDoDenBu; }
    public void setLyDoDenBu(String lyDoDenBu) { this.lyDoDenBu = lyDoDenBu; }

    public String getHinhThucXuLy() { return hinhThucXuLy; }
    public void setHinhThucXuLy(String hinhThucXuLy) { this.hinhThucXuLy = hinhThucXuLy; }

    public LocalDate getHanChotBoiThuong() { return hanChotBoiThuong; }
    public void setHanChotBoiThuong(LocalDate hanChotBoiThuong) { this.hanChotBoiThuong = hanChotBoiThuong; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
}
