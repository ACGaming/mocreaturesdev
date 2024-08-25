package drzhark.mocreatures.network.message;

import drzhark.mocreatures.network.MoCMessageHandler;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MoCMessageDismountRidingEntityClient implements IMessage, IMessageHandler<MoCMessageDismountRidingEntityClient, IMessage> {

    public int passengerId;

    public MoCMessageDismountRidingEntityClient() {
    }

    public MoCMessageDismountRidingEntityClient(int passengerId) {
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
    public IMessage onMessage(MoCMessageDismountRidingEntityClient message, MessageContext ctx) {
        MoCMessageHandler.handleMessage(message, ctx);
        return null;
    }

    @Override
    public String toString() {
        return String.format("MoCMessageDismountRidingEntityClient - passengerId:%s", this.passengerId);
    }

}
