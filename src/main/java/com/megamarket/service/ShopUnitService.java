package com.megamarket.service;

import com.megamarket.dto.ShopUnitRequest;
import com.megamarket.dto.ShopUnitStatisticResponse;
import com.megamarket.dto.ShopUnitStatisticUnit;

import java.time.LocalDateTime;
import java.util.UUID;

public interface ShopUnitService {

    void saveShopUnit(ShopUnitRequest shopUnitRequest);

    void deleteShopUnitById(UUID id);

    ShopUnitStatisticUnit getShopUnitStatistic(UUID id);

    ShopUnitStatisticResponse getSalesStatistic(String dateTimeString);
}
