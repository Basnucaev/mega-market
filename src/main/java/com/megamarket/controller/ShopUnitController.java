package com.megamarket.controller;

import com.megamarket.dto.ShopUnitRequest;
import com.megamarket.dto.ShopUnitStatisticResponse;
import com.megamarket.entity.ShopUnit;
import com.megamarket.service.ShopUnitService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.UUID;

@RestController
public class ShopUnitController {
    private final ShopUnitService shopUnitService;

    public ShopUnitController(ShopUnitService shopUnitService) {
        this.shopUnitService = shopUnitService;
    }

    @PostMapping("/imports")
    public ResponseEntity<?> saveShopUnit(@Valid @RequestBody ShopUnitRequest shopUnitRequest) {
        shopUnitService.saveShopUnit(shopUnitRequest);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteShopUnitById(@PathVariable UUID id) {
        shopUnitService.deleteShopUnitById(id);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/nodes/{id}")
    public ResponseEntity<?> getUnitNodesById(@PathVariable UUID id) {
        ShopUnit shopUnit = shopUnitService.getShopUnitStatisticById(id);

        return new ResponseEntity<>(shopUnit, HttpStatus.OK);
    }

    @GetMapping("/sales")
    public ResponseEntity<?> getUnitsByChangePriceLast24Hours(
            @RequestParam(name = "date") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") String dateTime) {
        ShopUnitStatisticResponse statistic = shopUnitService.getSalesStatistic(dateTime);

        return new ResponseEntity<>(statistic, HttpStatus.OK);
    }

    @GetMapping("/node/{id}/statistic")
    public ResponseEntity<?> getUnitsHistoryById(
            @PathVariable UUID id
            , @RequestParam(name = "dateStart", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") String fromDateString
            , @RequestParam(name = "dateEnd", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") String toDateString) {
        ShopUnitStatisticResponse statistic = shopUnitService.getUnitsHistoryById(id, fromDateString, toDateString);

        return new ResponseEntity<>(statistic , HttpStatus.OK);
    }
}
