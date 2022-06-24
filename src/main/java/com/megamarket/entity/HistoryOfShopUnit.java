package com.megamarket.entity;

import com.megamarket.entity.enums.ShopUnitType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "shop_unit_history")
@NoArgsConstructor
@Getter
@Setter
public class HistoryOfShopUnit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "shop_unit_id")
    private UUID shopUnitId;

    @Column(name = "name")
    private String name;

    @Column(name = "update_date")
    LocalDateTime updateDate;

    @Column(name = "parent_id")
    private UUID parentId;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "type")
    private ShopUnitType type;

    @Column(name = "price")
    private Integer price;
}
