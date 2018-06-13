package ladysnake.masquerade;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Mod.EventBusSubscriber(modid = Masquerade.MOD_ID)
public class ItemMasque extends Item {
    public static final String TAG_MASQUE = Masquerade.MOD_ID + ":masque";
    private static final Map<Masque, ItemStack> STACK_CACHE = new HashMap<>();

    private Masque masque;
    private final ResourceLocation maskId;

    public ItemMasque(ResourceLocation maskId) {
        super();
        this.maskId = maskId;
    }

    @Nullable
    @Override
    public EntityEquipmentSlot getEquipmentSlot(ItemStack stack) {
        return EntityEquipmentSlot.HEAD;
    }

    @Nonnull
    public Masque getMasque() {
        if (masque == null) {
            masque = Masque.REGISTRY.getValue(maskId);
            if (masque == null) {
                throw new IllegalStateException("Mask item " + getRegistryName() + " was registered with invalid mask " + maskId);
            }
        }
        return masque;
    }

    public static boolean isApplicable(ItemStack stack, EntityLivingBase entity) {
        return entity != null && stack.getItem().isValidArmor(stack, EntityEquipmentSlot.HEAD, entity);
    }

    @SubscribeEvent
    public static void onSpecialsPre(RenderLivingEvent.Specials.Pre event) {
        if (getMasque(event.getEntity().getItemStackFromSlot(EntityEquipmentSlot.HEAD)) != Masque.NONE) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRenderLiving(RenderLivingEvent.Pre event) {
        ItemStack stack = event.getEntity().getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        if (stack.getItem() instanceof ItemMasque) {
            return; // rendering is already handled by vanilla
        }
        Masque masque = getMasque(stack);
        if (masque != Masque.NONE) {
            Minecraft.getMinecraft().getRenderItem().renderItem(
                    STACK_CACHE.computeIfAbsent(masque, m -> new ItemStack(ForgeRegistries.ITEMS.getValue(m.getRegistryName()))),
                    event.getEntity(),
                    ItemCameraTransforms.TransformType.HEAD,
                    false
            );
        }
    }

    /**
     * Sets the mask of the specified item stack
     */
    public static void setMasque(@Nonnull ItemStack stack, @Nonnull Masque mask) {
        if (stack.getItem() instanceof ItemMasque) {
            return;
        }
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) {
            nbt = new NBTTagCompound();
            stack.setTagCompound(nbt);
        }
        nbt.setString(TAG_MASQUE, String.valueOf(mask.getRegistryName()));
    }

    /**
     * Returns the mask on the specified item stack
     */
    @Nonnull
    public static Masque getMasque(ItemStack stack) {
        if (stack.getItem() instanceof ItemMasque) {
            return ((ItemMasque) stack.getItem()).getMasque();
        }
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt != null && nbt.hasKey(TAG_MASQUE, Constants.NBT.TAG_INT)) {
            return Objects.requireNonNull(Masque.REGISTRY.getValue(new ResourceLocation(nbt.getString(TAG_MASQUE))));
        }
        return Masque.NONE;
    }
}
