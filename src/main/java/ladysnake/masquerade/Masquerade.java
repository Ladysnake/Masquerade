package ladysnake.masquerade;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.ref.WeakReference;
import java.util.UUID;

@Mod(
        modid = Masquerade.MOD_ID,
        name = Masquerade.MOD_NAME,
        version = "@VERSION@",
        certificateFingerprint = "@FINGERPRINT@"
)
public class Masquerade {

    public static final String MOD_ID = "masquerade";
    public static final String MOD_NAME = "Masquerade";

    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    /**
     * This is the instance of your mod as created by Forge. It will never be null.
     */
    @Mod.Instance(MOD_ID)
    public static Masquerade INSTANCE;
    public static final CreativeTabs CREATIVE_TAB = new CreativeTabs(MOD_ID) {
        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(ModItems.MASQUE);
        }
    };

    /**
     * This is the first initialization event. Register tile entities here.
     * The registry events below will have fired prior to entry to this method.
     */
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {

    }

    /**
     * This is the second initialization event. Register custom recipes
     */
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(ModItems.MASQUE);
    }

    /**
     * This is the final initialization event. Register actions from other mods here
     */
    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {

    }

    @Mod.EventHandler
    public void onFingerprintViolation(FMLFingerprintViolationEvent event) {
        LOGGER.warn("Invalid fingerprint detected! The file " + event.getSource().getName() + " may have been tampered with. This version will NOT be supported by the author!");
    }

    /**
     * Forge will automatically look up and bind items to the fields in this class
     * based on their registry name.
     */
    @GameRegistry.ObjectHolder(MOD_ID)
    public static class ModItems {
        public static final Item MASQUE = Items.AIR;
    }

    /**
     * This is a special class that listens to registry events, to allow creation of mod blocks and items at the proper time.
     */
    @Mod.EventBusSubscriber(modid = MOD_ID)
    public static class ObjectRegistryHandler {
        /**
         * Listen for the register event for creating custom items
         */
        @SubscribeEvent
        public static void addItems(RegistryEvent.Register<Item> event) {
            for (Masques masque : Masques.values())
                event.getRegistry().register(new ItemMasque(ItemArmor.ArmorMaterial.LEATHER, 1, masque).setCreativeTab(CREATIVE_TAB).setRegistryName(MOD_ID, "mask_" + masque).setUnlocalizedName(MOD_ID + ":" + masque));
        }

        @SubscribeEvent
        public static void addRecipes(RegistryEvent.Register<IRecipe> event) {
            event.getRegistry().register(new RecipeMasque().setRegistryName("mask"));
        }
    }

    @SidedProxy
    static CommonProxy proxy;

    public static class CommonProxy {
        private static final UUID FAKE_PLAYER_ID = UUID.fromString("66900070-9069-45b2-8c74-0c300e9cd0d4");
        private static final GameProfile FAKE_PLAYER_PROFILE = new GameProfile(FAKE_PLAYER_ID, "[Masquerade Fake Player]");
        private static WeakReference<EntityPlayer> fakePlayer = new WeakReference<>(null);

        public EntityPlayer getFakePlayerForWorld(World world) {
            if (world == null) {
                return fakePlayer.get();
            }
            if (world instanceof WorldServer) {
                EntityPlayer player = FakePlayerFactory.get((WorldServer) world, FAKE_PLAYER_PROFILE);
                fakePlayer = new WeakReference<>(player);
                return player;
            }
            return null;
        }

    }

    @SuppressWarnings("unused")
    public static class ClientProxy extends CommonProxy {

        @Override
        @SideOnly(Side.CLIENT)
        public EntityPlayer getFakePlayerForWorld(World world) {
            return world instanceof WorldClient ? Minecraft.getMinecraft().player : super.getFakePlayerForWorld(world);
        }
    }

    @SuppressWarnings("unused")
    public static class ServerProxy extends CommonProxy {

    }
}
