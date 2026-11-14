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
@RequestMapping("/capphat")
public class CapPhatController {
    @Autowired
    private CapPhatService capPhatService;

    @Autowired
    private TaiSanService taiSanService;

    @Autowired
    private DanhMucService danhMucService;

    @GetMapping
    public String list(Model model) {
        List<PhieuCapPhat> listPhieu = capPhatService.findAll();
        for (PhieuCapPhat p : listPhieu) {
            p.setChiTietList(capPhatService.getChiTietBySoPhieu(p.getSoPhieuCP()));
        }
        model.addAttribute("listPhieu", listPhieu);

        PhieuCapPhat phieuMoi = new PhieuCapPhat();
        phieuMoi.setSoPhieuCP("PCP" + System.currentTimeMillis() % 1000000);
        phieuMoi.setNgayCapPhat(LocalDate.now());

        model.addAttribute("phieuMoi", phieuMoi);
        model.addAttribute("listPhongBan", danhMucService.getAllPhongBan());
        model.addAttribute("listTaiSanTrongKho", taiSanService.findByTinhTrang("Trong kho"));
        return "capphat/list";
    }

    @GetMapping("/tao")
    public String taoForm(Model model) {
        PhieuCapPhat phieu = new PhieuCapPhat();
        phieu.setSoPhieuCP("PCP" + System.currentTimeMillis() % 1000000);
        phieu.setNgayCapPhat(LocalDate.now());

        model.addAttribute("phieu", phieu);
        model.addAttribute("listPhongBan", danhMucService.getAllPhongBan());
        // Chỉ lấy các tài sản đang ở trong kho
        model.addAttribute("listTaiSanTrongKho", taiSanService.findByTinhTrang("Trong kho"));
        return "capphat/form";
    }

    @PostMapping("/luu")
    public String luuPhieu(@ModelAttribute PhieuCapPhat phieu,
                           @RequestParam(value = "maTaiSanList", required = false) List<String> maTaiSanList,
                           @RequestParam(value = "viTriMoi", required = false, defaultValue = "Phòng làm việc") String viTriMoi,
                           HttpSession session,
                           RedirectAttributes ra) {
        try {
            NguoiDung currentUser = (NguoiDung) session.getAttribute("currentUser");
            if (currentUser != null) {
                currentUser = danhMucService.getAllPhongBan().stream().findFirst().isPresent() ? currentUser : currentUser;
            }
            phieu.setNvLap(currentUser);

            if (phieu.getPhongBanNhan() != null && phieu.getPhongBanNhan().getMaPhongBan() != null) {
                phieu.setPhongBanNhan(danhMucService.getPhongBanById(phieu.getPhongBanNhan().getMaPhongBan()).orElse(null));
            }

            if (maTaiSanList == null || maTaiSanList.isEmpty()) {
                ra.addFlashAttribute("errorMsg", "Vui lòng chọn ít nhất 1 tài sản để cấp phát!");
                return "redirect:/capphat/tao";
            }

            List<ChiTietCapPhat> chiTietList = new ArrayList<>();
            for (String maTS : maTaiSanList) {
                ChiTietCapPhat ct = new ChiTietCapPhat();
                TaiSan ts = taiSanService.findById(maTS).orElse(null);
                ct.setTaiSan(ts);
                ct.setSoLuong(1);
                ct.setTinhTrangBanGiao("Mới 100%");
                ct.setViTriMoi(viTriMoi);
                chiTietList.add(ct);
            }

            capPhatService.taoPhieuCapPhat(phieu, chiTietList);
            ra.addFlashAttribute("successMsg", "Lập phiếu cấp phát thành công! Các tài sản đã được bàn giao sang trạng thái 'Đang sử dụng'.");
            return "redirect:/capphat";
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", "Lỗi: " + e.getMessage());
            return "redirect:/capphat/tao";
        }
    }

    @GetMapping("/xem/{id}")
    public String xemChiTiet(@PathVariable String id, Model model, RedirectAttributes ra) {
        Optional<PhieuCapPhat> pOpt = capPhatService.findById(id);
        if (pOpt.isEmpty()) {
            ra.addFlashAttribute("errorMsg", "Không tìm thấy phiếu cấp phát!");
            return "redirect:/capphat";
        }
        model.addAttribute("phieu", pOpt.get());
        model.addAttribute("chiTietList", capPhatService.getChiTietBySoPhieu(id));
        return "capphat/detail";
    }

    @PostMapping("/capnhat")
    public String capNhatPhieu(@RequestParam String soPhieuCP,
                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ngayCapPhat,
                               @RequestParam String nguoiNhanDaiDien,
                               @RequestParam String lyDoCapPhat,
                               RedirectAttributes ra) {
        try {
            capPhatService.capNhatThongTinHanhChinh(soPhieuCP, ngayCapPhat, nguoiNhanDaiDien, lyDoCapPhat);
            ra.addFlashAttribute("successMsg", "Cập nhật thông tin phiếu cấp phát [" + soPhieuCP + "] thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }
        return "redirect:/capphat";
    }

    @GetMapping("/xoa/{id}")
    public String xoaPhieu(@PathVariable String id, RedirectAttributes ra) {
        try {
            capPhatService.xoaPhieuCapPhat(id);
            ra.addFlashAttribute("successMsg", "Hủy phiếu cấp phát [" + id + "] thành công! Toàn bộ tài sản trong phiếu đã được hoàn trả về kho.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", "Không thể hủy phiếu cấp phát: " + e.getMessage());
        }
        return "redirect:/capphat";
    }
}
