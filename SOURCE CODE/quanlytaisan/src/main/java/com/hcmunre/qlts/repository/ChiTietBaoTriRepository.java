package com.hcmunre.qlts.repository;

import com.hcmunre.qlts.model.ChiTietBaoTri;
import com.hcmunre.qlts.model.ChiTietBaoTriId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChiTietBaoTriRepository extends JpaRepository<ChiTietBaoTri, ChiTietBaoTriId> {
    List<ChiTietBaoTri> findById_SoPhieuBT(String soPhieuBT);
    boolean existsById_MaTaiSan(String maTaiSan);
}
