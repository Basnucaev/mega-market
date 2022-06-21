package com.megamarket.service.impl;

import com.megamarket.dto.ShopUnitImport;
import com.megamarket.dto.ShopUnitRequest;
import com.megamarket.dto.ShopUnitStatisticUnit;
import com.megamarket.entity.ShopUnit;
import com.megamarket.entity.enums.ShopUnitType;
import com.megamarket.repository.ShopUnitRepository;
import com.megamarket.service.ShopUnitObjectsParser;
import com.megamarket.service.Validator;
import com.megamarket.service.impl.help.TransferInteger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ShopUnitObjectsParserImpl implements ShopUnitObjectsParser {
    private final Validator validator;
    private final ShopUnitRepository shopUnitRepository;

    @Autowired
    public ShopUnitObjectsParserImpl(Validator validator, ShopUnitRepository shopUnitRepository) {
        this.validator = validator;
        this.shopUnitRepository = shopUnitRepository;
    }

    @Override
    public List<ShopUnit> parseShopUnitRequestObjectToShopUnit(ShopUnitRequest shopUnitRequest) {
        List<ShopUnit> shopUnitList = new ArrayList<>();
        List<ShopUnitImport> shopUnitImportList = shopUnitRequest.getItems();

        validator.validateShopUnitRequestObject(shopUnitRequest);
        validator.validateListOfShopUnitImport(shopUnitImportList);

        for (ShopUnitImport shopUnitImport : shopUnitImportList) {
            ShopUnit toSaveShopUnit = new ShopUnit();

            assignParametersFromImportObjectToShopUnit(toSaveShopUnit, shopUnitImport, shopUnitRequest.getUpdateDate());

            shopUnitRepository.save(toSaveShopUnit);
            shopUnitList.add(toSaveShopUnit);
        }

        addParentsToUnits(shopUnitImportList, shopUnitList);

        addAllParentCategoriesToShopUnitList(shopUnitList, new ArrayList<>(shopUnitList));
        updateDateForAlLCategories(shopUnitList, shopUnitRequest.getUpdateDate());

        setPriceToAllCategories(shopUnitList);

        setPriceNullToAllCategoriesWithNoChildren();
        return shopUnitList;
    }

    private void setPriceNullToAllCategoriesWithNoChildren() {
        List<ShopUnit> shopUnitList = shopUnitRepository.findAllByType(ShopUnitType.CATEGORY);
        for (ShopUnit shopUnit : shopUnitList) {
            if (shopUnit.getType() == ShopUnitType.CATEGORY && shopUnit.getChildren().size() == 0) {
                shopUnit.setPrice(null);
            }
        }
    }

    private void updateDateForAlLCategories(List<ShopUnit> shopUnitList, LocalDateTime updateDate) {
        shopUnitList.stream()
                .filter((x) -> x.getType() == ShopUnitType.CATEGORY)
                .filter((x) -> x.getDate() != updateDate).
                forEach((x) -> x.setDate(updateDate));
    }

    @Override
    public ShopUnitStatisticUnit parseShopUnitToShopUnitStatisticUnit(ShopUnit shopUnit) {
        ShopUnitStatisticUnit statisticUnit = transformShopUnitToStatisticUnit(shopUnit);

        if (statisticUnit.getType() == ShopUnitType.CATEGORY) {
            transformShopUnitChildrenToStatisticUnitChildren(shopUnit, statisticUnit);
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

    private void addParentsToUnits(List<ShopUnitImport> shopUnitImportList, List<ShopUnit> shopUnitList) {
        for (int i = 0; i < shopUnitImportList.size(); i++) {
            ShopUnitImport currentImportUnit = shopUnitImportList.get(i);

            ShopUnit parent = shopUnitList.stream()
                    .filter((x) -> x.getId().equals(currentImportUnit.getParentId()))
                    .filter((x) -> x.getType() == ShopUnitType.CATEGORY)
                    .findFirst().orElse(null);

            ShopUnit parentFromDb = shopUnitRepository.findByIdAndType(
                    currentImportUnit.getParentId(),
                    ShopUnitType.CATEGORY);

            ShopUnit currentUnit = shopUnitList.get(i);

            if (parent == null && parentFromDb != null && !shopUnitList.contains(parentFromDb)) {
                currentUnit.setParent(parentFromDb);
            } else {
                currentUnit.setParent(parent);
            }
        }
    }

    private void assignParametersFromImportObjectToShopUnit(ShopUnit shopUnit, ShopUnitImport shopUnitImport,
                                                            LocalDateTime dateFromShopUnitRequest) {
        // к моменту выполнения этого метода все поля будет провалидированы
        UUID id = shopUnitImport.getId();
        String name = shopUnitImport.getName();
        LocalDateTime updateDate = dateFromShopUnitRequest;
        Integer price = shopUnitImport.getPrice();
        ShopUnitType type = shopUnitImport.getType();


        shopUnit.setId(id);
        shopUnit.setName(name);
        shopUnit.setDate(updateDate);
        shopUnit.setType(type);
        shopUnit.setPrice(price);

        if (type == ShopUnitType.OFFER) {
            ShopUnit unitFromDb = shopUnitRepository.findByIdAndType(id, ShopUnitType.OFFER);
            if (unitFromDb != null && unitFromDb.getPrice().intValue() != price.intValue()) {
                shopUnit.setLastPriceUpdatedTime(updateDate);
            } else if (unitFromDb == null) {
                shopUnit.setLastPriceUpdatedTime(updateDate);
            } else {
                shopUnit.setLastPriceUpdatedTime(unitFromDb.getLastPriceUpdatedTime());
            }
        }
        // поле parent инициализируется в отдельном методе
    }

    private ShopUnitStatisticUnit transformShopUnitToStatisticUnit(ShopUnit shopUnit) {
        ShopUnitStatisticUnit statisticUnit = new ShopUnitStatisticUnit();
        statisticUnit.setId(shopUnit.getId());
        statisticUnit.setName(shopUnit.getName());
        if (shopUnit.getParent() != null) {
            statisticUnit.setParentId(shopUnit.getParent().getId());
        } else {
            statisticUnit.setParentId(null);
        }
        statisticUnit.setType(shopUnit.getType());
        statisticUnit.setDate(shopUnit.getDate());
        if (shopUnit.getPrice() == null) {
            statisticUnit.setPrice(null);
        } else {
            statisticUnit.setPrice(shopUnit.getPrice());
        }
        if (shopUnit.getType() == ShopUnitType.OFFER) {
            statisticUnit.setChildren(null);
        }

        return statisticUnit;
    }

    private void transformShopUnitChildrenToStatisticUnitChildren(ShopUnit shopUnit
            , ShopUnitStatisticUnit shopUnitStatisticUnit) {

        int count = 0;
        if (shopUnit.getChildren().size() > 0) {
            for (ShopUnit currentShopUnit : shopUnit.getChildren()) {
                shopUnitStatisticUnit.getChildren().add(transformShopUnitToStatisticUnit(currentShopUnit));

                ShopUnitStatisticUnit currentStatisticUnit = shopUnitStatisticUnit.getChildren().get(count++);

                transformShopUnitChildrenToStatisticUnitChildren(currentShopUnit, currentStatisticUnit);
            }
        }
    }

    private void setPriceToAllCategories(List<ShopUnit> shopUnitList) {
        for (ShopUnit currentShopUnit : shopUnitList) {
            if (currentShopUnit.getType() == ShopUnitType.CATEGORY) {

                TransferInteger transferInteger = calculatePriceForCategory(currentShopUnit);
                if (transferInteger.getOffersCount() == 0) {
                    currentShopUnit.setPrice(null);
                } else {
                    int avgCategoryPrice = transferInteger.getOffersSumPrice() / transferInteger.getOffersCount();
                    currentShopUnit.setPrice(avgCategoryPrice);
                }
            }
        }
    }

    private TransferInteger calculatePriceForCategory(ShopUnit currentShopUnit) {
        int offersSumPrice = 0;
        int offersCount = 0;
        if (currentShopUnit.getChildren().size() > 0) {
            for (ShopUnit currentUnit : currentShopUnit.getChildren()) {
                if (currentUnit.getType() == ShopUnitType.OFFER) {
                    offersSumPrice += currentUnit.getPrice();
                    offersCount += 1;
                }
                TransferInteger transferInteger = calculatePriceForCategory(currentUnit);
                offersSumPrice += transferInteger.getOffersSumPrice();
                offersCount += transferInteger.getOffersCount();
            }
        }
        return new TransferInteger(offersSumPrice, offersCount);
    }

    private void addAllParentCategoriesToShopUnitList(List<ShopUnit> shopUnitList, List<ShopUnit> copyOfOriginalList) {
        for (ShopUnit shopUnit : copyOfOriginalList) {
            while (true) {
                if (shopUnit.getParent() != null && !shopUnitList.contains(shopUnit.getParent())) {
                    ShopUnit parent = shopUnitRepository.findById(shopUnit.getParent().getId()).orElse(null);

                    shopUnitList.add(parent);
                    shopUnit = shopUnit.getParent();
                } else {
                    break;
                }
            }
        }
    }
}
