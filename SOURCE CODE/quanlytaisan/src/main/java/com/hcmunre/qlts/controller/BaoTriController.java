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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/baotri")
public class BaoTriController {
    @Autowired
    private BaoTriService baoTriService;

    @Autowired
    private TaiSanService taiSanService;

    @Autowired
    private NguoiDungService nguoiDungService;

    @Autowired
    private DanhMucService danhMucService;

    // 1. Yêu cầu báo hỏng / sự cố
    @GetMapping("/yeucau")
    public String yeuCauList(Model model) {
        model.addAttribute("listYeuCau", baoTriService.getAllYeuCau());
        model.addAttribute("listTaiSan", taiSanService.findAll());
        model.addAttribute("yeuCauMoi", new YeuCauBaoTri());
        return "baotri/yeucau";
    }

    @PostMapping("/yeucau/gui")
    public String guiYeuCau(@ModelAttribute YeuCauBaoTri yc, HttpSession session, RedirectAttributes ra) {
        try {
            NguoiDung currentUser = (NguoiDung) session.getAttribute("currentUser");
            yc.setNvGui(currentUser);
            yc.setMaYeuCau("YC" + System.currentTimeMillis() % 1000000);
            baoTriService.guiYeuCau(yc);
            ra.addFlashAttribute("successMsg", "Gửi yêu cầu báo hỏng tài sản thành công! Phòng kỹ thuật sẽ tiếp nhận xử lý.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }
        return "redirect:/baotri/yeucau";
    }

    @PostMapping("/yeucau/sua")
    public String suaYeuCau(@RequestParam String maYeuCau,
                            @RequestParam String mucDoUuTien,
                            @RequestParam String moTaSuCo,
                            RedirectAttributes ra) {
        try {
            baoTriService.suaYeuCau(maYeuCau, mucDoUuTien, moTaSuCo);
            ra.addFlashAttribute("successMsg", "Cập nhật yêu cầu báo hỏng [" + maYeuCau + "] thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }
        return "redirect:/baotri/yeucau";
    }

    @GetMapping("/yeucau/xoa/{id}")
    public String xoaYeuCau(@PathVariable String id, RedirectAttributes ra) {
        try {
            baoTriService.xoaYeuCau(id);
            ra.addFlashAttribute("successMsg", "Xóa yêu cầu báo hỏng [" + id + "] thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", "Không thể xóa yêu cầu: " + e.getMessage());
        }
        return "redirect:/baotri/yeucau";
    }

    @GetMapping("/yeucau/tuchoi/{id}")
    public String tuChoiYeuCau(@PathVariable String id, HttpSession session, RedirectAttributes ra) {
        try {
            NguoiDung currentUser = (NguoiDung) session.getAttribute("currentUser");
            if (currentUser == null || currentUser.getVaiTro() == null 
                    || (!"VT_ADMIN".equals(currentUser.getVaiTro().getMaVaiTro()) && !"VT_QLTS".equals(currentUser.getVaiTro().getMaVaiTro()))) {
                ra.addFlashAttribute("errorMsg", "Bạn không có quyền từ chối yêu cầu báo hỏng!");
                return "redirect:/baotri/yeucau";
            }
            baoTriService.tuChoiYeuCau(id);
            ra.addFlashAttribute("successMsg", "Đã từ chối tiếp nhận yêu cầu báo hỏng [" + id + "]!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }
        return "redirect:/baotri/yeucau";
    }

    // 2. Phiếu bảo trì, sửa chữa
    @GetMapping("/phieu")
    public String phieuList(@RequestParam(required = false) String maYeuCau, Model model) {
        model.addAttribute("listPhieu", baoTriService.getAllPhieuBaoTri());

        PhieuBaoTri phieuMoi = new PhieuBaoTri();
        phieuMoi.setSoPhieuBT("PBT" + System.currentTimeMillis() % 1000000);
        phieuMoi.setNgayLap(LocalDate.now());

        String defaultMaTaiSan = "";
        String defaultHienTrang = "";

        if (maYeuCau != null && !maYeuCau.isEmpty()) {
            Optional<YeuCauBaoTri> ycOpt = baoTriService.getYeuCauById(maYeuCau);
            if (ycOpt.isPresent()) {
                YeuCauBaoTri yc = ycOpt.get();
                phieuMoi.setYeuCauBaoTri(yc);
                if (yc.getTaiSan() != null) {
                    defaultMaTaiSan = yc.getTaiSan().getMaTaiSan();
                }
                defaultHienTrang = yc.getMoTaSuCo() != null ? yc.getMoTaSuCo() : "";
            }
            model.addAttribute("openModal", true);
        }

        model.addAttribute("phieuMoi", phieuMoi);
        model.addAttribute("defaultMaTaiSan", defaultMaTaiSan);
        model.addAttribute("defaultHienTrang", defaultHienTrang);

        List<YeuCauBaoTri> listYeuCauCho = new ArrayList<>(baoTriService.getAllYeuCau().stream()
                .filter(y -> !"Hoàn thành".equals(y.getTrangThai()))
                .toList());
        if (phieuMoi.getYeuCauBaoTri() != null && listYeuCauCho.stream().noneMatch(y -> y.getMaYeuCau().equals(phieuMoi.getYeuCauBaoTri().getMaYeuCau()))) {
            listYeuCauCho.add(0, phieuMoi.getYeuCauBaoTri());
        }
        model.addAttribute("listYeuCauCho", listYeuCauCho);
        model.addAttribute("listTaiSan", taiSanService.findAll());

        return "baotri/phieu_list";
    }

    @GetMapping("/phieu/tao")
    public String taoPhieuForm(@RequestParam(required = false) String maYeuCau) {
        if (maYeuCau != null && !maYeuCau.isEmpty()) {
            return "redirect:/baotri/phieu?maYeuCau=" + maYeuCau;
        }
        return "redirect:/baotri/phieu";
    }

    @PostMapping("/phieu/luu")
    public String luuPhieuBaoTri(@ModelAttribute PhieuBaoTri phieu,
                                 @RequestParam String maTaiSan,
                                 @RequestParam String hienTrangHuHong,
                                 @RequestParam String noiDungSuaChua,
                                 @RequestParam BigDecimal chiPhi,
                                 HttpSession session,
                                 RedirectAttributes ra) {
        try {
            NguoiDung currentUser = (NguoiDung) session.getAttribute("currentUser");
            phieu.setNvLap(currentUser);

            ChiTietBaoTri ct = new ChiTietBaoTri();
            TaiSan ts = taiSanService.findById(maTaiSan).orElse(null);
            ct.setTaiSan(ts);
            ct.setHienTrangHuHong(hienTrangHuHong);
            ct.setNoiDungSuaChua(noiDungSuaChua);
            ct.setChiPhi(chiPhi);

            baoTriService.taoPhieuBaoTri(phieu, Collections.singletonList(ct));
            ra.addFlashAttribute("successMsg", "Tạo phiếu sửa chữa bảo trì thành công! Tài sản đã chuyển sang trạng thái 'Đang bảo trì'.");
            return "redirect:/baotri/phieu";
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", "Lỗi: " + e.getMessage());
            return "redirect:/baotri/phieu";
        }
    }

    @PostMapping("/phieu/hoanthanh")
    public String hoanThanhBaoTri(@RequestParam String soPhieuBT,
                                  @RequestParam String ketQuaXuLy,
                                  RedirectAttributes ra) {
        try {
            baoTriService.hoanThanhBaoTri(soPhieuBT, ketQuaXuLy);
            ra.addFlashAttribute("successMsg", "Nghiệm thu bảo trì thành công! Tình trạng tài sản đã được tự động cập nhật.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }
        return "redirect:/baotri/phieu";
    }

    @PostMapping("/phieu/sua")
    public String suaPhieuBaoTri(@RequestParam String soPhieuBT,
                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ngayLap,
                                 @RequestParam String donViSuaChua,
                                 @RequestParam(required = false, defaultValue = "0") BigDecimal chiPhi,
                                 @RequestParam(required = false) String hienTrangHuHong,
                                 @RequestParam(required = false) String noiDungSuaChua,
                                 RedirectAttributes ra) {
        try {
            baoTriService.suaPhieuBaoTri(soPhieuBT, ngayLap, donViSuaChua, chiPhi, hienTrangHuHong, noiDungSuaChua);
            ra.addFlashAttribute("successMsg", "Cập nhật thông tin phiếu bảo trì [" + soPhieuBT + "] thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }
        return "redirect:/baotri/phieu";
    }

    @GetMapping("/phieu/xoa/{id}")
    public String xoaPhieuBaoTri(@PathVariable String id, RedirectAttributes ra) {
        try {
            baoTriService.xoaPhieuBaoTri(id);
            ra.addFlashAttribute("successMsg", "Hủy phiếu bảo trì [" + id + "] thành công! Tài sản đã được hoàn nguyên về trạng thái 'Đang sử dụng'.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", "Không thể hủy phiếu bảo trì: " + e.getMessage());
        }
        return "redirect:/baotri/phieu";
    }

    // 3. Phiếu đền bù hư hỏng
    @GetMapping("/denbu")
    public String denBuList(@RequestParam(required = false) String soPhieuBT, Model model) {
        model.addAttribute("listDenBu", baoTriService.getAllPhieuDenBu());
        model.addAttribute("listPhieuBT", baoTriService.getAllPhieuBaoTri());
        model.addAttribute("listNhanVien", nguoiDungService.findAll());

        PhieuDenBu db = new PhieuDenBu();
        db.setSoPhieuDB("PDB" + System.currentTimeMillis() % 1000000);
        db.setHanChotBoiThuong(LocalDate.now().plusMonths(1));

        if (soPhieuBT != null && !soPhieuBT.isEmpty()) {
            baoTriService.getPhieuBaoTriById(soPhieuBT).ifPresent(p -> {
                db.setPhieuBaoTri(p);
                db.setSoTienDenBu(p.getTongChiPhi());
            });
            model.addAttribute("openModal", true);
        }

        model.addAttribute("phieuDenBuMoi", db);
        return "baotri/denbu_list";
    }

    @GetMapping("/denbu/tao")
    public String taoDenBuForm(@RequestParam(required = false) String soPhieuBT) {
        if (soPhieuBT != null && !soPhieuBT.isEmpty()) {
            return "redirect:/baotri/denbu?soPhieuBT=" + soPhieuBT;
        }
        return "redirect:/baotri/denbu";
    }

    @PostMapping("/denbu/luu")
    public String luuDenBu(@ModelAttribute PhieuDenBu phieuDenBu, RedirectAttributes ra) {
        try {
            if (phieuDenBu.getNvBoiThuong() != null && phieuDenBu.getNvBoiThuong().getMaNguoiDung() != null) {
                phieuDenBu.setNvBoiThuong(nguoiDungService.findById(phieuDenBu.getNvBoiThuong().getMaNguoiDung()).orElse(null));
            }
            if (phieuDenBu.getPhieuBaoTri() != null && phieuDenBu.getPhieuBaoTri().getSoPhieuBT() != null) {
                phieuDenBu.setPhieuBaoTri(baoTriService.getPhieuBaoTriById(phieuDenBu.getPhieuBaoTri().getSoPhieuBT()).orElse(null));
            }

            baoTriService.taoPhieuDenBu(phieuDenBu);
            ra.addFlashAttribute("successMsg", "Lập phiếu đền bù hư hỏng thành công!");
            return "redirect:/baotri/denbu";
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", "Lỗi: " + e.getMessage());
            return "redirect:/baotri/denbu/tao";
        }
    }

    @GetMapping("/denbu/xacnhan/{id}")
    public String xacNhanDenBu(@PathVariable String id, RedirectAttributes ra) {
        try {
            baoTriService.xacNhanNopTien(id);
            ra.addFlashAttribute("successMsg", "Xác nhận thu tiền bồi thường thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }
        return "redirect:/baotri/denbu";
    }

    @PostMapping("/denbu/sua")
    public String suaDenBu(@RequestParam String soPhieuDB,
                           @RequestParam BigDecimal soTienDenBu,
                           @RequestParam String hinhThucXuLy,
                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hanChotBoiThuong,
                           @RequestParam String lyDoDenBu,
                           RedirectAttributes ra) {
        try {
            baoTriService.suaPhieuDenBu(soPhieuDB, soTienDenBu, hinhThucXuLy, hanChotBoiThuong, lyDoDenBu);
            ra.addFlashAttribute("successMsg", "Cập nhật thông tin biên bản đền bù [" + soPhieuDB + "] thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }
        return "redirect:/baotri/denbu";
    }

    @GetMapping("/denbu/xoa/{id}")
    public String xoaDenBu(@PathVariable String id, RedirectAttributes ra) {
        try {
            baoTriService.xoaPhieuDenBu(id);
            ra.addFlashAttribute("successMsg", "Hủy biên bản đền bù [" + id + "] thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", "Không thể hủy biên bản: " + e.getMessage());
        }
        return "redirect:/baotri/denbu";
    }
}
