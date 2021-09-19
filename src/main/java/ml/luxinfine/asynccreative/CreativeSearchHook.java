package ml.luxinfine.asynccreative;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class CreativeSearchHook implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if(!transformedName.equals("net.minecraft.client.gui.inventory.GuiContainerCreative")) return basicClass;
        ClassNode classNode = new ClassNode();
        boolean java7 = (((basicClass[6] & 0xFF) << 8) | (basicClass[7] & 0xFF)) > 50;
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, java7 ? ClassReader.SKIP_FRAMES : ClassReader.EXPAND_FRAMES);
        boolean obf = !transformedName.equals(name);
        classNode.methods.stream().filter(method -> method.name.equals(obf ? "g" : "func_147053_i") && method.desc.equals("()V")).findFirst().ifPresent(method -> {
            method.instructions.clear();
            InsnList list = new InsnList();
            list.add(new VarInsnNode(Opcodes.ALOAD, 0));
            list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "ml/luxinfine/asynccreative/SearchHandler", "search", "(L" + name + ";)V", false));
            list.add(new InsnNode(Opcodes.RETURN));
            method.instructions.insert(list);
        });
        classNode.methods.stream().filter(method -> method.name.equals(obf ? "b" : "func_147050_b") && method.desc.equals("(L" + (obf ? "abt" : "net/minecraft/creativetab/CreativeTabs") + ";)V")).findFirst().ifPresent(method -> method.instructions.insert(new MethodInsnNode(Opcodes.INVOKESTATIC, "ml/luxinfine/asynccreative/SearchHandler", "stopSearch", "()V", false)));
        classNode.methods.stream().filter(method -> method.name.equals(obf ? "m" : "func_146281_b") && method.desc.equals("()V")).findFirst().ifPresent(method -> method.instructions.insert(new MethodInsnNode(Opcodes.INVOKESTATIC, "ml/luxinfine/asynccreative/SearchHandler", "stopSearch", "()V", false)));
        ClassWriter writer = new ClassWriter(java7 ? ClassWriter.COMPUTE_FRAMES : ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
    }
}
