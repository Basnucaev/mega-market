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

@AllArgsConstructor
@NoArgsConstructor
public class ShopUnitImport {

    UUID id;

    String name;

    UUID parentId;

    Integer price;

    ShopUnitType type;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getParentId() {
        if (parentId != null) {
            return parentId;
        }
        return null;
    }

    public void setParentId(UUID parentId) {
        this.parentId = parentId;
    }

    public ShopUnitType getType() {
        return type;
    }

    public void setType(ShopUnitType type) {
        this.type = type;
    }

    public Integer getPrice() {
        if (price != null) {
            return price;
        }
        return null;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }
}
