package fish.crafting.fimfabric.util;

import fish.crafting.fimfabric.client.FIMModClient;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class ModUtil {


    public static Identifier identifier(@NotNull String id){
        return Identifier.of(FIMModClient.NAMESPACE, id);
    }

}
