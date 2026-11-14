package com.hcmunre.qlts.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ChiTietBaoTriId implements Serializable {
    @Column(name = "SoPhieuBT", length = 30)
    private String soPhieuBT;

    @Column(name = "MaTaiSan", length = 30)
    private String maTaiSan;

    public ChiTietBaoTriId() {}

    public ChiTietBaoTriId(String soPhieuBT, String maTaiSan) {
        this.soPhieuBT = soPhieuBT;
        this.maTaiSan = maTaiSan;
    }

    public String getSoPhieuBT() { return soPhieuBT; }
    public void setSoPhieuBT(String soPhieuBT) { this.soPhieuBT = soPhieuBT; }

    public String getMaTaiSan() { return maTaiSan; }
    public void setMaTaiSan(String maTaiSan) { this.maTaiSan = maTaiSan; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChiTietBaoTriId that)) return false;
        return Objects.equals(soPhieuBT, that.soPhieuBT) && Objects.equals(maTaiSan, that.maTaiSan);
    }

    @Override
    public int hashCode() {
        return Objects.hash(soPhieuBT, maTaiSan);
    }
}
