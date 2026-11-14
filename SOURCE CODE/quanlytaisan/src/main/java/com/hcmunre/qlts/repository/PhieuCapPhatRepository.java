package com.hcmunre.qlts.repository;

import com.hcmunre.qlts.model.PhieuCapPhat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhieuCapPhatRepository extends JpaRepository<PhieuCapPhat, String> {
    List<PhieuCapPhat> findAllByOrderByNgayCapPhatDesc();
}
