package com.hcmunre.qlts.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "ChiTietKiemKe")
public class ChiTietKiemKe implements Serializable {
    @EmbeddedId
    private ChiTietKiemKeId id = new ChiTietKiemKeId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("soPhieuKK")
    @JoinColumn(name = "SoPhieuKK")
    private PhieuKiemKe phieuKiemKe;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("maTaiSan")
    @JoinColumn(name = "MaTaiSan")
    private TaiSan taiSan;

    @Column(name = "SoLuongHeThong", nullable = false)
    private Integer soLuongHeThong = 1;

    @Column(name = "SoLuongThucTe", nullable = false)
    private Integer soLuongThucTe = 1;

    @Column(name = "ChenhLech", insertable = false, updatable = false)
    private Integer chenhLech;

    @Column(name = "TinhTrangThucTe", nullable = false, length = 50)
    private String tinhTrangThucTe = "Tốt, đang sử dụng";

    @Column(name = "DeXuatXuLy", length = 255)
    private String deXuatXuLy;

    public ChiTietKiemKe() {}

    public ChiTietKiemKeId getId() { return id; }
    public void setId(ChiTietKiemKeId id) { this.id = id; }

    public PhieuKiemKe getPhieuKiemKe() { return phieuKiemKe; }
    public void setPhieuKiemKe(PhieuKiemKe phieuKiemKe) { this.phieuKiemKe = phieuKiemKe; }

    public TaiSan getTaiSan() { return taiSan; }
    public void setTaiSan(TaiSan taiSan) { this.taiSan = taiSan; }

    public Integer getSoLuongHeThong() { return soLuongHeThong; }
    public void setSoLuongHeThong(Integer soLuongHeThong) { this.soLuongHeThong = soLuongHeThong; }

    public Integer getSoLuongThucTe() { return soLuongThucTe; }
    public void setSoLuongThucTe(Integer soLuongThucTe) { this.soLuongThucTe = soLuongThucTe; }

    public Integer getChenhLech() {
        if (chenhLech != null) return chenhLech;
        if (soLuongThucTe != null && soLuongHeThong != null) {
            return soLuongThucTe - soLuongHeThong;
        }
        return 0;
    }
    public void setChenhLech(Integer chenhLech) { this.chenhLech = chenhLech; }

    public String getTinhTrangThucTe() { return tinhTrangThucTe; }
    public void setTinhTrangThucTe(String tinhTrangThucTe) { this.tinhTrangThucTe = tinhTrangThucTe; }

    public String getDeXuatXuLy() { return deXuatXuLy; }
    public void setDeXuatXuLy(String deXuatXuLy) { this.deXuatXuLy = deXuatXuLy; }
}
