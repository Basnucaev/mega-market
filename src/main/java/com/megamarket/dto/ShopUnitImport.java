package com.megamarket.dto;

import com.megamarket.entity.enums.ShopUnitType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShopUnitImport {

    UUID id;

    String name;

    UUID parentId;

    Integer price;

    ShopUnitType type;
}
