package com.hcmunre.qlts.controller;

import com.hcmunre.qlts.model.*;
import com.hcmunre.qlts.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/thanhly")
public class ThanhLyController {
    @Autowired
    private ThanhLyService thanhLyService;

    @Autowired
    private TaiSanService taiSanService;

    @Autowired
    private NguoiDungService nguoiDungService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("listPhieu", thanhLyService.findAll());
        model.addAttribute("listLanhDao", nguoiDungService.findAll());

        PhieuThanhLy phieuMoi = new PhieuThanhLy();
        phieuMoi.setSoPhieuTL("PTL" + System.currentTimeMillis() % 1000000);
        phieuMoi.setNgayThanhLy(LocalDate.now());
        model.addAttribute("phieuMoi", phieuMoi);

        List<TaiSan> listCanThanhLy = taiSanService.findByTinhTrang("Chờ thanh lý");
        if (listCanThanhLy.isEmpty()) {
            listCanThanhLy = taiSanService.findAll();
        }
        model.addAttribute("listTaiSanCanThanhLy", listCanThanhLy);

        return "thanhly/list";
    }

    @GetMapping("/tao")
    public String taoForm(Model model) {
        PhieuThanhLy phieu = new PhieuThanhLy();
        phieu.setSoPhieuTL("PTL" + System.currentTimeMillis() % 1000000);
        phieu.setNgayThanhLy(LocalDate.now());

        model.addAttribute("phieu", phieu);
        // Ưu tiên tài sản có trạng thái 'Chờ thanh lý' hoặc tất cả tài sản
        List<TaiSan> listCanThanhLy = taiSanService.findByTinhTrang("Chờ thanh lý");
        if (listCanThanhLy.isEmpty()) {
            listCanThanhLy = taiSanService.findAll();
        }
        model.addAttribute("listTaiSanCanThanhLy", listCanThanhLy);
        model.addAttribute("listLanhDao", nguoiDungService.findAll());
        return "thanhly/form";
    }

    @PostMapping("/luu")
    public String luuPhieu(@ModelAttribute PhieuThanhLy phieu,
                           @RequestParam String maTaiSan,
                           @RequestParam BigDecimal giaTriThuHoi,
                           @RequestParam String lyDoThanhLy,
                           @RequestParam String hinhThucXuLy,
                           HttpSession session,
                           RedirectAttributes ra) {
        try {
            NguoiDung currentUser = (NguoiDung) session.getAttribute("currentUser");
            phieu.setNvLap(currentUser);

            if (phieu.getNvDuyet() != null && phieu.getNvDuyet().getMaNguoiDung() != null && !phieu.getNvDuyet().getMaNguoiDung().isEmpty()) {
                phieu.setNvDuyet(nguoiDungService.findById(phieu.getNvDuyet().getMaNguoiDung()).orElse(currentUser));
            } else {
                phieu.setNvDuyet(currentUser);
            }

            TaiSan ts = taiSanService.findById(maTaiSan)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài sản: " + maTaiSan));

            ChiTietThanhLy ct = new ChiTietThanhLy();
            ct.setTaiSan(ts);
            ct.setNguyenGia(ts.getNguyenGia());
            ct.setGiaTriConLai(ts.getGiaTriConLai());
            ct.setGiaTriThuHoi(giaTriThuHoi);
            ct.setLyDoThanhLy(lyDoThanhLy);
            ct.setHinhThucXuLy(hinhThucXuLy);

            thanhLyService.taoPhieuThanhLy(phieu, Collections.singletonList(ct));
            ra.addFlashAttribute("successMsg", "Lập hồ sơ đề xuất thanh lý tài sản thành công! Hồ sơ đã gửi đến Ban Lãnh đạo phê duyệt.");
            return "redirect:/thanhly";
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", "Lỗi: " + e.getMessage());
            return "redirect:/thanhly";
        }
    }

    @PostMapping("/duyet/{id}")
    public String pheDuyet(@PathVariable String id,
                           @RequestParam boolean dongY,
                           HttpSession session,
                           RedirectAttributes ra) {
        try {
            NguoiDung currentUser = (NguoiDung) session.getAttribute("currentUser");
            thanhLyService.pheDuyetThanhLy(id, dongY, currentUser);
            if (dongY) {
                ra.addFlashAttribute("successMsg", "Phê duyệt thanh lý thành công! Tài sản đã được cập nhật sang trạng thái 'Đã thanh lý'.");
            } else {
                ra.addFlashAttribute("warningMsg", "Đã từ chối phê duyệt hồ sơ thanh lý!");
            }
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }
        return "redirect:/thanhly";
    }

    @GetMapping("/xem/{id}")
    public String xemChiTiet(@PathVariable String id, Model model, RedirectAttributes ra) {
        Optional<PhieuThanhLy> pOpt = thanhLyService.findById(id);
        if (pOpt.isEmpty()) {
            ra.addFlashAttribute("errorMsg", "Không tìm thấy hồ sơ thanh lý!");
            return "redirect:/thanhly";
        }
        model.addAttribute("phieu", pOpt.get());
        model.addAttribute("chiTietList", thanhLyService.getChiTietBySoPhieu(id));
        return "thanhly/detail";
    }

    @PostMapping("/capnhat")
    public String capNhatPhieu(@RequestParam String soPhieuTL,
                               @RequestParam String phuongThucThanhLy,
                               @RequestParam BigDecimal giaTriThuHoi,
                               @RequestParam String lyDoThanhLy,
                               @RequestParam String hinhThucXuLy,
                               @RequestParam(required = false) String maNVDuyet,
                               RedirectAttributes ra) {
        try {
            thanhLyService.capNhatPhieu(soPhieuTL, phuongThucThanhLy, giaTriThuHoi, lyDoThanhLy, hinhThucXuLy, maNVDuyet);
            ra.addFlashAttribute("successMsg", "Cập nhật hồ sơ đề xuất thanh lý " + soPhieuTL + " thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }
        return "redirect:/thanhly";
    }

    @GetMapping("/xoa/{id}")
    public String xoaPhieu(@PathVariable String id, RedirectAttributes ra) {
        try {
            thanhLyService.xoaPhieuThanhLy(id);
            ra.addFlashAttribute("successMsg", "Đã xóa thành công hồ sơ đề xuất thanh lý " + id + "!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", "Lỗi khi xóa hồ sơ thanh lý: " + e.getMessage());
        }
        return "redirect:/thanhly";
    }
}
