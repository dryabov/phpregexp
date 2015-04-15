package com.dryabov.phpStorm.phpregexp;

import com.intellij.lang.Language;
import org.intellij.lang.regexp.RegExpLanguage;

public class PhpRegexpCommentModeLanguage extends Language {
    public static final PhpRegexpCommentModeLanguage INSTANCE = new PhpRegexpCommentModeLanguage();

    private PhpRegexpCommentModeLanguage() {
        super(RegExpLanguage.INSTANCE, "PhpRegExpCommentMode");
    }
}
