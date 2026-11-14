package com.hcmunre.qlts.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "ChiTietBaoTri")
public class ChiTietBaoTri implements Serializable {
    @EmbeddedId
    private ChiTietBaoTriId id = new ChiTietBaoTriId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("soPhieuBT")
    @JoinColumn(name = "SoPhieuBT")
    private PhieuBaoTri phieuBaoTri;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("maTaiSan")
    @JoinColumn(name = "MaTaiSan")
    private TaiSan taiSan;

    @Column(name = "HienTrangHuHong", nullable = false, length = 255)
    private String hienTrangHuHong;

    @Column(name = "NoiDungSuaChua", nullable = false, length = 255)
    private String noiDungSuaChua;

    @Column(name = "ChiPhi", nullable = false, precision = 18, scale = 2)
    private BigDecimal chiPhi = BigDecimal.ZERO;

    @Column(name = "TinhTrangSauXuLy", nullable = false, length = 50)
    private String tinhTrangSauXuLy = "Hoạt động bình thường";

    public ChiTietBaoTri() {}

    public ChiTietBaoTriId getId() { return id; }
    public void setId(ChiTietBaoTriId id) { this.id = id; }

    public PhieuBaoTri getPhieuBaoTri() { return phieuBaoTri; }
    public void setPhieuBaoTri(PhieuBaoTri phieuBaoTri) { this.phieuBaoTri = phieuBaoTri; }

    public TaiSan getTaiSan() { return taiSan; }
    public void setTaiSan(TaiSan taiSan) { this.taiSan = taiSan; }

    public String getHienTrangHuHong() { return hienTrangHuHong; }
    public void setHienTrangHuHong(String hienTrangHuHong) { this.hienTrangHuHong = hienTrangHuHong; }

    public String getNoiDungSuaChua() { return noiDungSuaChua; }
    public void setNoiDungSuaChua(String noiDungSuaChua) { this.noiDungSuaChua = noiDungSuaChua; }

    public BigDecimal getChiPhi() { return chiPhi; }
    public void setChiPhi(BigDecimal chiPhi) { this.chiPhi = chiPhi; }

    public String getTinhTrangSauXuLy() { return tinhTrangSauXuLy; }
    public void setTinhTrangSauXuLy(String tinhTrangSauXuLy) { this.tinhTrangSauXuLy = tinhTrangSauXuLy; }
}
