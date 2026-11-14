-- 1. KHỞI TẠO CƠ SỞ DỮ LIỆU
IF DB_ID(N'QuanLyTaiSanCoDinh') IS NULL 
    CREATE DATABASE QuanLyTaiSanCoDinh;
GO

USE QuanLyTaiSanCoDinh;
GO

SET ANSI_NULLS ON;
GO
SET QUOTED_IDENTIFIER ON;
GO

-- =========================================================================================
-- 2. XÓA CÁC BẢNG THEO THỨ TỰ PHỤ THUỘC (CHILD TRƯỚC, PARENT SAU)
-- =========================================================================================
IF OBJECT_ID('ChiTietThanhLy', 'U') IS NOT NULL DROP TABLE ChiTietThanhLy;
IF OBJECT_ID('PhieuThanhLy', 'U') IS NOT NULL DROP TABLE PhieuThanhLy;
IF OBJECT_ID('ChiTietKiemKe', 'U') IS NOT NULL DROP TABLE ChiTietKiemKe;
IF OBJECT_ID('PhieuKiemKe', 'U') IS NOT NULL DROP TABLE PhieuKiemKe;
IF OBJECT_ID('PhieuDenBu', 'U') IS NOT NULL DROP TABLE PhieuDenBu;
IF OBJECT_ID('ChiTietBaoTri', 'U') IS NOT NULL DROP TABLE ChiTietBaoTri;
IF OBJECT_ID('PhieuBaoTri', 'U') IS NOT NULL DROP TABLE PhieuBaoTri;
IF OBJECT_ID('YeuCauBaoTri', 'U') IS NOT NULL DROP TABLE YeuCauBaoTri;
IF OBJECT_ID('ChiTietDieuChuyen', 'U') IS NOT NULL DROP TABLE ChiTietDieuChuyen;
IF OBJECT_ID('PhieuDieuChuyen', 'U') IS NOT NULL DROP TABLE PhieuDieuChuyen;
IF OBJECT_ID('ChiTietCapPhat', 'U') IS NOT NULL DROP TABLE ChiTietCapPhat;
IF OBJECT_ID('PhieuCapPhat', 'U') IS NOT NULL DROP TABLE PhieuCapPhat;
IF OBJECT_ID('TaiSan', 'U') IS NOT NULL DROP TABLE TaiSan;
IF OBJECT_ID('NhaCungCap', 'U') IS NOT NULL DROP TABLE NhaCungCap;
IF OBJECT_ID('LoaiTaiSan', 'U') IS NOT NULL DROP TABLE LoaiTaiSan;
IF OBJECT_ID('NguoiDung', 'U') IS NOT NULL DROP TABLE NguoiDung;
IF OBJECT_ID('PhongBan', 'U') IS NOT NULL DROP TABLE PhongBan;
IF OBJECT_ID('VaiTro', 'U') IS NOT NULL DROP TABLE VaiTro;
GO

-- =========================================================================================
-- 3. ĐỊNH NGHĨA CẤU TRÚC 18 BẢNG DỮ LIỆU (DDL)
-- =========================================================================================

-- 3.1 Bảng Vai Trò Người Dùng
CREATE TABLE VaiTro (
    MaVaiTro varchar(20) NOT NULL PRIMARY KEY,
    TenVaiTro nvarchar(50) NOT NULL UNIQUE,
    MoTa nvarchar(255) NULL
);
GO

-- 3.2 Bảng Phòng Ban / Đơn Vị Sử Dụng
CREATE TABLE PhongBan (
    MaPhongBan varchar(20) NOT NULL PRIMARY KEY,
    TenPhongBan nvarchar(100) NOT NULL UNIQUE,
    DiaDiem nvarchar(150) NULL,
    SDTLienHe varchar(20) NULL
);
GO

-- 3.3 Bảng Tài Khoản Người Dùng Hệ Thống
CREATE TABLE NguoiDung (
    MaNguoiDung varchar(20) NOT NULL PRIMARY KEY,
    MaVaiTro varchar(20) NOT NULL,
    MaPhongBan varchar(20) NOT NULL,
    HoTen nvarchar(120) NOT NULL,
    TenDangNhap varchar(50) NOT NULL UNIQUE,
    MatKhau varchar(255) NOT NULL,
    Email varchar(100) NOT NULL UNIQUE,
    SoDienThoai varchar(20) NULL,
    TrangThai nvarchar(30) NOT NULL DEFAULT N'Đang hoạt động',
    CONSTRAINT CK_NguoiDung_TrangThai CHECK (TrangThai IN (N'Đang hoạt động', N'Bị khóa')),
    CONSTRAINT FK_NguoiDung_VaiTro FOREIGN KEY (MaVaiTro) REFERENCES VaiTro(MaVaiTro),
    CONSTRAINT FK_NguoiDung_PhongBan FOREIGN KEY (MaPhongBan) REFERENCES PhongBan(MaPhongBan)
);
GO

-- 3.4 Bảng Loại Tài Sản Cố Định
CREATE TABLE LoaiTaiSan (
    MaLoai varchar(20) NOT NULL PRIMARY KEY,
    TenLoai nvarchar(100) NOT NULL UNIQUE,
    TyLeKhauHao decimal(5,2) NOT NULL CHECK (TyLeKhauHao >= 0 AND TyLeKhauHao <= 100),
    ThoiGianSuDung int NOT NULL CHECK (ThoiGianSuDung > 0), -- Số năm sử dụng quy định
    GhiChu nvarchar(255) NULL
);
GO

-- 3.5 Bảng Nhà Cung Cấp
CREATE TABLE NhaCungCap (
    MaNCC varchar(20) NOT NULL PRIMARY KEY,
    TenNCC nvarchar(150) NOT NULL,
    DiaChi nvarchar(255) NULL,
    SoDienThoai varchar(20) NULL,
    MaSoThue varchar(30) NULL
);
GO

