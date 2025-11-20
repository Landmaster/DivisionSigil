package com.landmaster.divisionsigil.item;

import com.landmaster.divisionsigil.Config;
import com.landmaster.divisionsigil.DivisionSigil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;
import java.util.List;
import java.util.OptionalLong;

public class UnstableIngotItem extends Item {
    public UnstableIngotItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nonnull TooltipContext context, @Nonnull List<Component> tooltipComponents, @Nonnull TooltipFlag tooltipFlag) {
        instabilityCounter(stack, context.level()).ifPresentOrElse((counter) -> {
            tooltipComponents.add(Component.translatable("tooltip.divisionsigil.instability_counter",
                    (new DecimalFormat("0.0")).format(Math.max((Config.EXPLODE_TIME.getAsInt() - counter) / 20.0, 0))));
        }, () -> {
            tooltipComponents.add(Component.translatable("tooltip.divisionsigil.instability_warning", (new DecimalFormat("0.#")).format(Config.EXPLODE_TIME.getAsInt() / 20.0)));
        });
    }

    public static OptionalLong instabilityCounter(ItemStack stack, Level level) {
        var timestamp = stack.get(DivisionSigil.INSTABILITY_TIMESTAMP);
        if (timestamp == null || level == null) {
            return OptionalLong.empty();
        }
        return OptionalLong.of(level.getGameTime() - timestamp);
    }
}
