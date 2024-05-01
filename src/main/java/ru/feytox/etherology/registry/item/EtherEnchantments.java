package ru.feytox.etherology.registry.item;

import lombok.experimental.UtilityClass;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import ru.feytox.etherology.enchantment.PealEnchantment;
import ru.feytox.etherology.enchantment.ReflectionEnchantment;
import ru.feytox.etherology.util.misc.EIdentifier;

import java.util.function.Supplier;

@UtilityClass
public class EtherEnchantments {

    // TODO: 09.06.2023 move to class with the same name

    public static void registerAll() {
        register("reflection", ReflectionEnchantment.INSTANCE);
        register("peal", PealEnchantment.INSTANCE);
    }

    private void register(String id, Supplier<? extends Enchantment> enchantSupplier) {
        Registry.register(Registries.ENCHANTMENT, new EIdentifier(id), enchantSupplier.get());
    }
}
