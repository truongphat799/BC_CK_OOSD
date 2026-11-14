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
public class CapPhatService {
    @Autowired
    private PhieuCapPhatRepository phieuCapPhatRepository;

    @Autowired
    private ChiTietCapPhatRepository chiTietCapPhatRepository;

    @Autowired
    private TaiSanRepository taiSanRepository;

    @Autowired
    private ChiTietDieuChuyenRepository chiTietDieuChuyenRepository;

    @Autowired
    private ChiTietBaoTriRepository chiTietBaoTriRepository;

    @Autowired
    private ChiTietKiemKeRepository chiTietKiemKeRepository;

    @Autowired
    private ChiTietThanhLyRepository chiTietThanhLyRepository;

    @Autowired
    private YeuCauBaoTriRepository yeuCauBaoTriRepository;

    public List<PhieuCapPhat> findAll() {
        return phieuCapPhatRepository.findAllByOrderByNgayCapPhatDesc();
    }

    public Optional<PhieuCapPhat> findById(String id) {
        return phieuCapPhatRepository.findById(id);
    }

    public List<ChiTietCapPhat> getChiTietBySoPhieu(String soPhieuCP) {
        return chiTietCapPhatRepository.findById_SoPhieuCP(soPhieuCP);
    }

    @Transactional
    public PhieuCapPhat taoPhieuCapPhat(PhieuCapPhat phieu, List<ChiTietCapPhat> chiTietList) {
        if (phieu.getNgayCapPhat() == null) {
            phieu.setNgayCapPhat(LocalDate.now());
        }

        // Lưu phiếu cha trước
        PhieuCapPhat savedPhieu = phieuCapPhatRepository.save(phieu);

        for (ChiTietCapPhat ct : chiTietList) {
            TaiSan ts = taiSanRepository.findById(ct.getTaiSan().getMaTaiSan())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài sản: " + ct.getTaiSan().getMaTaiSan()));

            // Ràng buộc nghiệp vụ: Chỉ cấp phát tài sản đang trong kho
            if (!"Trong kho".equalsIgnoreCase(ts.getTinhTrang())) {
                throw new IllegalStateException("Tài sản [" + ts.getTenTaiSan() + "] không ở trạng thái 'Trong kho' (hiện tại: " + ts.getTinhTrang() + "), không thể cấp phát!");
            }

            // Cập nhật tình trạng tài sản và phòng ban mới
            ts.setTinhTrang("Đang sử dụng");
            ts.setPhongBan(savedPhieu.getPhongBanNhan());
            ts.setViTri(ct.getViTriMoi());
            taiSanRepository.save(ts);

            // Gắn khóa chính kết hợp và quan hệ
            ct.getId().setSoPhieuCP(savedPhieu.getSoPhieuCP());
            ct.getId().setMaTaiSan(ts.getMaTaiSan());
            ct.setPhieuCapPhat(savedPhieu);
            ct.setTaiSan(ts);
            chiTietCapPhatRepository.save(ct);
        }

        return savedPhieu;
    }

    @Transactional
    public PhieuCapPhat capNhatThongTinHanhChinh(String soPhieuCP, LocalDate ngayCapPhat, String nguoiNhanDaiDien, String lyDoCapPhat) {
        PhieuCapPhat phieu = phieuCapPhatRepository.findById(soPhieuCP)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phiếu cấp phát: " + soPhieuCP));
        if (ngayCapPhat != null) {
            phieu.setNgayCapPhat(ngayCapPhat);
        }
        if (nguoiNhanDaiDien != null && !nguoiNhanDaiDien.trim().isEmpty()) {
            phieu.setNguoiNhanDaiDien(nguoiNhanDaiDien.trim());
        }
        if (lyDoCapPhat != null) {
            phieu.setLyDoCapPhat(lyDoCapPhat.trim());
        }
        return phieuCapPhatRepository.save(phieu);
    }

    @Transactional
    public void xoaPhieuCapPhat(String soPhieuCP) {
        PhieuCapPhat phieu = phieuCapPhatRepository.findById(soPhieuCP)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phiếu cấp phát: " + soPhieuCP));

        List<ChiTietCapPhat> chiTietList = chiTietCapPhatRepository.findById_SoPhieuCP(soPhieuCP);

        // 1. Kiểm tra từng tài sản xem có phát sinh giao dịch sau đó chưa
        for (ChiTietCapPhat ct : chiTietList) {
            String maTS = ct.getTaiSan().getMaTaiSan();
            TaiSan ts = taiSanRepository.findById(maTS).orElse(null);
            if (ts != null) {
                // Kiểm tra điều chuyển
                if (chiTietDieuChuyenRepository.existsById_MaTaiSan(maTS)) {
                    throw new IllegalStateException("Tài sản [" + ts.getTenTaiSan() + " - " + maTS + "] đã phát sinh phiếu điều chuyển, không thể hủy phiếu cấp phát!");
                }
                // Kiểm tra bảo trì / sửa chữa
                if (chiTietBaoTriRepository.existsById_MaTaiSan(maTS) || yeuCauBaoTriRepository.existsByTaiSan_MaTaiSan(maTS)) {
                    throw new IllegalStateException("Tài sản [" + ts.getTenTaiSan() + " - " + maTS + "] đã phát sinh yêu cầu bảo trì / sửa chữa, không thể hủy phiếu cấp phát!");
                }
                // Kiểm tra kiểm kê
                if (chiTietKiemKeRepository.existsById_MaTaiSan(maTS)) {
                    throw new IllegalStateException("Tài sản [" + ts.getTenTaiSan() + " - " + maTS + "] đã được đưa vào đợt kiểm kê, không thể hủy phiếu cấp phát!");
                }
                // Kiểm tra thanh lý
                if (chiTietThanhLyRepository.existsById_MaTaiSan(maTS)) {
                    throw new IllegalStateException("Tài sản [" + ts.getTenTaiSan() + " - " + maTS + "] đã được đưa vào hồ sơ thanh lý, không thể hủy phiếu cấp phát!");
                }
            }
        }

        // 2. Rollback trạng thái toàn bộ tài sản về "Trong kho"
        for (ChiTietCapPhat ct : chiTietList) {
            TaiSan ts = taiSanRepository.findById(ct.getTaiSan().getMaTaiSan()).orElse(null);
            if (ts != null) {
                ts.setTinhTrang("Trong kho");
                ts.setPhongBan(null);
                ts.setViTri("Kho tài sản trung tâm");
                taiSanRepository.save(ts);
            }
        }

        // 3. Xóa chi tiết cấp phát và phiếu cấp phát
        chiTietCapPhatRepository.deleteAll(chiTietList);
        phieuCapPhatRepository.delete(phieu);
    }
}
