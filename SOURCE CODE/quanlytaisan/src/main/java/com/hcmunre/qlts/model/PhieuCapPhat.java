package com.hcmunre.qlts.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "PhieuCapPhat")
public class PhieuCapPhat implements Serializable {
    @Id
    @Column(name = "SoPhieuCP", length = 30)
    private String soPhieuCP;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MaNVLap", nullable = false)
    private NguoiDung nvLap;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MaPhongBanNhan", nullable = false)
    private PhongBan phongBanNhan;

    @Column(name = "NgayCapPhat", nullable = false)
    private LocalDate ngayCapPhat;

    @Column(name = "LyDoCapPhat", nullable = false, length = 255)
    private String lyDoCapPhat;

    @Column(name = "NguoiNhanDaiDien", nullable = false, length = 120)
    private String nguoiNhanDaiDien;

    @Column(name = "GhiChu", length = 255)
    private String ghiChu;

    @OneToMany(mappedBy = "phieuCapPhat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChiTietCapPhat> chiTietList = new ArrayList<>();

    public PhieuCapPhat() {}

    public String getSoPhieuCP() { return soPhieuCP; }
    public void setSoPhieuCP(String soPhieuCP) { this.soPhieuCP = soPhieuCP; }

    public NguoiDung getNvLap() { return nvLap; }
    public void setNvLap(NguoiDung nvLap) { this.nvLap = nvLap; }

    public PhongBan getPhongBanNhan() { return phongBanNhan; }
    public void setPhongBanNhan(PhongBan phongBanNhan) { this.phongBanNhan = phongBanNhan; }

    public LocalDate getNgayCapPhat() { return ngayCapPhat; }
    public void setNgayCapPhat(LocalDate ngayCapPhat) { this.ngayCapPhat = ngayCapPhat; }

    public String getLyDoCapPhat() { return lyDoCapPhat; }
    public void setLyDoCapPhat(String lyDoCapPhat) { this.lyDoCapPhat = lyDoCapPhat; }

    public String getNguoiNhanDaiDien() { return nguoiNhanDaiDien; }
    public void setNguoiNhanDaiDien(String nguoiNhanDaiDien) { this.nguoiNhanDaiDien = nguoiNhanDaiDien; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }

    public List<ChiTietCapPhat> getChiTietList() { return chiTietList; }
    public void setChiTietList(List<ChiTietCapPhat> chiTietList) { this.chiTietList = chiTietList; }
}
