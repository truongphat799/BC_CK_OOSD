package com.hcmunre.qlts.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ChiTietThanhLyId implements Serializable {
    @Column(name = "SoPhieuTL", length = 30)
    private String soPhieuTL;

    @Column(name = "MaTaiSan", length = 30)
    private String maTaiSan;

    public ChiTietThanhLyId() {}

    public ChiTietThanhLyId(String soPhieuTL, String maTaiSan) {
        this.soPhieuTL = soPhieuTL;
        this.maTaiSan = maTaiSan;
    }

    public String getSoPhieuTL() { return soPhieuTL; }
    public void setSoPhieuTL(String soPhieuTL) { this.soPhieuTL = soPhieuTL; }

    public String getMaTaiSan() { return maTaiSan; }
    public void setMaTaiSan(String maTaiSan) { this.maTaiSan = maTaiSan; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChiTietThanhLyId that)) return false;
        return Objects.equals(soPhieuTL, that.soPhieuTL) && Objects.equals(maTaiSan, that.maTaiSan);
    }

    @Override
    public int hashCode() {
        return Objects.hash(soPhieuTL, maTaiSan);
    }
}
