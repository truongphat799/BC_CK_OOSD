package com.hcmunre.qlts.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "ChiTietThanhLy")
public class ChiTietThanhLy implements Serializable {
    @EmbeddedId
    private ChiTietThanhLyId id = new ChiTietThanhLyId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("soPhieuTL")
    @JoinColumn(name = "SoPhieuTL")
    private PhieuThanhLy phieuThanhLy;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("maTaiSan")
    @JoinColumn(name = "MaTaiSan")
    private TaiSan taiSan;

    @Column(name = "NguyenGia", nullable = false, precision = 18, scale = 2)
    private BigDecimal nguyenGia;

    @Column(name = "GiaTriConLai", nullable = false, precision = 18, scale = 2)
    private BigDecimal giaTriConLai;

    @Column(name = "GiaTriThuHoi", nullable = false, precision = 18, scale = 2)
    private BigDecimal giaTriThuHoi = BigDecimal.ZERO;

    @Column(name = "LyDoThanhLy", nullable = false, length = 255)
    private String lyDoThanhLy;

    @Column(name = "HinhThucXuLy", nullable = false, length = 100)
    private String hinhThucXuLy = "Tiêu hủy phế liệu";

    public ChiTietThanhLy() {}

    public ChiTietThanhLyId getId() { return id; }
    public void setId(ChiTietThanhLyId id) { this.id = id; }

    public PhieuThanhLy getPhieuThanhLy() { return phieuThanhLy; }
    public void setPhieuThanhLy(PhieuThanhLy phieuThanhLy) { this.phieuThanhLy = phieuThanhLy; }

    public TaiSan getTaiSan() { return taiSan; }
    public void setTaiSan(TaiSan taiSan) { this.taiSan = taiSan; }

    public BigDecimal getNguyenGia() { return nguyenGia; }
    public void setNguyenGia(BigDecimal nguyenGia) { this.nguyenGia = nguyenGia; }

    public BigDecimal getGiaTriConLai() { return giaTriConLai; }
    public void setGiaTriConLai(BigDecimal giaTriConLai) { this.giaTriConLai = giaTriConLai; }

    public BigDecimal getGiaTriThuHoi() { return giaTriThuHoi; }
    public void setGiaTriThuHoi(BigDecimal giaTriThuHoi) { this.giaTriThuHoi = giaTriThuHoi; }

    public String getLyDoThanhLy() { return lyDoThanhLy; }
    public void setLyDoThanhLy(String lyDoThanhLy) { this.lyDoThanhLy = lyDoThanhLy; }

    public String getHinhThucXuLy() { return hinhThucXuLy; }
    public void setHinhThucXuLy(String hinhThucXuLy) { this.hinhThucXuLy = hinhThucXuLy; }
}
