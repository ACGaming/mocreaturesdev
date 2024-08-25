/*
 * GNU GENERAL PUBLIC LICENSE Version 3
 */
package drzhark.mocreatures.network;

import drzhark.mocreatures.network.message.*;
import drzhark.mocreatures.proxy.MoCProxyClient;
import drzhark.mocreatures.client.gui.MoCGUIEntityNamer;
import drzhark.mocreatures.entity.IMoCEntity;
import drzhark.mocreatures.entity.tameable.IMoCTameable;
import drzhark.mocreatures.entity.hostile.MoCEntityGolem;
import drzhark.mocreatures.entity.hostile.MoCEntityOgre;
import drzhark.mocreatures.entity.passive.MoCEntityHorse;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import java.util.List;

public class MoCMessageHandler {

    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("MoCreatures");

    public static void init() {
        INSTANCE.registerMessage(MoCMessageAnimation.class, MoCMessageAnimation.class, 0, Side.CLIENT);
        INSTANCE.registerMessage(MoCMessageAppear.class, MoCMessageAppear.class, 1, Side.CLIENT);
        INSTANCE.registerMessage(MoCMessageAttachedEntity.class, MoCMessageAttachedEntity.class, 2, Side.CLIENT);
        INSTANCE.registerMessage(MoCMessageEntityDive.class, MoCMessageEntityDive.class, 3, Side.SERVER);
        INSTANCE.registerMessage(MoCMessageEntityJump.class, MoCMessageEntityJump.class, 4, Side.SERVER);
        INSTANCE.registerMessage(MoCMessageExplode.class, MoCMessageExplode.class, 5, Side.CLIENT);
        INSTANCE.registerMessage(MoCMessageHealth.class, MoCMessageHealth.class, 6, Side.CLIENT);
        INSTANCE.registerMessage(MoCMessageHeart.class, MoCMessageHeart.class, 7, Side.CLIENT);
        INSTANCE.registerMessage(MoCMessageInstaSpawn.class, MoCMessageInstaSpawn.class, 8, Side.SERVER);
        INSTANCE.registerMessage(MoCMessageNameGUI.class, MoCMessageNameGUI.class, 9, Side.CLIENT);
        INSTANCE.registerMessage(MoCMessageUpdatePetName.class, MoCMessageUpdatePetName.class, 10, Side.SERVER);
        INSTANCE.registerMessage(MoCMessageShuffle.class, MoCMessageShuffle.class, 11, Side.CLIENT);
        INSTANCE.registerMessage(MoCMessageTwoBytes.class, MoCMessageTwoBytes.class, 12, Side.CLIENT);
        INSTANCE.registerMessage(MoCMessageVanish.class, MoCMessageVanish.class, 13, Side.CLIENT);
        INSTANCE.registerMessage(MoCMessageDismountRidingEntityServer.class, MoCMessageDismountRidingEntityServer.class, 14, Side.SERVER);
    }

    // Wrap client message handling due to 1.8 clients receiving packets on Netty thread
    // This solves random NPE issues when attempting to access world data on client
    @SuppressWarnings("rawtypes")
    public static void handleMessage(IMessageHandler message, MessageContext ctx) {
        if (ctx.side == Side.CLIENT) {
            FMLCommonHandler.instance().getWorldThread(FMLCommonHandler.instance().getClientToServerNetworkManager().getNetHandler()).addScheduledTask(new ClientPacketTask(message, ctx));
        }
    }

    // redirects client received packets to main thread to avoid NPEs
    public static class ClientPacketTask implements Runnable {

        @SuppressWarnings("rawtypes")
        private final IMessageHandler message;
        @SuppressWarnings("unused")
        private final MessageContext ctx;

        @SuppressWarnings("rawtypes")
        public ClientPacketTask(IMessageHandler message, MessageContext ctx) {
            this.message = message;
            this.ctx = ctx;
        }

