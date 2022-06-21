package com.megamarket.service;

import com.megamarket.dto.ShopUnitStatisticUnit;
import com.megamarket.entity.ShopUnit;
import com.megamarket.dto.ShopUnitRequest;

import java.time.LocalDateTime;
import java.util.List;

public interface ShopUnitObjectsParser {

    List<ShopUnit> parseShopUnitRequestObjectToShopUnit(ShopUnitRequest shopUnitRequest);

    ShopUnitStatisticUnit parseShopUnitToShopUnitStatisticUnit(ShopUnit shopUnit);

    LocalDateTime parseStringToDate(String dateTimeString);
}
