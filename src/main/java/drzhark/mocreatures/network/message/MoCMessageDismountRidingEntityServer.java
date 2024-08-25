package drzhark.mocreatures.network.message;

import drzhark.mocreatures.MoCTools;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MoCMessageDismountRidingEntityServer implements IMessage, IMessageHandler<MoCMessageDismountRidingEntityServer, IMessage> {

    public int passengerId;
    public MoCMessageDismountRidingEntityServer() {
    }

    public MoCMessageDismountRidingEntityServer(int passengerId) {
        this.passengerId = passengerId;
    }

    @Override
    public void toBytes(ByteBuf buffer) {
        buffer.writeInt(this.passengerId);
    }

    @Override
    public void fromBytes(ByteBuf buffer) {
        this.passengerId = buffer.readInt();
    }

    @Override
    public IMessage onMessage(MoCMessageDismountRidingEntityServer message, MessageContext ctx) {
        // get player from playerId
        EntityPlayer player = ctx.getServerHandler().player;
        MoCTools.handleDismountPassengerFromEntity(message.passengerId, player);
        return null;
    }

    @Override
    public String toString() {
        return String.format("MoCMessageDismountRidingEntityServer - passengerId:%s", this.passengerId);
    }

}
