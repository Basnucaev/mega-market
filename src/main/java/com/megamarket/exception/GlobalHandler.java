package com.megamarket.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.megamarket.dto.Error;
import com.megamarket.exception.my.ImportUnitBadRequestException;
import com.megamarket.exception.my.NoItemsToImportInRequestException;
import com.megamarket.exception.my.OfferTypeUnitPriceNullException;
import com.megamarket.exception.my.PriceLessThanZeroException;
import com.megamarket.exception.my.ShopUnitIdNullException;
import com.megamarket.exception.my.ShopUnitNameNullException;
import com.megamarket.exception.my.ShopUnitNotFoundException;
import com.megamarket.exception.my.ShopUnitTypeNullException;
import com.megamarket.exception.my.ShopUnitUpdateDateNullException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalHandler {

    @ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<?> handleInvalidFormatException(InvalidFormatException exception) {
        String message = "Invalid format in json, reason of error the string: \"" + exception.getValue() + "\"";

        return new ResponseEntity<>(message, HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(ShopUnitNotFoundException.class)
    public ResponseEntity<Error> handleShopUnitNotFoundException(ShopUnitNotFoundException exception) {
        String message = "Item not found";
        Error error = new Error(HttpStatus.NOT_FOUND.value(), message);

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception) {
        String message = "Validation Failed";
        Error error = new Error(HttpStatus.BAD_REQUEST.value(), message);

        return new ResponseEntity<>(error, HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(ImportUnitBadRequestException.class)
    public ResponseEntity<?> handleImportUnitBadRequestException(ImportUnitBadRequestException exception) {
        Error error = new Error(HttpStatus.BAD_REQUEST.value(), "Validation Failed");

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

}
