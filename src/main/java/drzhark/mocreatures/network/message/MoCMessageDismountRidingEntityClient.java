package drzhark.mocreatures.network.message;

import drzhark.mocreatures.entity.IMoCEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MoCMessageDismountRidingEntityClient implements IMessage, IMessageHandler<MoCMessageDismountRidingEntityClient, IMessage> {

    public int entityId;
    public MoCMessageDismountRidingEntityClient() {
    }

    public MoCMessageDismountRidingEntityClient(int entityId) {
        this.entityId = entityId;
    }

    @Override
    public void toBytes(ByteBuf buffer) {
        ByteBufUtils.writeVarInt(buffer, this.entityId, 5);
    }

    @Override
    public void fromBytes(ByteBuf buffer) {
        this.entityId = ByteBufUtils.readVarInt(buffer, 5);
    }

    @Override
    public IMessage onMessage(MoCMessageDismountRidingEntityClient message, MessageContext ctx) {
        EntityPlayer player = ctx.getServerHandler().player;

        Entity entity = player.world.getEntityByID(this.entityId);
        if (entity instanceof IMoCEntity) {
            entity.setPosition(player.posX, player.posY + 1D, player.posZ);
        }
        return null;
    }

    @Override
    public String toString() {
        return String.format("MoCMessageDismountRidingEntityClient - entityId:%s", this.entityId);
    }

}
