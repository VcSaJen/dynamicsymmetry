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
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.action.InteractEvent;
import org.spongepowered.api.event.block.CollideBlockEvent;
import org.spongepowered.api.event.filter.type.Exclude;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.event.item.inventory.UseItemStackEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginManager;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.slf4j.Logger;

import java.util.Locale;

@Plugin(id = "dynamicsymmetry", name = "DynamicSymmetry", version = "1.0", authors={"VcSaJen"}, description = "Build with continuous symmetry!")
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

    public CommandResult overloadtest1(CommandSource src, CommandContext args) throws CommandException
    {
        if (args.hasAny(DsArguments.cuboidZ2.toPlain()))
            spLoc.sendNonLocalizedMessage(src, "test.1 - {0} {1} {2} {3} {4} {5}",
                    args.getOne(DsArguments.cuboidX1.toPlain()).get(),
                    args.getOne(DsArguments.cuboidY1.toPlain()).get(),
                    args.getOne(DsArguments.cuboidZ1.toPlain()).get(),
                    args.getOne(DsArguments.cuboidX2.toPlain()).get(),
                    args.getOne(DsArguments.cuboidY2.toPlain()).get(),
                    args.getOne(DsArguments.cuboidZ2.toPlain()).get());
        else if (args.hasAny(DsArguments.cuboidW.toPlain()))
            spLoc.sendNonLocalizedMessage(src, "test.2 - {0} {1} {2}", args.getOne(DsArguments.cuboidW.toPlain()).get(), args.getOne(DsArguments.cuboidH.toPlain()).get(), args.getOne(DsArguments.cuboidD.toPlain()).get());
        else spLoc.sendNonLocalizedMessage(src, "TEST0");

        return CommandResult.success();
    }

    public CommandResult overloadtest2(CommandSource src, CommandContext args) throws CommandException
    {
        spLoc.sendMessage(src, "test.2");
        return CommandResult.success();
    }

    @Listener
    public void onGamePostInitialization(GamePostInitializationEvent event)
    {
        weAvailable = pluginManager.isLoaded("worldedit");
    }
    
    private static Text t(String key, Object... args)
    {
        //return TranslationHelper.getTranslatedText(key, args);
        return SpLocalization.getTextWithoutFormat(new Locale("en_US"), key);
    }

    @Listener
    @Exclude(CollideBlockEvent.class)
    public void onUseItemStack(Event event)
    {
        if (event instanceof CollideBlockEvent) return;
        Player src = event.getCause().first(Player.class).orElse(null);
        if (src!=null)
        {
            //spLoc.sendMessage(src, "test.1");
            logger.info(event.toString());
        }
    }

    @Listener
    public void onGameInitialization(GameInitializationEvent event)
    {
        spLoc = new SpLocalization(logger);

        CommandSpec myCommandSpec = CommandSpec.builder()
                .description(t("command.helloworld.desc"))
                .extendedDescription(t("command.helloworld.desc"))
                .permission("myplugin.command.helloworld")
                .executor(this::executehw)
                .build();

        CommandSpec myCommandSpec2 = CommandSpec.builder()
                .description(t("command.test2.desc"))
                .permission("myplugin.command.test2")
                .executor(this::executetest2)
                .build();


        CommandSpec cmdSpecAdd = CommandSpec.builder()
                .description(t("dynamicsymmetry.command.add.desc"))
                .arguments(GenericArguments.string(DsArguments.cuboidName),
                           GenericArguments.string(DsArguments.symmetryMode),
                           GenericArguments.optional(GenericArguments.firstParsing(GenericArguments.seq(GenericArguments.integer(DsArguments.cuboidX1),
                                                                                                        GenericArguments.integer(DsArguments.cuboidY1),
                                                                                                        GenericArguments.integer(DsArguments.cuboidZ1),
                                                                                                        GenericArguments.integer(DsArguments.cuboidX2),
                                                                                                        GenericArguments.integer(DsArguments.cuboidY2),
                                                                                                        GenericArguments.integer(DsArguments.cuboidZ2)),
                                                                                   GenericArguments.seq(GenericArguments.integer(DsArguments.cuboidW),
                                                                                                        GenericArguments.integer(DsArguments.cuboidH),
                                                                                                        GenericArguments.integer(DsArguments.cuboidD)))))
                .permission("dynamicsymmetry.command.add")
                .executor(this::overloadtest1)
                .build();

        CommandSpec cmdSpecRemove = CommandSpec.builder()
                .description(t("dynamicsymmetry.command.remove.desc"))
                .arguments(GenericArguments.string(DsArguments.cuboidName))
                .permission("dynamicsymmetry.command.remove")
                .executor(this::executehw)
                .build();

        CommandSpec cmdSpecChangeCoords = CommandSpec.builder()
                .description(t("dynamicsymmetry.command.change.coords.desc"))
                .arguments(GenericArguments.string(DsArguments.cuboidName),
                           GenericArguments.optional(GenericArguments.firstParsing(GenericArguments.seq(GenericArguments.integer(DsArguments.cuboidX1),
                                                                                                        GenericArguments.integer(DsArguments.cuboidY1),
                                                                                                        GenericArguments.integer(DsArguments.cuboidZ1),
                                                                                                        GenericArguments.integer(DsArguments.cuboidX2),
                                                                                                        GenericArguments.integer(DsArguments.cuboidY2),
                                                                                                        GenericArguments.integer(DsArguments.cuboidZ2)),
                                                                                   GenericArguments.seq(GenericArguments.integer(DsArguments.cuboidW),
                                                                                                        GenericArguments.integer(DsArguments.cuboidH),
                                                                                                        GenericArguments.integer(DsArguments.cuboidD)))))
                .permission("dynamicsymmetry.command.change.coords")
                .executor(this::executehw)
                .build();

        CommandSpec cmdSpecChangeMode = CommandSpec.builder()
                .description(t("dynamicsymmetry.command.change.mode.desc"))
                .arguments(GenericArguments.string(DsArguments.cuboidName),
                           GenericArguments.string(DsArguments.symmetryMode))
                .permission("dynamicsymmetry.command.change.mode")
                .executor(this::executehw)
                .build();

        CommandSpec cmdSpecSelect = CommandSpec.builder()
                .description(t("dynamicsymmetry.command.select.desc"))
                .arguments(GenericArguments.string(DsArguments.cuboidName))
                .permission("dynamicsymmetry.command.select")
                .executor(this::executehw)
                .build();

        CommandSpec cmdSpecVersion = CommandSpec.builder()
                .description(t("dynamicsymmetry.command.version.desc"))
                .permission("dynamicsymmetry.command.version")
                .executor(this::executehw)
                .build();

        CommandSpec cmdSpecAdminRemove = CommandSpec.builder()
                .description(t("dynamicsymmetry.command.admin.remove.desc"))
                .arguments(GenericArguments.onlyOne(GenericArguments.player(DsArguments.player)),
                           GenericArguments.string(DsArguments.cuboidName))
                .permission("dynamicsymmetry.command.admin.remove")
                .executor(this::executehw)
                .build();

        CommandSpec cmdSpecAdminPurge = CommandSpec.builder()
                .description(t("dynamicsymmetry.command.admin.purge.desc"))
                .arguments(GenericArguments.optional(GenericArguments.onlyOne(GenericArguments.player(DsArguments.player))))
                .permission("dynamicsymmetry.command.admin.purge")
                .executor(this::executehw)
                .build();

        CommandSpec cmdSpecChange = CommandSpec.builder()
                .permission("dynamicsymmetry.command.change")
                .child(cmdSpecChangeCoords, "coords", "coordinates", "c")
                .child(cmdSpecChangeMode, "mode", "m")
                .build();

        CommandSpec cmdSpecAdmin = CommandSpec.builder()
                .permission("dynamicsymmetry.command.admin")
                .child(cmdSpecAdminPurge, "purge")
                .child(cmdSpecAdminRemove, "remove", "r")
                .build();

        CommandSpec cmdSpecRoot = CommandSpec.builder()
                .permission("dynamicsymmetry.command")
                .child(cmdSpecAdmin, "admin")
                .child(cmdSpecChange, "change", "ch")
                .child(cmdSpecAdd, "add", "a") //DONE: РАСКОММЕНТИРОВАТЬ!!!
                .child(cmdSpecRemove, "remove", "r")
                .child(cmdSpecSelect, "select", "s")
                .child(cmdSpecVersion, "version", "v")
                .build();

        game.getCommandManager().register(this, cmdSpecRoot, "mycmd", "dynamicsymmetry", "dynsym");
        //game.getCommandManager().register(this, cmdSpecAdd, "testadd");

        /*CommandSpec cmdSpecAdminConfirm = CommandSpec.builder()
                .description(t("dynamicsymmetry.command.admin.confirm.desc"))
                .permission("dynamicsymmetry.command.admin.confirm")
                .executor(this::executehw)
                .build();*/


        /*CommandSpec overloadCmdSpec1 = CommandSpec.builder()
                .description(t("test.1"))
                .permission("myplugin.command.overloadtest1")
                .executor(this::overloadtest1)
                .build();

        CommandSpec overloadCmdSpec2 = CommandSpec.builder()
                .description(t("test.2"))
                .permission("myplugin.command.overloadtest2")
                .arguments(GenericArguments.repeated(GenericArguments.integer(Text.of("val")), 3))
                .executor(this::overloadtest2)
                .build();*/

        game.getCommandManager().register(this, myCommandSpec, "helloworld", "hello", "test");
        game.getCommandManager().register(this, myCommandSpec2, "test2");


    }

    private static class DsArguments
    {
        public static Text cuboidX1 = Text.of("x1");
        public static Text cuboidY1 = Text.of("y1");
        public static Text cuboidZ1 = Text.of("z1");
        public static Text cuboidX2 = Text.of("x2");
        public static Text cuboidY2 = Text.of("y2");
        public static Text cuboidZ2 = Text.of("z2");
        public static Text cuboidName = t("dynamicsymmetry.commandargs.regionname");
        public static Text symmetryMode = t("dynamicsymmetry.commandargs.mode");
        public static Text player = t("dynamicsymmetry.commandargs.playername");
        public static Text cuboidW = t("dynamicsymmetry.commandargs.w");
        public static Text cuboidH = t("dynamicsymmetry.commandargs.h");
        public static Text cuboidD = t("dynamicsymmetry.commandargs.d");
    }
}
