package ladysnake.masquerade;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import net.minecraft.item.Item;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.commons.io.FilenameUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = Masquerade.MOD_ID)
public class MasqueDeserializer {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @SubscribeEvent
    public static void loadMasks(RegistryEvent.Register<Item> event) {
        ModContainer gaspunkContainer = Loader.instance().activeModContainer();
        Loader.instance().getActiveModList().forEach(MasqueDeserializer::loadMasks);
        Loader.instance().setActiveModContainer(gaspunkContainer);
        File configFolder = new File(Loader.instance().getConfigDir(), Masquerade.MOD_ID + "/custom_masks");
        // if the config folder was just created or couldn't be created, no need to search it
        try {
            if (!configFolder.mkdirs() && configFolder.exists()) {
                Files.walk(configFolder.toPath()).forEach(path -> loadMasks(configFolder.toPath(), path));
            } else if (configFolder.exists()) {
                JsonObject exampleAgent = new JsonObject();
                exampleAgent.addProperty("registryName", "masquerade:rabbit");
                exampleAgent.addProperty("unlocalizedName", "masquerade.rabbit");
                exampleAgent.addProperty("model", "masquerade:masques/rabbit");
                exampleAgent.add("tooltipLines", new JsonArray());
                Files.write(configFolder.toPath().resolve("_example.json"), GSON.toJson(exampleAgent).getBytes(), StandardOpenOption.CREATE_NEW);
            }
        } catch (IOException e) {
            Masquerade.LOGGER.error("Error while loading masks from config", e);
        }
    }

    private static void loadMasks(ModContainer container) {
        Loader.instance().setActiveModContainer(container);
        CraftingHelper.findFiles(container, "assets/" + container.getModId() + "/masquerade_masks", p -> true,
                MasqueDeserializer::loadMasks, true, true);
    }

    private static boolean loadMasks(Path root, Path file) {
        String relative = root.relativize(file).toString();
        if (!"json".equals(FilenameUtils.getExtension(file.toString())) || relative.startsWith("_"))
            return true;

        String name = FilenameUtils.removeExtension(relative).replaceAll("\\\\", "/");
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String registryName = Masquerade.MOD_ID + ":" + name;
            String unlocalizedName = null;
            String model = registryName;
            List<String> tooltipLines = new ArrayList<>();
            JsonReader in = new JsonReader(reader);
            in.beginObject();
            while (in.hasNext()) {
                switch (in.nextName()) {
                    case "registryName": registryName = in.nextString(); break;
                    case "unlocalizedName": unlocalizedName = in.nextString(); break;
                    case "model": model = in.nextString(); break;
                    case "tooltipLines":
                        in.beginArray();
                        while (in.hasNext()) {
                            tooltipLines.add(in.nextString());
                        }
                        in.endArray();
                        break;
                    default: in.nextString();
                }
            }
            in.endObject();
            if (unlocalizedName == null) {
                unlocalizedName = registryName.replace(':', '.');
            }
            Masque mask = new Masque(unlocalizedName, tooltipLines, model).setRegistryName(registryName);
            Masque.REGISTRY.register(mask);
            ForgeRegistries.ITEMS.register(Masquerade.ObjectRegistryHandler.createMask(mask.getRegistryName()));
        } catch (IOException e) {
            Masquerade.LOGGER.error("Failed to load masks", e);
        }
        return true;
    }

}
