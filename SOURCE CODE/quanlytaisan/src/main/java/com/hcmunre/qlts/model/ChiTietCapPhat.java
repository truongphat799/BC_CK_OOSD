package com.hcmunre.qlts.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "ChiTietCapPhat")
public class ChiTietCapPhat implements Serializable {
    @EmbeddedId
    private ChiTietCapPhatId id = new ChiTietCapPhatId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("soPhieuCP")
    @JoinColumn(name = "SoPhieuCP")
    private PhieuCapPhat phieuCapPhat;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("maTaiSan")
    @JoinColumn(name = "MaTaiSan")
    private TaiSan taiSan;

    @Column(name = "SoLuong", nullable = false)
    private Integer soLuong = 1;

    @Column(name = "TinhTrangBanGiao", nullable = false, length = 50)
    private String tinhTrangBanGiao = "Mới 100%";

    @Column(name = "ViTriMoi", nullable = false, length = 150)
    private String viTriMoi;

    public ChiTietCapPhat() {}

    public ChiTietCapPhatId getId() { return id; }
    public void setId(ChiTietCapPhatId id) { this.id = id; }

    public PhieuCapPhat getPhieuCapPhat() { return phieuCapPhat; }
    public void setPhieuCapPhat(PhieuCapPhat phieuCapPhat) { this.phieuCapPhat = phieuCapPhat; }

    public TaiSan getTaiSan() { return taiSan; }
    public void setTaiSan(TaiSan taiSan) { this.taiSan = taiSan; }

    public Integer getSoLuong() { return soLuong; }
    public void setSoLuong(Integer soLuong) { this.soLuong = soLuong; }

    public String getTinhTrangBanGiao() { return tinhTrangBanGiao; }
    public void setTinhTrangBanGiao(String tinhTrangBanGiao) { this.tinhTrangBanGiao = tinhTrangBanGiao; }

    public String getViTriMoi() { return viTriMoi; }
    public void setViTriMoi(String viTriMoi) { this.viTriMoi = viTriMoi; }
}