-- 3.6 Bảng Danh Mục Cá Thể Tài Sản Cố Định (Lõi Trung Tâm)
CREATE TABLE TaiSan (
    MaTaiSan varchar(30) NOT NULL PRIMARY KEY,
    MaLoai varchar(20) NOT NULL,
    MaNCC varchar(20) NOT NULL,
    MaPhongBan varchar(20) NULL, -- Null khi còn trong kho trung tâm
    TenTaiSan nvarchar(150) NOT NULL,
    DonViTinh nvarchar(20) NOT NULL DEFAULT N'Cái',
    NguyenGia decimal(18,2) NOT NULL CHECK (NguyenGia >= 0),
    GiaTriConLai decimal(18,2) NOT NULL CHECK (GiaTriConLai >= 0),
    NgayTiepNhan date NOT NULL,
    TinhTrang nvarchar(50) NOT NULL DEFAULT N'Trong kho',
    ViTri nvarchar(150) NULL,
    NamSanXuat int NOT NULL CHECK (NamSanXuat >= 1990),
    SoHoaDon varchar(50) NOT NULL,
    CONSTRAINT CK_TaiSan_TinhTrang CHECK (TinhTrang IN (N'Trong kho', N'Đang sử dụng', N'Đang bảo trì', N'Chờ thanh lý', N'Đã thanh lý')),
    CONSTRAINT CK_TaiSan_GiaTri CHECK (GiaTriConLai <= NguyenGia),
    CONSTRAINT FK_TaiSan_Loai FOREIGN KEY (MaLoai) REFERENCES LoaiTaiSan(MaLoai),
    CONSTRAINT FK_TaiSan_NCC FOREIGN KEY (MaNCC) REFERENCES NhaCungCap(MaNCC),
    CONSTRAINT FK_TaiSan_PhongBan FOREIGN KEY (MaPhongBan) REFERENCES PhongBan(MaPhongBan)
);
GO

-- 3.7 Bảng Phiếu Cấp Phát Tài Sản
CREATE TABLE PhieuCapPhat (
    SoPhieuCP varchar(30) NOT NULL PRIMARY KEY,
    MaNVLap varchar(20) NOT NULL,
    MaPhongBanNhan varchar(20) NOT NULL,
    NgayCapPhat date NOT NULL,
    LyDoCapPhat nvarchar(255) NOT NULL,
    NguoiNhanDaiDien nvarchar(120) NOT NULL,
    GhiChu nvarchar(255) NULL,
    CONSTRAINT FK_PhieuCP_NV FOREIGN KEY (MaNVLap) REFERENCES NguoiDung(MaNguoiDung),
    CONSTRAINT FK_PhieuCP_PhongBan FOREIGN KEY (MaPhongBanNhan) REFERENCES PhongBan(MaPhongBan)
);
GO

-- 3.8 Bảng Chi Tiết Cấp Phát Tài Sản (Weak Entity)
CREATE TABLE ChiTietCapPhat (
    SoPhieuCP varchar(30) NOT NULL,
    MaTaiSan varchar(30) NOT NULL,
    SoLuong int NOT NULL DEFAULT 1 CHECK (SoLuong > 0),
    TinhTrangBanGiao nvarchar(50) NOT NULL DEFAULT N'Mới 100%',
    ViTriMoi nvarchar(150) NOT NULL,
    PRIMARY KEY (SoPhieuCP, MaTaiSan),
    CONSTRAINT FK_CTCP_Phieu FOREIGN KEY (SoPhieuCP) REFERENCES PhieuCapPhat(SoPhieuCP) ON DELETE CASCADE,
    CONSTRAINT FK_CTCP_TaiSan FOREIGN KEY (MaTaiSan) REFERENCES TaiSan(MaTaiSan)
);
GO

-- 3.9 Bảng Phiếu Điều Chuyển Tài Sản
CREATE TABLE PhieuDieuChuyen (
    SoPhieuDC varchar(30) NOT NULL PRIMARY KEY,
    MaNVLap varchar(20) NOT NULL,
    MaPhongBanChuyen varchar(20) NOT NULL,
    NgayDieuChuyen date NOT NULL,
    LyDoDieuChuyen nvarchar(255) NOT NULL,
    GhiChu nvarchar(255) NULL,
    CONSTRAINT FK_PhieuDC_NV FOREIGN KEY (MaNVLap) REFERENCES NguoiDung(MaNguoiDung),
    CONSTRAINT FK_PhieuDC_PhongBan FOREIGN KEY (MaPhongBanChuyen) REFERENCES PhongBan(MaPhongBan)
);
GO

-- 3.10 Bảng Chi Tiết Điều Chuyển Tài Sản
CREATE TABLE ChiTietDieuChuyen (
    SoPhieuDC varchar(30) NOT NULL,
    MaTaiSan varchar(30) NOT NULL,
    SoLuong int NOT NULL DEFAULT 1 CHECK (SoLuong > 0),
    TinhTrangKhiChuyen nvarchar(50) NOT NULL,
    ViTriMoi nvarchar(150) NOT NULL,
    PRIMARY KEY (SoPhieuDC, MaTaiSan),
    CONSTRAINT FK_CTDC_Phieu FOREIGN KEY (SoPhieuDC) REFERENCES PhieuDieuChuyen(SoPhieuDC) ON DELETE CASCADE,
    CONSTRAINT FK_CTDC_TaiSan FOREIGN KEY (MaTaiSan) REFERENCES TaiSan(MaTaiSan)
);
GO

