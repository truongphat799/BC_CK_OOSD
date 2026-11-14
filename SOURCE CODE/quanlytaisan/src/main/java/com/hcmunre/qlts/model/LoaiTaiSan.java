package com.hcmunre.qlts.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "LoaiTaiSan")
public class LoaiTaiSan implements Serializable {
    @Id
    @Column(name = "MaLoai", length = 20)
    private String maLoai;

    @Column(name = "TenLoai", nullable = false, length = 100)
    private String tenLoai;

    @Column(name = "TyLeKhauHao", nullable = false, precision = 5, scale = 2)
    private BigDecimal tyLeKhauHao;

    @Column(name = "ThoiGianSuDung", nullable = false)
    private Integer thoiGianSuDung;

    @Column(name = "GhiChu", length = 255)
    private String ghiChu;

    public LoaiTaiSan() {}

    public String getMaLoai() { return maLoai; }
    public void setMaLoai(String maLoai) { this.maLoai = maLoai; }

    public String getTenLoai() { return tenLoai; }
    public void setTenLoai(String tenLoai) { this.tenLoai = tenLoai; }

    public BigDecimal getTyLeKhauHao() { return tyLeKhauHao; }
    public void setTyLeKhauHao(BigDecimal tyLeKhauHao) { this.tyLeKhauHao = tyLeKhauHao; }

    public Integer getThoiGianSuDung() { return thoiGianSuDung; }
    public void setThoiGianSuDung(Integer thoiGianSuDung) { this.thoiGianSuDung = thoiGianSuDung; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
}
