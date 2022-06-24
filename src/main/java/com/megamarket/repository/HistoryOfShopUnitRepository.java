package com.megamarket.repository;

import com.megamarket.entity.HistoryOfShopUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface HistoryOfShopUnitRepository extends JpaRepository<HistoryOfShopUnit, Long> {

    List<HistoryOfShopUnit> findAllByShopUnitIdAndUpdateDateGreaterThanAndUpdateDateLessThan(
            UUID linkedShopUnitId, LocalDateTime from, LocalDateTime to);

    void deleteAllByShopUnitId(UUID linkedShopUnitId);
}
