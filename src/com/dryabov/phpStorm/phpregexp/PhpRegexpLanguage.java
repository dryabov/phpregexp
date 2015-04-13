package com.dryabov.phpStorm.phpregexp;

import com.intellij.lang.Language;
import org.intellij.lang.regexp.RegExpLanguage;

public class PhpRegexpLanguage extends Language {
    public static final PhpRegexpLanguage INSTANCE = new PhpRegexpLanguage();

    public PhpRegexpLanguage() {
        super(RegExpLanguage.INSTANCE, "PhpRegExp");
    }
}
