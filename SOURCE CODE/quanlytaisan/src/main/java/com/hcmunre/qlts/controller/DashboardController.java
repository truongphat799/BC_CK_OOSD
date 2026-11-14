package com.hcmunre.qlts.controller;

import com.hcmunre.qlts.service.CapPhatService;
import com.hcmunre.qlts.service.TaiSanService;
import com.hcmunre.qlts.service.ThongKeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
public class DashboardController {
    @Autowired
    private ThongKeService thongKeService;

    @Autowired
    private TaiSanService taiSanService;

    @Autowired
    private CapPhatService capPhatService;

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        Map<String, Object> stats = thongKeService.getDashboardStats();
        model.addAllAttributes(stats);

        // Danh sách tài sản mới nhất
        model.addAttribute("recentTaiSan", taiSanService.findAll().stream().limit(5).toList());
        // Danh sách phiếu cấp phát gần đây
        model.addAttribute("recentCapPhat", capPhatService.findAll().stream().limit(5).toList());

        return "dashboard/index";
    }
}
