package com.hcmunre.qlts.repository;

import com.hcmunre.qlts.model.ChiTietThanhLy;
import com.hcmunre.qlts.model.ChiTietThanhLyId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChiTietThanhLyRepository extends JpaRepository<ChiTietThanhLy, ChiTietThanhLyId> {
    List<ChiTietThanhLy> findById_SoPhieuTL(String soPhieuTL);
    boolean existsById_MaTaiSan(String maTaiSan);
}
