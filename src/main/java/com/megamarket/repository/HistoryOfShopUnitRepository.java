package com.megamarket.repository;

import com.megamarket.entity.HistoryOfShopUnit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface HistoryOfShopUnitRepository extends JpaRepository<HistoryOfShopUnit, Long> {

    List<HistoryOfShopUnit> findAllByLinkedShopUnitIdAndUpdateDateGreaterThanAndUpdateDateLessThan(
            UUID linkedShopUnitId, LocalDateTime from, LocalDateTime to);

    void deleteAllByLinkedShopUnitId(UUID linkedShopUnitId);
}
