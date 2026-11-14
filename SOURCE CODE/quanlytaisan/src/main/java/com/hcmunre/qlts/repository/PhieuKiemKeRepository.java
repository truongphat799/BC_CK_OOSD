package com.hcmunre.qlts.repository;

import com.hcmunre.qlts.model.PhieuKiemKe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhieuKiemKeRepository extends JpaRepository<PhieuKiemKe, String> {
    List<PhieuKiemKe> findAllByOrderByNgayKiemKeDesc();
}
