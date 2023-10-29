package ru.feytox.etherology.block.inventorTable;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.item.StaffPatternItem;
import ru.feytox.etherology.magic.staff.StaffMetals;
import ru.feytox.etherology.magic.staff.StaffPart;
import ru.feytox.etherology.registry.item.ToolItems;
import ru.feytox.etherology.registry.util.ScreenHandlersRegistry;
import ru.feytox.etherology.util.feyapi.*;

import java.util.List;

public class InventorTableScreenHandler extends ScreenHandler {

    @Getter
    private final UpdatableInventory tableInv;
    private int clientSelectedPart = -1;

    public InventorTableScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleUpdatableInventory(4));
    }

    public InventorTableScreenHandler(int syncId, PlayerInventory playerInventory, UpdatableInventory tableInv) {
        super(ScreenHandlersRegistry.CONSTRUCTOR_TABLE_SCREEN_HANDLER, syncId);
        checkSize(tableInv, 4);
        tableInv.onOpen(playerInventory.player);
        this.tableInv = tableInv;

        // input staff
        this.addSlot(new TrackedPredictableSlot(tableInv, 0, 8, 13, stack -> stack.isOf(ToolItems.ETHER_STAFF)));
        // pattern item
        this.addSlot(new TrackedPredictableSlot(tableInv, 1, 28, 13, stack -> stack.getItem() instanceof StaffPatternItem));
        // item for pattern apply
        this.addSlot(new TrackedSlot(tableInv, 2, 48, 13));
        // output staff
        this.addSlot(new OutputSlot(tableInv, 3, 148, 36));

        int m;
        int l;
        //The player inv
        for (m = 0; m < 3; ++m) {
            for (l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, 90 + m * 18));
            }
        }
        //The player Hotbar
        for (m = 0; m < 9; ++m) {
            this.addSlot(new Slot(playerInventory, m, 8 + m * 18, 148));
        }
    }

    public List<StaffPart> getStaffParts() {
        ItemStack inputStaff = tableInv.getStack(0);
        ItemStack patternItem = tableInv.getStack(1);
        ItemStack itemForPattern = tableInv.getStack(2);
        StaffMetals patternMetal = StaffMetals.getFromStack(itemForPattern);

        if (!inputStaff.isOf(ToolItems.ETHER_STAFF) || !(patternItem.getItem() instanceof StaffPatternItem) || patternMetal == null) {
            return new ObjectArrayList<>();
        }

        return ObjectArrayList.of(StaffPart.HEAD, StaffPart.DECOR, StaffPart.TIP);
    }

    @Nullable
    public StaffPart getSelectedPart() {
        if (tableInv instanceof InventorTableBlockEntity table) return table.getSelectedPart();
        List<StaffPart> staffParts = getStaffParts();
        if (clientSelectedPart >= 0 && clientSelectedPart < staffParts.size()) return staffParts.get(clientSelectedPart);
        return null;
    }

    public boolean onButtonClick(PlayerEntity player, int id) {
        List<StaffPart> staffParts = getStaffParts();
        if (id < 0 || id >= staffParts.size()) return false;
        if (player instanceof ClientPlayerEntity) {
            clientSelectedPart = id;
            return true;
        }
        if (!(tableInv instanceof InventorTableBlockEntity table)) return false;

        table.setSelectedPart(staffParts.get(id));
        table.updateResult();
        return true;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        // TODO: 08.09.2023 add quick moving
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
}
