package com.hcmunre.qlts.service;

import com.hcmunre.qlts.model.*;
import com.hcmunre.qlts.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ThanhLyService {
    @Autowired
    private PhieuThanhLyRepository phieuThanhLyRepository;

    @Autowired
    private ChiTietThanhLyRepository chiTietThanhLyRepository;

    @Autowired
    private TaiSanRepository taiSanRepository;

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    public List<PhieuThanhLy> findAll() {
        return phieuThanhLyRepository.findAllByOrderByNgayThanhLyDesc();
    }

    public Optional<PhieuThanhLy> findById(String id) {
        return phieuThanhLyRepository.findById(id);
    }

    public List<ChiTietThanhLy> getChiTietBySoPhieu(String soPhieuTL) {
        return chiTietThanhLyRepository.findById_SoPhieuTL(soPhieuTL);
    }

    @Transactional
    public PhieuThanhLy taoPhieuThanhLy(PhieuThanhLy phieu, List<ChiTietThanhLy> chiTietList) {
        if (phieu.getNgayThanhLy() == null) {
            phieu.setNgayThanhLy(LocalDate.now());
        }

        BigDecimal tongNguyenGia = BigDecimal.ZERO;
        BigDecimal tongThuHoi = BigDecimal.ZERO;

        for (ChiTietThanhLy ct : chiTietList) {
            if (ct.getNguyenGia() != null) tongNguyenGia = tongNguyenGia.add(ct.getNguyenGia());
            if (ct.getGiaTriThuHoi() != null) tongThuHoi = tongThuHoi.add(ct.getGiaTriThuHoi());
        }
        phieu.setTongNguyenGia(tongNguyenGia);
        phieu.setTongGiaTriThuHoi(tongThuHoi);
        phieu.setTrangThaiDuyet("Chờ phê duyệt");

        PhieuThanhLy savedPhieu = phieuThanhLyRepository.save(phieu);

        for (ChiTietThanhLy ct : chiTietList) {
            TaiSan ts = taiSanRepository.findById(ct.getTaiSan().getMaTaiSan())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài sản: " + ct.getTaiSan().getMaTaiSan()));

            ct.getId().setSoPhieuTL(savedPhieu.getSoPhieuTL());
            ct.getId().setMaTaiSan(ts.getMaTaiSan());
            ct.setPhieuThanhLy(savedPhieu);
            ct.setTaiSan(ts);
            chiTietThanhLyRepository.save(ct);
        }

        return savedPhieu;
    }

    @Transactional
    public PhieuThanhLy pheDuyetThanhLy(String soPhieuTL, boolean dongY, NguoiDung nvDuyet) {
        PhieuThanhLy phieu = phieuThanhLyRepository.findById(soPhieuTL)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phiếu: " + soPhieuTL));

        if (dongY) {
            phieu.setTrangThaiDuyet("Đã phê duyệt");
            phieu.setNvDuyet(nvDuyet);

            // Cập nhật tình trạng các tài sản sang 'Đã thanh lý' và giá trị còn lại = 0
            List<ChiTietThanhLy> list = chiTietThanhLyRepository.findById_SoPhieuTL(soPhieuTL);
            for (ChiTietThanhLy ct : list) {
                TaiSan ts = ct.getTaiSan();
                ts.setTinhTrang("Đã thanh lý");
                ts.setGiaTriConLai(BigDecimal.ZERO);
                taiSanRepository.save(ts);
            }
        } else {
            phieu.setTrangThaiDuyet("Bị từ chối");
        }

        return phieuThanhLyRepository.save(phieu);
    }

    @Transactional
    public PhieuThanhLy capNhatPhieu(String soPhieuTL, String phuongThucThanhLy, BigDecimal giaTriThuHoi, 
                                     String lyDoThanhLy, String hinhThucXuLy, String maNVDuyet) {
        PhieuThanhLy phieu = phieuThanhLyRepository.findById(soPhieuTL)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy hồ sơ thanh lý: " + soPhieuTL));

        if (!"Chờ phê duyệt".equals(phieu.getTrangThaiDuyet())) {
            throw new IllegalStateException("Chỉ được phép chỉnh sửa hồ sơ thanh lý khi đang ở trạng thái 'Chờ phê duyệt'!");
        }

        if (phuongThucThanhLy != null && !phuongThucThanhLy.trim().isEmpty()) {
            phieu.setPhuongThucThanhLy(phuongThucThanhLy.trim());
        }
        if (giaTriThuHoi != null) {
            phieu.setTongGiaTriThuHoi(giaTriThuHoi);
        }
        if (maNVDuyet != null && !maNVDuyet.isEmpty()) {
            NguoiDung nvDuyet = nguoiDungRepository.findById(maNVDuyet).orElse(phieu.getNvDuyet());
            phieu.setNvDuyet(nvDuyet);
        }

        List<ChiTietThanhLy> chiTietList = chiTietThanhLyRepository.findById_SoPhieuTL(soPhieuTL);
        for (ChiTietThanhLy ct : chiTietList) {
            if (giaTriThuHoi != null) ct.setGiaTriThuHoi(giaTriThuHoi);
            if (lyDoThanhLy != null) ct.setLyDoThanhLy(lyDoThanhLy.trim());
            if (hinhThucXuLy != null) ct.setHinhThucXuLy(hinhThucXuLy.trim());
            chiTietThanhLyRepository.save(ct);
        }

        return phieuThanhLyRepository.save(phieu);
    }

    @Transactional
    public void xoaPhieuThanhLy(String soPhieuTL) {
        PhieuThanhLy phieu = phieuThanhLyRepository.findById(soPhieuTL)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy hồ sơ thanh lý: " + soPhieuTL));

        if ("Đã phê duyệt".equals(phieu.getTrangThaiDuyet())) {
            throw new IllegalStateException("Không thể xóa hồ sơ thanh lý đã được Ban Lãnh đạo phê duyệt chính thức!");
        }

        phieuThanhLyRepository.delete(phieu);
    }
}
