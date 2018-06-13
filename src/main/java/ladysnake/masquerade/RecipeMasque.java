package ladysnake.masquerade;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;

public class RecipeMasque extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

    @Override
    public boolean matches(@Nonnull InventoryCrafting crafter, @Nonnull World world) {
        ItemStack helmet = ItemStack.EMPTY;
        ItemStack mask = ItemStack.EMPTY;
        for (int i = 0; i < crafter.getSizeInventory(); ++i) {
            ItemStack stack = crafter.getStackInSlot(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof ItemMasque) {
                    if (!mask.isEmpty()) {
                        return false;
                    }
                    mask = stack;
                } else {
                    if (!ItemMasque.isApplicable(stack, Masquerade.proxy.getFakePlayerForWorld(world))) {
                        return false;
                    } else {
                        if (!helmet.isEmpty()) {
                            return false;
                        }
                        helmet = stack;
                    }
                }
            }
        }
        return (!helmet.isEmpty() && !mask.isEmpty()) && (ItemMasque.getMasque(helmet) != ItemMasque.getMasque(mask));
    }

    @Nonnull
    @Override
    public ItemStack getCraftingResult(@Nonnull InventoryCrafting crafter) {
        ItemStack helmet = ItemStack.EMPTY;
        ItemStack mask = ItemStack.EMPTY;
        for (int i = 0; i < crafter.getSizeInventory(); ++i) {
            ItemStack stack = crafter.getStackInSlot(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof ItemMasque) {
                    mask = stack;
                } else {
                    // normally we can retrieve the player from RecipeMasque#matches
                    if (ItemMasque.isApplicable(stack, Masquerade.proxy.getFakePlayerForWorld(null))) {
                        helmet = stack;
                    }
                }
            }
        }
        if (!helmet.isEmpty() && !mask.isEmpty()) {
            ItemStack result = helmet.copy();
            Masque appliedMask = ItemMasque.getMasque(mask);
            ItemMasque.setMasque(result, appliedMask);
            return result;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public boolean isDynamic() {
        return true;
    }

    @Nonnull
    @Override
    public ItemStack getRecipeOutput() {
        return ItemStack.EMPTY;
    }
}
