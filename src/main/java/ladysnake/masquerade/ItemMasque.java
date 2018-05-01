package ladysnake.masquerade;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = Masquerade.MOD_ID)
public class ItemMasque extends ItemArmor {
    public static final String TAG_MASQUE = Masquerade.MOD_ID + ":masque";
    private Masques masque;

    public ItemMasque(ArmorMaterial materialIn, int renderIndexIn, Masques masque) {
        super(materialIn, renderIndexIn, EntityEquipmentSlot.HEAD);
        this.masque = masque;
    }

    public static boolean isApplicable(ItemStack stack, EntityLivingBase entity) {
        return entity != null && stack.getItem().isValidArmor(stack, EntityEquipmentSlot.HEAD, entity);
    }

    @SubscribeEvent
    public static void onCanRenderName(CanRenderNameEvent event) {
        if (getMasque(event.getEntity().getItemStackFromSlot(EntityEquipmentSlot.HEAD)) != Masques.NONE) {
            event.setCanceled(true);
        }
    }

    /**
     * Sets the mask of the specified item stack
     *
     * @param stack
     * @param mask
     */
    public static void setMasque(ItemStack stack, Masques mask) {
        if (stack.getItem() instanceof ItemMasque) return;
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) {
            nbt = new NBTTagCompound();
            stack.setTagCompound(nbt);
        }
        nbt.setInteger(TAG_MASQUE, mask.id);
    }

    /**
     * Returns the mask on the specified item stack
     *
     * @param stack
     * @return
     */
    public static Masques getMasque(ItemStack stack) {
        if (stack.getItem() instanceof ItemMasque)
            return ((ItemMasque) stack.getItem()).masque;
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt != null && nbt.hasKey(TAG_MASQUE, Constants.NBT.TAG_INT)) {
            return Masques.fromID(nbt.getInteger(TAG_MASQUE));
        }
        return Masques.NONE;
    }
}
