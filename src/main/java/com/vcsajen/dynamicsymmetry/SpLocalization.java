package com.vcsajen.dynamicsymmetry;

import com.ibm.icu.util.ULocale;
import com.ibm.icu.util.UResourceBundle;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import com.ibm.icu.text.MessageFormat;

import java.util.Locale;

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

    public String getNonLocalizedStringWithFormat(Locale locale, String msgFormat, Object... params)
    {
        MessageFormat msgFmt = new MessageFormat(msgFormat, locale);
        return msgFmt.format(params);
    }

    public Text getNonLocalizedTextWithFormat(Locale locale, String msgFormat, Object... params)
    {
        MessageFormat msgFmt = new MessageFormat(msgFormat, locale);

        //TODO: http://icu-project.org/apiref/icu4j/com/ibm/icu/text/MessageFormat.html#formatToCharacterIterator%28java.lang.Object%29

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

    public SpLocalization() {
        ULocale.setDefault(new ULocale("en_US"));
    }
}
