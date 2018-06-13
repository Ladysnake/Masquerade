package ladysnake.masquerade;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryBuilder;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

@Mod.EventBusSubscriber(modid = Masquerade.MOD_ID)
public class Masque extends IForgeRegistryEntry.Impl<Masque> {

    public static IForgeRegistry<Masque> REGISTRY;
    @GameRegistry.ObjectHolder("masquerade:none")
    public static Masque NONE;

    @SubscribeEvent
    public static void addRegistries(RegistryEvent.NewRegistry event) {
        REGISTRY = new RegistryBuilder<Masque>()
                .setType(Masque.class)
                .setName(new ResourceLocation(Masquerade.MOD_ID, "masks"))
                .setDefaultKey(new ResourceLocation("masquerade:none"))
                .create();
    }

    @SubscribeEvent
    public static void onRegistryRegister(RegistryEvent.Register<Masque> event) {
        event.getRegistry().register(new Masque("masquerade.none", Collections.emptyList(), "").setRegistryName("none"));
    }

    private final String unlocalizedName;
    private final List<String> tooltipLines;
    private final String modelLocation;

    public Masque(String unlocalizedName, List<String> tooltipLines, String modelLocation) {
        this.unlocalizedName = unlocalizedName;
        this.tooltipLines = tooltipLines;
        this.modelLocation = modelLocation;
    }

    public String getUnlocalizedName() {
        return unlocalizedName;
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        for (String line : tooltipLines) {
            tooltip.add(I18n.format(line));
        }
        if (flagIn.isAdvanced()) {
            tooltip.add(TextFormatting.DARK_GRAY + "" + this.getRegistryName());
        }
    }

    public String getModelLocation() {
        return modelLocation;
    }
}
