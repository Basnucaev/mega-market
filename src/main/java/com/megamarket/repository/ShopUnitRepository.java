package com.megamarket.repository;

import com.megamarket.entity.ShopUnit;
import com.megamarket.entity.enums.ShopUnitType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ShopUnitRepository  extends JpaRepository<ShopUnit, UUID> {

    ShopUnit findByIdAndType(UUID id, ShopUnitType type);

    List<ShopUnit> findAllByType(ShopUnitType type);

}
