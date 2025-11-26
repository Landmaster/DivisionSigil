package com.landmaster.divisionsigil.recipe;

import com.landmaster.divisionsigil.DivisionSigil;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

public class EnchantmentRemovalRecipe extends CustomRecipe {
    private static EnchantmentRemovalRecipe instance = null;

    public static EnchantmentRemovalRecipe getInstance() {
        if (instance == null) {
            instance = new EnchantmentRemovalRecipe();
        }
        return instance;
    }

    private EnchantmentRemovalRecipe() {
        super(CraftingBookCategory.MISC);
    }

    @Override
    public boolean matches(@Nonnull CraftingInput craftingInput, @Nonnull Level level) {
        return !result(craftingInput).isEmpty();
    }

    @Nonnull
    @Override
    public ItemStack assemble(@Nonnull CraftingInput craftingInput, @Nonnull HolderLookup.Provider provider) {
        return result(craftingInput);
    }

    private ItemStack result(CraftingInput craftingInput) {
        if (craftingInput.width() == 1 && craftingInput.height() == 3) {
            var tool = craftingInput.getItem(0, 0);
            var sigil = craftingInput.getItem(1, 0);
            var divisor = craftingInput.getItem(2, 0);
            if (tool.isEnchanted() && (sigil.is(DivisionSigil.DIVISION_SIGIL) || sigil.is(DivisionSigil.PSEUDO_INVERSION_SIGIL))) {
                if (divisor.is(Items.DIAMOND)) {
                    var result = tool.copy();
                    result.set(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
                    return result;
                } else if (divisor.is(Items.ENCHANTED_BOOK)) {
                    var result = tool.copy();
                    var bookEnchants = divisor.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
                    EnchantmentHelper.updateEnchantments(result, enchants -> {
                        for (var entry : bookEnchants.entrySet()) {
                            enchants.set(entry.getKey(), enchants.getLevel(entry.getKey()) - entry.getIntValue());
                        }
                    });
                    return result;
                }
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int i, int i1) {
        return i >= 1 && i1 >= 3;
    }

    @Nonnull
    @Override
    public RecipeSerializer<? extends EnchantmentRemovalRecipe> getSerializer() {
        return DivisionSigil.ENCHANTMENT_REMOVAL_RECIPE.get();
    }

    public static class Serializer implements RecipeSerializer<EnchantmentRemovalRecipe> {

        @Nonnull
        @Override
        public MapCodec<EnchantmentRemovalRecipe> codec() {
            return MapCodec.unit(getInstance());
        }

        @Nonnull
        @Override
        public StreamCodec<RegistryFriendlyByteBuf, EnchantmentRemovalRecipe> streamCodec() {
            return StreamCodec.unit(getInstance());
        }
    }
}
