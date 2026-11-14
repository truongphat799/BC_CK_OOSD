package com.hcmunre.qlts.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "YeuCauBaoTri")
public class YeuCauBaoTri implements Serializable {
    @Id
    @Column(name = "MaYeuCau", length = 30)
    private String maYeuCau;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MaTaiSan", nullable = false)
    private TaiSan taiSan;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MaNVGui", nullable = false)
    private NguoiDung nvGui;

    @Column(name = "NgayGui", nullable = false)
    private LocalDateTime ngayGui = LocalDateTime.now();

    @Column(name = "MoTaSuCo", nullable = false, length = 255)
    private String moTaSuCo;

    @Column(name = "MucDoUuTien", nullable = false, length = 30)
    private String mucDoUuTien = "Bình thường";

    @Column(name = "TrangThai", nullable = false, length = 30)
    private String trangThai = "Chờ tiếp nhận";

    public YeuCauBaoTri() {}

    public String getMaYeuCau() { return maYeuCau; }
    public void setMaYeuCau(String maYeuCau) { this.maYeuCau = maYeuCau; }

    public TaiSan getTaiSan() { return taiSan; }
    public void setTaiSan(TaiSan taiSan) { this.taiSan = taiSan; }

    public NguoiDung getNvGui() { return nvGui; }
    public void setNvGui(NguoiDung nvGui) { this.nvGui = nvGui; }

    public LocalDateTime getNgayGui() { return ngayGui; }
    public void setNgayGui(LocalDateTime ngayGui) { this.ngayGui = ngayGui; }

    public String getMoTaSuCo() { return moTaSuCo; }
    public void setMoTaSuCo(String moTaSuCo) { this.moTaSuCo = moTaSuCo; }

    public String getMucDoUuTien() { return mucDoUuTien; }
    public void setMucDoUuTien(String mucDoUuTien) { this.mucDoUuTien = mucDoUuTien; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
}
