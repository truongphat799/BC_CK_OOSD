package com.hcmunre.qlts.repository;

import com.hcmunre.qlts.model.ChiTietDieuChuyen;
import com.hcmunre.qlts.model.ChiTietDieuChuyenId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChiTietDieuChuyenRepository extends JpaRepository<ChiTietDieuChuyen, ChiTietDieuChuyenId> {
    List<ChiTietDieuChuyen> findById_SoPhieuDC(String soPhieuDC);
    boolean existsById_MaTaiSan(String maTaiSan);
    boolean existsById_MaTaiSanAndId_SoPhieuDCNot(String maTaiSan, String soPhieuDC);
}
