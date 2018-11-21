package com.porpit.ultimatestack.transform;

import com.porpit.ppcore.transform.PPCoreTransformer;
import com.porpit.ppcore.transform.Transformer;
import com.porpit.ultimatestack.common.config.ConfigLoader;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import javax.security.auth.login.Configuration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class UltimateStackTransformer extends PPCoreTransformer implements IClassTransformer {

    protected void initTransformers() {

        List<String> invClassList = new ArrayList<String>();
        invClassList.add("net.minecraft.inventory.InventoryBasic");
        invClassList.add("net.minecraft.entity.player.InventoryPlayer");
        invClassList.add("net.minecraft.inventory.InventoryCrafting");
        invClassList.add("net.minecraft.inventory.InventoryCraftResult");
        invClassList.add("net.minecraft.inventory.InventoryMerchant");
        invClassList.add("net.minecraft.tileentity.TileEntityChest");
        invClassList.add("net.minecraft.tileentity.TileEntityFurnace");
        invClassList.add("net.minecraft.tileentity.TileEntityHopper");
        invClassList.add("net.minecraft.tileentity.TileEntityShulkerBox");
        invClassList.add("net.minecraft.tileentity.TileEntityDispenser");
        invClassList.add("net.minecraft.tileentity.TileEntityBrewingStand");
        invClassList.add("net.minecraft.entity.item.EntityMinecartContainer");

        for (String className : invClassList) {
            addTransformer(new Transformer(className) {
                @Override
                public byte[] transform(byte[] data) {
                    ClassReader classReader = new ClassReader(data);
                    ClassNode node = new ClassNode();
                    classReader.accept(node, 0);
                    MethodNode m = findMethod(node, "getInventoryStackLimit", "()I");
                    AbstractInsnNode currentNode = null;
                    @SuppressWarnings("unchecked")
                    Iterator<AbstractInsnNode> iter = m.instructions.iterator();
                    while (iter.hasNext()) {
                        currentNode = iter.next();
                        if (currentNode instanceof IntInsnNode) {
                            if (((IntInsnNode) currentNode).operand == 64) {
                                m.instructions.set(currentNode, new IntInsnNode(Opcodes.SIPUSH, ConfigLoader.MAX_STACK_SIZE));
                                System.out.println("Patched Method:" + className + ".getInventoryStackLimit:" + "MaxStackSize");
                            }
                        }
                    }
                    ClassWriter writer = new ClassWriter(0);
                    node.accept(writer);
                    return writer.toByteArray();
                }
            });
        }

        addTransformer(new Transformer("net.minecraft.inventory.InventoryHelper") {
            @Override
            public byte[] transform(byte[] data) {
                ClassReader classReader = new ClassReader(data);
                ClassNode node = new ClassNode();
                classReader.accept(node, 0);
                MethodNode m = findMethod(node, "spawnItemStack", "(Lnet/minecraft/world/World;DDDLnet/minecraft/item/ItemStack;)V");
                AbstractInsnNode currentNode = null;
                @SuppressWarnings("unchecked")
                Iterator<AbstractInsnNode> iter = m.instructions.iterator();
                while (iter.hasNext()) {
                    currentNode = iter.next();
                    if (currentNode instanceof FieldInsnNode) {
                        if (((FieldInsnNode) currentNode).name.equals(patchFieldName("RANDOM"))) {
                            if (currentNode.getNext() instanceof IntInsnNode && ((IntInsnNode) currentNode.getNext()).operand == 21) {
                                m.instructions.set(currentNode.getNext(), new IntInsnNode(Opcodes.SIPUSH, ConfigLoader.MAX_STACK_SIZE/3));
                                System.out.println("Patched Method:" + className + ".spawnItemStack:" + "Random Range");
                            }
                        }
                    }
                }
                ClassWriter writer = new ClassWriter(0);
                node.accept(writer);
                return writer.toByteArray();
            }
        });
        addTransformer(new Transformer("org.spongepowered.common.item.inventory.util.ContainerUtil") {
            @Override
            public byte[] transform(byte[] data) {
                ClassReader classReader = new ClassReader(data);
                ClassNode node = new ClassNode();
                classReader.accept(node, 0);
                MethodNode m = findMethod(node, "performBlockInventoryDrops", "(Lnet/minecraft/world/WorldServer;DDDLnet/minecraft/inventory/IInventory;)V", false);
                if (m != null) {
                    AbstractInsnNode currentNode = null;
                    @SuppressWarnings("unchecked")
                    Iterator<AbstractInsnNode> iter = m.instructions.iterator();
                    while (iter.hasNext()) {
                        currentNode = iter.next();
                        if (currentNode instanceof FieldInsnNode) {
                            if (((FieldInsnNode) currentNode).name.equals(patchFieldName("RANDOM"))) {
                                if (currentNode.getNext() instanceof IntInsnNode && ((IntInsnNode) currentNode.getNext()).operand == 21) {
                                    m.instructions.set(currentNode.getNext(), new IntInsnNode(Opcodes.SIPUSH, 7000));
                                    System.out.println("Patched Method:" + className + ".performBlockInventoryDrops:" + "Random Range");
                                }
                            }
                        }
                    }
                }
                ClassWriter writer = new ClassWriter(0);
                node.accept(writer);
                return writer.toByteArray();

            }
        });
        addTransformer(new Transformer("net.minecraftforge.common.util.PacketUtil") {
            @Override
            public byte[] transform(byte[] data) {
                ClassReader classReader = new ClassReader(data);
                ClassNode node = new ClassNode();
                classReader.accept(node, 0);
                spliceClasses(node, "com.porpit.ultimatestack.transform.patch.PacketUtilPatch", "writeItemStackFromClientToServer");

               /* MethodNode m = findMethod(node, "writeItemStackFromClientToServer", "(Lnet/minecraft/network/PacketBuffer;Lnet/minecraft/item/ItemStack;)V");
                AbstractInsnNode currentNode = null;
                @SuppressWarnings("unchecked")
                Iterator<AbstractInsnNode> iter = m.instructions.iterator();
                while (iter.hasNext()) {
                    currentNode = iter.next();
                    if (currentNode instanceof MethodInsnNode) {
                        if (((MethodInsnNode) currentNode).name.equals(patchMethodName("net.minecraft.item.ItemStack", "getCount", "()I"))) {
                            if (currentNode.getNext() instanceof MethodInsnNode) {
                                m.instructions.set(currentNode.getNext(), new MethodInsnNode(currentNode.getNext().getOpcode(), ((MethodInsnNode) currentNode.getNext()).owner, patchMethodName("writeShort", "(I)Lio/netty/buffer/ByteBuf;"), "(I)Lio/netty/buffer/ByteBuf;", ((MethodInsnNode) currentNode.getNext()).itf));
                                System.out.println("Patched Method:" + className + ".writeItemStackFromClientToServer:" + " Item Count Byte->Short");
                            }
                        }
                    }
                }*/
                ClassWriter writer = new ClassWriter(0);
                node.accept(writer);
                return writer.toByteArray();

            }
        });
        addTransformer(new Transformer("net.minecraft.network.PacketBuffer") {
            @Override
            public byte[] transform(byte[] data) {

                // node.methods.forEach(it-> System.out.println(it.name+";"+it.desc));

                return spliceClasses(data, "com.porpit.ultimatestack.transform.patch.PacketBufferPatch", "readItemStack", "func_150791_c",
                        "writeItemStack", "func_150788_a");
               /* ClassNode patchNode = getClassNode(PPDummy.class,"com.porpit.ultimatestack.transform.patch.PacketBufferPatch");


                for(int i=0;i<node.methods.size();i++){
                    if(node.methods.get(i).name.equals(patchMethodName("readItemStack", "()Lnet/minecraft/item/ItemStack;"))
                            &&node.methods.get(i).desc.equals(patchDESC("()Lnet/minecraft/item/ItemStack;")))
                    {
                        for (String s : patchNode.interfaces) {
                            if (s.equals(patchMethodName("readItemStack", "()Lnet/minecraft/item/ItemStack;"))) {
                                node.interfaces.add(s);
                                System.out.println("Added INTERFACE: " + s);
                            }
                        }

                        MethodNode oldNode=node.methods.get(i);
                        if (patchNode != null) {

                            MethodNode newNode= findMethod(patchNode, "readItemStack",patchDESC("()Lnet/minecraft/item/ItemStack;"),false );
                            if(newNode!=null){
                                node.methods.set(i,newNode);
                                if (node.superName != null && node.name.equals(patchNode.superName)) {
                                    ListIterator<AbstractInsnNode> nodeListIterator = newNode.instructions.iterator();
                                    while (nodeListIterator.hasNext()) {
                                        AbstractInsnNode tempnode = nodeListIterator.next();
                                        if (tempnode instanceof MethodInsnNode
                                                && tempnode.getOpcode() == Opcodes.INVOKESPECIAL) {
                                            MethodInsnNode methodNode = (MethodInsnNode) tempnode;
                                            System.out.println(node.name);
                                            System.out.println(methodNode.owner);
                                            if (node.name.equals(methodNode.owner)) {
                                                methodNode.owner = node.superName;
                                            }
                                        }
                                    }
                                }


                            }
                        }
                    }

                    if(node.methods.get(i).name.equals(patchMethodName("writeItemStack", "(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/network/PacketBuffer;"))
                            &&node.methods.get(i).desc.equals(patchDESC("(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/network/PacketBuffer;")))
                    {
                        for (String s : patchNode.interfaces) {
                            if (s.equals(patchMethodName("writeItemStack", "(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/network/PacketBuffer;"))) {
                                node.interfaces.add(s);
                                System.out.println("Added INTERFACE: " + s);
                            }
                        }
                        if (patchNode != null) {
                            MethodNode newNode= findMethod(patchNode, "writeItemStack",patchDESC("(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/network/PacketBuffer;"),false );
                            if(newNode!=null){
                                node.methods.set(i,newNode);

                                if (node.superName != null && node.name.equals(patchNode.superName)) {
                                    ListIterator<AbstractInsnNode> nodeListIterator = newNode.instructions.iterator();
                                    while (nodeListIterator.hasNext()) {
                                        AbstractInsnNode tempnode = nodeListIterator.next();
                                        if (tempnode instanceof MethodInsnNode
                                                && tempnode.getOpcode() == Opcodes.INVOKESPECIAL) {
                                            MethodInsnNode methodNode = (MethodInsnNode) tempnode;
                                            if (node.name.equals(methodNode.owner)) {
                                                methodNode.owner = node.superName;
                                            }
                                        }
                                    }
                                }
                                System.out.println("Patched Method:" + className + ".writeItemStack:");
                            }
                        }
                    }
                }*/


                /*MethodNode m = findMethod(node, "readItemStack", "()Lnet/minecraft/item/ItemStack;");


                m.instructions.clear();
                m.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/porpit/ultimatestack/transform/patch/PacketBufferPatch", "readItemStack", "()Lnet/minecraft/item/ItemStack;", false));
                System.out.println("Patched Method:" + className + ".readItemStack:");


                m = findMethod(node, "writeItemStack", "(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/network/PacketBuffer;");
                m.instructions.clear();
                m.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/porpit/ultimatestack/transform/patch/PacketBufferPatch", "writeItemStack", "(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/network/PacketBuffer;", false));
                System.out.println("Patched Method:" + className + ".writeItemStack:");*/


/*                MethodNode m = findMethod(node, "readItemStack", "()Lnet/minecraft/item/ItemStack;");
                AbstractInsnNode currentNode = null;
                Iterator<AbstractInsnNode> iter = m.instructions.iterator();
                while (iter.hasNext()) {
                    currentNode = iter.next();
                    if (currentNode instanceof MethodInsnNode) {
                        System.out.println("MethodInsnNode:" + ((MethodInsnNode) currentNode).name);

                    }
                    if (currentNode instanceof FieldInsnNode) {
                        System.out.println("FieldInsnNode:" + ((FieldInsnNode) currentNode).name);
                    }
                    if (currentNode instanceof VarInsnNode) {
                        System.out.println("VarInsnNode:" + ((VarInsnNode) currentNode).var);
                    }
                    if (currentNode instanceof LdcInsnNode) {
                        System.out.println("LdcInsnNode:" + ((LdcInsnNode) currentNode).cst);
                    }

                    if (currentNode instanceof InsnNode) {
                        System.out.println("InsnNode:" + ((InsnNode) currentNode).getType());
                    }
                    if (currentNode instanceof IntInsnNode) {
                        System.out.println("InsnNode:" + ((IntInsnNode) currentNode).operand);
                    }
                    if (currentNode instanceof LabelNode) {
                        System.out.println("LabelNode:" + ((LabelNode) currentNode).getLabel().info);
                    }
                    if (currentNode instanceof LineNumberNode) {
                        System.out.println("LineNumberNode:" + ((LineNumberNode) currentNode).line);
                    }
                }
                m.instructions.clear();
                m.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/porpit/ultimatestack/transform/PacketBuffer", "readItemStack", "()Lnet/minecraft/item/ItemStack;", false));
                m.instructions.add(new InsnNode(Opcodes.F_FULL));
                System.out.println("--------------------------");
                iter = m.instructions.iterator();
                while (iter.hasNext()) {
                    currentNode = iter.next();
                    if (currentNode instanceof MethodInsnNode) {
                        System.out.println("MethodInsnNode:" + ((MethodInsnNode) currentNode).name);

                    }
                    if (currentNode instanceof FieldInsnNode) {
                        System.out.println("FieldInsnNode:" + ((FieldInsnNode) currentNode).name);
                    }
                    if (currentNode instanceof VarInsnNode) {
                        System.out.println("VarInsnNode:" + ((VarInsnNode) currentNode).var);
                    }
                    if (currentNode instanceof LdcInsnNode) {
                        System.out.println("LdcInsnNode:" + ((LdcInsnNode) currentNode).cst);
                    }

                    if (currentNode instanceof InsnNode) {
                        System.out.println("InsnNode:" + ((InsnNode) currentNode).getType());
                    }
                    if (currentNode instanceof IntInsnNode) {
                        System.out.println("InsnNode:" + ((IntInsnNode) currentNode).operand);
                    }
                    if (currentNode instanceof LabelNode) {
                        System.out.println("LabelNode:" + ((LabelNode) currentNode).getLabel().info);
                    }
                    if (currentNode instanceof LineNumberNode) {
                        System.out.println("LineNumberNode:" + ((LineNumberNode) currentNode).line);
                    }
                }


                System.out.println("Patched Method:" + className + ".readItemStack:" + " Item Count Byte->Short");



                m = findMethod(node, "writeItemStack", "(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/network/PacketBuffer;");
                m.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/porpit/ultimatestack/transform/PacketBuffer", "writeItemStack", "Lnet/minecraft/item/ItemStack;)Lnet/minecraft/network/PacketBuffer;", false));
                System.out.println("Patched Method:" + className + ".writeItemStack:" + " Item Count Byte->Short");
                m.instructions.add(new InsnNode(Opcodes.ARETURN));*/




/*
                MethodNode m = findMethod(node, "readItemStack", "()Lnet/minecraft/item/ItemStack;");
                 AbstractInsnNode currentNode = null;
                @SuppressWarnings("unchecked")
                Iterator<AbstractInsnNode> iter = m.instructions.iterator();
                while (iter.hasNext()) {
                    currentNode = iter.next();
                    if (currentNode instanceof MethodInsnNode) {
                        if (((MethodInsnNode) currentNode).name.equals(patchMethodName("readByte", "()B"))) {
                            m.instructions.set(currentNode, new MethodInsnNode(currentNode.getOpcode(), ((MethodInsnNode) currentNode).owner, patchMethodName("readShort", "()S"), "()S", ((MethodInsnNode) currentNode).itf));
                            System.out.println("Patched Method:" + className + ".readItemStack:" + " Item Count Byte->Short");

                        }
                    }
                }
                m = findMethod(node, "writeItemStack", "(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/network/PacketBuffer;");
                iter = m.instructions.iterator();
                while (iter.hasNext()) {
                    currentNode = iter.next();
                    if (currentNode instanceof MethodInsnNode) {
                        if (((MethodInsnNode) currentNode).name.equals(patchMethodName("net.minecraft.item.ItemStack", "getCount", "()I"))) {
                            if (currentNode.getNext() instanceof MethodInsnNode) {
                                m.instructions.set(currentNode.getNext(), new MethodInsnNode(currentNode.getNext().getOpcode(), ((MethodInsnNode) currentNode.getNext()).owner, patchMethodName("writeShort", "(I)Lio/netty/buffer/ByteBuf;"), "(I)Lio/netty/buffer/ByteBuf;", ((MethodInsnNode) currentNode.getNext()).itf));
                                System.out.println("Patched Method:" + className + ".writeItemStack:" + " Item Count Byte->Short");
                            }
                        }
                    }
                }*/


            }
        });
        addTransformer(new Transformer("net.minecraft.client.renderer.RenderItem") {
            @Override
            public byte[] transform(byte[] data) {
                ClassReader classReader = new ClassReader(data);
                ClassNode node = new ClassNode();
                classReader.accept(node, 0);

                MethodNode m = findMethod(node, "renderItemOverlayIntoGUI", "(Lnet/minecraft/client/gui/FontRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V");
                AbstractInsnNode currentNode = null;
                @SuppressWarnings("unchecked")
                Iterator<AbstractInsnNode> iter = m.instructions.iterator();
                while (iter.hasNext()) {
                    currentNode = iter.next();
                    if (currentNode instanceof MethodInsnNode && currentNode.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                        if (((MethodInsnNode) currentNode).owner.equals(patchClassName("net/minecraft/client/gui/FontRenderer")) &&
                                ((MethodInsnNode) currentNode).desc.equals(patchDESC("(Ljava/lang/String;FFI)I"))) {
                            ((ListIterator<AbstractInsnNode>) iter).set(
                                    new MethodInsnNode(
                                            Opcodes.INVOKESTATIC,
                                            "com/porpit/ultimatestack/transform/patch/RenderItemPatch",
                                            "drawItemCountWithShadow",
                                            patchDESC("(Lnet/minecraft/client/gui/FontRenderer;Ljava/lang/String;FFI)I"),
                                            false
                                    ));

                            System.out.println("Patched item count render in RenderItem!");
                            break;
                        }
                    }
                }
                ClassWriter writer = new ClassWriter(0);
                node.accept(writer);
                return writer.toByteArray();

            }
        });
        addTransformer(new Transformer("net.minecraft.network.NetHandlerPlayServer") {
            @Override
            public byte[] transform(byte[] data) {
                ClassReader classReader = new ClassReader(data);
                ClassNode node = new ClassNode();
                classReader.accept(node, 0);

                MethodNode m = findMethod(node, "processCreativeInventoryAction", "(Lnet/minecraft/network/play/client/CPacketCreativeInventoryAction;)V");

                AbstractInsnNode currentNode = null;
                @SuppressWarnings("unchecked")
                Iterator<AbstractInsnNode> iter = m.instructions.iterator();
                while (iter.hasNext()) {
                    currentNode = iter.next();
                    if (currentNode instanceof MethodInsnNode) {
                        if (((MethodInsnNode) currentNode).name.equals(patchMethodName("net.minecraft.item.ItemStack", "getCount", "()I"))) {
                            if (currentNode.getNext() instanceof IntInsnNode) {
                                m.instructions.set(currentNode.getNext(), new IntInsnNode(Opcodes.SIPUSH,  ConfigLoader.MAX_STACK_SIZE));
                                System.out.println("Patched Method:" + className + ".processCreativeInventoryAction:" + " Creative Clone");
                            }
                        }
                    }
                }
                ClassWriter writer = new ClassWriter(0);
                node.accept(writer);
                return writer.toByteArray();

            }
        });
        addTransformer(new Transformer("net.minecraftforge.items.ItemStackHandler") {
            @Override
            public byte[] transform(byte[] data) {
                ClassReader classReader = new ClassReader(data);
                ClassNode node = new ClassNode();
                classReader.accept(node, 0);
                MethodNode m = findMethod(node, "getSlotLimit", "(I)I");
                AbstractInsnNode currentNode = null;
                @SuppressWarnings("unchecked")
                Iterator<AbstractInsnNode> iter = m.instructions.iterator();
                while (iter.hasNext()) {
                    currentNode = iter.next();
                    if (currentNode instanceof IntInsnNode && ((IntInsnNode) currentNode).operand == 64) {
                        m.instructions.set(currentNode, new IntInsnNode(Opcodes.SIPUSH, ConfigLoader.MAX_STACK_SIZE));
                        System.out.println("Patched Method:" + className + ".getSlotLimit:" + " MaxSlotLimit");
                    }
                }
                ClassWriter writer = new ClassWriter(0);
                node.accept(writer);
                return writer.toByteArray();

            }
        });
        addTransformer(new Transformer("net.minecraft.item.ItemStack") {
            @Override
            public byte[] transform(byte[] data) {
                ClassReader classReader = new ClassReader(data);
                ClassNode node = new ClassNode();
                classReader.accept(node, 0);
                MethodNode m = findMethod(node, "writeToNBT", "(Lnet/minecraft/nbt/NBTTagCompound;)Lnet/minecraft/nbt/NBTTagCompound;");
                AbstractInsnNode currentNode = null;
                @SuppressWarnings("unchecked")
                Iterator<AbstractInsnNode> iter = m.instructions.iterator();
                while (iter.hasNext()) {
                    currentNode = iter.next();
                    if (currentNode instanceof LdcInsnNode) {
                        if (((LdcInsnNode) currentNode).cst.equals("Count")) {
                            AbstractInsnNode convertTaget = currentNode.getNext().getNext().getNext();
                            AbstractInsnNode countMethodTaget = currentNode.getNext().getNext().getNext().getNext();
                            if (convertTaget instanceof InsnNode && convertTaget.getOpcode() == Opcodes.I2B) {
                                m.instructions.set(convertTaget, new InsnNode(Opcodes.I2S));
                                System.out.println("Patched Method:" + className + ".writeToNBT:" + " Int to Short");
                            }
                            if (countMethodTaget instanceof MethodInsnNode && ((MethodInsnNode) countMethodTaget).name.equals(patchMethodName("net.minecraft.nbt.NBTTagCompound", "setByte", "(Ljava/lang/String;B)V"))) {
                                m.instructions.set(countMethodTaget, new MethodInsnNode(countMethodTaget.getOpcode(), ((MethodInsnNode) countMethodTaget).owner, patchMethodName("net.minecraft.nbt.NBTTagCompound", "setShort", "(Ljava/lang/String;S)V"), "(Ljava/lang/String;S)V", ((MethodInsnNode) countMethodTaget).itf));
                                System.out.println("Patched Method:" + className + ".writeToNBT:" + " Item Count Byte->Short");

                            }


                        }
                    }

                }

                m = findMethod(node, "<init>", "(Lnet/minecraft/nbt/NBTTagCompound;)V");

                iter = m.instructions.iterator();

                while (iter.hasNext()) {
                    currentNode = iter.next();
                    if (currentNode instanceof LdcInsnNode) {
                        if (((LdcInsnNode) currentNode).cst.equals("Count")) {
                            AbstractInsnNode countMethodTaget = currentNode.getNext();
                            if (countMethodTaget instanceof MethodInsnNode && ((MethodInsnNode) countMethodTaget).name.equals(patchMethodName("net.minecraft.nbt.NBTTagCompound", "getByte", "(Ljava/lang/String;)B"))) {
                                m.instructions.set(countMethodTaget, new MethodInsnNode(countMethodTaget.getOpcode(), ((MethodInsnNode) countMethodTaget).owner, patchMethodName("net.minecraft.nbt.NBTTagCompound", "getShort", "(Ljava/lang/String;)S"), "(Ljava/lang/String;)S", ((MethodInsnNode) countMethodTaget).itf));
                                System.out.println("Patched Method:" + className + ".<init>:" + " Item Count Byte->Short");
                            }
                        }
                    }
                }

                ClassWriter writer = new ClassWriter(0);
                node.accept(writer);
                return writer.toByteArray();
            }
        });
        addTransformer(new Transformer("ic2.core.block.invslot.InvSlot") {
            @Override
            public byte[] transform(byte[] data) {
                ClassReader classReader = new ClassReader(data);
                ClassNode node = new ClassNode();
                classReader.accept(node, 0);
                //node.methods.forEach(it-> System.out.println(it.name+";"+it.desc));
                MethodNode m = findMethod(node, "<init>", "(Lic2/core/block/TileEntityInventory;Ljava/lang/String;Lic2/core/block/invslot/InvSlot$Access;ILic2/core/block/invslot/InvSlot$InvSide;)V");
                AbstractInsnNode currentNode = null;
                @SuppressWarnings("unchecked")
                Iterator<AbstractInsnNode> iter = m.instructions.iterator();

                while (iter.hasNext()) {
                    currentNode = iter.next();
/*                    if (currentNode instanceof MethodInsnNode) {
                        System.out.println("MethodInsnNode:"+((MethodInsnNode) currentNode).name);

                    }*/
                    if (currentNode instanceof FieldInsnNode) {
                        if (currentNode.getPrevious() instanceof IntInsnNode && ((IntInsnNode) currentNode.getPrevious()).operand == 64) {
                            m.instructions.set(currentNode.getPrevious(), new IntInsnNode(Opcodes.SIPUSH, ConfigLoader.MAX_STACK_SIZE));
                            System.out.println("[patched IC2 Method]" + className + ".<init>:" + " MaxStackSize");

                        }
                    }
                   /* if (currentNode instanceof VarInsnNode) {
                        System.out.println("VarInsnNode:"+((VarInsnNode) currentNode).var);
                    }
                    if (currentNode instanceof LdcInsnNode) {
                        System.out.println("LdcInsnNode:"+((LdcInsnNode) currentNode).cst);
                    }

                    if (currentNode instanceof InsnNode) {
                        System.out.println("InsnNode:"+((InsnNode) currentNode).getType());
                    }
                    if (currentNode instanceof IntInsnNode) {
                        System.out.println("InsnNode:"+((IntInsnNode) currentNode).operand);
                    }
                    if (currentNode instanceof LabelNode) {
                        System.out.println("LabelNode:"+((LabelNode) currentNode).getLabel().info);
                    }
                    if (currentNode instanceof LineNumberNode) {
                        System.out.println("LineNumberNode:"+((LineNumberNode) currentNode).line);
                    }*/
                }
                ClassWriter writer = new ClassWriter(0);
                node.accept(writer);
                return writer.toByteArray();
            }
        });

        List<String> ironChestClass = new ArrayList<>();
        ironChestClass.add("cpw.mods.ironchest.common.tileentity.chest.TileEntityIronChest");
        ironChestClass.add("cpw.mods.ironchest.common.tileentity.shulker.TileEntityIronShulkerBox");
        for (String className : ironChestClass) {
            addTransformer(new Transformer(className) {
                @Override
                public byte[] transform(byte[] data) {
                    ClassReader classReader = new ClassReader(data);
                    ClassNode node = new ClassNode();
                    classReader.accept(node, 0);
                    MethodNode m = findMethod(node, "func_70297_j_", "()I");
                    AbstractInsnNode currentNode = null;
                    @SuppressWarnings("unchecked")
                    Iterator<AbstractInsnNode> iter = m.instructions.iterator();
                    while (iter.hasNext()) {
                        currentNode = iter.next();
                        if (currentNode instanceof IntInsnNode && ((IntInsnNode) currentNode).operand == 64) {
                            m.instructions.set(currentNode, new IntInsnNode(Opcodes.SIPUSH, ConfigLoader.MAX_STACK_SIZE));
                            System.out.println("[patched IronChest Method]" + className + ".func_70297_j_:" + " MaxStackSize");
                        }
                    }
                    ClassWriter writer = new ClassWriter(0);
                    node.accept(writer);
                    return writer.toByteArray();
                }
            });
        }

    }


}
