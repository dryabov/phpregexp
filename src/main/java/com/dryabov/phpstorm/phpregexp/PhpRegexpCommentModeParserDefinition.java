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

public class PhpRegexpCommentModeParserDefinition extends RegExpParserDefinition {
    @NotNull
    private static final IFileElementType PHP_REGEXPCOMMENTMODE_FILE =
            new IFileElementType("PHP_REGEXPXT_COMMENTMODE_FILE", PhpRegexpCommentModeLanguage.INSTANCE);

    @NotNull
    private static final EnumSet<RegExpCapability> CAPABILITIES;

    static {
        CAPABILITIES = EnumSet.copyOf(PhpRegexpParserDefinition.CAPABILITIES);
        CAPABILITIES.add(RegExpCapability.COMMENT_MODE);
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
        return PHP_REGEXPCOMMENTMODE_FILE;
    }

    @NotNull
    @Override
    public PsiFile createFile(@NotNull final FileViewProvider viewProvider) {
        return new RegExpFile(viewProvider, PhpRegexpCommentModeLanguage.INSTANCE);
    }
}
