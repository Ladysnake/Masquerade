package ladysnake.masquerade;

import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class CanRenderNameEvent extends Event {
    private final EntityLivingBase entity;
    private final RenderLivingBase renderer;
    public CanRenderNameEvent(RenderLivingBase renderer, EntityLivingBase entity) {
        this.renderer = renderer;
        this.entity = entity;
    }

    public EntityLivingBase getEntity() {
        return entity;
    }

    public RenderLivingBase getRenderer() {
        return renderer;
    }
}
