package com.hcmunre.qlts.repository;

import com.hcmunre.qlts.model.PhieuBaoTri;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhieuBaoTriRepository extends JpaRepository<PhieuBaoTri, String> {
    List<PhieuBaoTri> findAllByOrderByNgayLapDesc();
    boolean existsByYeuCauBaoTri_MaYeuCau(String maYeuCau);
}
