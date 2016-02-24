package com.vcsajen.dynamicsymmetry;

import com.flowpowered.math.vector.Vector3i;
import com.google.inject.Inject;
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
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginManager;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.action.HoverAction;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.slf4j.Logger;

@Plugin(id = "dynamicsymmetry", name = "DynamicSymmetry", version = "1.0")
public class DynamicSymmetry
{
    @Inject
    private Game game;

    @Inject
    private PluginManager pluginManager;

    @Inject
    private Logger logger;

    private WESelection sel;
    private SpLocalization spLoc;
    private boolean weAvailable;

    public CommandResult executehw(CommandSource src, CommandContext args) throws CommandException
    {

        //src.sendMessage(Text.of(""));
        ItemStack itemStack = ((Player)src).getItemInHand().orElse(ItemStack.of(ItemTypes.BEDROCK,1));
        src.sendMessage(Text.builder(itemStack).onHover(TextActions.showItem(itemStack)).build());
        //((Player)src).getName();
        String playerName = src.getName();
        //src.sendMessage(Text.builder(playerName).onHover(TextActions.showEntity(((Player)src).getUniqueId(), playerName, ((Player)src).getType())).build());

        spLoc.sendNonLocalizedMessage(src, "{2}: {1}, {0}", itemStack, 24.1001, src);

        return CommandResult.success();
        /*try {
            spLoc.sendMessage(src, "test.helloworld");
            if (!weAvailable) return CommandResult.empty();

            if (!(src instanceof Player)) return CommandResult.empty();

            WEBridge.getSelection((Player) src).ifPresent(sel ->
                    spLoc.sendMessage(src, "test.weShowSelCoords", sel.minPos.toString(), sel.maxPos.toString(), sel.world.getName()));
            //else return CommandResult.empty();
            return CommandResult.success();
        } catch (WorldNotFoundException e) {
            src.sendMessage(Text.of(TextColors.RED, "Selection world could not be found!"));
            return CommandResult.empty();
        }*/
    }

    public CommandResult executetest2(CommandSource src, CommandContext args) throws CommandException
    {
        if (!weAvailable) return CommandResult.empty();
        if (sel==null) {
            sel = new WESelection();
            sel.world = Sponge.getServer().getWorld("world").get();
            sel.minPos = new Vector3i(-140, 75, 366);
            sel.maxPos = new Vector3i(-135, 79, 371);
        }
        try {
            WEBridge.setSelection((Player) src, sel);
            //src.sendMessage(Text.of("Selected!"));
            spLoc.sendMessage(src, "test.selSuccess");
            return CommandResult.success();
        } catch (WorldNotFoundException e) {
            src.sendMessage(Text.of(TextColors.RED, "Selection world could not be found!"));
            return CommandResult.empty();
        }
    }

    @Listener
    public void onGamePostInitialization(GamePostInitializationEvent event)
    {
        weAvailable = pluginManager.isLoaded("worldedit");
    }

    @Listener
    public void onGameInitialization(GameInitializationEvent event)
    {
        CommandSpec myCommandSpec = CommandSpec.builder()
                .description(TranslationHelper.t("command.helloworld.desc"))
                .extendedDescription(TranslationHelper.t("command.helloworld.desc"))
                .permission("myplugin.command.helloworld")
                .executor(this::executehw)
                .build();

        CommandSpec myCommandSpec2 = CommandSpec.builder()
                .description(TranslationHelper.t("command.test2.desc"))
                .permission("myplugin.command.test2")
                .executor(this::executetest2)
                .build();

        game.getCommandManager().register(this, myCommandSpec, "helloworld", "hello", "test");
        game.getCommandManager().register(this, myCommandSpec2, "test2");

        spLoc = new SpLocalization(logger);
    }
}
