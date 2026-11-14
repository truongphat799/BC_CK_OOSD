package com.hcmunre.qlts.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "PhongBan")
public class PhongBan implements Serializable {
    @Id
    @Column(name = "MaPhongBan", length = 20)
    private String maPhongBan;

    @Column(name = "TenPhongBan", nullable = false, length = 100)
    private String tenPhongBan;

    @Column(name = "DiaDiem", length = 150)
    private String diaDiem;

    @Column(name = "SDTLienHe", length = 20)
    private String sdtLienHe;

    public PhongBan() {}

    public PhongBan(String maPhongBan, String tenPhongBan, String diaDiem, String sdtLienHe) {
        this.maPhongBan = maPhongBan;
        this.tenPhongBan = tenPhongBan;
        this.diaDiem = diaDiem;
        this.sdtLienHe = sdtLienHe;
    }

    public String getMaPhongBan() { return maPhongBan; }
    public void setMaPhongBan(String maPhongBan) { this.maPhongBan = maPhongBan; }

    public String getTenPhongBan() { return tenPhongBan; }
    public void setTenPhongBan(String tenPhongBan) { this.tenPhongBan = tenPhongBan; }

    public String getDiaDiem() { return diaDiem; }
    public void setDiaDiem(String diaDiem) { this.diaDiem = diaDiem; }

    public String getSdtLienHe() { return sdtLienHe; }
    public void setSdtLienHe(String sdtLienHe) { this.sdtLienHe = sdtLienHe; }
}
