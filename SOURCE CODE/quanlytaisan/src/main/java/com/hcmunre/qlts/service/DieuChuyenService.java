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
public class DieuChuyenService {
    @Autowired
    private PhieuDieuChuyenRepository phieuDieuChuyenRepository;

    @Autowired
    private ChiTietDieuChuyenRepository chiTietDieuChuyenRepository;

    @Autowired
    private TaiSanRepository taiSanRepository;

    @Autowired
    private ChiTietBaoTriRepository chiTietBaoTriRepository;

    @Autowired
    private YeuCauBaoTriRepository yeuCauBaoTriRepository;

    @Autowired
    private ChiTietKiemKeRepository chiTietKiemKeRepository;

    @Autowired
    private ChiTietThanhLyRepository chiTietThanhLyRepository;

    public List<PhieuDieuChuyen> findAll() {
        return phieuDieuChuyenRepository.findAllByOrderByNgayDieuChuyenDesc();
    }

    public Optional<PhieuDieuChuyen> findById(String id) {
        return phieuDieuChuyenRepository.findById(id);
    }

    public List<ChiTietDieuChuyen> getChiTietBySoPhieu(String soPhieuDC) {
        return chiTietDieuChuyenRepository.findById_SoPhieuDC(soPhieuDC);
    }

    @Transactional
    public PhieuDieuChuyen taoPhieuDieuChuyen(PhieuDieuChuyen phieu, List<ChiTietDieuChuyen> chiTietList, PhongBan phongBanNhanMoi) {
        if (phieu.getNgayDieuChuyen() == null) {
            phieu.setNgayDieuChuyen(LocalDate.now());
        }

        PhieuDieuChuyen savedPhieu = phieuDieuChuyenRepository.save(phieu);

        for (ChiTietDieuChuyen ct : chiTietList) {
            TaiSan ts = taiSanRepository.findById(ct.getTaiSan().getMaTaiSan())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài sản: " + ct.getTaiSan().getMaTaiSan()));

            if (phongBanNhanMoi != null) {
                ts.setPhongBan(phongBanNhanMoi);
            }
            ts.setViTri(ct.getViTriMoi());
            taiSanRepository.save(ts);

            ct.getId().setSoPhieuDC(savedPhieu.getSoPhieuDC());
            ct.getId().setMaTaiSan(ts.getMaTaiSan());
            ct.setPhieuDieuChuyen(savedPhieu);
            ct.setTaiSan(ts);
            chiTietDieuChuyenRepository.save(ct);
        }

        return savedPhieu;
    }

    @Transactional
    public PhieuDieuChuyen capNhatThongTinHanhChinh(String soPhieuDC, LocalDate ngayDieuChuyen, String lyDoDieuChuyen, String ghiChu) {
        PhieuDieuChuyen phieu = phieuDieuChuyenRepository.findById(soPhieuDC)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phiếu điều chuyển: " + soPhieuDC));
        if (ngayDieuChuyen != null) {
            phieu.setNgayDieuChuyen(ngayDieuChuyen);
        }
        if (lyDoDieuChuyen != null && !lyDoDieuChuyen.trim().isEmpty()) {
            phieu.setLyDoDieuChuyen(lyDoDieuChuyen.trim());
        }
        if (ghiChu != null) {
            phieu.setGhiChu(ghiChu.trim());
        }
        return phieuDieuChuyenRepository.save(phieu);
    }

    @Transactional
    public void xoaPhieuDieuChuyen(String soPhieuDC) {
        PhieuDieuChuyen phieu = phieuDieuChuyenRepository.findById(soPhieuDC)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phiếu điều chuyển: " + soPhieuDC));

        List<ChiTietDieuChuyen> chiTietList = chiTietDieuChuyenRepository.findById_SoPhieuDC(soPhieuDC);

        // 1. Kiểm tra an toàn: từng tài sản có phát sinh nghiệp vụ tiếp theo không
        for (ChiTietDieuChuyen ct : chiTietList) {
            String maTS = ct.getTaiSan().getMaTaiSan();
            TaiSan ts = taiSanRepository.findById(maTS).orElse(null);
            if (ts != null) {
                // Kiểm tra xem tài sản có bị điều chuyển tiếp ở phiếu điều chuyển khác không
                if (chiTietDieuChuyenRepository.existsById_MaTaiSanAndId_SoPhieuDCNot(maTS, soPhieuDC)) {
                    throw new IllegalStateException("Tài sản [" + ts.getTenTaiSan() + " - " + maTS + "] đã phát sinh phiếu điều chuyển khác sau đó, không thể hủy phiếu này!");
                }
                // Kiểm tra bảo trì / sửa chữa
                if (chiTietBaoTriRepository.existsById_MaTaiSan(maTS) || yeuCauBaoTriRepository.existsByTaiSan_MaTaiSan(maTS)) {
                    throw new IllegalStateException("Tài sản [" + ts.getTenTaiSan() + " - " + maTS + "] đã phát sinh yêu cầu bảo trì / sửa chữa, không thể hủy phiếu điều chuyển!");
                }
                // Kiểm tra kiểm kê
                if (chiTietKiemKeRepository.existsById_MaTaiSan(maTS)) {
                    throw new IllegalStateException("Tài sản [" + ts.getTenTaiSan() + " - " + maTS + "] đã được đưa vào đợt kiểm kê, không thể hủy phiếu điều chuyển!");
                }
                // Kiểm tra thanh lý
                if (chiTietThanhLyRepository.existsById_MaTaiSan(maTS)) {
                    throw new IllegalStateException("Tài sản [" + ts.getTenTaiSan() + " - " + maTS + "] đã được đưa vào hồ sơ thanh lý, không thể hủy phiếu điều chuyển!");
                }
            }
        }

        // 2. Rollback trạng thái: hoàn trả tài sản về phòng ban chuyển ban đầu
        for (ChiTietDieuChuyen ct : chiTietList) {
            TaiSan ts = taiSanRepository.findById(ct.getTaiSan().getMaTaiSan()).orElse(null);
            if (ts != null) {
                ts.setPhongBan(phieu.getPhongBanChuyen());
                if (phieu.getPhongBanChuyen() != null) {
                    ts.setViTri("Khu vực " + phieu.getPhongBanChuyen().getTenPhongBan());
                } else {
                    ts.setViTri("Kho tài sản trung tâm");
                }
                taiSanRepository.save(ts);
            }
        }

        // 3. Xóa chi tiết và phiếu điều chuyển
        chiTietDieuChuyenRepository.deleteAll(chiTietList);
        phieuDieuChuyenRepository.delete(phieu);
    }
}