-- 3.11 Bảng Yêu Cầu Báo Hỏng / Bảo Trì Từ Đơn Vị
CREATE TABLE YeuCauBaoTri (
    MaYeuCau varchar(30) NOT NULL PRIMARY KEY,
    MaTaiSan varchar(30) NOT NULL,
    MaNVGui varchar(20) NOT NULL,
    NgayGui datetime NOT NULL DEFAULT GETDATE(),
    MoTaSuCo nvarchar(255) NOT NULL,
    MucDoUuTien nvarchar(30) NOT NULL DEFAULT N'Bình thường',
    TrangThai nvarchar(30) NOT NULL DEFAULT N'Chờ tiếp nhận',
    CONSTRAINT CK_YCBT_UuTien CHECK (MucDoUuTien IN (N'Khẩn cấp', N'Cao', N'Bình thường', N'Thấp')),
    CONSTRAINT CK_YCBT_TrangThai CHECK (TrangThai IN (N'Chờ tiếp nhận', N'Đã tiếp nhận', N'Đang xử lý', N'Hoàn thành', N'Hủy')),
    CONSTRAINT FK_YCBT_TaiSan FOREIGN KEY (MaTaiSan) REFERENCES TaiSan(MaTaiSan),
    CONSTRAINT FK_YCBT_NV FOREIGN KEY (MaNVGui) REFERENCES NguoiDung(MaNguoiDung)
);
GO

-- 3.12 Bảng Phiếu Bảo Trì, Sửa Chữa Tài Sản
CREATE TABLE PhieuBaoTri (
    SoPhieuBT varchar(30) NOT NULL PRIMARY KEY,
    MaYeuCau varchar(30) NULL UNIQUE,
    MaNVLap varchar(20) NOT NULL,
    NgayLap date NOT NULL,
    NgayHoanThanh date NULL,
    DonViSuaChua nvarchar(150) NOT NULL,
    TongChiPhi decimal(18,2) NOT NULL DEFAULT 0 CHECK (TongChiPhi >= 0),
    KetQuaXuLy nvarchar(50) NOT NULL DEFAULT N'Đang sửa chữa',
    CONSTRAINT CK_PBT_Ngay CHECK (NgayHoanThanh IS NULL OR NgayHoanThanh >= NgayLap),
    CONSTRAINT CK_PBT_KetQua CHECK (KetQuaXuLy IN (N'Đang sửa chữa', N'Đạt yêu cầu', N'Không thể phục hồi')),
    CONSTRAINT FK_PBT_YCBT FOREIGN KEY (MaYeuCau) REFERENCES YeuCauBaoTri(MaYeuCau),
    CONSTRAINT FK_PBT_NV FOREIGN KEY (MaNVLap) REFERENCES NguoiDung(MaNguoiDung)
);
GO

-- 3.13 Bảng Chi Tiết Hạng Mục Sửa Chữa Trong Phiếu Bảo Trì
CREATE TABLE ChiTietBaoTri (
    SoPhieuBT varchar(30) NOT NULL,
    MaTaiSan varchar(30) NOT NULL,
    HienTrangHuHong nvarchar(255) NOT NULL,
    NoiDungSuaChua nvarchar(255) NOT NULL,
    ChiPhi decimal(18,2) NOT NULL DEFAULT 0 CHECK (ChiPhi >= 0),
    TinhTrangSauXuLy nvarchar(50) NOT NULL,
    PRIMARY KEY (SoPhieuBT, MaTaiSan),
    CONSTRAINT FK_CTBT_Phieu FOREIGN KEY (SoPhieuBT) REFERENCES PhieuBaoTri(SoPhieuBT) ON DELETE CASCADE,
    CONSTRAINT FK_CTBT_TaiSan FOREIGN KEY (MaTaiSan) REFERENCES TaiSan(MaTaiSan)
);
GO

-- 3.14 Bảng Phiếu Đền Bù Hư Hỏng (Xử lý lỗi chủ quan)
CREATE TABLE PhieuDenBu (
    SoPhieuDB varchar(30) NOT NULL PRIMARY KEY,
    SoPhieuBT varchar(30) NOT NULL,
    MaNVBoiThuong varchar(20) NOT NULL,
    SoTienDenBu decimal(18,2) NOT NULL CHECK (SoTienDenBu > 0),
    LyDoDenBu nvarchar(255) NOT NULL,
    HinhThucXuLy nvarchar(100) NOT NULL DEFAULT N'Khấu trừ lương',
    HanChotBoiThuong date NOT NULL,
    TrangThai nvarchar(30) NOT NULL DEFAULT N'Chưa bồi thường',
    CONSTRAINT CK_PhieuDB_TrangThai CHECK (TrangThai IN (N'Chưa bồi thường', N'Đã hoàn tất')),
    CONSTRAINT FK_PhieuDB_PBT FOREIGN KEY (SoPhieuBT) REFERENCES PhieuBaoTri(SoPhieuBT),
    CONSTRAINT FK_PhieuDB_NV FOREIGN KEY (MaNVBoiThuong) REFERENCES NguoiDung(MaNguoiDung)
);
GO

-- 3.15 Bảng Phiếu Kiểm Kê Tài Sản Định Kỳ
CREATE TABLE PhieuKiemKe (
    SoPhieuKK varchar(30) NOT NULL PRIMARY KEY,
    MaPhongBan varchar(20) NOT NULL,
    MaNVChuTri varchar(20) NOT NULL,
    NgayKiemKe date NOT NULL,
    DotKiemKe nvarchar(100) NOT NULL,
    TruongBanKiemKe nvarchar(120) NOT NULL,
    GhiChuChung nvarchar(255) NULL,
    CONSTRAINT FK_PhieuKK_PhongBan FOREIGN KEY (MaPhongBan) REFERENCES PhongBan(MaPhongBan),
    CONSTRAINT FK_PhieuKK_NV FOREIGN KEY (MaNVChuTri) REFERENCES NguoiDung(MaNguoiDung)
);
GO

