package com.vcsajen.dynamicsymmetry;

import com.flowpowered.math.vector.Vector3i;
import com.vcsajen.dynamicsymmetry.exceptions.WorldNotFoundException;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.format.TextColors;

@Plugin(id = "dynamicsymmetry", name = "DynamicSymmetry", version = "1.0")
public class DynamicSymmetry
{
    private Game game;
    private WESelection sel;

    public CommandResult executehw(CommandSource src, CommandContext args) throws CommandException
    {
        try {
            WESelection sel = WEBridge.getSelection((Player) src).get();
            src.sendMessage(Text.of("Hello World!"));
            src.sendMessage(Text.of("Sel: " + sel.minPos.toString() + " " + sel.maxPos.toString() + " World: " + sel.world.getName()));
            return CommandResult.success();
        } catch (WorldNotFoundException e) {
            src.sendMessage(Text.of(TextColors.RED, "Selection world could not be found!"));
            return CommandResult.empty();
        }
    }

    public CommandResult executetest2(CommandSource src, CommandContext args) throws CommandException
    {
        if (sel==null) {
            sel = new WESelection();
            sel.world = Sponge.getServer().getWorld("MyNewDimension").get();
            sel.minPos = new Vector3i(-140, 75, 366);
            sel.maxPos = new Vector3i(-135, 78, 371);
        }
        try {
            WEBridge.setSelection((Player) src, sel);
            src.sendMessage(Text.of("Selected!"));
            return CommandResult.success();
        } catch (WorldNotFoundException e) {
            src.sendMessage(Text.of(TextColors.RED, "Selection world could not be found!"));
            return CommandResult.empty();
        }
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

        CommandSpec myCommandSpec2 = CommandSpec.builder()
                .description(Text.of("Test Command"))
                .permission("myplugin.command.test2")
                .executor(this::executetest2)
                .build();

        game.getCommandManager().register(this, myCommandSpec, "helloworld", "hello", "test");
        game.getCommandManager().register(this, myCommandSpec2, "test2");
    }
}
