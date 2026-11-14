package com.hcmunre.qlts.repository;

import com.hcmunre.qlts.model.TaiSan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaiSanRepository extends JpaRepository<TaiSan, String> {
    List<TaiSan> findByTinhTrang(String tinhTrang);
    List<TaiSan> findByPhongBan_MaPhongBan(String maPhongBan);
    List<TaiSan> findByLoaiTaiSan_MaLoai(String maLoai);

    @Query("SELECT t FROM TaiSan t WHERE " +
           "(:kw IS NULL OR LOWER(t.tenTaiSan) LIKE LOWER(CONCAT('%', :kw, '%')) OR LOWER(t.maTaiSan) LIKE LOWER(CONCAT('%', :kw, '%')) OR LOWER(t.soHoaDon) LIKE LOWER(CONCAT('%', :kw, '%'))) AND " +
           "(:maLoai IS NULL OR :maLoai = '' OR t.loaiTaiSan.maLoai = :maLoai) AND " +
           "(:maPB IS NULL OR :maPB = '' OR (t.phongBan IS NOT NULL AND t.phongBan.maPhongBan = :maPB)) AND " +
           "(:tinhTrang IS NULL OR :tinhTrang = '' OR t.tinhTrang = :tinhTrang)")
    List<TaiSan> searchTaiSan(@Param("kw") String keyword, 
                              @Param("maLoai") String maLoai, 
                              @Param("maPB") String maPhongBan, 
                              @Param("tinhTrang") String tinhTrang);

    long countByTinhTrang(String tinhTrang);

    @Query("SELECT SUM(t.nguyenGia) FROM TaiSan t")
    Double sumNguyenGia();

    @Query("SELECT SUM(t.giaTriConLai) FROM TaiSan t")
    Double sumGiaTriConLai();
}
