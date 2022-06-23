package com.megamarket.service;

import com.megamarket.dto.ShopUnitRequest;
import com.megamarket.dto.ShopUnitStatisticResponse;
import com.megamarket.dto.ShopUnitStatisticUnit;
import com.megamarket.entity.ShopUnit;

import java.util.List;
import java.util.UUID;

public interface ShopUnitService {

    void saveShopUnit(ShopUnitRequest shopUnitRequest);

    void deleteShopUnitById(UUID id);

    ShopUnitStatisticUnit getShopUnitStatisticById(UUID id);

    ShopUnitStatisticResponse getSalesStatistic(String dateTimeString);

    ShopUnitStatisticResponse getUnitsHistoryById(UUID id, String from, String to);
}
