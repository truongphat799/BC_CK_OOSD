package com.hcmunre.qlts.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "PhieuBaoTri")
public class PhieuBaoTri implements Serializable {
    @Id
    @Column(name = "SoPhieuBT", length = 30)
    private String soPhieuBT;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MaYeuCau", unique = true)
    private YeuCauBaoTri yeuCauBaoTri;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MaNVLap", nullable = false)
    private NguoiDung nvLap;

    @Column(name = "NgayLap", nullable = false)
    private LocalDate ngayLap;

    @Column(name = "NgayHoanThanh")
    private LocalDate ngayHoanThanh;

    @Column(name = "DonViSuaChua", nullable = false, length = 150)
    private String donViSuaChua;

    @Column(name = "TongChiPhi", nullable = false, precision = 18, scale = 2)
    private BigDecimal tongChiPhi = BigDecimal.ZERO;

    @Column(name = "KetQuaXuLy", nullable = false, length = 50)
    private String ketQuaXuLy = "Đang sửa chữa";

    @OneToMany(mappedBy = "phieuBaoTri", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChiTietBaoTri> chiTietList = new ArrayList<>();

    public PhieuBaoTri() {}

    public String getSoPhieuBT() { return soPhieuBT; }
    public void setSoPhieuBT(String soPhieuBT) { this.soPhieuBT = soPhieuBT; }

    public YeuCauBaoTri getYeuCauBaoTri() { return yeuCauBaoTri; }
    public void setYeuCauBaoTri(YeuCauBaoTri yeuCauBaoTri) { this.yeuCauBaoTri = yeuCauBaoTri; }

    public NguoiDung getNvLap() { return nvLap; }
    public void setNvLap(NguoiDung nvLap) { this.nvLap = nvLap; }

    public LocalDate getNgayLap() { return ngayLap; }
    public void setNgayLap(LocalDate ngayLap) { this.ngayLap = ngayLap; }

    public LocalDate getNgayHoanThanh() { return ngayHoanThanh; }
    public void setNgayHoanThanh(LocalDate ngayHoanThanh) { this.ngayHoanThanh = ngayHoanThanh; }

    public String getDonViSuaChua() { return donViSuaChua; }
    public void setDonViSuaChua(String donViSuaChua) { this.donViSuaChua = donViSuaChua; }

    public BigDecimal getTongChiPhi() { return tongChiPhi; }
    public void setTongChiPhi(BigDecimal tongChiPhi) { this.tongChiPhi = tongChiPhi; }

    public String getKetQuaXuLy() { return ketQuaXuLy; }
    public void setKetQuaXuLy(String ketQuaXuLy) { this.ketQuaXuLy = ketQuaXuLy; }

    public List<ChiTietBaoTri> getChiTietList() { return chiTietList; }
    public void setChiTietList(List<ChiTietBaoTri> chiTietList) { this.chiTietList = chiTietList; }
}
