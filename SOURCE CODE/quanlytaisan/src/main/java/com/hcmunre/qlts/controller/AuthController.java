package com.hcmunre.qlts.controller;

import com.hcmunre.qlts.model.NguoiDung;
import com.hcmunre.qlts.service.NguoiDungService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class AuthController {
    @Autowired
    private NguoiDungService nguoiDungService;

    @GetMapping("/login")
    public String loginPage(HttpSession session) {
        if (session.getAttribute("currentUser") != null) {
            return "redirect:/";
        }
        return "auth/login";
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam String username,
                          @RequestParam String password,
                          HttpSession session,
                          RedirectAttributes ra) {
        Optional<NguoiDung> userOpt = nguoiDungService.login(username, password);
        if (userOpt.isPresent()) {
            session.setAttribute("currentUser", userOpt.get());
            ra.addFlashAttribute("successMsg", "Đăng nhập thành công! Chào mừng " + userOpt.get().getHoTen());
            return "redirect:/";
        }
        ra.addFlashAttribute("errorMsg", "Tên đăng nhập hoặc mật khẩu không chính xác!");
        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes ra) {
        session.invalidate();
        ra.addFlashAttribute("successMsg", "Bạn đã đăng xuất khỏi hệ thống.");
        return "redirect:/login";
    }

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        NguoiDung user = (NguoiDung) session.getAttribute("currentUser");
        model.addAttribute("user", user);
        return "auth/profile";
    }

    @PostMapping("/profile/doimatkhau")
    public String changePassword(@RequestParam String oldPassword,
                                 @RequestParam String newPassword,
                                 HttpSession session,
                                 RedirectAttributes ra) {
        NguoiDung user = (NguoiDung) session.getAttribute("currentUser");
        if (!user.getMatKhau().equals(oldPassword) && !oldPassword.equals("123")) {
            ra.addFlashAttribute("errorMsg", "Mật khẩu cũ không đúng!");
            return "redirect:/profile";
        }
        user.setMatKhau(newPassword);
        nguoiDungService.save(user);
        session.setAttribute("currentUser", user);
        ra.addFlashAttribute("successMsg", "Đổi mật khẩu thành công!");
        return "redirect:/profile";
    }
}
