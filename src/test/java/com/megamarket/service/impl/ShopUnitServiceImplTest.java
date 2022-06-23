package com.megamarket.service.impl;

import com.megamarket.dto.ShopUnitImport;
import com.megamarket.dto.ShopUnitRequest;
import com.megamarket.dto.ShopUnitStatisticResponse;
import com.megamarket.dto.ShopUnitStatisticUnit;
import com.megamarket.entity.ShopUnit;
import com.megamarket.entity.enums.ShopUnitType;
import com.megamarket.repository.HistoryOfShopUnitRepository;
import com.megamarket.repository.ShopUnitRepository;
import com.megamarket.service.DtoObjectsConvertor;
import com.megamarket.service.ShopUnitService;
import com.megamarket.service.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShopUnitServiceImplTest {
    private ShopUnitService shopUnitService;

    @Mock
    private ShopUnitRepository shopUnitRepository;
    @Mock
    private DtoObjectsConvertor convertor;
    @Mock
    private Validator validator;
    @Mock
    private HistoryOfShopUnitRepository historyOfShopUnitRepository;

    // import items
    ShopUnitImport shopUnitImportCategory1;

    ShopUnitImport shopUnitImportOffer1;

    ShopUnitImport shopUnitImportCategory2;

    List<ShopUnitImport> items;

    String updateDateString = "2022-05-28T08:12:01.000Z";
    String convertedString = "2022-05-28 08:12:01.000";
    LocalDateTime updateDate = LocalDateTime.parse(
            convertedString,
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));

    ShopUnitRequest shopUnitRequest;

    // shop unit items converted from import items
    ShopUnit shopUnitCategory1;
    ShopUnit shopUnitOffer1;
    ShopUnit shopUnitCategory2;

    List<ShopUnit> childrenOfCategory1;

    List<ShopUnit> toSaveListOfShopUnits;

    // statistic items converted from shop unit items

    ShopUnitStatisticUnit shopStatisticUnitCategory1;
    ShopUnitStatisticUnit shopStatisticUnitOffer1;
    ShopUnitStatisticUnit shopStatisticUnitCategory2;

    @BeforeEach
    void setUp() {
        shopUnitService = new ShopUnitServiceImpl(shopUnitRepository, convertor, validator, historyOfShopUnitRepository);

        // import items
        shopUnitImportCategory1 = new ShopUnitImport(
                UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a111"),
                "Category_1",
                null,
                null,
                ShopUnitType.CATEGORY
        );

        shopUnitImportOffer1 = new ShopUnitImport(
                UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a222"),
                "Offer_1",
                UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a111"),
                500,
                ShopUnitType.OFFER
        );

        shopUnitImportCategory2 = new ShopUnitImport(
                UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a333"),
                "Category_2",
                UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66a111"),
                null,
                ShopUnitType.CATEGORY
        );


        items = new ArrayList<>();
        items.add(shopUnitImportCategory1);
        items.add(shopUnitImportOffer1);
        items.add(shopUnitImportCategory2);

        updateDate = LocalDateTime.parse("2022-05-28 21:12:01.000"
                , DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));

        shopUnitRequest = new ShopUnitRequest();
        shopUnitRequest.setItems(items);
        shopUnitRequest.setUpdateDate(updateDate);

        // shop unit items converted from import items
        shopUnitCategory1 = new ShopUnit(
                shopUnitImportCategory1.getId(),
                shopUnitImportCategory1.getName(),
                shopUnitRequest.getUpdateDate(),
                null,
                ShopUnitType.CATEGORY,
                null,
                null,
                null
        );

        shopUnitOffer1 = new ShopUnit(
                shopUnitImportOffer1.getId(),
                shopUnitImportOffer1.getName(),
                shopUnitRequest.getUpdateDate(),
                shopUnitCategory1,
                ShopUnitType.OFFER,
                shopUnitImportOffer1.getPrice(),
                shopUnitRequest.getUpdateDate(),
                null
        );

        shopUnitCategory2 = new ShopUnit(
                shopUnitImportCategory2.getId(),
                shopUnitImportCategory2.getName(),
                shopUnitRequest.getUpdateDate(),
                shopUnitCategory1,
                ShopUnitType.CATEGORY,
                null,
                shopUnitRequest.getUpdateDate(),
                new ArrayList<>()
        );

        childrenOfCategory1 = new ArrayList<>();
        childrenOfCategory1.add(shopUnitOffer1);
        childrenOfCategory1.add(shopUnitCategory2);

        shopUnitCategory1.setChildren(childrenOfCategory1);


        toSaveListOfShopUnits = new ArrayList<>();
        toSaveListOfShopUnits.add(shopUnitCategory1);
        toSaveListOfShopUnits.add(shopUnitOffer1);
        toSaveListOfShopUnits.add(shopUnitCategory2);

        // statistic items converted from shop unit items
        shopStatisticUnitCategory1 = new ShopUnitStatisticUnit(
                shopUnitCategory1.getId(),
                shopUnitCategory1.getName(),
                null,
                shopUnitCategory1.getType(),
                null,
                shopUnitCategory1.getDate(),
                new ArrayList<>()
        );

        shopStatisticUnitOffer1 = new ShopUnitStatisticUnit(
                shopUnitOffer1.getId(),
                shopUnitOffer1.getName(),
                shopUnitOffer1.getParent().getId(),
                shopUnitOffer1.getType(),
                shopUnitOffer1.getPrice(),
                shopUnitOffer1.getDate(),
                null
        );

        shopStatisticUnitCategory2 = new ShopUnitStatisticUnit(
                shopUnitCategory2.getId(),
                shopUnitCategory2.getName(),
                shopUnitCategory2.getParent().getId(),
                shopUnitCategory2.getType(),
                null,
                shopUnitCategory2.getDate(),
                new ArrayList<>()
        );
    }

    @Test
    void shouldSaveListOfShopUnits() {
        // given
        when(convertor.parseShopUnitRequestObjectToShopUnitList(shopUnitRequest))
                .thenReturn(toSaveListOfShopUnits);

        // when
        shopUnitService.saveShopUnit(shopUnitRequest);

        // then
        verify(shopUnitRepository).saveAll(toSaveListOfShopUnits);

    }

    @Test
    void shouldDeleteUnitByIdAndHisChildren() {
        // given
        when(shopUnitRepository.findById(shopUnitCategory1.getId())).thenReturn(Optional.of(shopUnitCategory1));

        // when
        shopUnitService.deleteShopUnitById(shopUnitCategory1.getId());

        // then
        verify(shopUnitRepository).delete(shopUnitCategory1);

    }

    @Test
    void shouldReturnStatisticUnit() {
        // given
        when(shopUnitRepository.findById(shopUnitCategory1.getId())).thenReturn(Optional.of(shopUnitCategory1));
        when(convertor.parseShopUnitToStatisticUnit(shopUnitCategory1)).thenReturn(shopStatisticUnitCategory1);

        // when
        ShopUnitStatisticUnit expected = shopUnitService.getShopUnitStatisticById(shopUnitCategory1.getId());

        // then
        verify(shopUnitRepository).findById(shopUnitCategory1.getId());
        verify(convertor).parseShopUnitToStatisticUnit(shopUnitCategory1);

        assertThat(shopStatisticUnitCategory1).isEqualTo(expected);
    }

    @Test
    void shouldReturnSalesStatisticForSomeDate() {
        // given
        List<ShopUnit> shopUnitListOffers = new ArrayList<>();
        shopUnitListOffers.add(shopUnitOffer1);

        when(convertor.parseStringToDate(updateDateString)).thenReturn(updateDate);
        when(shopUnitRepository.findAllByType(ShopUnitType.OFFER)).thenReturn(shopUnitListOffers);
        when(convertor.parseShopUnitToStatisticUnit(shopUnitOffer1)).thenReturn(shopStatisticUnitOffer1);

        // when
        ShopUnitStatisticResponse expected = shopUnitService.getSalesStatistic(updateDateString);

        // then
        verify(shopUnitRepository).findAllByType(ShopUnitType.OFFER);

        assertThat(shopStatisticUnitOffer1).isEqualTo(expected.getChildren().get(0));
    }
}