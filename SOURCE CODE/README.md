# HỆ THỐNG QUẢN LÝ TÀI SẢN CỐ ĐỊNH (HCMUNRE)
> **Đồ án môn học:** Phương pháp phát triển phần mềm hướng đối tượng (OOSD)  
> **Trường Đại học:** Tài nguyên và Môi trường Thành phố Hồ Chí Minh (HCMUNRE)  
> **Khoa:** Công nghệ Thông tin  
> **Sinh viên thực hiện:** Trương Gia Phát — **MSSV:** 1250080139 — **Lớp:** 12_ĐH_CNPM2  
> **Giảng viên hướng dẫn:** ThS. Phạm Trọng Huynh  

---

## 📌 1. Giới thiệu tổng quan

Hệ thống **Quản lý tài sản cố định** là giải pháp phần mềm web giúp tin học hóa và số hóa toàn diện quy trình quản lý vòng đời tài sản công tại Trường Đại học Tài nguyên và Môi trường TP.HCM. Hệ thống được xây dựng bám sát theo các quy định hiện hành của Nhà nước về quản lý tài sản công (**Thông tư 23/2023/TT-BTC** của Bộ Tài chính).

### Các phân hệ chức năng chính:
* **Xác thực & Phân quyền (RBAC):** Kiểm soát truy cập chặt chẽ theo 4 nhóm vai trò (*Quản trị viên, Cán bộ QLTS, Đơn vị sử dụng, Ban Lãnh đạo*).
* **Quản lý danh mục:** Quản lý Loại tài sản, Phòng ban/Khoa/Trung tâm, Nhà cung cấp, Người dùng.
* **Tiếp nhận & Lý lịch tài sản:** Quản lý mã tài sản, nguồn gốc, nguyên giá, tình trạng sử dụng và hồ sơ chi tiết.
* **Cấp phát & Bàn giao:** Lập phiếu cấp phát, phân bổ tài sản cho các đơn vị sử dụng trực thuộc.
* **Điều chuyển nội bộ:** Quản lý quy trình luân chuyển tài sản giữa các khoa, phòng ban trong trường.
* **Bảo trì, sửa chữa & Đền bù:** Tiếp nhận yêu cầu báo hỏng từ đơn vị, theo dõi tiến trình sửa chữa, chi phí phát sinh và lập phiếu đền bù hư hỏng.
* **Kiểm kê định kỳ:** Khởi tạo đợt kiểm kê, đối soát tự động số lượng thực tế với sổ sách CSDL, phát hiện chênh lệch thừa/thiếu.
* **Thanh lý tài sản:** Thành lập hội đồng, xử lý thanh lý các tài sản đã hết niên hạn khấu hao hoặc hư hỏng không thể phục hồi.
* **Báo cáo & Tính khấu hao:** Tự động tính toán hao mòn lũy kế, trích khấu hao tài sản theo phương pháp đường thẳng và kết xuất báo cáo thống kê trực quan.

---

## 🛠️ 2. Công nghệ sử dụng

* **Ngôn ngữ lập trình:** Java 21 LTS
* **Bộ khung ứng dụng (Framework):** Spring Boot 3.3.5
  * *Spring MVC:* Điều hướng và xử lý yêu cầu HTTP Web
  * *Spring Data JPA / Hibernate:* Ánh xạ quan hệ đối tượng (ORM) và thao tác cơ sở dữ liệu
  * *Spring Security Interceptor:* Kiểm soát phiên làm việc (Session) và phân quyền vai trò người dùng
* **Giao diện người dùng (Frontend):**
  * *Thymeleaf Template Engine:* Kết xuất giao diện động từ phía máy chủ
  * *Bootstrap 5.3:* Thiết kế giao diện phản hồi (Responsive Design)
  * *Font Awesome 6.5:* Hệ thống biểu tượng giao diện
  * *Chart.js:* Vẽ biểu đồ thống kê trực quan
