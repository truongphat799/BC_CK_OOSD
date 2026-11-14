package com.hcmunre.qlts.repository;

import com.hcmunre.qlts.model.PhieuDenBu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhieuDenBuRepository extends JpaRepository<PhieuDenBu, String> {
    List<PhieuDenBu> findAllByOrderByHanChotBoiThuongDesc();
    boolean existsByPhieuBaoTri_SoPhieuBT(String soPhieuBT);
}
