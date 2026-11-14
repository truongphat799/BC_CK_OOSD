package com.hcmunre.qlts.service;

import com.hcmunre.qlts.model.TaiSan;
import com.hcmunre.qlts.repository.TaiSanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;

@Service
public class TaiSanService {
    @Autowired
    private TaiSanRepository taiSanRepository;

    public List<TaiSan> findAll() {
        return taiSanRepository.findAll();
    }

    public Optional<TaiSan> findById(String id) {
        return taiSanRepository.findById(id);
    }

    public List<TaiSan> findByTinhTrang(String tinhTrang) {
        return taiSanRepository.findByTinhTrang(tinhTrang);
    }

    public List<TaiSan> search(String kw, String maLoai, String maPB, String tinhTrang) {
        return taiSanRepository.searchTaiSan(kw, maLoai, maPB, tinhTrang);
    }

    @Transactional
    public TaiSan tiepNhanMoi(TaiSan ts) {
        // Khi tiếp nhận mới vào kho:
        // Giá trị còn lại = Nguyên giá
        ts.setGiaTriConLai(ts.getNguyenGia());
        if (ts.getNgayTiepNhan() == null) {
            ts.setNgayTiepNhan(LocalDate.now());
        }
        ts.setTinhTrang("Trong kho");
        return taiSanRepository.save(ts);
    }

    @Transactional
    public TaiSan save(TaiSan ts) {
        return taiSanRepository.save(ts);
    }

    @Transactional
    public void delete(String id) {
        taiSanRepository.deleteById(id);
    }

    // Tính hao mòn và giá trị còn lại theo tỷ lệ khấu hao hàng năm
    public BigDecimal tinhGiaTriHienTai(TaiSan ts) {
        if (ts.getLoaiTaiSan() == null || ts.getLoaiTaiSan().getTyLeKhauHao() == null) {
            return ts.getGiaTriConLai();
        }
        int soNam = Period.between(ts.getNgayTiepNhan(), LocalDate.now()).getYears();
        if (soNam <= 0) return ts.getNguyenGia();

        BigDecimal tyLe = ts.getLoaiTaiSan().getTyLeKhauHao().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        BigDecimal haoMonHangNam = ts.getNguyenGia().multiply(tyLe);
        BigDecimal tongHaoMon = haoMonHangNam.multiply(BigDecimal.valueOf(soNam));

        BigDecimal conLai = ts.getNguyenGia().subtract(tongHaoMon);
        return conLai.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : conLai;
    }
}
