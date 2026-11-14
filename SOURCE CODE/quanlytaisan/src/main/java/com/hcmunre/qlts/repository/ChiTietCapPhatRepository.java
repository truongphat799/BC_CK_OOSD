package com.hcmunre.qlts.repository;

import com.hcmunre.qlts.model.ChiTietCapPhat;
import com.hcmunre.qlts.model.ChiTietCapPhatId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChiTietCapPhatRepository extends JpaRepository<ChiTietCapPhat, ChiTietCapPhatId> {
    List<ChiTietCapPhat> findById_SoPhieuCP(String soPhieuCP);
}
