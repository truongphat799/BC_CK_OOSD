package com.hcmunre.qlts.controller;

import com.hcmunre.qlts.model.*;
import com.hcmunre.qlts.service.DanhMucService;
import com.hcmunre.qlts.service.NguoiDungService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/danhmuc")
public class DanhMucController {
    @Autowired
    private DanhMucService danhMucService;

    @Autowired
    private NguoiDungService nguoiDungService;

    // 1. PHÒNG BAN
    @GetMapping("/phongban")
    public String phongBanList(Model model) {
        model.addAttribute("listPhongBan", danhMucService.getAllPhongBan());
        model.addAttribute("phongBanMoi", new PhongBan());
        return "danhmuc/phongban";
    }

    @PostMapping("/phongban/luu")
    public String luuPhongBan(@ModelAttribute PhongBan pb, RedirectAttributes ra) {
        try {
            danhMucService.savePhongBan(pb);
            ra.addFlashAttribute("successMsg", "Lưu thông tin phòng ban thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }
        return "redirect:/danhmuc/phongban";
    }

    @GetMapping("/phongban/xoa/{id}")
    public String xoaPhongBan(@PathVariable String id, RedirectAttributes ra) {
        try {
            danhMucService.deletePhongBan(id);
            ra.addFlashAttribute("successMsg", "Xóa phòng ban thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", "Không thể xóa phòng ban đang có tài sản hoặc người dùng trực thuộc!");
        }
        return "redirect:/danhmuc/phongban";
    }

    // 2. LOẠI TÀI SẢN
    @GetMapping("/loaitaisan")
    public String loaiTaiSanList(Model model) {
        model.addAttribute("listLoai", danhMucService.getAllLoaiTaiSan());
        model.addAttribute("loaiMoi", new LoaiTaiSan());
        return "danhmuc/loaitaisan";
    }

    @PostMapping("/loaitaisan/luu")
    public String luuLoaiTaiSan(@ModelAttribute LoaiTaiSan lts, RedirectAttributes ra) {
        try {
            danhMucService.saveLoaiTaiSan(lts);
            ra.addFlashAttribute("successMsg", "Lưu loại tài sản thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }
        return "redirect:/danhmuc/loaitaisan";
    }

    @GetMapping("/loaitaisan/xoa/{id}")
    public String xoaLoaiTaiSan(@PathVariable String id, RedirectAttributes ra) {
        try {
            danhMucService.deleteLoaiTaiSan(id);
            ra.addFlashAttribute("successMsg", "Xóa loại tài sản thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", "Không thể xóa loại tài sản đã có thiết bị trong kho!");
        }
        return "redirect:/danhmuc/loaitaisan";
    }

    // 3. NHÀ CUNG CẤP
    @GetMapping("/nhacungcap")
    public String nhaCungCapList(Model model) {
        model.addAttribute("listNCC", danhMucService.getAllNhaCungCap());
        model.addAttribute("nccMoi", new NhaCungCap());
        return "danhmuc/nhacungcap";
    }

    @PostMapping("/nhacungcap/luu")
    public String luuNhaCungCap(@ModelAttribute NhaCungCap ncc, RedirectAttributes ra) {
        try {
            danhMucService.saveNhaCungCap(ncc);
            ra.addFlashAttribute("successMsg", "Lưu nhà cung cấp thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }
        return "redirect:/danhmuc/nhacungcap";
    }

    @GetMapping("/nhacungcap/xoa/{id}")
    public String xoaNhaCungCap(@PathVariable String id, RedirectAttributes ra) {
        try {
            danhMucService.deleteNhaCungCap(id);
            ra.addFlashAttribute("successMsg", "Xóa nhà cung cấp thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", "Không thể xóa nhà cung cấp đã có hợp đồng cung cấp tài sản!");
        }
        return "redirect:/danhmuc/nhacungcap";
    }

    // 4. NGƯỜI DÙNG & PHÂN QUYỀN
    @GetMapping("/nguoidung")
    public String nguoiDungList(Model model) {
        model.addAttribute("listNguoiDung", nguoiDungService.findAll());
        model.addAttribute("listVaiTro", danhMucService.getAllVaiTro());
        model.addAttribute("listPhongBan", danhMucService.getAllPhongBan());
        model.addAttribute("nguoiDungMoi", new NguoiDung());
        return "danhmuc/nguoidung";
    }

    @PostMapping("/nguoidung/luu")
    public String luuNguoiDung(@ModelAttribute NguoiDung nd, RedirectAttributes ra) {
        try {
            if (nd.getMatKhau() == null || nd.getMatKhau().isEmpty()) {
                nd.setMatKhau("123");
            }
            if (nd.getTrangThai() == null || nd.getTrangThai().isEmpty()) {
                nd.setTrangThai("Đang hoạt động");
            }
            if (nd.getVaiTro() != null && nd.getVaiTro().getMaVaiTro() != null) {
                nd.setVaiTro(danhMucService.getAllVaiTro().stream().filter(v -> v.getMaVaiTro().equals(nd.getVaiTro().getMaVaiTro())).findFirst().orElse(null));
            }
            if (nd.getPhongBan() != null && nd.getPhongBan().getMaPhongBan() != null) {
                nd.setPhongBan(danhMucService.getPhongBanById(nd.getPhongBan().getMaPhongBan()).orElse(null));
            }
            nguoiDungService.save(nd);
            ra.addFlashAttribute("successMsg", "Tạo mới tài khoản người dùng thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }
        return "redirect:/danhmuc/nguoidung";
    }

    @PostMapping("/nguoidung/capnhat")
    public String capNhatNguoiDung(@ModelAttribute NguoiDung nd,
                                   @RequestParam(value = "matKhauMoi", required = false) String matKhauMoi,
                                   HttpSession session,
                                   RedirectAttributes ra) {
        try {
            Optional<NguoiDung> existingOpt = nguoiDungService.findById(nd.getMaNguoiDung());
            if (existingOpt.isEmpty()) {
                ra.addFlashAttribute("errorMsg", "Không tìm thấy người dùng có mã: " + nd.getMaNguoiDung());
                return "redirect:/danhmuc/nguoidung";
            }
            NguoiDung existing = existingOpt.get();
            existing.setHoTen(nd.getHoTen());
            existing.setTenDangNhap(nd.getTenDangNhap());
            existing.setEmail(nd.getEmail());
            existing.setSoDienThoai(nd.getSoDienThoai());
            if (nd.getTrangThai() != null && !nd.getTrangThai().isEmpty()) {
                existing.setTrangThai(nd.getTrangThai());
            }

            // Nếu admin nhập mật khẩu mới thì cập nhật
            if (matKhauMoi != null && !matKhauMoi.trim().isEmpty()) {
                existing.setMatKhau(matKhauMoi.trim());
            }

            if (nd.getVaiTro() != null && nd.getVaiTro().getMaVaiTro() != null) {
                existing.setVaiTro(danhMucService.getAllVaiTro().stream()
                        .filter(v -> v.getMaVaiTro().equals(nd.getVaiTro().getMaVaiTro()))
                        .findFirst().orElse(existing.getVaiTro()));
            }
            if (nd.getPhongBan() != null && nd.getPhongBan().getMaPhongBan() != null) {
                existing.setPhongBan(danhMucService.getPhongBanById(nd.getPhongBan().getMaPhongBan())
                        .orElse(existing.getPhongBan()));
            }

            NguoiDung saved = nguoiDungService.save(existing);

            // Nếu người được cập nhật chính là tài khoản đang đăng nhập, làm mới session
            NguoiDung currentSessionUser = (NguoiDung) session.getAttribute("currentUser");
            if (currentSessionUser != null && currentSessionUser.getMaNguoiDung().equals(saved.getMaNguoiDung())) {
                session.setAttribute("currentUser", saved);
            }

            ra.addFlashAttribute("successMsg", "Cập nhật tài khoản [" + existing.getHoTen() + "] thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", "Lỗi khi cập nhật tài khoản: " + e.getMessage());
        }
        return "redirect:/danhmuc/nguoidung";
    }

    @GetMapping("/nguoidung/xoa/{id}")
    public String xoaNguoiDung(@PathVariable String id, HttpSession session, RedirectAttributes ra) {
        try {
            NguoiDung currentUser = (NguoiDung) session.getAttribute("currentUser");
            if (currentUser != null && id.equals(currentUser.getMaNguoiDung())) {
                ra.addFlashAttribute("errorMsg", "Không thể xóa tài khoản của chính bạn đang đăng nhập!");
                return "redirect:/danhmuc/nguoidung";
            }
            if ("admin".equalsIgnoreCase(id) || "ND_01".equalsIgnoreCase(id)) {
                ra.addFlashAttribute("errorMsg", "Không thể xóa tài khoản Quản trị viên hệ thống gốc!");
                return "redirect:/danhmuc/nguoidung";
            }
            nguoiDungService.delete(id);
            ra.addFlashAttribute("successMsg", "Xóa tài khoản người dùng thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", "Không thể xóa tài khoản này vì đã có dữ liệu chứng từ / giao dịch (phiếu cấp phát, phiếu bảo trì, thanh lý...)! Bạn có thể chọn 'Khóa tài khoản' thay thế.");
        }
        return "redirect:/danhmuc/nguoidung";
    }

    @GetMapping("/nguoidung/trangthai/{id}")
    public String doiTrangThaiNguoiDung(@PathVariable String id, RedirectAttributes ra) {
        try {
            nguoiDungService.findById(id).ifPresent(u -> {
                if ("Đang hoạt động".equals(u.getTrangThai())) {
                    u.setTrangThai("Bị khóa");
                } else {
                    u.setTrangThai("Đang hoạt động");
                }
                nguoiDungService.save(u);
            });
            ra.addFlashAttribute("successMsg", "Đổi trạng thái tài khoản thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }
        return "redirect:/danhmuc/nguoidung";
    }
}
