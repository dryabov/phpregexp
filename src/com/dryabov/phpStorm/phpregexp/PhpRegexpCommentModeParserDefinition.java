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

public class PhpRegexpCommentModeParserDefinition extends RegExpParserDefinition {
    private static final IFileElementType PHP_REGEXPCOMMENTMODE_FILE =
            new IFileElementType("PHP_REGEXPCOMMENTMODE_FILE", PhpRegexpCommentModeLanguage.INSTANCE);

    private static final EnumSet<RegExpCapability> CAPABILITIES = EnumSet.of(
            RegExpCapability.DANGLING_METACHARACTERS,
            RegExpCapability.OCTAL_NO_LEADING_ZERO,
            RegExpCapability.ALLOW_HEX_DIGIT_CLASS,
            RegExpCapability.COMMENT_MODE
    );

    @NotNull
    public Lexer createLexer(Project project) {
        return new RegExpLexer(CAPABILITIES);
    }

    @Override
    public PsiParser createParser(Project project) {
        return new RegExpParser(CAPABILITIES);
    }

    @Override
    public IFileElementType getFileNodeType() {
        return PHP_REGEXPCOMMENTMODE_FILE;
    }

    @Override
    public PsiFile createFile(FileViewProvider viewProvider) {
        return new RegExpFile(viewProvider, PhpRegexpCommentModeLanguage.INSTANCE);
    }
}