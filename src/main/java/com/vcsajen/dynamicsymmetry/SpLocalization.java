package com.vcsajen.dynamicsymmetry;

import com.ibm.icu.util.ULocale;
import com.ibm.icu.util.UResourceBundle;
import org.slf4j.Logger;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import com.ibm.icu.text.MessageFormat;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextFormat;
import org.spongepowered.api.text.translation.Translatable;
//import org.spongepowered.api.text.TextRepresentable;

import java.text.AttributedCharacterIterator;
import java.text.CharacterIterator;
import java.util.*;

/**
 * Created by VcSaJen on 07.02.2016 22:40.
 * Localisation class for Sponge integration
 */
public class SpLocalization {
//    private static SpLocalization ourInstance = new SpLocalization();
//
//    public static SpLocalization getInstance() {
//        return ourInstance;
//    }
    Logger logger;
    Map<Class<?>, TextFormat> typeFormats;

    public String getNonLocalizedStringWithFormat(Locale locale, String msgFormat, Object... params)
    {
        MessageFormat msgFmt = new MessageFormat(msgFormat, locale);
        return msgFmt.format(params);
    }

    public Text getNonLocalizedPlainTextWithFormat(Locale locale, String msgFormat, Object... params)
    {
        MessageFormat msgFmt = new MessageFormat(msgFormat, locale);

        //DONE: http://icu-project.org/apiref/icu4j/com/ibm/icu/text/MessageFormat.html#formatToCharacterIterator%28java.lang.Object%29

        return Text.of(getNonLocalizedStringWithFormat(locale, msgFormat, params));
    }

    public Text getTextWithFormat(Locale locale, String localizationKey, Object... params)
    {
        return Text.of(getStringWithFormat(locale, localizationKey, params));
    }

    public String getStringWithFormat(Locale locale, String localizationKey, Object... params)
    {
        UResourceBundle b1 = UResourceBundle.getBundleInstance("com/vcsajen/dynamicsymmetry/lang", locale);
        return getNonLocalizedStringWithFormat(locale, b1.getString(localizationKey), params);
    }

    public void sendRawMessage(CommandSource src, String msg)
    {
        src.sendMessage(Text.of(msg));
    }

    public void sendNonLocalizedMessage(CommandSource src, String msgFormat, Object... params)
    {
        src.sendMessage(getNonLocalizedTextWithFormat(src.getLocale(), msgFormat, params));
    }

    public void sendMessage(CommandSource src, String localizationKey, Object... params)
    {
        src.sendMessage(getTextWithFormat(src.getLocale(), localizationKey, params));
        //Locale locale = src.getLocale();
    }

    public TextFormat getFormatForType(Object obj)
    {
        if (obj instanceof Player) return typeFormats.get(Player.class);
        if (obj instanceof ItemStack) return typeFormats.get(ItemStack.class);

        if (typeFormats.containsKey(obj.getClass()))
            return typeFormats.get(obj.getClass());
        else return typeFormats.get(Object.class);
    }

    private String stripNonprintableCharacters(String s) // Prevent Minecraft from displaying non-displaying characters
    {
        return s.replaceAll("[^\\P{Z} ]|[^\\P{Cf} ]", "");
    }

    private Object preprocessObject(Object obj)
    {
        if (obj instanceof Player) return ((Player) obj).getName();
        //TODO: Доделать для Cuboid и Vector
        return obj;
    }

    private Text preprocessText(Object obj, TextFormat textFormat, String s)
    {
        if (obj instanceof ItemStack) return Text.builder((ItemStack)obj).format(textFormat).onHover(TextActions.showItem((ItemStack)obj)).build();

        if (obj instanceof Translatable) Text.of(textFormat, obj);

        return Text.of(textFormat, s);
    }

    public Text getNonLocalizedTextWithFormat(Locale locale, String msgFormat, Object... args)
    {
        List<TextFormat> textFormats = new ArrayList<>(args.length);
        Object[] params = new Object[args.length];
        int i = 0;
        for (Object param: args) {
            textFormats.add(getFormatForType(param));
            params[i] = preprocessObject(param);
            i++;
        }

        MessageFormat msgFmt = new MessageFormat(msgFormat, locale);

        //Object[] params = {42000000, 24.1001, "TEST"};
        AttributedCharacterIterator charIterator = msgFmt.formatToCharacterIterator(params);

        StringBuilder buf = new StringBuilder("");
        for(char c = charIterator.first(); c != CharacterIterator.DONE; c = charIterator.next()) {
            buf.append(c);
        }
        logger.debug("Plain: "+buf.toString());

        Set<AttributedCharacterIterator.Attribute> attributes = charIterator.getAllAttributeKeys();
        Text.Builder tb = Text.builder();
        for (AttributedCharacterIterator.Attribute attr: attributes) {
            if (!attr.equals(MessageFormat.Field.ARGUMENT)) continue;
            Object f;
            //StringBuilder sb = new StringBuilder(buf);

            Deque<ParamPos> deque = new ArrayDeque<>();
            char c = charIterator.first();
            while (c!=AttributedCharacterIterator.DONE)
            {
                int start = charIterator.getRunStart(attr);
                c = charIterator.setIndex(start);
                f = charIterator.getAttribute(attr);
                int end = charIterator.getRunLimit(attr);
                c = charIterator.setIndex(end);
                c = charIterator.next();
                if (f!=null)
                  deque.add(new ParamPos((Integer)f,start,end));
            }
            int lastInd = 0;
            while (deque.peek()!=null)
            {
                ParamPos p = deque.poll();
                //sb.insert(p.end, "]");
                //sb.insert(p.start, "{"+p.paramInd+"}[");
                if (p.start>0 && lastInd<p.start)
                    tb.append(Text.of(stripNonprintableCharacters(buf.substring(lastInd, p.start))));
                //if (params[p.paramInd] instanceof Text) ;
                String str = stripNonprintableCharacters(buf.substring(p.start, p.end));
                tb.append(preprocessText(params[p.paramInd], textFormats.get(p.paramInd), str));

                lastInd = p.end;
            }
            if (lastInd<buf.length()-1)
                tb.append(Text.of(stripNonprintableCharacters(buf.substring(lastInd, buf.length()))));

            //logger.debug("Attr: {"+f+"}");
            //logger.debug(sb.toString());
        }

        return tb.build();
    }

    public SpLocalization(Logger logger) {
        this.logger = logger;
        ULocale.setDefault(new ULocale("en_US"));

        typeFormats = new HashMap<>();
        typeFormats.put(Integer.class, Text.of(TextColors.AQUA).getFormat());
        typeFormats.put(Float.class, Text.of(TextColors.AQUA).getFormat());
        typeFormats.put(Double.class, Text.of(TextColors.AQUA).getFormat());
        typeFormats.put(String.class, Text.of(TextColors.DARK_PURPLE).getFormat());
        typeFormats.put(Player.class, Text.of(TextColors.YELLOW).getFormat());
        typeFormats.put(CommandSource.class, Text.of(TextColors.YELLOW).getFormat());
        typeFormats.put(ItemStack.class, Text.of(TextColors.LIGHT_PURPLE).getFormat());
        typeFormats.put(Object.class, Text.of(TextColors.GRAY).getFormat());

    }

    private class ParamPos
    {
        public ParamPos(int paramInd, int start, int end) {
            this.paramInd = paramInd;
            this.start = start;
            this.end = end;
        }

        public int paramInd;
        public int start;
        public int end;
    }
}

























