package com.dryabov.phpstorm.phpregexp;

import com.intellij.lang.Language;
import org.intellij.lang.regexp.RegExpLanguage;
import org.jetbrains.annotations.NotNull;

final class PhpRegexpCommentModeLanguage extends Language {
    @NotNull
    public static final PhpRegexpCommentModeLanguage INSTANCE = new PhpRegexpCommentModeLanguage();

    private PhpRegexpCommentModeLanguage() {
        super(RegExpLanguage.INSTANCE, "PhpRegExpXTCommentMode");
    }
}
