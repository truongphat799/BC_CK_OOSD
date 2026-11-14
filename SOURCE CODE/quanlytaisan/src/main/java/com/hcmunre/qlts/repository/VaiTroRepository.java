package com.hcmunre.qlts.repository;

import com.hcmunre.qlts.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VaiTroRepository extends JpaRepository<VaiTro, String> {}