-- 3.16 Bảng Chi Tiết Kết Quả Kiểm Kê
CREATE TABLE ChiTietKiemKe (
    SoPhieuKK varchar(30) NOT NULL,
    MaTaiSan varchar(30) NOT NULL,
    SoLuongHeThong int NOT NULL DEFAULT 1 CHECK (SoLuongHeThong >= 0),
    SoLuongThucTe int NOT NULL DEFAULT 1 CHECK (SoLuongThucTe >= 0),
    ChenhLech AS (CONVERT(int, SoLuongThucTe - SoLuongHeThong)) PERSISTED,
    TinhTrangThucTe nvarchar(50) NOT NULL,
    DeXuatXuLy nvarchar(255) NULL,
    PRIMARY KEY (SoPhieuKK, MaTaiSan),
    CONSTRAINT FK_CTKK_Phieu FOREIGN KEY (SoPhieuKK) REFERENCES PhieuKiemKe(SoPhieuKK) ON DELETE CASCADE,
    CONSTRAINT FK_CTKK_TaiSan FOREIGN KEY (MaTaiSan) REFERENCES TaiSan(MaTaiSan)
);
GO

-- 3.17 Bảng Phiếu Thanh Lý Tài Sản Cố Định
CREATE TABLE PhieuThanhLy (
    SoPhieuTL varchar(30) NOT NULL PRIMARY KEY,
    MaNVLap varchar(20) NOT NULL,
    MaNVDuyet varchar(20) NOT NULL,
    NgayThanhLy date NOT NULL,
    PhuongThucThanhLy nvarchar(100) NOT NULL DEFAULT N'Bán đấu giá',
    TongNguyenGia decimal(18,2) NOT NULL DEFAULT 0 CHECK (TongNguyenGia >= 0),
    TongGiaTriThuHoi decimal(18,2) NOT NULL DEFAULT 0 CHECK (TongGiaTriThuHoi >= 0),
    TrangThaiDuyet nvarchar(30) NOT NULL DEFAULT N'Chờ phê duyệt',
    CONSTRAINT CK_PhieuTL_TrangThai CHECK (TrangThaiDuyet IN (N'Chờ phê duyệt', N'Đã phê duyệt', N'Bị từ chối')),
    CONSTRAINT FK_PhieuTL_NVLap FOREIGN KEY (MaNVLap) REFERENCES NguoiDung(MaNguoiDung),
    CONSTRAINT FK_PhieuTL_NVDuyet FOREIGN KEY (MaNVDuyet) REFERENCES NguoiDung(MaNguoiDung)
);
GO

-- 3.18 Bảng Chi Tiết Tài Sản Thanh Lý
CREATE TABLE ChiTietThanhLy (
    SoPhieuTL varchar(30) NOT NULL,
    MaTaiSan varchar(30) NOT NULL,
    NguyenGia decimal(18,2) NOT NULL CHECK (NguyenGia >= 0),
    GiaTriConLai decimal(18,2) NOT NULL CHECK (GiaTriConLai >= 0),
    GiaTriThuHoi decimal(18,2) NOT NULL DEFAULT 0 CHECK (GiaTriThuHoi >= 0),
    LyDoThanhLy nvarchar(255) NOT NULL,
    HinhThucXuLy nvarchar(100) NOT NULL,
    PRIMARY KEY (SoPhieuTL, MaTaiSan),
    CONSTRAINT FK_CTTL_Phieu FOREIGN KEY (SoPhieuTL) REFERENCES PhieuThanhLy(SoPhieuTL) ON DELETE CASCADE,
    CONSTRAINT FK_CTTL_TaiSan FOREIGN KEY (MaTaiSan) REFERENCES TaiSan(MaTaiSan)
);
GO

-- =========================================================================================
-- 4. TẠO INDEX TỐI ƯU HIỆU NĂNG TRUY VẤN
-- =========================================================================================
CREATE INDEX IX_NguoiDung_PhongBan ON NguoiDung(MaPhongBan, MaVaiTro);
CREATE INDEX IX_TaiSan_Loai_PhongBan ON TaiSan(MaLoai, MaPhongBan, TinhTrang);
CREATE INDEX IX_PhieuCapPhat_Ngay ON PhieuCapPhat(NgayCapPhat, MaPhongBanNhan);
CREATE INDEX IX_PhieuDieuChuyen_Ngay ON PhieuDieuChuyen(NgayDieuChuyen, MaPhongBanChuyen);
CREATE INDEX IX_YeuCauBaoTri_TrangThai ON YeuCauBaoTri(TrangThai, MucDoUuTien);
CREATE INDEX IX_PhieuBaoTri_Ngay ON PhieuBaoTri(NgayLap, KetQuaXuLy);
CREATE INDEX IX_PhieuKiemKe_Dot ON PhieuKiemKe(DotKiemKe, MaPhongBan);
CREATE INDEX IX_PhieuThanhLy_TrangThai ON PhieuThanhLy(TrangThaiDuyet, NgayThanhLy);
GO

-- =========================================================================================
-- 5. CHÈN DỮ LIỆU KHỞI TẠO MẪU (SEED DATA - MÔI TRƯỜNG THỰC TẾ HCMUNRE)
-- =========================================================================================

-- 5.1 Bảng VaiTro
INSERT INTO VaiTro (MaVaiTro, TenVaiTro, MoTa) VALUES
('VT_ADMIN', N'Quản trị viên', N'Quản trị hệ thống, cấp tài khoản và cấu hình danh mục'),
('VT_QLTS', N'Nhân viên Quản lý tài sản', N'Thực hiện toàn bộ nghiệp vụ quản lý vòng đời tài sản'),
('VT_DONVI', N'Đơn vị sử dụng', N'Đại diện các phòng ban tiếp nhận và báo hỏng tài sản'),
('VT_LANHDAO', N'Ban Lãnh đạo', N'Giám sát kiểm kê, phê duyệt thanh lý và báo cáo');

-- 5.2 Bảng PhongBan
INSERT INTO PhongBan (MaPhongBan, TenPhongBan, DiaDiem, SDTLienHe) VALUES
('PB_KHO', N'Kho tài sản trung tâm', N'Tầng hầm Tòa A, 236B Lê Văn Sỹ, Tân Bình', '02838443001'),
('PB_CNTT', N'Khoa Công nghệ Thông tin', N'Lầu 3 Tòa B, 236B Lê Văn Sỹ, Tân Bình', '02838443002'),
('PB_QTTB', N'Phòng Quản trị - Thiết bị', N'Phòng 102 Tòa A, 236B Lê Văn Sỹ, Tân Bình', '02838443003'),
('PB_KHTC', N'Phòng Kế hoạch - Tài chính', N'Phòng 201 Tòa A, 236B Lê Văn Sỹ, Tân Bình', '02838443004');

