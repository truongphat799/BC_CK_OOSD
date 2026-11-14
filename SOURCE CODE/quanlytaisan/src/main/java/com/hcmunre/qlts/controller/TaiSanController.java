package com.hcmunre.qlts.controller;

import com.hcmunre.qlts.model.TaiSan;
import com.hcmunre.qlts.service.DanhMucService;
import com.hcmunre.qlts.service.TaiSanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/taisan")
public class TaiSanController {
    @Autowired
    private TaiSanService taiSanService;

    @Autowired
    private DanhMucService danhMucService;

    @GetMapping
    public String list(@RequestParam(required = false) String kw,
                       @RequestParam(required = false) String maLoai,
                       @RequestParam(required = false) String maPB,
                       @RequestParam(required = false) String tinhTrang,
                       Model model) {
        model.addAttribute("listTaiSan", taiSanService.search(kw, maLoai, maPB, tinhTrang));
        model.addAttribute("listLoai", danhMucService.getAllLoaiTaiSan());
        model.addAttribute("listPhongBan", danhMucService.getAllPhongBan());
        model.addAttribute("listNCC", danhMucService.getAllNhaCungCap());

        TaiSan tsMoi = new TaiSan();
        tsMoi.setNgayTiepNhan(java.time.LocalDate.now());
        tsMoi.setDonViTinh("Cái");
        tsMoi.setNamSanXuat(java.time.LocalDate.now().getYear());
        tsMoi.setTinhTrang("Trong kho");
        model.addAttribute("taiSanMoi", tsMoi);

        model.addAttribute("kw", kw);
        model.addAttribute("maLoai", maLoai);
        model.addAttribute("maPB", maPB);
        model.addAttribute("tinhTrang", tinhTrang);

        return "taisan/list";
    }

    @GetMapping("/them")
    public String themForm(Model model) {
        model.addAttribute("taiSan", new TaiSan());
        model.addAttribute("isEdit", false);
        model.addAttribute("listLoai", danhMucService.getAllLoaiTaiSan());
        model.addAttribute("listNCC", danhMucService.getAllNhaCungCap());
        model.addAttribute("listPhongBan", danhMucService.getAllPhongBan());
        return "taisan/form";
    }

    @PostMapping("/luu")
    public String luuTaiSan(@ModelAttribute TaiSan taiSan, RedirectAttributes ra) {
        try {
            if (taiSan.getLoaiTaiSan() != null && taiSan.getLoaiTaiSan().getMaLoai() != null && !taiSan.getLoaiTaiSan().getMaLoai().isEmpty()) {
                taiSan.setLoaiTaiSan(danhMucService.getLoaiTaiSanById(taiSan.getLoaiTaiSan().getMaLoai()).orElse(null));
            } else {
                taiSan.setLoaiTaiSan(null);
            }

            if (taiSan.getNhaCungCap() != null && taiSan.getNhaCungCap().getMaNCC() != null && !taiSan.getNhaCungCap().getMaNCC().isEmpty()) {
                taiSan.setNhaCungCap(danhMucService.getNhaCungCapById(taiSan.getNhaCungCap().getMaNCC()).orElse(null));
            } else {
                taiSan.setNhaCungCap(null);
            }

            if (taiSan.getPhongBan() != null && taiSan.getPhongBan().getMaPhongBan() != null && !taiSan.getPhongBan().getMaPhongBan().isEmpty()) {
                taiSan.setPhongBan(danhMucService.getPhongBanById(taiSan.getPhongBan().getMaPhongBan()).orElse(null));
            } else {
                taiSan.setPhongBan(null);
            }

            Optional<TaiSan> existingOpt = taiSanService.findById(taiSan.getMaTaiSan());
            if (existingOpt.isEmpty()) {
                // Tiếp nhận mới vào kho: giá trị còn lại bằng nguyên giá
                taiSanService.tiepNhanMoi(taiSan);
                ra.addFlashAttribute("successMsg", "Tiếp nhận tài sản mới vào kho thành công!");
            } else {
                // Cập nhật thông tin tài sản đã tồn tại
                TaiSan existing = existingOpt.get();
                existing.setTenTaiSan(taiSan.getTenTaiSan());
                existing.setLoaiTaiSan(taiSan.getLoaiTaiSan());
                existing.setNhaCungCap(taiSan.getNhaCungCap());
                existing.setPhongBan(taiSan.getPhongBan());
                existing.setNguyenGia(taiSan.getNguyenGia());
                existing.setDonViTinh(taiSan.getDonViTinh());
                existing.setSoHoaDon(taiSan.getSoHoaDon());
                existing.setNamSanXuat(taiSan.getNamSanXuat());
                if (taiSan.getNgayTiepNhan() != null) {
                    existing.setNgayTiepNhan(taiSan.getNgayTiepNhan());
                }
                if (taiSan.getTinhTrang() != null && !taiSan.getTinhTrang().isEmpty()) {
                    existing.setTinhTrang(taiSan.getTinhTrang());
                }
                existing.setViTri(taiSan.getViTri());

                // Tự động tính toán lại giá trị còn lại theo khấu hao, không để null
                existing.setGiaTriConLai(taiSanService.tinhGiaTriHienTai(existing));

                taiSanService.save(existing);
                ra.addFlashAttribute("successMsg", "Cập nhật thông tin tài sản [" + existing.getMaTaiSan() + "] thành công!");
            }
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", "Lỗi: " + e.getMessage());
            return "redirect:/taisan";
        }
        return "redirect:/taisan";
    }

