package com.hcmunre.qlts.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ChiTietKiemKeId implements Serializable {
    @Column(name = "SoPhieuKK", length = 30)
    private String soPhieuKK;

    @Column(name = "MaTaiSan", length = 30)
    private String maTaiSan;

    public ChiTietKiemKeId() {}

    public ChiTietKiemKeId(String soPhieuKK, String maTaiSan) {
        this.soPhieuKK = soPhieuKK;
        this.maTaiSan = maTaiSan;
    }

    public String getSoPhieuKK() { return soPhieuKK; }
    public void setSoPhieuKK(String soPhieuKK) { this.soPhieuKK = soPhieuKK; }

    public String getMaTaiSan() { return maTaiSan; }
    public void setMaTaiSan(String maTaiSan) { this.maTaiSan = maTaiSan; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChiTietKiemKeId that)) return false;
        return Objects.equals(soPhieuKK, that.soPhieuKK) && Objects.equals(maTaiSan, that.maTaiSan);
    }

    @Override
    public int hashCode() {
        return Objects.hash(soPhieuKK, maTaiSan);
    }
}