-- 5.3 Bảng NguoiDung
INSERT INTO NguoiDung (MaNguoiDung, MaVaiTro, MaPhongBan, HoTen, TenDangNhap, MatKhau, Email, SoDienThoai, TrangThai) VALUES
('ND_ADMIN', 'VT_ADMIN', 'PB_QTTB', N'Quản Trị Viên', 'admin', 'hash_admin_123', 'admin@hcmunre.edu.vn', '0901000001', N'Đang hoạt động'),
('ND_QL01', 'VT_QLTS', 'PB_QTTB', N'Trương Gia Phát', 'tgphat', 'hash_pass_123', 'tgphat@hcmunre.edu.vn', '0901000002', N'Đang hoạt động'),
('ND_DV01', 'VT_DONVI', 'PB_CNTT', N'TS. Lê Hoàng Nam', 'lhnam', 'hash_pass_123', 'lhnam@hcmunre.edu.vn', '0901000003', N'Đang hoạt động'),
('ND_LD01', 'VT_LANHDAO', 'PB_QTTB', N'ThS. Nguyễn Thị Thu', 'ntthu', 'hash_pass_123', 'ntthu@hcmunre.edu.vn', '0901000004', N'Đang hoạt động');

-- 5.4 Bảng LoaiTaiSan
INSERT INTO LoaiTaiSan (MaLoai, TenLoai, TyLeKhauHao, ThoiGianSuDung, GhiChu) VALUES
('LTS_MT', N'Máy vi tính & Máy chủ', 20.00, 5, N'Khấu hao 20% mỗi năm theo quy định'),
('LTS_MC', N'Máy chiếu giảng đường', 25.00, 4, N'Thiết bị phục vụ giảng dạy'),
('LTS_BG', N'Bàn ghế văn phòng & phòng lab', 10.00, 10, N'Bàn ghế chống tĩnh điện và hội trường');

-- 5.5 Bảng NhaCungCap
INSERT INTO NhaCungCap (MaNCC, TenNCC, DiaChi, SoDienThoai, MaSoThue) VALUES
('NCC_DELL', N'Công ty TNHH Dell Technologies VN', N'Quận 1, TP. Hồ Chí Minh', '02838222333', '0301112223'),
('NCC_SONY', N'Công ty CP Thiết bị Trình chiếu Sony', N'Quận 3, TP. Hồ Chí Minh', '02839333444', '0304445556'),
('NCC_HOAPHAT', N'Công ty CP Nội Thất Hòa Phát', N'Bình Thạnh, TP. Hồ Chí Minh', '02835111222', '0307778889');

-- 5.6 Bảng TaiSan
INSERT INTO TaiSan (MaTaiSan, MaLoai, MaNCC, MaPhongBan, TenTaiSan, DonViTinh, NguyenGia, GiaTriConLai, NgayTiepNhan, TinhTrang, ViTri, NamSanXuat, SoHoaDon) VALUES
('TS_PC01', 'LTS_MT', 'NCC_DELL', 'PB_CNTT', N'Máy tính đồng bộ Dell OptiPlex 7090', N'Bộ', 22500000, 18000000, '2025-01-15', N'Đang sử dụng', N'Phòng Lab 302', 2024, 'HD-DELL-001'),
('TS_PC02', 'LTS_MT', 'NCC_DELL', 'PB_CNTT', N'Máy tính đồng bộ Dell OptiPlex 7090', N'Bộ', 22500000, 18000000, '2025-01-15', N'Đang sử dụng', N'Phòng Lab 302', 2024, 'HD-DELL-001'),
('TS_MC01', 'LTS_MC', 'NCC_SONY', 'PB_CNTT', N'Máy chiếu Laser Sony VPL-CWZ10', N'Cái', 32000000, 24000000, '2025-02-10', N'Đang sử dụng', N'Hội trường B301', 2024, 'HD-SONY-002'),
('TS_PC03', 'LTS_MT', 'NCC_DELL', 'PB_KHO', N'Máy tính trạm Dell Precision 3660', N'Bộ', 45000000, 45000000, '2026-03-01', N'Trong kho', N'Kho trung tâm A', 2025, 'HD-DELL-003'),
('TS_BG01', 'LTS_BG', 'NCC_HOAPHAT', 'PB_CNTT', N'Bàn thực hành máy tính 6 chỗ', N'Cái', 8500000, 3000000, '2021-08-15', N'Chờ thanh lý', N'Phòng Lab 302', 2021, 'HD-HP-004');

-- 5.7 Bảng PhieuCapPhat & ChiTietCapPhat
INSERT INTO PhieuCapPhat (SoPhieuCP, MaNVLap, MaPhongBanNhan, NgayCapPhat, LyDoCapPhat, NguoiNhanDaiDien, GhiChu) VALUES
('PCP2025-001', 'ND_QL01', 'PB_CNTT', '2025-01-16', N'Trang bị máy tính và máy chiếu phục vụ thực hành môn CNPM', N'TS. Lê Hoàng Nam', N'Bàn giao đầy đủ phụ kiện');

INSERT INTO ChiTietCapPhat (SoPhieuCP, MaTaiSan, SoLuong, TinhTrangBanGiao, ViTriMoi) VALUES
('PCP2025-001', 'TS_PC01', 1, N'Mới 100% nguyên seal', N'Bàn số 01 - Phòng Lab 302'),
('PCP2025-001', 'TS_PC02', 1, N'Mới 100% nguyên seal', N'Bàn số 02 - Phòng Lab 302'),
('PCP2025-001', 'TS_MC01', 1, N'Mới 100%', N'Trần Hội trường B301');

