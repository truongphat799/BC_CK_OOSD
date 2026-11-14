package com.hcmunre.qlts.controller;

import com.hcmunre.qlts.model.TaiSan;
import com.hcmunre.qlts.service.TaiSanService;
import com.hcmunre.qlts.service.ThongKeService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/baocao")
public class BaoCaoController {
    @Autowired
    private ThongKeService thongKeService;

    @Autowired
    private TaiSanService taiSanService;

    @GetMapping
    public String index(Model model) {
        model.addAllAttributes(thongKeService.getDashboardStats());

        List<TaiSan> allAssets = taiSanService.findAll();
        List<Map<String, Object>> khauHaoList = new ArrayList<>();
        for (TaiSan ts : allAssets) {
            Map<String, Object> row = new HashMap<>();
            row.put("taiSan", ts);
            BigDecimal giaTriHienTai = taiSanService.tinhGiaTriHienTai(ts);
            row.put("giaTriHienTai", giaTriHienTai);
            BigDecimal daKhauHao = ts.getNguyenGia().subtract(giaTriHienTai);
            row.put("daKhauHao", daKhauHao);
            khauHaoList.add(row);
        }
        model.addAttribute("khauHaoList", khauHaoList);

        return "baocao/index";
    }

    @GetMapping("/xuat-excel")
    public void xuatExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=BaoCao_TaiSan_HCMUNRE_" + LocalDate.now() + ".xlsx";
        response.setHeader(headerKey, headerValue);

        try (Workbook workbook = new XSSFWorkbook()) {
            // Style tiêu đề lớn
            CellStyle titleStyle = workbook.createCellStyle();
            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 13);
            titleFont.setColor(IndexedColors.DARK_BLUE.getIndex());
            titleStyle.setFont(titleFont);

