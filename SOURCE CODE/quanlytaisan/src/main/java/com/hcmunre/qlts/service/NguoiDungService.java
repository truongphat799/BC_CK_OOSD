package com.hcmunre.qlts.service;

import com.hcmunre.qlts.model.NguoiDung;
import com.hcmunre.qlts.repository.NguoiDungRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NguoiDungService {
    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    public Optional<NguoiDung> login(String username, String password) {
        if (username == null || password == null || username.trim().isEmpty() || password.isEmpty()) {
            return Optional.empty();
        }
        Optional<NguoiDung> userOpt = nguoiDungRepository.findByTenDangNhap(username.trim());
        if (userOpt.isPresent()) {
            NguoiDung user = userOpt.get();
            String dbPass = user.getMatKhau();

            boolean passwordMatches = false;
            // 1. Khớp chính xác mật khẩu nhập vào với mật khẩu trong CSDL
            if (password.equals(dbPass)) {
                passwordMatches = true;
            } 
            // 2. Với các tài khoản mẫu ban đầu (lưu hash_pass_123 hoặc hash_admin_123), CHỈ chấp nhận mật khẩu là "123"
            else if (password.equals("123") && (dbPass.equals("hash_pass_123") || dbPass.equals("hash_admin_123"))) {
                passwordMatches = true;
            }

            if (passwordMatches && "Đang hoạt động".equals(user.getTrangThai())) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    public List<NguoiDung> findAll() {
        return nguoiDungRepository.findAll();
    }

    public Optional<NguoiDung> findById(String id) {
        return nguoiDungRepository.findById(id);
    }

    public NguoiDung save(NguoiDung nguoiDung) {
        return nguoiDungRepository.save(nguoiDung);
    }

    public void delete(String id) {
        nguoiDungRepository.deleteById(id);
    }
}
