package narakomii.kotweaks.mixin.accessors;

import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SuggestionsBuilder.class)
public interface SuggestionsBuilderAccessor {
    @Accessor("inputLowerCase")
    String kotweaks$getInputLowerCase();
}
