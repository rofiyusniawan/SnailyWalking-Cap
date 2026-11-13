
package com.snailywalking.snailywalking.service;

import com.snailywalking.snailywalking.dao.TopiDAO;
import com.snailywalking.snailywalking.model.TopiEntity;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TopiService {

    private final TopiDAO topiDAO;

    @Autowired
    public TopiService(TopiDAO topiDAO) {
        this.topiDAO = topiDAO;
    }

    public List<TopiEntity> getAllTopi() {
        return topiDAO.getAll();
    }

    public TopiEntity getTopiByKode(String kode) {
        return topiDAO.getByKode(kode);
    }

    public void tambahTopi(TopiEntity topi) {
        topiDAO.save(topi);
    }

    public void ubahTopi(TopiEntity topi) {
        topiDAO.update(topi);
    }

    public void hapusTopi(String kode) {
        topiDAO.delete(kode);
    }
}
