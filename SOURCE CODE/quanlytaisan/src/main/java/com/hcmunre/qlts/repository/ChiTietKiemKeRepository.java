package com.hcmunre.qlts.repository;

import com.hcmunre.qlts.model.ChiTietKiemKe;
import com.hcmunre.qlts.model.ChiTietKiemKeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChiTietKiemKeRepository extends JpaRepository<ChiTietKiemKe, ChiTietKiemKeId> {
    List<ChiTietKiemKe> findById_SoPhieuKK(String soPhieuKK);
    boolean existsById_MaTaiSan(String maTaiSan);
}
