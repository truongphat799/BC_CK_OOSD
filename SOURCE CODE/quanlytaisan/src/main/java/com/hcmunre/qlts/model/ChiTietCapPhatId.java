package com.hcmunre.qlts.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ChiTietCapPhatId implements Serializable {
    @Column(name = "SoPhieuCP", length = 30)
    private String soPhieuCP;

    @Column(name = "MaTaiSan", length = 30)
    private String maTaiSan;

    public ChiTietCapPhatId() {}

    public ChiTietCapPhatId(String soPhieuCP, String maTaiSan) {
        this.soPhieuCP = soPhieuCP;
        this.maTaiSan = maTaiSan;
    }

    public String getSoPhieuCP() { return soPhieuCP; }
    public void setSoPhieuCP(String soPhieuCP) { this.soPhieuCP = soPhieuCP; }

    public String getMaTaiSan() { return maTaiSan; }
    public void setMaTaiSan(String maTaiSan) { this.maTaiSan = maTaiSan; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChiTietCapPhatId that)) return false;
        return Objects.equals(soPhieuCP, that.soPhieuCP) && Objects.equals(maTaiSan, that.maTaiSan);
    }

    @Override
    public int hashCode() {
        return Objects.hash(soPhieuCP, maTaiSan);
    }
}
