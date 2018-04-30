package ladysnake.masquerade.core;

import ladysnake.masquerade.PlayerGetRenderNameEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.function.Consumer;

public class MasqueradeClassTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String className, String transformedName, byte[] basicClass) {
        switch (transformedName) {
            /*
             * This mod prevents players with a mask from showing their nameplate.
             * This hook is needed to reliably control the player's nameplate display,
             * as vanilla always render it unless the player is fully invisible.
             */
            case "net.minecraft.entity.player.EntityPlayer":
                return transformClass(basicClass, classNode -> {
                    // func_94059_bO <=> getAlwaysRenderNameTagForRender
                    String name = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName("net/minecraft/entity/player/EntityPlayer", "func_94059_bO", "()Z");
                    for (MethodNode methodNode : classNode.methods) {
                        if (methodNode.name.equals(name) && methodNode.desc.equals("()Z")) {
                            for (int i = 0; i < methodNode.instructions.size(); i++) {
                                AbstractInsnNode insnNode = methodNode.instructions.get(i);
                                if (insnNode.getOpcode() == Opcodes.IRETURN) {
                                    InsnList insnList = new InsnList();
                                    // pop the previous value
                                    insnList.add(new InsnNode(Opcodes.POP));
                                    insnList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                                    insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "ladysnake/masquerade/core/MasqueradeClassTransformer", "hook", "(Lnet/minecraft/entity/player/EntityPlayer;)Z;", false));
                                    i += insnList.size();
                                    methodNode.instructions.insertBefore(insnNode, insnList);
                                }
                            }
                        }
                    }
                });
        }
        return basicClass;
    }

    private byte[] transformClass(byte[] basicClass, Consumer<ClassNode> transformer) {
        ClassReader reader = new ClassReader(basicClass);
        ClassNode classNode = new ClassNode();
        reader.accept(classNode, 0);

        transformer.accept(classNode);

        ClassWriter writer = new SafeClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        classNode.accept(writer);
        return writer.toByteArray();
    }

    public static boolean hook(EntityPlayer player) {
        return !MinecraftForge.EVENT_BUS.post(new PlayerGetRenderNameEvent(player));
    }
}
