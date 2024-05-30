package ru.practicum.shareit.util;

import ru.practicum.shareit.item.model.Item;

public class ItemUtil {
    public static Item getItem1_WhereOwnerUser1() {
        return Item.builder()
                .id(1)
                .name("Item1")
                .owner(UserUtil.getUser1())
                .description("Item1")
                .available(true)
                .build();
    }

    public static Item getItem2_WhereOwnerUser2() {
        return Item.builder()
                .id(2)
                .name("Item2")
                .owner(UserUtil.getUser2())
                .description("Item2")
                .available(true)
                .build();
    }

    public static Item getItem3_WhereOwnerUser3() {
        return Item.builder()
                .id(3)
                .name("Item3")
                .owner(UserUtil.getUser1())
                .description("Item3")
                .available(true)
                .build();
    }
}
