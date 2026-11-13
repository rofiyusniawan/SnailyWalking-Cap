
package com.snailywalking.snailywalking.service;

import com.snailywalking.snailywalking.dao.PembelianDAO;
import com.snailywalking.snailywalking.model.PembelianEntity;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PembelianService {

    private final PembelianDAO pembelianDAO;

    @Autowired
    public PembelianService(PembelianDAO pembelianDAO) {
        this.pembelianDAO = pembelianDAO;
    }

    public List<PembelianEntity> getAllPembelian() {
        return pembelianDAO.getAll();
    }

    public PembelianEntity getPembelianByKode(String kode) {
        return pembelianDAO.getByKode(kode);
    }

    public void tambahPembelian(PembelianEntity pembelian) {
        pembelianDAO.save(pembelian);
    }

    public void ubahPembelian(PembelianEntity pembelian) {
        pembelianDAO.update(pembelian);
    }

    public void hapusPembelian(String kode) {
        pembelianDAO.delete(kode);
    }
}
