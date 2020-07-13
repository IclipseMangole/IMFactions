package de.imfactions.functions.items;

//   |    ----  |       |   |---  -----  |---
//   |   |      |       |   |  |  |      |
//   |   |      |       |   |--   -----  |---
//   |   |      |       |   |         |  |
//   |    ----   ----   |   |     -----  |---

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Iclipse on 20.06.2020
 */
public class ItemModifierType<T> {
    private static Map<String, ItemModifierType<?>> itemModifierTypes = new HashMap<>();

    private final String name;
    private final Class<T> type;

    public ItemModifierType(String name, Class<T> type) {
        this.name = name;
        this.type = type;
        itemModifierTypes.put(name, this);
    }
}
