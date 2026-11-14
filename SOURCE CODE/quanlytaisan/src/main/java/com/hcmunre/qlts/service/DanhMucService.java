package com.hcmunre.qlts.service;

import com.hcmunre.qlts.model.*;
import com.hcmunre.qlts.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DanhMucService {
    @Autowired
    private PhongBanRepository phongBanRepository;

    @Autowired
    private LoaiTaiSanRepository loaiTaiSanRepository;

    @Autowired
    private NhaCungCapRepository nhaCungCapRepository;

    @Autowired
    private VaiTroRepository vaiTroRepository;

    // PhongBan
    public List<PhongBan> getAllPhongBan() { return phongBanRepository.findAll(); }
    public Optional<PhongBan> getPhongBanById(String id) { return phongBanRepository.findById(id); }
    public PhongBan savePhongBan(PhongBan pb) { return phongBanRepository.save(pb); }
    public void deletePhongBan(String id) { phongBanRepository.deleteById(id); }

    // LoaiTaiSan
    public List<LoaiTaiSan> getAllLoaiTaiSan() { return loaiTaiSanRepository.findAll(); }
    public Optional<LoaiTaiSan> getLoaiTaiSanById(String id) { return loaiTaiSanRepository.findById(id); }
    public LoaiTaiSan saveLoaiTaiSan(LoaiTaiSan lts) { return loaiTaiSanRepository.save(lts); }
    public void deleteLoaiTaiSan(String id) { loaiTaiSanRepository.deleteById(id); }

    // NhaCungCap
    public List<NhaCungCap> getAllNhaCungCap() { return nhaCungCapRepository.findAll(); }
    public Optional<NhaCungCap> getNhaCungCapById(String id) { return nhaCungCapRepository.findById(id); }
    public NhaCungCap saveNhaCungCap(NhaCungCap ncc) { return nhaCungCapRepository.save(ncc); }
    public void deleteNhaCungCap(String id) { nhaCungCapRepository.deleteById(id); }

    // VaiTro
    public List<VaiTro> getAllVaiTro() { return vaiTroRepository.findAll(); }
}