-- 5.8 Bảng YeuCauBaoTri, PhieuBaoTri, ChiTietBaoTri & PhieuDenBu
INSERT INTO YeuCauBaoTri (MaYeuCau, MaTaiSan, MaNVGui, NgayGui, MoTaSuCo, MucDoUuTien, TrangThai) VALUES
('YCBT2026-001', 'TS_PC01', 'ND_DV01', '2026-04-10 09:30:00', N'Máy không lên nguồn, quạt tản nhiệt kêu to', N'Cao', N'Đang xử lý');

INSERT INTO PhieuBaoTri (SoPhieuBT, MaYeuCau, MaNVLap, NgayLap, NgayHoanThanh, DonViSuaChua, TongChiPhi, KetQuaXuLy) VALUES
('PBT2026-001', 'YCBT2026-001', 'ND_QL01', '2026-04-11', '2026-04-12', N'Trung tâm Bảo hành Dell Care', 1850000, N'Đạt yêu cầu');

INSERT INTO ChiTietBaoTri (SoPhieuBT, MaTaiSan, HienTrangHuHong, NoiDungSuaChua, ChiPhi, TinhTrangSauXuLy) VALUES
('PBT2026-001', 'TS_PC01', N'Hỏng bộ nguồn và đứt dây cắm do kéo lê mạnh', N'Thay mới bộ nguồn chính hãng Dell 500W', 1850000, N'Hoạt động bình thường');

INSERT INTO PhieuDenBu (SoPhieuDB, SoPhieuBT, MaNVBoiThuong, SoTienDenBu, LyDoDenBu, HinhThucXuLy, HanChotBoiThuong, TrangThai) VALUES
('PDB2026-001', 'PBT2026-001', 'ND_DV01', 925000, N'Chịu 50% chi phí do bất cẩn làm đứt cáp nguồn', N'Khấu trừ lương tháng 05/2026', '2026-05-15', N'Chưa bồi thường');

-- 5.9 Bảng PhieuKiemKe & ChiTietKiemKe
INSERT INTO PhieuKiemKe (SoPhieuKK, MaPhongBan, MaNVChuTri, NgayKiemKe, DotKiemKe, TruongBanKiemKe, GhiChuChung) VALUES
('PKK2026-Q1', 'PB_CNTT', 'ND_LD01', '2026-03-30', N'Kiểm kê tài sản Quý 1/2026', N'ThS. Nguyễn Thị Thu', N'Khảo sát toàn bộ thiết bị tại dãy phòng Lab');

INSERT INTO ChiTietKiemKe (SoPhieuKK, MaTaiSan, SoLuongHeThong, SoLuongThucTe, TinhTrangThucTe, DeXuatXuLy) VALUES
('PKK2026-Q1', 'TS_PC01', 1, 1, N'Tốt, đang sử dụng', N'Tiếp tục khai thác'),
('PKK2026-Q1', 'TS_PC02', 1, 1, N'Tốt, đang sử dụng', N'Tiếp tục khai thác'),
('PKK2026-Q1', 'TS_BG01', 1, 1, N'Gãy chân bàn, xuống cấp', N'Đề xuất lập hội đồng thanh lý');

-- 5.10 Bảng PhieuThanhLy & ChiTietThanhLy
INSERT INTO PhieuThanhLy (SoPhieuTL, MaNVLap, MaNVDuyet, NgayThanhLy, PhuongThucThanhLy, TongNguyenGia, TongGiaTriThuHoi, TrangThaiDuyet) VALUES
('PTL2026-001', 'ND_QL01', 'ND_LD01', '2026-04-05', N'Bán phế liệu thu hồi', 8500000, 500000, N'Đã phê duyệt');

INSERT INTO ChiTietThanhLy (SoPhieuTL, MaTaiSan, NguyenGia, GiaTriConLai, GiaTriThuHoi, LyDoThanhLy, HinhThucXuLy) VALUES
('PTL2026-001', 'TS_BG01', 8500000, 3000000, 500000, N'Hết khấu hao và hư hại không thể tái sử dụng', N'Tiêu hủy phế liệu');
GO

-- =========================================================================================
-- 6. CÁC VIEW (KHUNG NHÌN BÁO CÁO & THỐNG KÊ NGHIỆP VỤ)
-- =========================================================================================
GO

-- 6.1 View Hồ Sơ Tài Sản Toàn Diện (JOIN 4 bảng để lấy thông tin chi tiết)
CREATE OR ALTER VIEW v_HoSoTaiSanChiTiet
AS
SELECT 
    ts.MaTaiSan,
    ts.TenTaiSan,
    lts.TenLoai,
    lts.TyLeKhauHao,
    ncc.TenNCC,
    pb.TenPhongBan AS DonViSuDung,
    ts.DonViTinh,
    ts.NguyenGia,
    ts.GiaTriConLai,
    ts.NgayTiepNhan,
    ts.TinhTrang,
    ts.ViTri,
    ts.SoHoaDon
FROM TaiSan ts
INNER JOIN LoaiTaiSan lts ON ts.MaLoai = lts.MaLoai
INNER JOIN NhaCungCap ncc ON ts.MaNCC = ncc.MaNCC
LEFT JOIN PhongBan pb ON ts.MaPhongBan = pb.MaPhongBan;
GO

-- 6.2 View Báo Cáo Chênh Lệch Kiểm Kê (Thừa/Thiếu Tài Sản)
CREATE OR ALTER VIEW v_BaoCaoChenhLechKiemKe
AS
SELECT 
    kk.SoPhieuKK,
    kk.DotKiemKe,
    kk.NgayKiemKe,
    pb.TenPhongBan,
    ct.MaTaiSan,
    ts.TenTaiSan,
    ct.SoLuongHeThong,
    ct.SoLuongThucTe,
    ct.ChenhLech,
    ct.TinhTrangThucTe,
    ct.DeXuatXuLy
