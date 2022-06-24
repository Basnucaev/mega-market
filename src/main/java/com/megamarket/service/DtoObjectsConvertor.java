package com.megamarket.service;

import com.megamarket.dto.ShopUnitStatisticUnit;
import com.megamarket.entity.HistoryOfShopUnit;
import com.megamarket.entity.ShopUnit;
import com.megamarket.dto.ShopUnitRequest;

import java.time.LocalDateTime;
import java.util.List;

public interface DtoObjectsConvertor {

    List<ShopUnit> parseShopUnitRequestObjectToShopUnitList(ShopUnitRequest shopUnitRequest);

    ShopUnitStatisticUnit parseShopUnitToStatisticUnit(ShopUnit shopUnit);

    LocalDateTime parseStringToDate(String dateTimeString);

    List<ShopUnitStatisticUnit> parseHistoryUnitsToStatisticUnits(List<HistoryOfShopUnit> historyOfUnitList);
}