* **Hệ quản trị cơ sở dữ liệu:** Microsoft SQL Server 2019+
* **Công cụ build:** Apache Maven 3.9+
* **Kiểm thử hệ thống:** JUnit 5, Mockito, Apache JMeter 5.6

---

## 📋 3. Yêu cầu môi trường (Prerequisites)

Trước khi cài đặt và khởi chạy dự án, máy tính cần được cài đặt sẵn:

1. **Java Development Kit (JDK):** Phiên bản **Java 21 LTS** hoặc mới hơn.
2. **Hệ quản trị CSDL:** **Microsoft SQL Server** (2017, 2019, 2022) kèm công cụ **SQL Server Management Studio (SSMS)**.
3. **Môi trường phát triển (IDE):** IntelliJ IDEA (khuyên dùng), Eclipse IDE, hoặc VS Code (kèm Extension Pack for Java).
4. **Apache Maven:** Tích hợp sẵn trong IDE hoặc cài đặt độc lập phiên bản 3.8+.

---

## 🚀 4. Hướng dẫn cài đặt & Khởi chạy từng bước

### Bước 1: Khởi tạo Cơ sở dữ liệu (Database Setup)
1. Mở phần mềm **SQL Server Management Studio (SSMS)** và đăng nhập vào SQL Server instance của bạn (mặc định cổng `1433`).
2. Mở file script SQL: `QuanLyTaiSanCoDinh.sql` (nằm ngay trong thư mục này).
3. Bấm nút **Execute** (hoặc nhấn phím `F5`) để thực thi script.
4. *Kết quả:* Hệ thống sẽ tự động tạo cơ sở dữ liệu `QuanLyTaiSanCoDinh`, thiết lập toàn bộ bảng dữ liệu, khóa chính, khóa ngoại, ràng buộc toàn vẹn và nạp sẵn dữ liệu mẫu.

---

### Bước 2: Cấu hình kết nối CSDL (Configuration)
Mở file `quanlytaisan/src/main/resources/application.properties` và kiểm tra thông tin tài khoản kết nối SQL Server:

```properties
server.port=8080

# Cấu hình chuỗi kết nối SQL Server
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=QuanLyTaiSanCoDinh;encrypt=true;trustServerCertificate=true;
spring.datasource.username=sa
spring.datasource.password=123
spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver
```

> ⚠️ **Lưu ý:** Nếu tài khoản đăng nhập SQL Server của bạn khác `sa` hoặc mật khẩu khác `123`, vui lòng sửa lại tương ứng với máy tính của bạn.

---

### Bước 3: Khởi chạy ứng dụng (Run Application)

#### Cách 1: Chạy trực tiếp từ IDE (IntelliJ IDEA / Eclipse)
1. Mở IDE và chọn **Open / Import Project** trỏ vào thư mục `quanlytaisan` (dưới dạng Maven Project).
2. Chờ IDE tự động tải các thư viện Maven (Dependencies).
3. Tìm đến file khởi chạy chính:  
   `quanlytaisan/src/main/java/com/hcmunre/qlts/QuanLyTaiSanApplication.java`
4. Nhấp chuột phải chọn **Run 'QuanLyTaiSanApplication'** (hoặc `Shift + F10`).

#### Cách 2: Chạy bằng dòng lệnh (Command Line / Terminal)
Mở cửa sổ Terminal/PowerShell tại thư mục `quanlytaisan` và thực thi lệnh:
```bash
# Tải dependencies và chạy ứng dụng trực tiếp bằng Maven
mvn spring-boot:run
```
*(Hoặc đóng gói thành file thực thi JAR rồi chạy):*
```bash
mvn clean package
java -jar target/quanlytaisan-1.0.0.jar
```

---

