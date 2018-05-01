package ladysnake.masquerade.core;

import ladysnake.masquerade.CanRenderNameEvent;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityLivingBase;
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
            case "net.minecraft.client.renderer.entity.RenderLivingBase":
                return transformClass(basicClass, classNode -> {
                    // RenderLivingBase#canRenderName
                    String desc = "(Lnet/minecraft/entity/EntityLivingBase;)Z";
                    String name = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(
                            "net/minecraft/client/renderer/entity/RenderLivingBase",
                            "func_177070_b",
                            desc
                    );
                    for (MethodNode methodNode : classNode.methods) {
                        if (methodNode.name.equals(name) && methodNode.desc.equals(desc)) {
                            System.out.println("Found " + name + " !");
                            InsnList preIns = new InsnList();
                            preIns.add(new VarInsnNode(Opcodes.ALOAD, 0));    // this renderer
                            preIns.add(new VarInsnNode(Opcodes.ALOAD, 1));    // the rendered entity
                            preIns.add(new MethodInsnNode(
                                    Opcodes.INVOKESTATIC,
                                    "ladysnake/masquerade/core/MasqueradeClassTransformer",
                                    "hook",
                                    "(Lnet/minecraft/client/renderer/entity/RenderLivingBase;Lnet/minecraft/entity/EntityLivingBase;)Z",
                                    false
                            ));
                            LabelNode lbl = new LabelNode();
                            preIns.add(new JumpInsnNode(Opcodes.IFEQ, lbl));
                            preIns.add(new InsnNode(Opcodes.ICONST_0));
                            preIns.add(new InsnNode(Opcodes.IRETURN));
                            preIns.add(lbl);
                            methodNode.instructions.insert(preIns);
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

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        classNode.accept(writer);
        return writer.toByteArray();
    }

    public static boolean hook(RenderLivingBase renderer, EntityLivingBase entity) {
        return MinecraftForge.EVENT_BUS.post(new CanRenderNameEvent(renderer, entity));
    }
}
