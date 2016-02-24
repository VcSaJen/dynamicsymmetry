package com.vcsajen.dynamicsymmetry;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TranslatableText;
import org.spongepowered.api.text.translation.ResourceBundleTranslation;
import org.spongepowered.api.text.translation.Translation;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Function;

/*
 * Created by VcSaJen on 21.02.2016 16:49 17:07.
 */

/**
 * Placeholder for coming <a href="https://github.com/SpongePowered/SpongeAPI/pull/1088">Localization API</a>
 */
public class TranslationHelper {
    private static final Function<Locale, ResourceBundle> LOOKUP_FUNC = new Function<Locale, ResourceBundle>() {
        @Nullable
        @Override
        public ResourceBundle apply(Locale input) {
            return ResourceBundle.getBundle("com/vcsajen/dynamicsymmetry/lang", input);
        }
    };

    private TranslationHelper() {} // Prevent instance creation

    public static Text t(String key, Object... args) {
        Translation tr = new ResourceBundleTranslation(key, LOOKUP_FUNC);
        return TranslatableText.builder(tr).build();
    }
}