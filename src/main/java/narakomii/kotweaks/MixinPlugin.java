package narakomii.kotweaks;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

// https://github.com/MeteorDevelopment/meteor-client/blob/23880216627d72d28394c0cddab0168fc028e42b/src/main/java/meteordevelopment/meteorclient/MixinPlugin.java
public class MixinPlugin implements IMixinConfigPlugin {
    private static String MIXIN_PACKAGE = "narakomii.kotweaks.mixin.mods.";

    private static boolean loaded;

    private static final Set<String> modMap = new HashSet<>();

    @Override
    public void onLoad(String mixinPackage) {
        if (loaded) return;
        addMod("vmp");
        loaded = true;
    }

    private static void addMod(String mod) {
        if (FabricLoader.getInstance().isModLoaded(mod)) {
            modMap.add(mod);
        }
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (!mixinClassName.startsWith(MIXIN_PACKAGE)) {
            throw new RuntimeException("Mixin " + mixinClassName + " is not in the mixin package");
        }

        mixinClassName = mixinClassName.substring(MIXIN_PACKAGE.length()).split("\\.")[0];
        System.out.printf("mixinClassName: %s%n", mixinClassName);
        return modMap.contains(mixinClassName);
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
