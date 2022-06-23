package com.megamarket.service.impl;

import com.megamarket.dto.ShopUnitImport;
import com.megamarket.dto.ShopUnitRequest;
import com.megamarket.entity.enums.ShopUnitType;
import com.megamarket.exception.my.ImportUnitBadRequestException;
import com.megamarket.exception.my.NoItemsToImportInRequestException;
import com.megamarket.service.Validator;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ValidatorImpl implements Validator {

    @Override
    public void validateListOfShopUnitImport(List<ShopUnitImport> shopUnitImportList) {
        boolean isBadRequest = false;
        for (ShopUnitImport shopUnitImport : shopUnitImportList) {
            if (shopUnitImport.getId() == null) {
                isBadRequest = true;
                break;
            }
            if (shopUnitImport.getName() == null
                    || shopUnitImport.getName().equals("")
                    || shopUnitImport.getName().trim().length() == 0) {
                isBadRequest = true;
                break;
            }
            if (shopUnitImport.getType() == ShopUnitType.OFFER && shopUnitImport.getPrice() == null) {
                isBadRequest = true;
                break;
            }
            if (shopUnitImport.getPrice() != null && shopUnitImport.getPrice() < 0) {
                isBadRequest = true;
                break;
            }
            if (shopUnitImport.getType() == null) {
                isBadRequest = true;
                break;
            }
        }

        if (isBadRequest) throw new ImportUnitBadRequestException();
    }

    @Override
    public void validateShopUnitRequestObject(ShopUnitRequest shopUnitRequest) {
        if (shopUnitRequest.getItems().size() < 1) {
            throw new NoItemsToImportInRequestException();
        }

        if (shopUnitRequest.getUpdateDate() == null) {
            throw new ImportUnitBadRequestException();
        }
    }

    @Override
    public void validDateFormat(String dateTimeString) {
        Pattern pattern = Pattern.compile(
                "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{3}Z");
        Matcher matcher = pattern.matcher(dateTimeString);

        if (!matcher.matches()) {
            throw new ImportUnitBadRequestException();
        }
    }
}
