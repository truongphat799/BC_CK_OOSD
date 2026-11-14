package com.hcmunre.qlts.config;

import com.hcmunre.qlts.model.NguoiDung;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();

        // 1. Cho phép tài nguyên tĩnh và xác thực
        if (uri.startsWith("/login") || uri.startsWith("/css") || uri.startsWith("/js") 
                || uri.startsWith("/images") || uri.startsWith("/error") || uri.equals("/favicon.ico")) {
            return true;
        }

        HttpSession session = request.getSession();
        NguoiDung currentUser = (NguoiDung) session.getAttribute("currentUser");

        // 2. Chưa đăng nhập -> Chuyển về trang đăng nhập
        if (currentUser == null) {
            response.sendRedirect("/login");
            return false;
        }

        String role = (currentUser.getVaiTro() != null) ? currentUser.getVaiTro().getMaVaiTro() : "";

        // VT_ADMIN: Toàn quyền truy cập mọi chức năng
        if ("VT_ADMIN".equals(role)) {
            return true;
        }

        // 3. Phân quyền VT_QLTS (Nhân viên Quản lý tài sản)
        if ("VT_QLTS".equals(role)) {
            // Không được duyệt thanh lý (chỉ Ban Lãnh đạo hoặc Admin)
            if (uri.startsWith("/thanhly/duyet")) {
                session.setAttribute("errorMsg", "Chức năng phê duyệt thanh lý chỉ dành cho Ban Lãnh đạo!");
                response.sendRedirect("/thanhly");
                return false;
            }
            // Không được quản lý tài khoản người dùng
            if (uri.startsWith("/danhmuc/nguoidung")) {
                session.setAttribute("errorMsg", "Chức năng quản trị tài khoản chỉ dành cho Quản trị viên (Admin)!");
                response.sendRedirect("/dashboard");
                return false;
            }
            return true;
        }

        // 4. Phân quyền VT_LANHDAO (Ban Lãnh đạo)
        if ("VT_LANHDAO".equals(role)) {
            // Ban Lãnh đạo chỉ xem tra cứu tài sản, xem báo cáo, giám sát kiểm kê, duyệt thanh lý
            // Chặn các thao tác tạo/sửa/xóa tài sản, nhập xuất kho hoặc cấu hình danh mục
            if (uri.startsWith("/taisan/them") || uri.startsWith("/taisan/sua") || uri.startsWith("/taisan/luu") || uri.startsWith("/taisan/xoa")
                    || uri.startsWith("/capphat/tao") || uri.startsWith("/capphat/luu") || uri.startsWith("/capphat/capnhat") || uri.startsWith("/capphat/xoa")
                    || uri.startsWith("/dieuchuyen/tao") || uri.startsWith("/dieuchuyen/luu") || uri.startsWith("/dieuchuyen/capnhat") || uri.startsWith("/dieuchuyen/xoa")
                    || uri.startsWith("/baotri/phieu") || uri.startsWith("/baotri/denbu") || uri.startsWith("/baotri/yeucau/sua") || uri.startsWith("/baotri/yeucau/xoa") || uri.startsWith("/baotri/yeucau/tuchoi")
                    || uri.startsWith("/kiemke/tao") || uri.startsWith("/kiemke/luu") || uri.startsWith("/kiemke/capnhat") || uri.startsWith("/kiemke/xoa")
                    || uri.startsWith("/thanhly/tao") || uri.startsWith("/thanhly/luu") || uri.startsWith("/thanhly/capnhat") || uri.startsWith("/thanhly/xoa")
                    || uri.startsWith("/danhmuc")) {
                session.setAttribute("errorMsg", "Tài khoản Ban Lãnh đạo không có quyền thực hiện thao tác tác nghiệp này!");
                response.sendRedirect("/dashboard");
                return false;
            }
            return true;
        }

        // 5. Phân quyền VT_DONVI (Đơn vị sử dụng - Giảng viên / Khoa phòng)
        if ("VT_DONVI".equals(role)) {
            boolean isAllowed = uri.equals("/") || uri.equals("/dashboard") 
                    || uri.equals("/taisan") || uri.startsWith("/taisan/chitiet")
                    || uri.equals("/capphat") || uri.startsWith("/capphat/xem")
                    || uri.equals("/dieuchuyen") || uri.startsWith("/dieuchuyen/xem")
                    || uri.startsWith("/baotri/yeucau")
                    || uri.startsWith("/profile") || uri.equals("/logout");

            if (!isAllowed) {
                session.setAttribute("errorMsg", "Tài khoản Đơn vị sử dụng chỉ có quyền xem tài sản, phiếu bàn giao và gửi báo hỏng!");
                response.sendRedirect("/dashboard");
                return false;
            }
            return true;
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HttpSession session = request.getSession(false);
        if (session != null && modelAndView != null) {
            Object err = session.getAttribute("errorMsg");
            if (err != null) {
                modelAndView.addObject("errorMsg", err);
                session.removeAttribute("errorMsg");
            }
            Object succ = session.getAttribute("successMsg");
            if (succ != null) {
                modelAndView.addObject("successMsg", succ);
                session.removeAttribute("successMsg");
            }
        }
    }
}
