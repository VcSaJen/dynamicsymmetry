package com.vcsajen.dynamicsymmetry;

import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.command.spec.CommandSpec;

@Plugin(id = "dynamicsymmetry", name = "DynamicSymmetry", version = "1.0")
public class DynamicSymmetry
{
    private Game game;

    public CommandResult executehw(CommandSource src, CommandContext args) throws CommandException
    {
        src.sendMessage(Text.of("Hello World!"));
        return CommandResult.success();
    }

    @Listener
    public void onGameInitialization(GameInitializationEvent event)
    {
        game = Sponge.getGame();

        CommandSpec myCommandSpec = CommandSpec.builder()
                .description(Text.of("Hello World Command"))
                .permission("myplugin.command.helloworld")
                .executor(this::executehw)
                .build();

        game.getCommandManager().register(this, myCommandSpec, "helloworld", "hello", "test");
    }
}
