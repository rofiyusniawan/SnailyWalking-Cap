
package com.snailywalking.snailywalking.service;

import com.snailywalking.snailywalking.dao.PenjualanDAO;
import com.snailywalking.snailywalking.model.PenjualanEntity;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PenjualanService {

    private final PenjualanDAO penjualanDAO;

    @Autowired
    public PenjualanService(PenjualanDAO penjualanDAO) {
        this.penjualanDAO = penjualanDAO;
    }

    public List<PenjualanEntity> getAllPenjualan() {
        return penjualanDAO.getAll();
    }

    public PenjualanEntity getPenjualanByKode(String kode) {
        return penjualanDAO.getByKode(kode);
    }

    public void tambahPenjualan(PenjualanEntity penjualan) {
        penjualanDAO.save(penjualan);
    }

    public void ubahPenjualan(PenjualanEntity penjualan) {
        penjualanDAO.update(penjualan);
    }

    public void hapusPenjualan(String kode) {
        penjualanDAO.delete(kode);
    }
}
