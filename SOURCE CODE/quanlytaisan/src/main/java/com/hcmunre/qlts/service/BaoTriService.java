package com.hcmunre.qlts.service;

import com.hcmunre.qlts.model.*;
import com.hcmunre.qlts.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BaoTriService {
    @Autowired
    private YeuCauBaoTriRepository yeuCauBaoTriRepository;

    @Autowired
    private PhieuBaoTriRepository phieuBaoTriRepository;

    @Autowired
    private ChiTietBaoTriRepository chiTietBaoTriRepository;

    @Autowired
    private PhieuDenBuRepository phieuDenBuRepository;

    @Autowired
    private TaiSanRepository taiSanRepository;

    // YeuCauBaoTri
    public List<YeuCauBaoTri> getAllYeuCau() {
        return yeuCauBaoTriRepository.findAllByOrderByNgayGuiDesc();
    }

    public Optional<YeuCauBaoTri> getYeuCauById(String id) {
        return yeuCauBaoTriRepository.findById(id);
    }

    @Transactional
    public YeuCauBaoTri guiYeuCau(YeuCauBaoTri yc) {
        if (yc.getNgayGui() == null) {
            yc.setNgayGui(LocalDateTime.now());
        }
        yc.setTrangThai("Chờ tiếp nhận");
        return yeuCauBaoTriRepository.save(yc);
    }

    @Transactional
    public YeuCauBaoTri suaYeuCau(String maYeuCau, String mucDoUuTien, String moTaSuCo) {
        YeuCauBaoTri yc = yeuCauBaoTriRepository.findById(maYeuCau)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy yêu cầu: " + maYeuCau));
        if (!"Chờ tiếp nhận".equalsIgnoreCase(yc.getTrangThai())) {
            throw new IllegalStateException("Chỉ được chỉnh sửa khi yêu cầu đang ở trạng thái 'Chờ tiếp nhận'!");
        }
        if (mucDoUuTien != null && !mucDoUuTien.trim().isEmpty()) {
            yc.setMucDoUuTien(mucDoUuTien.trim());
        }
        if (moTaSuCo != null && !moTaSuCo.trim().isEmpty()) {
            yc.setMoTaSuCo(moTaSuCo.trim());
        }
        return yeuCauBaoTriRepository.save(yc);
    }

    @Transactional
    public void xoaYeuCau(String maYeuCau) {
        YeuCauBaoTri yc = yeuCauBaoTriRepository.findById(maYeuCau)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy yêu cầu: " + maYeuCau));
        if ("Đang xử lý".equalsIgnoreCase(yc.getTrangThai()) || "Hoàn thành".equalsIgnoreCase(yc.getTrangThai()) || "Đã tiếp nhận".equalsIgnoreCase(yc.getTrangThai())) {
            throw new IllegalStateException("Không thể xóa yêu cầu báo hỏng đã được tiếp nhận xử lý hoặc đã hoàn thành!");
        }
        if (phieuBaoTriRepository.existsByYeuCauBaoTri_MaYeuCau(maYeuCau)) {
            throw new IllegalStateException("Yêu cầu đã liên kết với phiếu bảo trì, không thể xóa!");
        }
        yeuCauBaoTriRepository.delete(yc);
    }

    @Transactional
    public YeuCauBaoTri tuChoiYeuCau(String maYeuCau) {
        YeuCauBaoTri yc = yeuCauBaoTriRepository.findById(maYeuCau)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy yêu cầu: " + maYeuCau));
        if (!"Chờ tiếp nhận".equalsIgnoreCase(yc.getTrangThai())) {
            throw new IllegalStateException("Chỉ có thể từ chối yêu cầu đang ở trạng thái 'Chờ tiếp nhận'!");
        }
        yc.setTrangThai("Hủy");
        return yeuCauBaoTriRepository.save(yc);
    }

    // PhieuBaoTri
    public List<PhieuBaoTri> getAllPhieuBaoTri() {
        return phieuBaoTriRepository.findAllByOrderByNgayLapDesc();
    }

    public Optional<PhieuBaoTri> getPhieuBaoTriById(String id) {
        return phieuBaoTriRepository.findById(id);
    }

    public List<ChiTietBaoTri> getChiTietBySoPhieuBT(String soPhieuBT) {
        return chiTietBaoTriRepository.findById_SoPhieuBT(soPhieuBT);
    }

    @Transactional
    public PhieuBaoTri taoPhieuBaoTri(PhieuBaoTri phieu, List<ChiTietBaoTri> chiTietList) {
        if (phieu.getNgayLap() == null) {
            phieu.setNgayLap(LocalDate.now());
        }

        BigDecimal tong = BigDecimal.ZERO;
        for (ChiTietBaoTri ct : chiTietList) {
            if (ct.getChiPhi() != null) {
                tong = tong.add(ct.getChiPhi());
            }
        }
        phieu.setTongChiPhi(tong);

        if (phieu.getYeuCauBaoTri() != null && phieu.getYeuCauBaoTri().getMaYeuCau() != null && !phieu.getYeuCauBaoTri().getMaYeuCau().isEmpty()) {
            YeuCauBaoTri yc = yeuCauBaoTriRepository.findById(phieu.getYeuCauBaoTri().getMaYeuCau()).orElse(null);
            if (yc != null) {
                yc.setTrangThai("Đang xử lý");
                yeuCauBaoTriRepository.save(yc);
                phieu.setYeuCauBaoTri(yc);
            } else {
                phieu.setYeuCauBaoTri(null);
            }
        } else {
            phieu.setYeuCauBaoTri(null);
        }

        PhieuBaoTri savedPhieu = phieuBaoTriRepository.save(phieu);

        for (ChiTietBaoTri ct : chiTietList) {
            TaiSan ts = taiSanRepository.findById(ct.getTaiSan().getMaTaiSan())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài sản: " + ct.getTaiSan().getMaTaiSan()));

            // Cập nhật tình trạng sang Đang bảo trì
            ts.setTinhTrang("Đang bảo trì");
            taiSanRepository.save(ts);

            ct.getId().setSoPhieuBT(savedPhieu.getSoPhieuBT());
            ct.getId().setMaTaiSan(ts.getMaTaiSan());
            ct.setPhieuBaoTri(savedPhieu);
            ct.setTaiSan(ts);
            chiTietBaoTriRepository.save(ct);
        }

        return savedPhieu;
    }

    @Transactional
    public PhieuBaoTri hoanThanhBaoTri(String soPhieuBT, String ketQuaXuLy) {
        PhieuBaoTri phieu = phieuBaoTriRepository.findById(soPhieuBT)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phiếu: " + soPhieuBT));

        phieu.setNgayHoanThanh(LocalDate.now());
        phieu.setKetQuaXuLy(ketQuaXuLy);

        List<ChiTietBaoTri> chiTietList = chiTietBaoTriRepository.findById_SoPhieuBT(soPhieuBT);
        BigDecimal tong = BigDecimal.ZERO;

        for (ChiTietBaoTri ct : chiTietList) {
            if (ct.getChiPhi() != null) tong = tong.add(ct.getChiPhi());
            TaiSan ts = ct.getTaiSan();
            if ("Đạt yêu cầu".equalsIgnoreCase(ketQuaXuLy)) {
                ts.setTinhTrang("Đang sử dụng");
            } else {
                ts.setTinhTrang("Chờ thanh lý");
            }
            taiSanRepository.save(ts);
        }
        phieu.setTongChiPhi(tong);

        if (phieu.getYeuCauBaoTri() != null) {
            phieu.getYeuCauBaoTri().setTrangThai("Hoàn thành");
            yeuCauBaoTriRepository.save(phieu.getYeuCauBaoTri());
        }

        return phieuBaoTriRepository.save(phieu);
    }

    @Transactional
    public PhieuBaoTri suaPhieuBaoTri(String soPhieuBT, LocalDate ngayLap, String donViSuaChua, BigDecimal chiPhi, String hienTrangHuHong, String noiDungSuaChua) {
        PhieuBaoTri phieu = phieuBaoTriRepository.findById(soPhieuBT)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phiếu bảo trì: " + soPhieuBT));
        if (!"Đang sửa chữa".equalsIgnoreCase(phieu.getKetQuaXuLy())) {
            throw new IllegalStateException("Chỉ được chỉnh sửa phiếu bảo trì khi đang ở trạng thái 'Đang sửa chữa'!");
        }
        if (ngayLap != null) {
            phieu.setNgayLap(ngayLap);
        }
        if (donViSuaChua != null && !donViSuaChua.trim().isEmpty()) {
            phieu.setDonViSuaChua(donViSuaChua.trim());
        }
        if (chiPhi != null) {
            phieu.setTongChiPhi(chiPhi);
        }

        List<ChiTietBaoTri> chiTietList = chiTietBaoTriRepository.findById_SoPhieuBT(soPhieuBT);
        for (ChiTietBaoTri ct : chiTietList) {
            if (hienTrangHuHong != null) ct.setHienTrangHuHong(hienTrangHuHong.trim());
            if (noiDungSuaChua != null) ct.setNoiDungSuaChua(noiDungSuaChua.trim());
            if (chiPhi != null) ct.setChiPhi(chiPhi);
            chiTietBaoTriRepository.save(ct);
        }

        return phieuBaoTriRepository.save(phieu);
    }

    @Transactional
    public void xoaPhieuBaoTri(String soPhieuBT) {
        PhieuBaoTri phieu = phieuBaoTriRepository.findById(soPhieuBT)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phiếu bảo trì: " + soPhieuBT));

        // 1. Kiểm tra an toàn: chỉ xóa khi Đang sửa chữa và chưa có phiếu đền bù
        if (!"Đang sửa chữa".equalsIgnoreCase(phieu.getKetQuaXuLy())) {
            throw new IllegalStateException("Không thể xóa phiếu bảo trì đã nghiệm thu hoàn thành!");
        }
        if (phieuDenBuRepository.existsByPhieuBaoTri_SoPhieuBT(soPhieuBT)) {
            throw new IllegalStateException("Không thể xóa phiếu bảo trì đã phát sinh phiếu đền bù!");
        }

        List<ChiTietBaoTri> chiTietList = chiTietBaoTriRepository.findById_SoPhieuBT(soPhieuBT);

        // 2. Hoàn tác trạng thái tài sản về "Đang sử dụng"
        for (ChiTietBaoTri ct : chiTietList) {
            TaiSan ts = ct.getTaiSan();
            if (ts != null) {
                ts.setTinhTrang("Đang sử dụng");
                taiSanRepository.save(ts);
            }
        }

        // 3. Hoàn tác trạng thái yêu cầu báo hỏng (nếu có) về "Chờ tiếp nhận"
        if (phieu.getYeuCauBaoTri() != null) {
            YeuCauBaoTri yc = phieu.getYeuCauBaoTri();
            yc.setTrangThai("Chờ tiếp nhận");
            yeuCauBaoTriRepository.save(yc);
        }

        // 4. Xóa chi tiết bảo trì và phiếu bảo trì
        chiTietBaoTriRepository.deleteAll(chiTietList);
        phieuBaoTriRepository.delete(phieu);
    }

    // PhieuDenBu
    public List<PhieuDenBu> getAllPhieuDenBu() {
        return phieuDenBuRepository.findAllByOrderByHanChotBoiThuongDesc();
    }

    @Transactional
    public PhieuDenBu taoPhieuDenBu(PhieuDenBu db) {
        return phieuDenBuRepository.save(db);
    }

    @Transactional
    public PhieuDenBu xacNhanNopTien(String soPhieuDB) {
        PhieuDenBu db = phieuDenBuRepository.findById(soPhieuDB)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phiếu đền bù: " + soPhieuDB));
        db.setTrangThai("Đã hoàn tất");
        return phieuDenBuRepository.save(db);
    }

    @Transactional
    public PhieuDenBu suaPhieuDenBu(String soPhieuDB, BigDecimal soTienDenBu, String hinhThucXuLy, LocalDate hanChotBoiThuong, String lyDoDenBu) {
        PhieuDenBu db = phieuDenBuRepository.findById(soPhieuDB)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy biên bản đền bù: " + soPhieuDB));
        if ("Đã hoàn tất".equalsIgnoreCase(db.getTrangThai())) {
            throw new IllegalStateException("Không thể chỉnh sửa biên bản đền bù đã hoàn tất nộp tiền!");
        }
        if (soTienDenBu != null) {
            db.setSoTienDenBu(soTienDenBu);
        }
        if (hinhThucXuLy != null && !hinhThucXuLy.trim().isEmpty()) {
            db.setHinhThucXuLy(hinhThucXuLy.trim());
        }
        if (hanChotBoiThuong != null) {
            db.setHanChotBoiThuong(hanChotBoiThuong);
        }
        if (lyDoDenBu != null && !lyDoDenBu.trim().isEmpty()) {
            db.setLyDoDenBu(lyDoDenBu.trim());
        }
        return phieuDenBuRepository.save(db);
    }

    @Transactional
    public void xoaPhieuDenBu(String soPhieuDB) {
        PhieuDenBu db = phieuDenBuRepository.findById(soPhieuDB)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy biên bản đền bù: " + soPhieuDB));
        if ("Đã hoàn tất".equalsIgnoreCase(db.getTrangThai())) {
            throw new IllegalStateException("Không thể xóa biên bản đền bù đã hoàn tất nộp tiền!");
        }
        phieuDenBuRepository.delete(db);
    }
}
