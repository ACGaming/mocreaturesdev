package drzhark.mocreatures.network.message;

import drzhark.mocreatures.network.MoCMessageHandler;
import io.netty.buffer.ByteBuf;
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
        MoCMessageHandler.handleMessage(message, ctx);
        return null;
    }

    @Override
    public String toString() {
        return String.format("MoCMessageDismountRidingEntityClient - entityId:%s", this.entityId);
    }

}