FROM ChiTietKiemKe ct
INNER JOIN PhieuKiemKe kk ON ct.SoPhieuKK = kk.SoPhieuKK
INNER JOIN TaiSan ts ON ct.MaTaiSan = ts.MaTaiSan
INNER JOIN PhongBan pb ON kk.MaPhongBan = pb.MaPhongBan;
GO

-- 6.3 View Thống Kê Tổng Hợp Chi Phí Bảo Trì & Đền Bù Theo Phòng Ban
CREATE OR ALTER VIEW v_ThongKeBaoTriTheoPhongBan
AS
SELECT 
    pb.MaPhongBan,
    pb.TenPhongBan,
    COUNT(DISTINCT pbt.SoPhieuBT) AS SoLuotBaoTri,
    ISNULL(SUM(ctbt.ChiPhi), 0) AS TongChiPhiSuaChua,
    ISNULL(SUM(pdb.SoTienDenBu), 0) AS TongTienThuHoiDenBu
FROM PhongBan pb
LEFT JOIN TaiSan ts ON pb.MaPhongBan = ts.MaPhongBan
LEFT JOIN ChiTietBaoTri ctbt ON ts.MaTaiSan = ctbt.MaTaiSan
LEFT JOIN PhieuBaoTri pbt ON ctbt.SoPhieuBT = pbt.SoPhieuBT
LEFT JOIN PhieuDenBu pdb ON pbt.SoPhieuBT = pdb.SoPhieuBT
GROUP BY pb.MaPhongBan, pb.TenPhongBan;
GO

-- =========================================================================================
-- 7. CÁC STORED PROCEDURE (THỦ TỤC XỬ LÝ NGHIỆP VỤ)
-- =========================================================================================
GO

-- 7.1 Store Tiếp nhận tài sản mới vào kho (Kiểm tra hợp lệ & Tự động gán giá trị)
CREATE OR ALTER PROCEDURE sp_TiepNhanTaiSan
    @MaTaiSan varchar(30),
    @MaLoai varchar(20),
    @MaNCC varchar(20),
    @TenTaiSan nvarchar(150),
    @DonViTinh nvarchar(20),
    @NguyenGia decimal(18,2),
    @NamSanXuat int,
    @SoHoaDon varchar(50)
AS
BEGIN
    SET NOCOUNT ON;

    -- Kiểm tra mã tài sản trùng lặp
    IF EXISTS (SELECT 1 FROM TaiSan WHERE MaTaiSan = @MaTaiSan)
    BEGIN
        RAISERROR(N'LỖI: Mã tài sản này đã tồn tại trong hệ thống!', 16, 1);
        RETURN;
    END

    -- Kiểm tra khóa ngoại Loại tài sản và Nhà cung cấp
    IF NOT EXISTS (SELECT 1 FROM LoaiTaiSan WHERE MaLoai = @MaLoai)
    BEGIN
        RAISERROR(N'LỖI: Mã loại tài sản không hợp lệ!', 16, 1);
        RETURN;
    END

    IF NOT EXISTS (SELECT 1 FROM NhaCungCap WHERE MaNCC = @MaNCC)
    BEGIN
        RAISERROR(N'LỖI: Nhà cung cấp không tồn tại!', 16, 1);
        RETURN;
    END

    -- Thêm mới tài sản: Khi mới tiếp nhận, Giá trị còn lại = Nguyên giá, Vị trí ở Kho, Phòng ban là PB_KHO
    INSERT INTO TaiSan (MaTaiSan, MaLoai, MaNCC, MaPhongBan, TenTaiSan, DonViTinh, NguyenGia, GiaTriConLai, NgayTiepNhan, TinhTrang, ViTri, NamSanXuat, SoHoaDon)
    VALUES (@MaTaiSan, @MaLoai, @MaNCC, 'PB_KHO', @TenTaiSan, @DonViTinh, @NguyenGia, @NguyenGia, CAST(GETDATE() AS date), N'Trong kho', N'Kho trung tâm', @NamSanXuat, @SoHoaDon);

    PRINT N'Tiếp nhận tài sản mới vào kho thành công!';
END;
GO

-- 7.2 Store Lập Phiếu Cấp Phát Tài Sản (Sử dụng Transaction an toàn)
CREATE OR ALTER PROCEDURE sp_LapPhieuCapPhat
    @SoPhieuCP varchar(30),
    @MaNVLap varchar(20),
    @MaPhongBanNhan varchar(20),
    @LyDoCapPhat nvarchar(255),
    @NguoiNhanDaiDien nvarchar(120),
    @GhiChu nvarchar(255) = NULL
AS
BEGIN
    SET NOCOUNT ON;

    -- Kiểm tra phòng ban nhận
    IF NOT EXISTS (SELECT 1 FROM PhongBan WHERE MaPhongBan = @MaPhongBanNhan)
    BEGIN
        RAISERROR(N'LỖI: Phòng ban tiếp nhận không tồn tại!', 16, 1);
        RETURN;
    END

    -- Kiểm tra nhân viên lập
    IF NOT EXISTS (SELECT 1 FROM NguoiDung WHERE MaNguoiDung = @MaNVLap AND TrangThai = N'Đang hoạt động')
    BEGIN
        RAISERROR(N'LỖI: Nhân viên lập phiếu không tồn tại hoặc tài khoản bị khóa!', 16, 1);
        RETURN;
    END

    -- Thêm phiếu cấp phát
    INSERT INTO PhieuCapPhat (SoPhieuCP, MaNVLap, MaPhongBanNhan, NgayCapPhat, LyDoCapPhat, NguoiNhanDaiDien, GhiChu)
    VALUES (@SoPhieuCP, @MaNVLap, @MaPhongBanNhan, CAST(GETDATE() AS date), @LyDoCapPhat, @NguoiNhanDaiDien, @GhiChu);

    PRINT N'Tạo phiếu cấp phát thành công. Vui lòng thêm các dòng chi tiết tài sản!';
END;
GO

