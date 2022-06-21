package com.megamarket.service;

import com.megamarket.dto.ShopUnitImport;
import com.megamarket.dto.ShopUnitRequest;
import com.megamarket.entity.ShopUnit;

import java.util.List;

public interface Validator {

    void validateListOfShopUnitImport(List<ShopUnitImport> shopUnitImportList);

    void validateShopUnitRequestObject(ShopUnitRequest shopUnitRequest);
}