            // Style tiêu đề bảng (Header)
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            // Style dữ liệu thông thường
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);

            CellStyle dataCenterStyle = workbook.createCellStyle();
            dataCenterStyle.cloneStyleFrom(dataStyle);
            dataCenterStyle.setAlignment(HorizontalAlignment.CENTER);

            // Style số tiền (VNĐ)
            DataFormat format = workbook.createDataFormat();
            CellStyle currencyStyle = workbook.createCellStyle();
            currencyStyle.cloneStyleFrom(dataStyle);
            currencyStyle.setAlignment(HorizontalAlignment.RIGHT);
            currencyStyle.setDataFormat(format.getFormat("#,##0"));

            // Style dòng tổng cộng
            CellStyle totalStyle = workbook.createCellStyle();
            Font totalFont = workbook.createFont();
            totalFont.setBold(true);
            totalStyle.setFont(totalFont);
            totalStyle.setBorderTop(BorderStyle.THIN);
            totalStyle.setBorderBottom(BorderStyle.DOUBLE);
            totalStyle.setBorderLeft(BorderStyle.THIN);
            totalStyle.setBorderRight(BorderStyle.THIN);
            totalStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            totalStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle totalCurrencyStyle = workbook.createCellStyle();
            totalCurrencyStyle.cloneStyleFrom(totalStyle);
            totalCurrencyStyle.setAlignment(HorizontalAlignment.RIGHT);
            totalCurrencyStyle.setDataFormat(format.getFormat("#,##0"));

            // =========================================================================
            // TAB 1: PHÂN BỔ TÀI SẢN THEO PHÒNG BAN / KHOA ĐÀO TẠO
            // =========================================================================
            Sheet sheetPB = workbook.createSheet("Phân Bổ Phòng Ban");

            Row titleRowPB = sheetPB.createRow(0);
            Cell titleCellPB = titleRowPB.createCell(0);
            titleCellPB.setCellValue("BÁO CÁO PHÂN BỔ TÀI SẢN THEO PHÒNG BAN / KHOA ĐÀO TẠO");
            titleCellPB.setCellStyle(titleStyle);

            Row subTitlePB = sheetPB.createRow(1);
            subTitlePB.createCell(0).setCellValue("Trường Đại học Tài nguyên và Môi trường TP.HCM (HCMUNRE) - Ngày trích xuất: " + LocalDate.now());

            String[] headersPB = {"STT", "Tên Phòng Ban / Đơn Vị", "Số Lượng Tài Sản", "Tổng Nguyên Giá Đầu Tư (VNĐ)"};
            Row headerRowPB = sheetPB.createRow(3);
            headerRowPB.setHeightInPoints(24);
            for (int i = 0; i < headersPB.length; i++) {
                Cell cell = headerRowPB.createCell(i);
                cell.setCellValue(headersPB[i]);
                cell.setCellStyle(headerStyle);
            }

            Map<String, Object> stats = thongKeService.getDashboardStats();
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> pbStats = (List<Map<String, Object>>) stats.get("phongBanStats");

            int rowIdx = 4;
            int stt = 1;
            int sumSoLuong = 0;
            BigDecimal sumNguyenGia = BigDecimal.ZERO;

            if (pbStats != null) {
                for (Map<String, Object> item : pbStats) {
                    Row row = sheetPB.createRow(rowIdx++);

                    Cell c0 = row.createCell(0);
                    c0.setCellValue(stt++);
                    c0.setCellStyle(dataCenterStyle);

                    Cell c1 = row.createCell(1);
                    c1.setCellValue(String.valueOf(item.get("tenPhongBan")));
                    c1.setCellStyle(dataStyle);

                    Cell c2 = row.createCell(2);
                    int sl = item.get("soLuong") != null ? (Integer) item.get("soLuong") : 0;
                    c2.setCellValue(sl);
                    c2.setCellStyle(dataCenterStyle);
                    sumSoLuong += sl;

                    Cell c3 = row.createCell(3);
                    BigDecimal ng = item.get("tongGiaTri") != null ? (BigDecimal) item.get("tongGiaTri") : BigDecimal.ZERO;
                    c3.setCellValue(ng.doubleValue());
                    c3.setCellStyle(currencyStyle);
                    sumNguyenGia = sumNguyenGia.add(ng);
                }
            }

            // Dòng tổng cộng Sheet PB
            Row totalRowPB = sheetPB.createRow(rowIdx);
            Cell tp0 = totalRowPB.createCell(0);
            tp0.setCellValue("");
            tp0.setCellStyle(totalStyle);

            Cell tp1 = totalRowPB.createCell(1);
            tp1.setCellValue("TỔNG CỘNG TOÀN TRƯỜNG:");
            tp1.setCellStyle(totalStyle);

            Cell tp2 = totalRowPB.createCell(2);
            tp2.setCellValue(sumSoLuong);
            CellStyle totalCenterStyle = workbook.createCellStyle();
            totalCenterStyle.cloneStyleFrom(totalStyle);
            totalCenterStyle.setAlignment(HorizontalAlignment.CENTER);
            tp2.setCellStyle(totalCenterStyle);

            Cell tp3 = totalRowPB.createCell(3);
            tp3.setCellValue(sumNguyenGia.doubleValue());
            tp3.setCellStyle(totalCurrencyStyle);

            for (int i = 0; i < headersPB.length; i++) {
                sheetPB.autoSizeColumn(i);
                sheetPB.setColumnWidth(i, Math.max(sheetPB.getColumnWidth(i) + 1200, 3500));
            }

            // =========================================================================
            // TAB 2: BẢNG TÍNH KHẤU HAO & HAO MÒN CHI TIẾT
            // =========================================================================
            Sheet sheetKH = workbook.createSheet("Chi Tiết Khấu Hao");

            Row titleRowKH = sheetKH.createRow(0);
            Cell titleCellKH = titleRowKH.createCell(0);
            titleCellKH.setCellValue("BẢNG TÍNH KHẤU HAO & GIÁ TRỊ CÒN LẠI CHI TIẾT");
            titleCellKH.setCellStyle(titleStyle);

            Row subTitleKH = sheetKH.createRow(1);
            subTitleKH.createCell(0).setCellValue("Trường Đại học Tài nguyên và Môi trường TP.HCM (HCMUNRE) - Ngày trích xuất: " + LocalDate.now());

            String[] headersKH = {
                "STT", "Mã Tài Sản", "Tên Tài Sản", "Phòng Ban Quản Lý", "Ngày Nhận", 
                "Tỷ Lệ KH (%/năm)", "Nguyên Giá (VNĐ)", "Đã Khấu Hao (VNĐ)", "Giá Trị Hiện Tại (VNĐ)", "Tình Trạng"
            };
            Row headerRowKH = sheetKH.createRow(3);
            headerRowKH.setHeightInPoints(24);
            for (int i = 0; i < headersKH.length; i++) {
                Cell cell = headerRowKH.createCell(i);
                cell.setCellValue(headersKH[i]);
                cell.setCellStyle(headerStyle);
            }

            List<TaiSan> allAssets = taiSanService.findAll();
            int khRowIdx = 4;
            int khStt = 1;
            BigDecimal totalNguyenGia = BigDecimal.ZERO;
            BigDecimal totalDaKhauHao = BigDecimal.ZERO;
            BigDecimal totalHienTai = BigDecimal.ZERO;

            for (TaiSan ts : allAssets) {
                Row row = sheetKH.createRow(khRowIdx++);
                BigDecimal giaTriHienTai = taiSanService.tinhGiaTriHienTai(ts);
                BigDecimal daKhauHao = (ts.getNguyenGia() != null) ? ts.getNguyenGia().subtract(giaTriHienTai) : BigDecimal.ZERO;

                if (ts.getNguyenGia() != null) totalNguyenGia = totalNguyenGia.add(ts.getNguyenGia());
                totalDaKhauHao = totalDaKhauHao.add(daKhauHao);
                totalHienTai = totalHienTai.add(giaTriHienTai);

                Cell c0 = row.createCell(0);
                c0.setCellValue(khStt++);
                c0.setCellStyle(dataCenterStyle);

                Cell c1 = row.createCell(1);
                c1.setCellValue(ts.getMaTaiSan());
                c1.setCellStyle(dataCenterStyle);

                Cell c2 = row.createCell(2);
                c2.setCellValue(ts.getTenTaiSan());
                c2.setCellStyle(dataStyle);

                Cell c3 = row.createCell(3);
                c3.setCellValue(ts.getPhongBan() != null ? ts.getPhongBan().getTenPhongBan() : "Trong kho");
                c3.setCellStyle(dataStyle);

                Cell c4 = row.createCell(4);
                c4.setCellValue(ts.getNgayTiepNhan() != null ? ts.getNgayTiepNhan().toString() : "");
                c4.setCellStyle(dataCenterStyle);

                Cell c5 = row.createCell(5);
                double tyLe = (ts.getLoaiTaiSan() != null && ts.getLoaiTaiSan().getTyLeKhauHao() != null)
                        ? ts.getLoaiTaiSan().getTyLeKhauHao().doubleValue() : 0.0;
                c5.setCellValue(tyLe + "%");
                c5.setCellStyle(dataCenterStyle);

                Cell c6 = row.createCell(6);
                c6.setCellValue(ts.getNguyenGia() != null ? ts.getNguyenGia().doubleValue() : 0);
                c6.setCellStyle(currencyStyle);

                Cell c7 = row.createCell(7);
                c7.setCellValue(daKhauHao.doubleValue());
                c7.setCellStyle(currencyStyle);

                Cell c8 = row.createCell(8);
                c8.setCellValue(giaTriHienTai.doubleValue());
                c8.setCellStyle(currencyStyle);

                Cell c9 = row.createCell(9);
                c9.setCellValue(ts.getTinhTrang() != null ? ts.getTinhTrang() : "");
                c9.setCellStyle(dataCenterStyle);
            }

            // Dòng tổng cộng Sheet KH
            Row totalRowKH = sheetKH.createRow(khRowIdx);
            for (int i = 0; i < headersKH.length; i++) {
                Cell cell = totalRowKH.createCell(i);
                cell.setCellStyle(totalStyle);
            }
            totalRowKH.getCell(2).setCellValue("TỔNG CỘNG TOÀN TRƯỜNG:");
            totalRowKH.getCell(6).setCellValue(totalNguyenGia.doubleValue());
            totalRowKH.getCell(6).setCellStyle(totalCurrencyStyle);
            totalRowKH.getCell(7).setCellValue(totalDaKhauHao.doubleValue());
            totalRowKH.getCell(7).setCellStyle(totalCurrencyStyle);
            totalRowKH.getCell(8).setCellValue(totalHienTai.doubleValue());
            totalRowKH.getCell(8).setCellStyle(totalCurrencyStyle);

            for (int i = 0; i < headersKH.length; i++) {
                sheetKH.autoSizeColumn(i);
                sheetKH.setColumnWidth(i, Math.max(sheetKH.getColumnWidth(i) + 1200, 3200));
            }

            workbook.write(response.getOutputStream());
            response.flushBuffer();
        }
    }
}
