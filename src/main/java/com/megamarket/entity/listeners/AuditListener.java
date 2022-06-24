package com.megamarket.entity.listeners;

import com.megamarket.entity.HistoryOfShopUnit;
import com.megamarket.entity.ShopUnit;
import com.megamarket.repository.HistoryOfShopUnitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

@Component
public class AuditListener {
    private static HistoryOfShopUnitRepository historyOfShopUnitRepository;

    @Autowired
    public void setRepository(HistoryOfShopUnitRepository historyOfShopUnitRepository) {
        AuditListener.historyOfShopUnitRepository = historyOfShopUnitRepository;
    }

    @PrePersist
    @PreUpdate
    @Transactional
    void afterSaveOrUpdate(ShopUnit shopUnit) {
        HistoryOfShopUnit historyOfShopUnit = new HistoryOfShopUnit();
        historyOfShopUnit.setShopUnitId(shopUnit.getId());
        historyOfShopUnit.setName(shopUnit.getName());
        if (shopUnit.getParent() != null)
            historyOfShopUnit.setParentId(shopUnit.getParent().getId());
        historyOfShopUnit.setUpdateDate(shopUnit.getDate());
        historyOfShopUnit.setType(shopUnit.getType());
        historyOfShopUnit.setPrice(shopUnit.getPrice());

        historyOfShopUnitRepository.save(historyOfShopUnit);
    }
}