    @GetMapping("/chitiet/{id}")
    public String chiTiet(@PathVariable String id, Model model, RedirectAttributes ra) {
        Optional<TaiSan> tsOpt = taiSanService.findById(id);
        if (tsOpt.isEmpty()) {
            ra.addFlashAttribute("errorMsg", "Không tìm thấy tài sản!");
            return "redirect:/taisan";
        }
        TaiSan ts = tsOpt.get();
        model.addAttribute("taiSan", ts);
        model.addAttribute("giaTriHaoMon", taiSanService.tinhGiaTriHienTai(ts));
        return "taisan/detail";
    }

    @GetMapping("/sua/{id}")
    public String suaForm(@PathVariable String id, Model model, RedirectAttributes ra) {
        Optional<TaiSan> tsOpt = taiSanService.findById(id);
        if (tsOpt.isEmpty()) {
            ra.addFlashAttribute("errorMsg", "Không tìm thấy tài sản!");
            return "redirect:/taisan";
        }
        model.addAttribute("taiSan", tsOpt.get());
        model.addAttribute("isEdit", true);
        model.addAttribute("listLoai", danhMucService.getAllLoaiTaiSan());
        model.addAttribute("listNCC", danhMucService.getAllNhaCungCap());
        model.addAttribute("listPhongBan", danhMucService.getAllPhongBan());
        return "taisan/form";
    }

    @GetMapping("/xoa/{id}")
    public String xoaTaiSan(@PathVariable String id, RedirectAttributes ra) {
        try {
            Optional<TaiSan> tsOpt = taiSanService.findById(id);
            if (tsOpt.isEmpty()) {
                ra.addFlashAttribute("errorMsg", "Không tìm thấy tài sản cần xóa!");
                return "redirect:/taisan";
            }
            TaiSan ts = tsOpt.get();

            // RÀNG BUỘC NGHIỆP VỤ: Chỉ được xóa khi tình trạng là "Trong kho"
            if (!"Trong kho".equalsIgnoreCase(ts.getTinhTrang())) {
                ra.addFlashAttribute("errorMsg", "Quy định nghiệp vụ: Chỉ được xóa tài sản khi đang ở trạng thái 'Trong kho'! Tài sản [" + ts.getMaTaiSan() + "] đang ở trạng thái '" + ts.getTinhTrang() + "' nên không được phép xóa.");
                return "redirect:/taisan";
            }

            taiSanService.delete(id);
            ra.addFlashAttribute("successMsg", "Xóa tài sản trong kho [" + ts.getMaTaiSan() + "] thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", "Không thể xóa tài sản vì đã phát sinh chứng từ liên quan trong hệ thống!");
        }
        return "redirect:/taisan";
    }
}