        @Override
        public void run() {
            if (this.message instanceof MoCMessageAnimation) {
                MoCMessageAnimation message = (MoCMessageAnimation) this.message;
                List<Entity> entList = MoCProxyClient.mc.player.world.loadedEntityList;
                for (Entity ent : entList) {
                    if (ent.getEntityId() == message.entityId && ent instanceof IMoCEntity) {
                        ((IMoCEntity) ent).performAnimation(message.animationType);
                        break;
                    }
                }
            } else if (this.message instanceof MoCMessageAppear) {
                MoCMessageAppear message = (MoCMessageAppear) this.message;
                List<Entity> entList = MoCProxyClient.mc.player.world.loadedEntityList;
                for (Entity ent : entList) {
                    if (ent.getEntityId() == message.entityId && ent instanceof MoCEntityHorse) {
                        ((MoCEntityHorse) ent).MaterializeFX();
                        break;
                    }
                }
            } else if (this.message instanceof MoCMessageAttachedEntity) {
                MoCMessageAttachedEntity message = (MoCMessageAttachedEntity) this.message;
                Entity var2 = MoCProxyClient.mc.player.world.getEntityByID(message.sourceEntityId);
                Entity var3 = MoCProxyClient.mc.player.world.getEntityByID(message.targetEntityId);

                if (var2 != null) {
                    var2.startRiding(var3);
                }
            } else if (this.message instanceof MoCMessageExplode) {
                MoCMessageExplode message = (MoCMessageExplode) this.message;
                List<Entity> entList = MoCProxyClient.mc.player.world.loadedEntityList;
                for (Entity ent : entList) {
                    if (ent.getEntityId() == message.entityId && ent instanceof MoCEntityOgre) {
                        ((MoCEntityOgre) ent).performDestroyBlastAttack();
                        break;
                    }
                }
            } else if (this.message instanceof MoCMessageHealth) {
                MoCMessageHealth message = (MoCMessageHealth) this.message;
                List<Entity> entList = MoCProxyClient.mc.player.world.loadedEntityList;
                for (Entity ent : entList) {
                    if (ent.getEntityId() == message.entityId && ent instanceof EntityLiving) {
                        ((EntityLiving) ent).setHealth(message.health);
                        break;
                    }
                }
            } else if (this.message instanceof MoCMessageHeart) {
                MoCMessageHeart message = (MoCMessageHeart) this.message;
                Entity entity = null;
                while (entity == null) {
                    entity = MoCProxyClient.mc.player.world.getEntityByID(message.entityId);
                    if (entity != null) {
                        if (entity instanceof IMoCTameable) {
                            ((IMoCTameable) entity).spawnHeart();
                        }
                    }
                }
            } else if (this.message instanceof MoCMessageShuffle) {
                MoCMessageShuffle message = (MoCMessageShuffle) this.message;
                List<Entity> entList = MoCProxyClient.mc.player.world.loadedEntityList;
                for (Entity ent : entList) {
                    if (ent.getEntityId() == message.entityId && ent instanceof MoCEntityHorse) {
                        if (message.flag) {
                            //((MoCEntityHorse) ent).shuffle();
                        } else {
                            ((MoCEntityHorse) ent).shuffleCounter = 0;
                        }
                        break;
                    }
                }
            } else if (this.message instanceof MoCMessageTwoBytes) {
                MoCMessageTwoBytes message = (MoCMessageTwoBytes) this.message;
                Entity ent = MoCProxyClient.mc.player.world.getEntityByID(message.entityId);
                if (ent instanceof MoCEntityGolem) {
                    ((MoCEntityGolem) ent).saveGolemCube(message.slot, message.value);
                }
            } else if (this.message instanceof MoCMessageVanish) {
                MoCMessageVanish message = (MoCMessageVanish) this.message;
                List<Entity> entList = MoCProxyClient.mc.player.world.loadedEntityList;
                for (Entity ent : entList) {
                    if (ent.getEntityId() == message.entityId && ent instanceof MoCEntityHorse) {
                        ((MoCEntityHorse) ent).setVanishC((byte) 1);
                        break;
                    }
                }
            } else if (this.message instanceof MoCMessageNameGUI) {
                MoCMessageNameGUI message = (MoCMessageNameGUI) this.message;
                Entity entity = MoCProxyClient.mc.player.world.getEntityByID(message.entityId);
                MoCProxyClient.mc.displayGuiScreen(new MoCGUIEntityNamer(((IMoCEntity) entity), ((IMoCEntity) entity).getPetName()));
            }
        }
    }
}
