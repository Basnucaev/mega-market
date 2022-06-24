//package com.megamarket.json.custom;
//
//import com.fasterxml.jackson.core.JsonGenerator;
//import com.fasterxml.jackson.databind.SerializerProvider;
//import com.fasterxml.jackson.databind.ser.std.StdSerializer;
//import com.megamarket.entity.ShopUnit;
//import com.megamarket.entity.enums.ShopUnitType;
//
//import java.io.IOException;
//
//public class ShopUnitSerializer extends StdSerializer<ShopUnit> {
//
//    public ShopUnitSerializer() {
//        this(null);
//    }
//
//    public ShopUnitSerializer(Class<ShopUnit> t) {
//        super(t);
//    }
//
//    @Override
//    public void serialize(ShopUnit shopUnit, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
//            throws IOException {
//
//        if (shopUnit.getParent() != null) {
//            jsonGenerator.writeString( String.valueOf(shopUnit.getParent().getId()));
//        } else {
//            jsonGenerator.writeNull();
//        }
//
////        jsonGenerator.writeStartObject();
////        jsonGenerator.writeStringField("id", String.valueOf(shopUnit.getId()));
////        jsonGenerator.writeStringField("name", shopUnit.getName());
////        jsonGenerator.writeStringField("date", String.valueOf(shopUnit.getDate()));
////        if (shopUnit.getParent() != null) {
////            jsonGenerator.writeStringField("parentId", String.valueOf(shopUnit.getParent().getId()));
////        } else {
////            jsonGenerator.writeNullField("parentId");
////        }
////        jsonGenerator.writeObjectField("type", shopUnit.getType());
////        jsonGenerator.writeNumberField("price", shopUnit.getPrice());
////        jsonGenerator.writeStringField("lastPriceUpdatedTime", String.valueOf(shopUnit.getLastPriceUpdatedTime()));
////
////        jsonGenerator.writeEndObject();
//    }
//}
