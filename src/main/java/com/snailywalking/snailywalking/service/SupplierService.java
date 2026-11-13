
package com.snailywalking.snailywalking.service;

import com.snailywalking.snailywalking.dao.SupplierDAO;
import com.snailywalking.snailywalking.model.SupplierEntity;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SupplierService {

    private final SupplierDAO supplierDAO;

    @Autowired
    public SupplierService(SupplierDAO supplierDAO) {
        this.supplierDAO = supplierDAO;
    }

    public List<SupplierEntity> getAllSupplier() {
        return supplierDAO.getAll();
    }

    public SupplierEntity getSupplierByKode(String kode) {
        return supplierDAO.getByKode(kode);
    }

    public void tambahSupplier(SupplierEntity supplier) {
        supplierDAO.save(supplier);
    }

    public void ubahSupplier(SupplierEntity supplier) {
        supplierDAO.update(supplier);
    }

    public void hapusSupplier(String kode) {
        supplierDAO.delete(kode);
    }
}
