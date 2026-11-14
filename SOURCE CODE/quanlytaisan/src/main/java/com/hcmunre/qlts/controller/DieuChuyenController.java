package com.hcmunre.qlts.controller;

import com.hcmunre.qlts.model.*;
import com.hcmunre.qlts.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/dieuchuyen")
public class DieuChuyenController {
    @Autowired
    private DieuChuyenService dieuChuyenService;

    @Autowired
    private TaiSanService taiSanService;

    @Autowired
    private DanhMucService danhMucService;

    @GetMapping
    public String list(Model model) {
        List<PhieuDieuChuyen> listPhieu = dieuChuyenService.findAll();
        for (PhieuDieuChuyen p : listPhieu) {
            p.setChiTietList(dieuChuyenService.getChiTietBySoPhieu(p.getSoPhieuDC()));
        }
        model.addAttribute("listPhieu", listPhieu);

        PhieuDieuChuyen phieuMoi = new PhieuDieuChuyen();
        phieuMoi.setSoPhieuDC("PDC" + System.currentTimeMillis() % 1000000);
        phieuMoi.setNgayDieuChuyen(LocalDate.now());

        model.addAttribute("phieuMoi", phieuMoi);
        model.addAttribute("listPhongBan", danhMucService.getAllPhongBan());
        model.addAttribute("listTaiSanDangDung", taiSanService.findByTinhTrang("Đang sử dụng"));
        return "dieuchuyen/list";
    }

    @GetMapping("/tao")
    public String taoForm(Model model) {
        PhieuDieuChuyen phieu = new PhieuDieuChuyen();
        phieu.setSoPhieuDC("PDC" + System.currentTimeMillis() % 1000000);
        phieu.setNgayDieuChuyen(LocalDate.now());

        model.addAttribute("phieu", phieu);
        model.addAttribute("listPhongBan", danhMucService.getAllPhongBan());
        model.addAttribute("listTaiSanDangDung", taiSanService.findByTinhTrang("Đang sử dụng"));
        return "dieuchuyen/form";
    }

    @PostMapping("/luu")
    public String luuPhieu(@ModelAttribute PhieuDieuChuyen phieu,
                           @RequestParam(value = "maPhongBanNhanMoi", required = false) String maPhongBanNhanMoi,
                           @RequestParam(value = "maTaiSanList", required = false) List<String> maTaiSanList,
                           @RequestParam(value = "viTriMoi", required = false, defaultValue = "Vị trí mới") String viTriMoi,
                           HttpSession session,
                           RedirectAttributes ra) {
        try {
            NguoiDung currentUser = (NguoiDung) session.getAttribute("currentUser");
            phieu.setNvLap(currentUser);

            if (maTaiSanList == null || maTaiSanList.isEmpty()) {
                ra.addFlashAttribute("errorMsg", "Vui lòng chọn ít nhất 1 tài sản để điều chuyển!");
                return "redirect:/dieuchuyen/tao";
            }

            PhongBan pbNhan = null;
            if (maPhongBanNhanMoi != null && !maPhongBanNhanMoi.isEmpty()) {
                pbNhan = danhMucService.getPhongBanById(maPhongBanNhanMoi).orElse(null);
            }

            List<ChiTietDieuChuyen> chiTietList = new ArrayList<>();
            for (String maTS : maTaiSanList) {
                ChiTietDieuChuyen ct = new ChiTietDieuChuyen();
                TaiSan ts = taiSanService.findById(maTS).orElse(null);
                ct.setTaiSan(ts);
                ct.setSoLuong(1);
                ct.setTinhTrangKhiChuyen(ts != null ? ts.getTinhTrang() : "Bình thường");
                ct.setViTriMoi(viTriMoi);
                chiTietList.add(ct);
            }

            if (phieu.getPhongBanChuyen() != null && phieu.getPhongBanChuyen().getMaPhongBan() != null) {
                phieu.setPhongBanChuyen(danhMucService.getPhongBanById(phieu.getPhongBanChuyen().getMaPhongBan()).orElse(null));
            }

            dieuChuyenService.taoPhieuDieuChuyen(phieu, chiTietList, pbNhan);
            ra.addFlashAttribute("successMsg", "Lập phiếu điều chuyển thành công!");
            return "redirect:/dieuchuyen";
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", "Lỗi: " + e.getMessage());
            return "redirect:/dieuchuyen/tao";
        }
    }

    @GetMapping("/xem/{id}")
    public String xemChiTiet(@PathVariable String id, Model model, RedirectAttributes ra) {
        Optional<PhieuDieuChuyen> pOpt = dieuChuyenService.findById(id);
        if (pOpt.isEmpty()) {
            ra.addFlashAttribute("errorMsg", "Không tìm thấy phiếu điều chuyển!");
            return "redirect:/dieuchuyen";
        }
        model.addAttribute("phieu", pOpt.get());
        model.addAttribute("chiTietList", dieuChuyenService.getChiTietBySoPhieu(id));
        return "dieuchuyen/detail";
    }

    @PostMapping("/capnhat")
    public String capNhatPhieu(@RequestParam String soPhieuDC,
                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ngayDieuChuyen,
                               @RequestParam String lyDoDieuChuyen,
                               @RequestParam(required = false) String ghiChu,
                               RedirectAttributes ra) {
        try {
            dieuChuyenService.capNhatThongTinHanhChinh(soPhieuDC, ngayDieuChuyen, lyDoDieuChuyen, ghiChu);
            ra.addFlashAttribute("successMsg", "Cập nhật thông tin phiếu điều chuyển [" + soPhieuDC + "] thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }
        return "redirect:/dieuchuyen";
    }

    @GetMapping("/xoa/{id}")
    public String xoaPhieu(@PathVariable String id, RedirectAttributes ra) {
        try {
            dieuChuyenService.xoaPhieuDieuChuyen(id);
            ra.addFlashAttribute("successMsg", "Hủy phiếu điều chuyển [" + id + "] thành công! Toàn bộ tài sản đã được hoàn tác về phòng ban ban đầu.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", "Không thể hủy phiếu điều chuyển: " + e.getMessage());
        }
        return "redirect:/dieuchuyen";
    }
}
