package ladysnake.masquerade;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public class PlayerGetRenderNameEvent extends PlayerEvent {
    public PlayerGetRenderNameEvent(EntityPlayer player) {
        super(player);
    }
}
