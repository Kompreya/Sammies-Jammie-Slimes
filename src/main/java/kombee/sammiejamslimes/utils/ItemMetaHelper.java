package kombee.sammiejamslimes.utils;

/*
This class was taken from Quark and created by Vazkii.
https://github.com/Vazkii/Quark
Quark is Open Source and distributed under
CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB

Modifications will be annotated as such.
*/

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;

import kombee.sammiejamslimes.SammieJamSlimes;
import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ItemMetaHelper {

    // K: Added my own log method
    private static final Logger LOGGER = LogManager.getLogger(SammieJamSlimes.MODID);

    public static Set<ItemStack> getFromString(String debugString, String s) {
        return getFromString(debugString, s, true);
    }

    public static Set<ItemStack> getFromString(String debugString, String s, boolean allowWildcard) {
        String[] itemData = s.split(":");
        if (itemData.length < 2 || itemData.length > 3) {
            LOGGER.warn("Invalid {} '{}'", debugString, s);
            return Sets.newHashSet(ItemStack.EMPTY);
        }
        ResourceLocation r = new ResourceLocation(itemData[0], itemData[1]);
        if (!ForgeRegistries.ITEMS.containsKey(r)) return Sets.newHashSet(ItemStack.EMPTY);
        Item item = ForgeRegistries.ITEMS.getValue(r);

        // has meta
        if (itemData.length == 3) {
            int meta = MathHelper.getInt(itemData[2], -1);
            if (meta < 0) {
                if (!itemData[2].equals("*")) {
                    LOGGER.warn("Invalid meta '{}' for {} '{}'", meta, debugString, s);
                    return Sets.newHashSet(ItemStack.EMPTY);
                }
            } else {
                return Sets.newHashSet(new ItemStack(item, 1, meta));
            }
        }

        if (allowWildcard) {
            // no meta, wildcard it
            NonNullList<ItemStack> subItems = NonNullList.create();
            item.getSubItems(CreativeTabs.SEARCH, subItems);

            return new HashSet<>(subItems);
        } else {
            return Sets.newHashSet(new ItemStack(item));
        }
    }

    public static Set<ItemStack> getFromStringArray(String debugString, String[] a) {
        return Arrays.stream(a)
                .map(s -> ItemMetaHelper.getFromString(debugString, s))
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

    public static Set<ItemStack> getFromStringCollection(String debugString, Collection<String> c) {
        return getFromStringArray(debugString, c.toArray(new String[0]));
    }

    public static boolean itemEqualsPair(ItemStack stack, Pair<Item, Integer> item) {
        return stack.getItem().equals(item.getLeft()) && stack.getMetadata() == item.getRight();
    }
}
