package com.hcmunre.qlts.service;

import com.hcmunre.qlts.model.*;
import com.hcmunre.qlts.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class KiemKeService {
    @Autowired
    private PhieuKiemKeRepository phieuKiemKeRepository;

    @Autowired
    private ChiTietKiemKeRepository chiTietKiemKeRepository;

    @Autowired
    private TaiSanRepository taiSanRepository;

    public List<PhieuKiemKe> findAll() {
        return phieuKiemKeRepository.findAllByOrderByNgayKiemKeDesc();
    }

    public Optional<PhieuKiemKe> findById(String id) {
        return phieuKiemKeRepository.findById(id);
    }

    public List<ChiTietKiemKe> getChiTietBySoPhieu(String soPhieuKK) {
        return chiTietKiemKeRepository.findById_SoPhieuKK(soPhieuKK);
    }

    @Transactional
    public PhieuKiemKe taoPhieuKiemKe(PhieuKiemKe phieu, List<ChiTietKiemKe> chiTietList) {
        if (phieu.getNgayKiemKe() == null) {
            phieu.setNgayKiemKe(LocalDate.now());
        }

        PhieuKiemKe savedPhieu = phieuKiemKeRepository.save(phieu);

        for (ChiTietKiemKe ct : chiTietList) {
            TaiSan ts = taiSanRepository.findById(ct.getTaiSan().getMaTaiSan())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài sản: " + ct.getTaiSan().getMaTaiSan()));

            ct.getId().setSoPhieuKK(savedPhieu.getSoPhieuKK());
            ct.getId().setMaTaiSan(ts.getMaTaiSan());
            ct.setPhieuKiemKe(savedPhieu);
            ct.setTaiSan(ts);
            // ChenhLech = SoLuongThucTe - SoLuongHeThong
            if (ct.getSoLuongThucTe() != null && ct.getSoLuongHeThong() != null) {
                ct.setChenhLech(ct.getSoLuongThucTe() - ct.getSoLuongHeThong());
            }
            chiTietKiemKeRepository.save(ct);
        }

        return savedPhieu;
    }

    @Transactional
    public PhieuKiemKe capNhatThongTin(String soPhieuKK, String dotKiemKe, LocalDate ngayKiemKe, String truongBanKiemKe, String ghiChuChung) {
        PhieuKiemKe phieu = phieuKiemKeRepository.findById(soPhieuKK)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phiếu kiểm kê: " + soPhieuKK));
        if (dotKiemKe != null && !dotKiemKe.trim().isEmpty()) {
            phieu.setDotKiemKe(dotKiemKe.trim());
        }
        if (ngayKiemKe != null) {
            phieu.setNgayKiemKe(ngayKiemKe);
        }
        if (truongBanKiemKe != null && !truongBanKiemKe.trim().isEmpty()) {
            phieu.setTruongBanKiemKe(truongBanKiemKe.trim());
        }
        phieu.setGhiChuChung(ghiChuChung);
        return phieuKiemKeRepository.save(phieu);
    }

    @Transactional
    public void xoaPhieuKiemKe(String soPhieuKK) {
        PhieuKiemKe phieu = phieuKiemKeRepository.findById(soPhieuKK)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phiếu kiểm kê: " + soPhieuKK));
        phieuKiemKeRepository.delete(phieu);
    }
}
