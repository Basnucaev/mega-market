package com.megamarket.service.impl.help;

import com.megamarket.dto.ShopUnitImport;
import com.megamarket.dto.ShopUnitStatisticUnit;
import com.megamarket.entity.ShopUnit;
import com.megamarket.entity.enums.ShopUnitType;
import com.megamarket.repository.ShopUnitRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ConvertorHelper {
    private final ShopUnitRepository shopUnitRepository;

    public ConvertorHelper(ShopUnitRepository shopUnitRepository) {
        this.shopUnitRepository = shopUnitRepository;
    }

    public void assignParametersFromImportObjectToShopUnit(ShopUnit shopUnit, ShopUnitImport shopUnitImport,
                                                           LocalDateTime dateFromShopUnitRequest) {
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
    }

    public ShopUnitStatisticUnit transformShopUnitToStatisticUnit(ShopUnit shopUnit) {
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

    public void transformShopUnitChildrenToStatisticUnitChildren(ShopUnit shopUnit
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

    public void addParentsToUnits(List<ShopUnitImport> shopUnitImportList, List<ShopUnit> shopUnitList) {
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

    public void setPriceToAllCategories(List<ShopUnit> shopUnitList) {
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

    public TransferInteger calculatePriceForCategory(ShopUnit currentShopUnit) {
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

    public void addAllParentCategoriesToShopUnitList(List<ShopUnit> shopUnitList, List<ShopUnit> copyOfOriginalList) {
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

    public void setPriceNullToAllCategoriesWithNoChildren() {
        List<ShopUnit> shopUnitList = shopUnitRepository.findAllByType(ShopUnitType.CATEGORY);
        for (ShopUnit shopUnit : shopUnitList) {
            if (shopUnit.getType() == ShopUnitType.CATEGORY && shopUnit.getChildren().size() == 0) {
                shopUnit.setPrice(null);
            }
        }
    }

    public void updateDateForAlLCategories(List<ShopUnit> shopUnitList, LocalDateTime updateDate) {
        shopUnitList.stream()
                .filter((x) -> x.getType() == ShopUnitType.CATEGORY)
                .filter((x) -> x.getDate() != updateDate).
                forEach((x) -> x.setDate(updateDate));
    }
}