-- 7.3 Store Cập Nhật Hoàn Tất Bảo Trì & Tính Tổng Chi Phí
CREATE OR ALTER PROCEDURE sp_HoanThanhBaoTri
    @SoPhieuBT varchar(30),
    @KetQuaXuLy nvarchar(50) -- 'Đạt yêu cầu' hoặc 'Không thể phục hồi'
AS
BEGIN
    SET NOCOUNT ON;

    IF NOT EXISTS (SELECT 1 FROM PhieuBaoTri WHERE SoPhieuBT = @SoPhieuBT)
    BEGIN
        RAISERROR(N'LỖI: Phiếu bảo trì không tồn tại!', 16, 1);
        RETURN;
    END

    -- Tính tổng chi phí từ bảng ChiTietBaoTri
    DECLARE @TongTien decimal(18,2);
    SELECT @TongTien = ISNULL(SUM(ChiPhi), 0) FROM ChiTietBaoTri WHERE SoPhieuBT = @SoPhieuBT;

    -- Cập nhật phiếu bảo trì
    UPDATE PhieuBaoTri
    SET NgayHoanThanh = CAST(GETDATE() AS date),
        TongChiPhi = @TongTien,
        KetQuaXuLy = @KetQuaXuLy
    WHERE SoPhieuBT = @SoPhieuBT;

    -- Nếu kết quả đạt yêu cầu, trả trạng thái tài sản về 'Đang sử dụng', ngược lại chuyển sang 'Chờ thanh lý'
    IF @KetQuaXuLy = N'Đạt yêu cầu'
        UPDATE TaiSan SET TinhTrang = N'Đang sử dụng' 
        WHERE MaTaiSan IN (SELECT MaTaiSan FROM ChiTietBaoTri WHERE SoPhieuBT = @SoPhieuBT);
    ELSE
        UPDATE TaiSan SET TinhTrang = N'Chờ thanh lý' 
        WHERE MaTaiSan IN (SELECT MaTaiSan FROM ChiTietBaoTri WHERE SoPhieuBT = @SoPhieuBT);

    PRINT N'Cập nhật kết quả bảo trì và đồng bộ tình trạng tài sản thành công!';
END;
GO

-- =========================================================================================
-- 8. CÁC TRIGGER (RÀNG BUỘC TOÀN VẸN & TỰ ĐỘNG ĐỒNG BỘ DỮ LIỆU)
-- =========================================================================================
GO

-- 8.1 Trigger BẮT LỖI: Chặn cấp phát nếu tài sản KHÔNG Ở TRẠNG THÁI 'Trong kho'
CREATE OR ALTER TRIGGER trg_KiemTraTaiSanTruocKhiCapPhat
ON ChiTietCapPhat
FOR INSERT, UPDATE
AS
BEGIN
    SET NOCOUNT ON;

    IF EXISTS (
        SELECT 1 
        FROM TaiSan ts
        INNER JOIN inserted i ON ts.MaTaiSan = i.MaTaiSan
        WHERE ts.TinhTrang <> N'Trong kho'
    )
    BEGIN
        RAISERROR (N'LỖI NGHIỆP VỤ: Tài sản được chọn hiện KHÔNG Ở TRONG KHO (đang sử dụng, bảo trì hoặc thanh lý), không thể cấp phát!', 16, 1);
        ROLLBACK TRANSACTION;
        RETURN;
    END
END;
GO

-- 8.2 Trigger TỰ ĐỘNG ĐỒNG BỘ: Chuyển trạng thái sang 'Đang sử dụng' & gán Phòng ban sau khi cấp phát
CREATE OR ALTER TRIGGER trg_CapNhatTrangThai_SauCapPhat
ON ChiTietCapPhat
AFTER INSERT
AS
BEGIN
    SET NOCOUNT ON;

    -- Cập nhật tình trạng tài sản và phòng ban sử dụng mới
    UPDATE ts
    SET ts.TinhTrang = N'Đang sử dụng',
        ts.MaPhongBan = pcp.MaPhongBanNhan,
        ts.ViTri = i.ViTriMoi
    FROM TaiSan ts
    INNER JOIN inserted i ON ts.MaTaiSan = i.MaTaiSan
    INNER JOIN PhieuCapPhat pcp ON i.SoPhieuCP = pcp.SoPhieuCP;
END;
GO

-- 8.3 Trigger TỰ ĐỘNG: Đổi trạng thái tài sản thành 'Đang bảo trì' khi tiếp nhận yêu cầu bảo trì
CREATE OR ALTER TRIGGER trg_CapNhatTrangThai_KhiTaoPhieuBaoTri
ON ChiTietBaoTri
AFTER INSERT
AS
BEGIN
    SET NOCOUNT ON;

    UPDATE TaiSan
    SET TinhTrang = N'Đang bảo trì'
    WHERE MaTaiSan IN (SELECT MaTaiSan FROM inserted);
END;
GO

-- 8.4 Trigger TỰ ĐỘNG ĐỒNG BỘ: Đổi trạng thái tài sản sang 'Đã thanh lý' khi phiếu thanh lý ĐÃ PHÊ DUYỆT
CREATE OR ALTER TRIGGER trg_DongBoThanhLyTaiSan
ON PhieuThanhLy
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;

    -- Khi phiếu thanh lý được chuyển trạng thái sang 'Đã phê duyệt'
    IF EXISTS (SELECT 1 FROM inserted WHERE TrangThaiDuyet = N'Đã phê duyệt')
    BEGIN
        UPDATE ts
        SET ts.TinhTrang = N'Đã thanh lý',
            ts.GiaTriConLai = 0
        FROM TaiSan ts
        INNER JOIN ChiTietThanhLy cttl ON ts.MaTaiSan = cttl.MaTaiSan
        INNER JOIN inserted i ON cttl.SoPhieuTL = i.SoPhieuTL
        WHERE i.TrangThaiDuyet = N'Đã phê duyệt';
    END
END;
GO