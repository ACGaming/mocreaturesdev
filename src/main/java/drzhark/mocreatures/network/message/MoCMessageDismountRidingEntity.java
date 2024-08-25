package drzhark.mocreatures.network.message;

import drzhark.mocreatures.entity.IMoCEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MoCMessageDismountRidingEntity implements IMessage, IMessageHandler<MoCMessageDismountRidingEntity, IMessage> {

    public int entityId;
    public MoCMessageDismountRidingEntity() {
    }

    public MoCMessageDismountRidingEntity(int entityId) {
        this.entityId = entityId;
    }

    @Override
    public void toBytes(ByteBuf buffer) {
        buffer.writeInt(this.entityId);
    }

    @Override
    public void fromBytes(ByteBuf buffer) {
        this.entityId = buffer.readInt();
    }

    @Override
    public IMessage onMessage(MoCMessageDismountRidingEntity message, MessageContext ctx) {
        EntityPlayer player = ctx.getServerHandler().player;

        Entity entity = player.world.getEntityByID(this.entityId);
        if (entity instanceof IMoCEntity) {
            entity.dismountRidingEntity();
            entity.setPosition(player.posX, player.posY + 1D, player.posZ);
        }
        return null;
    }

    @Override
    public String toString() {
        return String.format("MoCMessageDismountRidingEntity - entityId:%s", this.entityId);
    }

}
