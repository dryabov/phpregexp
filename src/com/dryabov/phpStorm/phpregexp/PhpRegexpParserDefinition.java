package com.dryabov.phpStorm.phpregexp;

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
    private static final IFileElementType PHP_REGEXP_FILE = new IFileElementType("PHP_REGEXP_FILE", PhpRegexpLanguage.INSTANCE);

    private static final EnumSet<RegExpCapability> CAPABILITIES = EnumSet.of(
            RegExpCapability.DANGLING_METACHARACTERS,
            RegExpCapability.OCTAL_NO_LEADING_ZERO,
            RegExpCapability.ALLOW_HEX_DIGIT_CLASS
//            @todo add for PhpStorm 9
//            RegExpCapability.ALLOW_HORIZONTAL_WHITESPACE_CLASS,
//            RegExpCapability.UNICODE_CATEGORY_SHORTHAND,
//            RegExpCapability.POSIX_BRACKET_EXPRESSIONS,
    );

    @Override
    @NotNull
    public Lexer createLexer(final Project project) {
        return new RegExpLexer(CAPABILITIES);
    }

    @Override
    public PsiParser createParser(final Project project) {
        return new RegExpParser(CAPABILITIES);
    }

    @Override
    public IFileElementType getFileNodeType() {
        return PHP_REGEXP_FILE;
    }

    @Override
    public PsiFile createFile(final FileViewProvider viewProvider) {
        return new RegExpFile(viewProvider, PhpRegexpLanguage.INSTANCE);
    }
}
