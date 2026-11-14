package com.hcmunre.qlts.service;

import com.hcmunre.qlts.model.PhongBan;
import com.hcmunre.qlts.model.TaiSan;
import com.hcmunre.qlts.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class ThongKeService {
    @Autowired
    private TaiSanRepository taiSanRepository;

    @Autowired
    private PhongBanRepository phongBanRepository;

    @Autowired
    private PhieuBaoTriRepository phieuBaoTriRepository;

    @Autowired
    private PhieuCapPhatRepository phieuCapPhatRepository;

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        long tongSoTaiSan = taiSanRepository.count();
        long trongKho = taiSanRepository.countByTinhTrang("Trong kho");
        long dangSuDung = taiSanRepository.countByTinhTrang("Đang sử dụng");
        long dangBaoTri = taiSanRepository.countByTinhTrang("Đang bảo trì");
        long choThanhLy = taiSanRepository.countByTinhTrang("Chờ thanh lý");
        long daThanhLy = taiSanRepository.countByTinhTrang("Đã thanh lý");

        Double tongNguyenGia = taiSanRepository.sumNguyenGia();
        Double tongGiaTriConLai = taiSanRepository.sumGiaTriConLai();

        stats.put("tongSoTaiSan", tongSoTaiSan);
        stats.put("trongKho", trongKho);
        stats.put("dangSuDung", dangSuDung);
        stats.put("dangBaoTri", dangBaoTri);
        stats.put("choThanhLy", choThanhLy);
        stats.put("daThanhLy", daThanhLy);
        stats.put("tongNguyenGia", tongNguyenGia != null ? BigDecimal.valueOf(tongNguyenGia) : BigDecimal.ZERO);
        stats.put("tongGiaTriConLai", tongGiaTriConLai != null ? BigDecimal.valueOf(tongGiaTriConLai) : BigDecimal.ZERO);

        // Thống kê theo phòng ban
        List<PhongBan> phongBans = phongBanRepository.findAll();
        List<Map<String, Object>> pbStats = new ArrayList<>();
        for (PhongBan pb : phongBans) {
            List<TaiSan> tsList = taiSanRepository.findByPhongBan_MaPhongBan(pb.getMaPhongBan());
            BigDecimal tongGiaTri = tsList.stream()
                    .map(TaiSan::getNguyenGia)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Map<String, Object> item = new HashMap<>();
            item.put("tenPhongBan", pb.getTenPhongBan());
            item.put("soLuong", tsList.size());
            item.put("tongGiaTri", tongGiaTri);
            pbStats.add(item);
        }
        stats.put("phongBanStats", pbStats);

        return stats;
    }
}
