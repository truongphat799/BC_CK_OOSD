package com.hcmunre.qlts.controller;

import com.hcmunre.qlts.model.*;
import com.hcmunre.qlts.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/kiemke")
public class KiemKeController {
    @Autowired
    private KiemKeService kiemKeService;

    @Autowired
    private TaiSanService taiSanService;

    @Autowired
    private DanhMucService danhMucService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("listPhieu", kiemKeService.findAll());
        model.addAttribute("listPhongBan", danhMucService.getAllPhongBan());

        PhieuKiemKe phieuMoi = new PhieuKiemKe();
        phieuMoi.setSoPhieuKK("PKK" + System.currentTimeMillis() % 1000000);
        phieuMoi.setNgayKiemKe(LocalDate.now());
        phieuMoi.setDotKiemKe("Kiểm kê định kỳ Quý " + ((LocalDate.now().getMonthValue() - 1) / 3 + 1) + "/" + LocalDate.now().getYear());
        model.addAttribute("phieuMoi", phieuMoi);

        return "kiemke/list";
    }

    @GetMapping("/tao")
    public String taoForm(@RequestParam(required = false) String maPhongBan,
                          @RequestParam(required = false) String soPhieuKK,
                          @RequestParam(required = false) String dotKiemKe,
                          @RequestParam(required = false) String ngayKiemKe,
                          @RequestParam(required = false) String truongBanKiemKe,
                          @RequestParam(required = false) String ghiChuChung,
                          Model model) {
        PhieuKiemKe phieu = new PhieuKiemKe();
        phieu.setSoPhieuKK(soPhieuKK != null && !soPhieuKK.isEmpty() ? soPhieuKK : ("PKK" + System.currentTimeMillis() % 1000000));
        phieu.setNgayKiemKe(ngayKiemKe != null && !ngayKiemKe.isEmpty() ? LocalDate.parse(ngayKiemKe) : LocalDate.now());
        phieu.setDotKiemKe(dotKiemKe != null && !dotKiemKe.isEmpty() ? dotKiemKe : ("Kiểm kê định kỳ Quý " + ((LocalDate.now().getMonthValue() - 1) / 3 + 1) + "/" + LocalDate.now().getYear()));
        if (truongBanKiemKe != null && !truongBanKiemKe.isEmpty()) phieu.setTruongBanKiemKe(truongBanKiemKe);
        if (ghiChuChung != null && !ghiChuChung.isEmpty()) phieu.setGhiChuChung(ghiChuChung);

        model.addAttribute("phieu", phieu);
        model.addAttribute("listPhongBan", danhMucService.getAllPhongBan());

        List<TaiSan> assetsInDept = new ArrayList<>();
        if (maPhongBan != null && !maPhongBan.isEmpty()) {
            assetsInDept = taiSanService.findAll().stream()
                    .filter(t -> t.getPhongBan() != null && t.getPhongBan().getMaPhongBan().equals(maPhongBan))
                    .toList();
            danhMucService.getPhongBanById(maPhongBan).ifPresent(phieu::setPhongBan);
        }
        model.addAttribute("assetsInDept", assetsInDept);
        model.addAttribute("selectedMaPB", maPhongBan);

        return "kiemke/form";
    }

    @PostMapping("/luu")
    public String luuPhieu(@ModelAttribute PhieuKiemKe phieu,
                           @RequestParam(value = "maTaiSanList", required = false) List<String> maTaiSanList,
                           @RequestParam(value = "soLuongThucTeList", required = false) List<Integer> soLuongThucTeList,
                           @RequestParam(value = "tinhTrangList", required = false) List<String> tinhTrangList,
                           @RequestParam(value = "deXuatList", required = false) List<String> deXuatList,
                           HttpSession session,
                           RedirectAttributes ra) {
        try {
            NguoiDung currentUser = (NguoiDung) session.getAttribute("currentUser");
            phieu.setNvChuTri(currentUser);

            if (phieu.getPhongBan() != null && phieu.getPhongBan().getMaPhongBan() != null) {
                phieu.setPhongBan(danhMucService.getPhongBanById(phieu.getPhongBan().getMaPhongBan()).orElse(null));
            }

            if (maTaiSanList == null || maTaiSanList.isEmpty()) {
                ra.addFlashAttribute("errorMsg", "Không có tài sản nào để kiểm kê!");
                return "redirect:/kiemke/tao";
            }

            List<ChiTietKiemKe> chiTietList = new ArrayList<>();
            for (int i = 0; i < maTaiSanList.size(); i++) {
                String maTS = maTaiSanList.get(i);
                int slThucTe = (soLuongThucTeList != null && i < soLuongThucTeList.size()) ? soLuongThucTeList.get(i) : 1;
                String tinhTrang = (tinhTrangList != null && i < tinhTrangList.size()) ? tinhTrangList.get(i) : "Tốt, đang sử dụng";
                String deXuat = (deXuatList != null && i < deXuatList.size()) ? deXuatList.get(i) : "Tiếp tục sử dụng";

                ChiTietKiemKe ct = new ChiTietKiemKe();
                TaiSan ts = taiSanService.findById(maTS).orElse(null);
                ct.setTaiSan(ts);
                ct.setSoLuongHeThong(1);
                ct.setSoLuongThucTe(slThucTe);
                ct.setChenhLech(slThucTe - 1);
                ct.setTinhTrangThucTe(tinhTrang);
                ct.setDeXuatXuLy(deXuat);
                chiTietList.add(ct);
            }

            kiemKeService.taoPhieuKiemKe(phieu, chiTietList);
            ra.addFlashAttribute("successMsg", "Lập hồ sơ kiểm kê tài sản thành công! Chênh lệch số liệu đã được tự động tính toán.");
            return "redirect:/kiemke";
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", "Lỗi: " + e.getMessage());
            return "redirect:/kiemke/tao";
        }
    }

    @GetMapping("/xem/{id}")
    public String xemChiTiet(@PathVariable String id, Model model, RedirectAttributes ra) {
        Optional<PhieuKiemKe> pOpt = kiemKeService.findById(id);
        if (pOpt.isEmpty()) {
            ra.addFlashAttribute("errorMsg", "Không tìm thấy phiếu kiểm kê!");
            return "redirect:/kiemke";
        }
        model.addAttribute("phieu", pOpt.get());
        model.addAttribute("chiTietList", kiemKeService.getChiTietBySoPhieu(id));
        return "kiemke/detail";
    }

    @PostMapping("/capnhat")
    public String capNhatPhieu(@RequestParam String soPhieuKK,
                               @RequestParam String dotKiemKe,
                               @RequestParam String ngayKiemKe,
                               @RequestParam String truongBanKiemKe,
                               @RequestParam(required = false) String ghiChuChung,
                               RedirectAttributes ra) {
        try {
            LocalDate ngayKK = (ngayKiemKe != null && !ngayKiemKe.isEmpty()) ? LocalDate.parse(ngayKiemKe) : LocalDate.now();
            kiemKeService.capNhatThongTin(soPhieuKK, dotKiemKe, ngayKK, truongBanKiemKe, ghiChuChung);
            ra.addFlashAttribute("successMsg", "Cập nhật thông tin đợt kiểm kê " + soPhieuKK + " thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", "Lỗi khi cập nhật đợt kiểm kê: " + e.getMessage());
        }
        return "redirect:/kiemke";
    }

    @GetMapping("/xoa/{id}")
    public String xoaPhieu(@PathVariable String id, RedirectAttributes ra) {
        try {
            kiemKeService.xoaPhieuKiemKe(id);
            ra.addFlashAttribute("successMsg", "Đã xóa thành công đợt kiểm kê " + id + " cùng toàn bộ bảng đối soát!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", "Lỗi khi xóa đợt kiểm kê: " + e.getMessage());
        }
        return "redirect:/kiemke";
    }
}
