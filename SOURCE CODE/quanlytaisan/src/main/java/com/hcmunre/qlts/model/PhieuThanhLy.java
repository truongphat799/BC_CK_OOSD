package com.hcmunre.qlts.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "PhieuThanhLy")
public class PhieuThanhLy implements Serializable {
    @Id
    @Column(name = "SoPhieuTL", length = 30)
    private String soPhieuTL;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MaNVLap", nullable = false)
    private NguoiDung nvLap;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MaNVDuyet", nullable = false)
    private NguoiDung nvDuyet;

    @Column(name = "NgayThanhLy", nullable = false)
    private LocalDate ngayThanhLy;

    @Column(name = "PhuongThucThanhLy", nullable = false, length = 100)
    private String phuongThucThanhLy = "Bán đấu giá";

    @Column(name = "TongNguyenGia", nullable = false, precision = 18, scale = 2)
    private BigDecimal tongNguyenGia = BigDecimal.ZERO;

    @Column(name = "TongGiaTriThuHoi", nullable = false, precision = 18, scale = 2)
    private BigDecimal tongGiaTriThuHoi = BigDecimal.ZERO;

    @Column(name = "TrangThaiDuyet", nullable = false, length = 30)
    private String trangThaiDuyet = "Chờ phê duyệt";

    @OneToMany(mappedBy = "phieuThanhLy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChiTietThanhLy> chiTietList = new ArrayList<>();

    public PhieuThanhLy() {}

    public String getSoPhieuTL() { return soPhieuTL; }
    public void setSoPhieuTL(String soPhieuTL) { this.soPhieuTL = soPhieuTL; }

    public NguoiDung getNvLap() { return nvLap; }
    public void setNvLap(NguoiDung nvLap) { this.nvLap = nvLap; }

    public NguoiDung getNvDuyet() { return nvDuyet; }
    public void setNvDuyet(NguoiDung nvDuyet) { this.nvDuyet = nvDuyet; }

    public LocalDate getNgayThanhLy() { return ngayThanhLy; }
    public void setNgayThanhLy(LocalDate ngayThanhLy) { this.ngayThanhLy = ngayThanhLy; }

    public String getPhuongThucThanhLy() { return phuongThucThanhLy; }
    public void setPhuongThucThanhLy(String phuongThucThanhLy) { this.phuongThucThanhLy = phuongThucThanhLy; }

    public BigDecimal getTongNguyenGia() { return tongNguyenGia; }
    public void setTongNguyenGia(BigDecimal tongNguyenGia) { this.tongNguyenGia = tongNguyenGia; }

    public BigDecimal getTongGiaTriThuHoi() { return tongGiaTriThuHoi; }
    public void setTongGiaTriThuHoi(BigDecimal tongGiaTriThuHoi) { this.tongGiaTriThuHoi = tongGiaTriThuHoi; }

    public String getTrangThaiDuyet() { return trangThaiDuyet; }
    public void setTrangThaiDuyet(String trangThaiDuyet) { this.trangThaiDuyet = trangThaiDuyet; }

    public List<ChiTietThanhLy> getChiTietList() { return chiTietList; }
    public void setChiTietList(List<ChiTietThanhLy> chiTietList) { this.chiTietList = chiTietList; }
}