### Bước 4: Truy cập hệ thống trên trình duyệt
Sau khi màn hình console hiển thị thông báo Spring Boot đã khởi động thành công trên cổng `8080`:
* Mở trình duyệt web (Chrome, Edge, Firefox) và truy cập địa chỉ:
  👉 **`http://localhost:8080`** hoặc **`http://localhost:8080/login`**

---

## 🔑 5. Danh sách tài khoản thử nghiệm (Test Accounts)

Hệ thống đã nạp sẵn dữ liệu 4 tài khoản đại diện cho 4 vai trò tác nghiệp thực tế. **Mật khẩu mặc định cho tất cả tài khoản mẫu là:** `123`

| STT | Tên đăng nhập | Mật khẩu | Họ và tên | Mã vai trò | Quyền hạn và phạm vi nghiệp vụ |
|:---:|:---|:---:|:---|:---:|:---|
| **1** | `admin` | `123` | Quản Trị Viên | `VT_ADMIN` | Toàn quyền cấu hình hệ thống, quản lý tài khoản người dùng và phân quyền vai trò. |
| **2** | `tgphat` | `123` | Trương Gia Phát | `VT_QLTS` | **Cán bộ QLTS:** Quản lý vòng đời tài sản (tiếp nhận, cấp phát, điều chuyển, bảo trì, thanh lý, tính khấu hao). |
| **3** | `lhnam` | `123` | TS. Lê Hoàng Nam | `VT_DONVI` | **Trưởng khoa CNTT:** Đại diện đơn vị tiếp nhận tài sản, tra cứu tài sản của khoa và gửi yêu cầu bảo trì. |
| **4** | `ntthu` | `123` | ThS. Nguyễn Thị Thu | `VT_LANHDAO` | **Ban Lãnh đạo:** Theo dõi biểu đồ Dashboard tổng quan, giám sát kỳ kiểm kê và phê duyệt thanh lý tài sản. |

---

## 📁 6. Cấu trúc thư mục (Folder Structure)

```text
SOURCE CODE/
├── QuanLyTaiSanCoDinh.sql      # Kịch bản tạo Database và nạp dữ liệu mẫu
├── README.md                   # Hướng dẫn chi tiết cài đặt và khởi chạy hệ thống
└── quanlytaisan/               # Mã nguồn ứng dụng Spring Boot
    ├── pom.xml                 # Cấu hình dự án Maven
    └── src/                    # Toàn bộ mã nguồn Java và giao diện HTML/CSS
```

---

## 🛠️ 7. Xử lý sự cố thường gặp (Troubleshooting)

1. **Lỗi "Cannot connect to SQL Server on localhost:1433":**
   * Mở ứng dụng **SQL Server Configuration Manager** trên Windows.
   * Chọn **SQL Server Network Configuration** -> **Protocols for MSSQLSERVER**.
   * Đảm bảo giao thức **TCP/IP** đang ở trạng thái **Enabled**.
   * Nhấp đúp vào **TCP/IP** -> Thẻ **IP Addresses** -> Kéo xuống mục **IPAll** và đảm bảo **TCP Port** là `1433`.
   * Khởi động lại dịch vụ SQL Server trong mục **SQL Server Services**.

2. **Lỗi "Port 8080 is already in use":**
   * Cổng 8080 đang bị ứng dụng khác (hoặc tiến trình Tomcat cũ) chiếm dụng.
   * Bạn có thể đổi sang cổng khác trong file `application.properties`, ví dụ: `server.port=8088`.
   * Khi đó, truy cập qua: `http://localhost:8088`.

3. **Lỗi sai mật khẩu tài khoản SQL Server:**
   * Mở SSMS, đăng nhập bằng Windows Authentication.
   * Vào mục **Security** -> **Logins** -> Nhấp đúp vào tài khoản `sa` -> Đặt lại mật khẩu thành `123` và bấm OK.

---
*© 2026 — Đồ án môn học Phương pháp phát triển phần mềm hướng đối tượng - Khoa CNTT HCMUNRE.*
