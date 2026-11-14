package com.hcmunre.qlts.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "VaiTro")
public class VaiTro implements Serializable {
    @Id
    @Column(name = "MaVaiTro", length = 20)
    private String maVaiTro;

    @Column(name = "TenVaiTro", nullable = false, length = 50)
    private String tenVaiTro;

    @Column(name = "MoTa", length = 255)
    private String moTa;

    public VaiTro() {}

    public VaiTro(String maVaiTro, String tenVaiTro, String moTa) {
        this.maVaiTro = maVaiTro;
        this.tenVaiTro = tenVaiTro;
        this.moTa = moTa;
    }

    public String getMaVaiTro() { return maVaiTro; }
    public void setMaVaiTro(String maVaiTro) { this.maVaiTro = maVaiTro; }

    public String getTenVaiTro() { return tenVaiTro; }
    public void setTenVaiTro(String tenVaiTro) { this.tenVaiTro = tenVaiTro; }

    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
}
