package com.megamarket.service.impl;

import com.megamarket.dto.ShopUnitRequest;
import com.megamarket.dto.ShopUnitStatisticResponse;
import com.megamarket.dto.ShopUnitStatisticUnit;
import com.megamarket.entity.HistoryOfShopUnit;
import com.megamarket.entity.ShopUnit;
import com.megamarket.entity.enums.ShopUnitType;
import com.megamarket.exception.my.ShopUnitNotFoundException;
import com.megamarket.repository.HistoryOfShopUnitRepository;
import com.megamarket.repository.ShopUnitRepository;
import com.megamarket.service.DtoObjectsConvertor;
import com.megamarket.service.ShopUnitService;
import com.megamarket.service.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ShopUnitServiceImpl implements ShopUnitService {
    private final ShopUnitRepository shopUnitRepository;
    private final DtoObjectsConvertor convertor;
    private final Validator validator;
    private final HistoryOfShopUnitRepository historyOfShopUnitRepository;

    @Autowired
    public ShopUnitServiceImpl(ShopUnitRepository shopUnitRepository, DtoObjectsConvertor convertor,
                               Validator validator, HistoryOfShopUnitRepository historyOfShopUnitRepository) {
        this.shopUnitRepository = shopUnitRepository;
        this.convertor = convertor;
        this.validator = validator;
        this.historyOfShopUnitRepository = historyOfShopUnitRepository;
    }

    @Override
    public void saveShopUnit(ShopUnitRequest shopUnitRequest) {
        List<ShopUnit> shopUnits = convertor.parseShopUnitRequestObjectToShopUnitList(shopUnitRequest);

        for (ShopUnit shopUnit : shopUnits) {
            historyOfShopUnitRepository.save(new HistoryOfShopUnit(shopUnit.getDate(), shopUnit));
        }

        shopUnitRepository.saveAll(shopUnits);
    }

    @Override
    @Transactional
    public void deleteShopUnitById(UUID id) {
        ShopUnit shopUnit = shopUnitRepository.findById(id).orElseThrow(ShopUnitNotFoundException::new);

        deleteUnitAndAllHisChildrenIfTheFounded(shopUnit);
    }

    @Override
    public ShopUnitStatisticUnit getShopUnitStatisticById(UUID id) {
        ShopUnit shopUnit = shopUnitRepository.findById(id).orElseThrow(ShopUnitNotFoundException::new);

        ShopUnitStatisticUnit statisticUnit = convertor.parseShopUnitToStatisticUnit(shopUnit);

        return statisticUnit;
    }

    @Override
    public ShopUnitStatisticResponse getSalesStatistic(String dateTimeString) {
        validator.validDateFormat(dateTimeString);

        LocalDateTime dateTime = convertor.parseStringToDate(dateTimeString);

        List<ShopUnit> shopUnitListOffers = shopUnitRepository.findAllByType(ShopUnitType.OFFER);

        List<ShopUnit> toShowUnits = new ArrayList<>();
        long dateRequestInSeconds = dateTime.toInstant(ZoneOffset.UTC).getEpochSecond();

        for (ShopUnit current : shopUnitListOffers) {
            long currentUnitDateTimeInSeconds = shopUnitListOffers.get(0)
                    .getLastPriceUpdatedTime().toInstant(ZoneOffset.UTC).getEpochSecond();

            long result = dateRequestInSeconds - currentUnitDateTimeInSeconds;
            if (result < 86400 && result > -1) {
                toShowUnits.add(current);
            }
        }

        List<ShopUnitStatisticUnit> statisticUnitList = getListOfStatisticUnits(toShowUnits);

        ShopUnitStatisticResponse statisticResponse = new ShopUnitStatisticResponse();
        statisticResponse.setChildren(statisticUnitList);
        return statisticResponse;
    }

    @Override
    public ShopUnitStatisticResponse getUnitsHistoryById(UUID id, String fromDateString, String toDateString) {
        validator.validDateFormat(fromDateString);
        validator.validDateFormat(toDateString);

        LocalDateTime from = convertor.parseStringToDate(fromDateString);
        LocalDateTime to = convertor.parseStringToDate(toDateString);


        List<HistoryOfShopUnit> historyOfUnitList = historyOfShopUnitRepository
                .findAllByLinkedShopUnitIdAndUpdateDateGreaterThanAndUpdateDateLessThan(id, from, to);

        List<ShopUnit> shopUnitList = new ArrayList<>();
        for (HistoryOfShopUnit historyUnit : historyOfUnitList) {
            shopUnitList.add(historyUnit.getLinkedShopUnit());
        }

        List<ShopUnitStatisticUnit> statisticUnitList = getListOfStatisticUnits(shopUnitList);

        ShopUnitStatisticResponse statisticResponse = new ShopUnitStatisticResponse();
        statisticResponse.setChildren(statisticUnitList);
        return statisticResponse;
    }

    private void deleteUnitAndAllHisChildrenIfTheFounded(ShopUnit shopUnit) {
        if (shopUnit.getChildren() != null && shopUnit.getChildren().size() > 0) {
            List<ShopUnit> shopUnitList = shopUnit.getChildren();
            for (ShopUnit currentUnit : shopUnitList) {
                deleteUnitAndAllHisChildrenIfTheFounded(currentUnit);
            }
        }
        historyOfShopUnitRepository.deleteAllByLinkedShopUnitId(shopUnit.getId());
        shopUnitRepository.delete(shopUnit);
    }

    private List<ShopUnitStatisticUnit> getListOfStatisticUnits(List<ShopUnit> toShowUnits) {
        List<ShopUnitStatisticUnit> statisticUnitList = new ArrayList<>();

        for (ShopUnit shopUnit : toShowUnits) {
            ShopUnitStatisticUnit statisticUnit = convertor.parseShopUnitToStatisticUnit(shopUnit);
            statisticUnitList.add(statisticUnit);
        }
        return statisticUnitList;
    }
}
