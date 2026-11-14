package com.hcmunre.qlts.repository;

import com.hcmunre.qlts.model.YeuCauBaoTri;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YeuCauBaoTriRepository extends JpaRepository<YeuCauBaoTri, String> {
    List<YeuCauBaoTri> findAllByOrderByNgayGuiDesc();
    List<YeuCauBaoTri> findByTrangThai(String trangThai);
    boolean existsByTaiSan_MaTaiSan(String maTaiSan);
}
