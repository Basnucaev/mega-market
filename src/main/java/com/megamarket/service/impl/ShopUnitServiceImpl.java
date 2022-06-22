package com.megamarket.service.impl;

import com.megamarket.dto.ShopUnitRequest;
import com.megamarket.dto.ShopUnitStatisticResponse;
import com.megamarket.dto.ShopUnitStatisticUnit;
import com.megamarket.entity.ShopUnit;
import com.megamarket.entity.enums.ShopUnitType;
import com.megamarket.exception.my.ImportUnitBadRequestException;
import com.megamarket.exception.my.ShopUnitNotFoundException;
import com.megamarket.repository.ShopUnitRepository;
import com.megamarket.service.ShopUnitObjectsParser;
import com.megamarket.service.ShopUnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ShopUnitServiceImpl implements ShopUnitService {
    private final ShopUnitRepository shopUnitRepository;
    private final ShopUnitObjectsParser parser;

    @Autowired
    public ShopUnitServiceImpl(ShopUnitRepository shopUnitRepository, ShopUnitObjectsParser parser) {
        this.shopUnitRepository = shopUnitRepository;
        this.parser = parser;
    }

    @Override
    public void saveShopUnit(ShopUnitRequest shopUnitRequest) {
        List<ShopUnit> shopUnits = parser.parseShopUnitRequestObjectToShopUnit(shopUnitRequest);

        shopUnitRepository.saveAll(shopUnits);
    }

    @Override
    public void deleteShopUnitById(UUID id) {
        ShopUnit shopUnit = shopUnitRepository.findById(id).orElseThrow(ShopUnitNotFoundException::new);

        deleteUnitAndAllHisChildrenIfTheFounded(shopUnit);
    }

    @Override
    public ShopUnitStatisticUnit getShopUnitStatistic(UUID id) {
        ShopUnit shopUnit = shopUnitRepository.findById(id).orElseThrow(ShopUnitNotFoundException::new);

        ShopUnitStatisticUnit statisticUnit = parser.parseShopUnitToShopUnitStatisticUnit(shopUnit);

        return statisticUnit;
    }

    @Override
    public ShopUnitStatisticResponse getSalesStatistic(String dateTimeString) {
        checkDateFormatAndThrowExceptionIfIncorrect(dateTimeString);

        LocalDateTime dateTime  = parser.parseStringToDate(dateTimeString);

        List<ShopUnit> shopUnitListOffers = shopUnitRepository.findAllByType(ShopUnitType.OFFER);

        List<ShopUnit> toShowUnits = new ArrayList<>();
        long dateRequestInSeconds = dateTime.toInstant(ZoneOffset.UTC).getEpochSecond();

        for (ShopUnit current : shopUnitListOffers) {
            long currentUnitDateTimeInSeconds = shopUnitListOffers.get(0)
                    .getLastPriceUpdatedTime().toInstant(ZoneOffset.UTC).getEpochSecond();

            long result = dateRequestInSeconds - currentUnitDateTimeInSeconds;
            if (result < 86400 && result > 0) {
                toShowUnits.add(current);
            }
        }

        List<ShopUnitStatisticUnit> statisticUnitList = new ArrayList<>();
        for (ShopUnit shopUnit : toShowUnits) {
            ShopUnitStatisticUnit statisticUnit = parser.parseShopUnitToShopUnitStatisticUnit(shopUnit);
            statisticUnitList.add(statisticUnit);
        }

        ShopUnitStatisticResponse statisticResponse = new ShopUnitStatisticResponse();
        statisticResponse.setChildren(statisticUnitList);
        return statisticResponse;
    }

    private void checkDateFormatAndThrowExceptionIfIncorrect(String dateTimeString) {
        Pattern pattern = Pattern.compile(
                "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{3}Z");
        Matcher matcher = pattern.matcher(dateTimeString);

        if (!matcher.matches()) {
            throw new ImportUnitBadRequestException();
        }
    }

    private void deleteUnitAndAllHisChildrenIfTheFounded(ShopUnit shopUnit) {
        if (shopUnit.getChildren().size() > 0) {
            List<ShopUnit> shopUnitList = shopUnit.getChildren();
            for (ShopUnit currentUnit : shopUnitList) {
                deleteUnitAndAllHisChildrenIfTheFounded(currentUnit);
            }
        }
        shopUnitRepository.delete(shopUnit);
    }
}
