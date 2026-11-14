package com.hcmunre.qlts.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ChiTietDieuChuyenId implements Serializable {
    @Column(name = "SoPhieuDC", length = 30)
    private String soPhieuDC;

    @Column(name = "MaTaiSan", length = 30)
    private String maTaiSan;

    public ChiTietDieuChuyenId() {}

    public ChiTietDieuChuyenId(String soPhieuDC, String maTaiSan) {
        this.soPhieuDC = soPhieuDC;
        this.maTaiSan = maTaiSan;
    }

    public String getSoPhieuDC() { return soPhieuDC; }
    public void setSoPhieuDC(String soPhieuDC) { this.soPhieuDC = soPhieuDC; }

    public String getMaTaiSan() { return maTaiSan; }
    public void setMaTaiSan(String maTaiSan) { this.maTaiSan = maTaiSan; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChiTietDieuChuyenId that)) return false;
        return Objects.equals(soPhieuDC, that.soPhieuDC) && Objects.equals(maTaiSan, that.maTaiSan);
    }

    @Override
    public int hashCode() {
        return Objects.hash(soPhieuDC, maTaiSan);
    }
}
