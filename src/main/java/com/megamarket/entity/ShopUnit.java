package com.megamarket.entity;

import com.megamarket.entity.enums.ShopUnitType;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Entity
@Table(name = "shop_unit")
@NoArgsConstructor
public class ShopUnit {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private ShopUnit parent;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "type", updatable = false)
    private ShopUnitType type;

    @Column(name = "price")
    private Integer price;


    @Column(name = "last_price_updated_time")
    private LocalDateTime lastPriceUpdatedTime;

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, mappedBy = "parent")
    private List<ShopUnit> children = new ArrayList<>();

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

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        if (date == null) {
            date = LocalDateTime.now();
        }
        this.date = date;
    }

    public ShopUnit getParent() {
        return parent;
    }

    public void setParent(ShopUnit parent) {
        if (parent != null) {
            if (!parent.getChildren().contains(this)) {
                parent.getChildren().add(this);

            }
            if (this.getId() == parent.getId()) {
                parent = null;
            }
        } else {
            if (this.parent != null) {
                this.parent.getChildren().remove(this);
            }
        }
        this.parent = parent;
    }

    public ShopUnitType getType() {
        return type;
    }

    public void setType(ShopUnitType type) {
        if (this.type == null) {
            this.type = type;
        }
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public LocalDateTime getLastPriceUpdatedTime() {
        return lastPriceUpdatedTime;
    }

    public void setLastPriceUpdatedTime(LocalDateTime lastPriceUpdatedTime) {
        this.lastPriceUpdatedTime = lastPriceUpdatedTime;
    }

    public List<ShopUnit> getChildren() {
        return children;
    }

    public void setChildren(List<ShopUnit> children) {
        if (this.type.equals(ShopUnitType.OFFER)) {
            this.children = null;
        } else {
            this.children = children;
        }
    }
}
