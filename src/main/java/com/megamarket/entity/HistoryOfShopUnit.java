package com.megamarket.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

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

    @Column(name = "update_date", nullable = false)
    LocalDateTime updateDate;

    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "shop_unit_id", nullable = false)
    private ShopUnit linkedShopUnit;

    public HistoryOfShopUnit(LocalDateTime updateDate, ShopUnit linkedShopUnit) {
        this.updateDate = updateDate;
        this.linkedShopUnit = linkedShopUnit;
    }
}
