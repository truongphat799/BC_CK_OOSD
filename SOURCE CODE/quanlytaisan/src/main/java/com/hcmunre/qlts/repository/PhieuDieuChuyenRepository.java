package com.hcmunre.qlts.repository;

import com.hcmunre.qlts.model.PhieuDieuChuyen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhieuDieuChuyenRepository extends JpaRepository<PhieuDieuChuyen, String> {
    List<PhieuDieuChuyen> findAllByOrderByNgayDieuChuyenDesc();
}
