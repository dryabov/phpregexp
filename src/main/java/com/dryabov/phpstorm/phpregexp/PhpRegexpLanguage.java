package com.dryabov.phpstorm.phpregexp;

import com.intellij.lang.Language;
import org.intellij.lang.regexp.RegExpLanguage;
import org.jetbrains.annotations.NotNull;

final class PhpRegexpLanguage extends Language {
    @NotNull
    public static final PhpRegexpLanguage INSTANCE = new PhpRegexpLanguage();

    private PhpRegexpLanguage() {
        super(RegExpLanguage.INSTANCE, "PhpRegExpXT");
    }
}
