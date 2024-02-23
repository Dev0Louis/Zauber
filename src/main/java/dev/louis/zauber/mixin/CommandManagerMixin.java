package dev.louis.zauber.mixin;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.context.ContextChain;
import net.minecraft.command.CommandExecutionContext;
import net.minecraft.command.ReturnValueConsumer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.Consumer;
import java.util.function.Supplier;

@Mixin(CommandManager.class)
public abstract class CommandManagerMixin {

    @Shadow
    @Nullable
    protected static ContextChain<ServerCommandSource> checkCommand(ParseResults<ServerCommandSource> parseResults, String command, ServerCommandSource source) {
        return null;
    }

    @Shadow @Final private static Logger LOGGER;

    @Shadow
    public static void callWithContext(ServerCommandSource commandSource, Consumer<CommandExecutionContext<ServerCommandSource>> callback) {

    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void execute(ParseResults<ServerCommandSource> parseResults, String command) {
        ServerCommandSource serverCommandSource = parseResults.getContext().getSource();
        serverCommandSource.getServer().getProfiler().push((Supplier<String>)(() -> "/" + command));
        ContextChain<ServerCommandSource> contextChain = checkCommand(parseResults, command, serverCommandSource);

        try {
            if (contextChain != null) {
                callWithContext(
                        serverCommandSource, context -> CommandExecutionContext.enqueueCommand(context, command, contextChain, serverCommandSource, ReturnValueConsumer.EMPTY)
                );
            }
        } catch (Exception var12) {
            var12.printStackTrace();
            throw new RuntimeException(var12);
        } finally {
            serverCommandSource.getServer().getProfiler().pop();
        }
    }
}
