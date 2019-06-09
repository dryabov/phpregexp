package com.dryabov.phpstorm.phpregexp;

import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IFileElementType;
import org.intellij.lang.regexp.*;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

public class PhpRegexpParserDefinition extends RegExpParserDefinition {
    @NotNull
    private static final IFileElementType PHP_REGEXP_FILE = new IFileElementType("PHP_REGEXPXT_FILE", PhpRegexpLanguage.INSTANCE);

    @NotNull
    public static final EnumSet<RegExpCapability> CAPABILITIES = EnumSet.of(
            RegExpCapability.DANGLING_METACHARACTERS,
            RegExpCapability.ALLOW_HORIZONTAL_WHITESPACE_CLASS,
            RegExpCapability.UNICODE_CATEGORY_SHORTHAND,
            RegExpCapability.POSIX_BRACKET_EXPRESSIONS,
            RegExpCapability.EXTENDED_UNICODE_CHARACTER,
            RegExpCapability.ONE_HEX_CHAR_ESCAPE
    );

    static {
        try {
            CAPABILITIES.add(RegExpCapability.valueOf("PCRE_BACK_REFERENCES"));
        } catch (IllegalArgumentException x) {
            // do nothing
        }
    }

    @Override
    @NotNull
    public Lexer createLexer(@NotNull final Project project) {
        return new RegExpLexer(CAPABILITIES);
    }

    @NotNull
    @Override
    public PsiParser createParser(@NotNull final Project project) {
        return new RegExpParser(CAPABILITIES);
    }

    @NotNull
    @Override
    public IFileElementType getFileNodeType() {
        return PHP_REGEXP_FILE;
    }

    @NotNull
    @Override
    public PsiFile createFile(@NotNull final FileViewProvider viewProvider) {
        return new RegExpFile(viewProvider, PhpRegexpLanguage.INSTANCE);
    }
}
