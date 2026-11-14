package com.hcmunre.qlts.repository;

import com.hcmunre.qlts.model.PhieuThanhLy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhieuThanhLyRepository extends JpaRepository<PhieuThanhLy, String> {
    List<PhieuThanhLy> findAllByOrderByNgayThanhLyDesc();
}
