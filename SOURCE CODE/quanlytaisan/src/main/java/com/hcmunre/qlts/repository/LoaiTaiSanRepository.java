package com.hcmunre.qlts.repository;

import com.hcmunre.qlts.model.LoaiTaiSan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoaiTaiSanRepository extends JpaRepository<LoaiTaiSan, String> {}
