package com.megamarket.service.impl;

import com.megamarket.dto.ShopUnitImport;
import com.megamarket.dto.ShopUnitRequest;
import com.megamarket.dto.ShopUnitStatisticUnit;
import com.megamarket.entity.ShopUnit;
import com.megamarket.entity.enums.ShopUnitType;
import com.megamarket.repository.ShopUnitRepository;
import com.megamarket.service.DtoObjectsConvertor;
import com.megamarket.service.Validator;
import com.megamarket.service.impl.help.ConvertorHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class DtoObjectsConvertorImpl implements DtoObjectsConvertor {
    private final Validator validator;
    private final ConvertorHelper convertorHelper;
    private final ShopUnitRepository shopUnitRepository;

    @Autowired
    public DtoObjectsConvertorImpl(Validator validator, ConvertorHelper convertorHelper, ShopUnitRepository shopUnitRepository) {
        this.validator = validator;
        this.convertorHelper = convertorHelper;
        this.shopUnitRepository = shopUnitRepository;
    }

    @Override
    public List<ShopUnit> parseShopUnitRequestObjectToShopUnitList(ShopUnitRequest shopUnitRequest) {
        List<ShopUnit> shopUnitList = new ArrayList<>();
        List<ShopUnitImport> shopUnitImportList = shopUnitRequest.getItems();

        validator.validateShopUnitRequestObject(shopUnitRequest);
        validator.validateListOfShopUnitImport(shopUnitImportList);

        for (ShopUnitImport shopUnitImport : shopUnitImportList) {
            ShopUnit toSaveShopUnit = new ShopUnit();

            convertorHelper.assignParametersFromImportObjectToShopUnit(toSaveShopUnit, shopUnitImport,
                    shopUnitRequest.getUpdateDate());

            shopUnitRepository.save(toSaveShopUnit);
            shopUnitList.add(toSaveShopUnit);
        }

        convertorHelper.addParentsToUnits(shopUnitImportList, shopUnitList);

        convertorHelper.addAllParentCategoriesToShopUnitList(shopUnitList, new ArrayList<>(shopUnitList));
        convertorHelper.updateDateForAlLCategories(shopUnitList, shopUnitRequest.getUpdateDate());

        convertorHelper.setPriceToAllCategories(shopUnitList);

        convertorHelper.setPriceNullToAllCategoriesWithNoChildren();
        return shopUnitList;
    }

    @Override
    public ShopUnitStatisticUnit parseShopUnitToStatisticUnit(ShopUnit shopUnit) {
        ShopUnitStatisticUnit statisticUnit = convertorHelper.transformShopUnitToStatisticUnit(shopUnit);

        if (statisticUnit.getType() == ShopUnitType.CATEGORY) {
            convertorHelper.transformShopUnitChildrenToStatisticUnitChildren(shopUnit, statisticUnit);
        }
        return statisticUnit;
    }

    @Override
    public LocalDateTime parseStringToDate(String dateTimeString) {
        dateTimeString = dateTimeString.replace('T', ' ');
        dateTimeString = dateTimeString.replace('Z', ' ').trim();

        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, format);

        return dateTime;
    }
}
