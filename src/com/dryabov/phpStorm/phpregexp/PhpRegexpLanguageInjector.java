package com.dryabov.phpStorm.phpregexp;

import com.intellij.lang.injection.MultiHostInjector;
import com.intellij.lang.injection.MultiHostRegistrar;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.jetbrains.php.lang.psi.elements.*;
import com.jetbrains.php.lang.psi.elements.impl.StringLiteralExpressionImpl;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PhpRegexpLanguageInjector implements MultiHostInjector {
    private static final List<String> PREG_METHODS_STRING_ARG = Arrays.asList(
            "\\preg_filter", // (string|array,...)
            "\\preg_grep", // (string,...)
            "\\preg_match", // (string,...)
            "\\preg_match_all", // (string,...)
            "\\preg_replace", // (string|array,...)
            "\\preg_replace_callback", // (string|array,...)
            "\\preg_split" // (string,...)
    );
    private static final List<String> PREG_METHODS_ARRAY_ARG = Arrays.asList(
            "\\preg_filter", "\\preg_replace", "\\preg_replace_callback"
    );

    @NotNull
    @Override
    public List<? extends Class<? extends PsiElement>> elementsToInjectIn() {
        return Collections.singletonList(StringLiteralExpressionImpl.class);
    }

    @Override
    public void getLanguagesToInject(@NotNull final MultiHostRegistrar registrar, @NotNull final PsiElement element) {
        if (!(element instanceof StringLiteralExpression) || !((PsiLanguageInjectionHost) element).isValidHost()) {
            return;
        }

        final String regex = ((StringLiteralExpression) element).getContents();
        final int length = regex.length();
        if (length <= 2) {
            return;
        }

        PsiElement parent = element.getParent();
        boolean skipElement = true;

        if (parent instanceof ParameterList &&
                ((PhpPsiElement) element).getPrevPsiSibling() == null) {
            parent = parent.getParent();
            if (parent instanceof FunctionReference) {
                final String fqn = ((FunctionReference) parent).getFQN();
                if (PREG_METHODS_STRING_ARG.contains(fqn)) {
                    skipElement = false;
                }
            }
        } else if (parent instanceof PhpPsiElement) { // array value element
            parent = parent.getParent();
            if (parent instanceof ArrayCreationExpression &&
                    ((PhpPsiElement) parent).getPrevPsiSibling() == null) {
                parent = parent.getParent();
                if (parent instanceof ParameterList) {
                    parent = parent.getParent();
                    if (parent instanceof FunctionReference) {
                        final String fqn = ((FunctionReference) parent).getFQN();
                        if (PREG_METHODS_ARRAY_ARG.contains(fqn)) {
                            skipElement = false;
                        }
                    }
                }
            }
        }

        if (skipElement) {
            final char c = regex.charAt(0);
            // popular regexp delimiters
            if ("!#%&/=@_`|~".indexOf(c) >= 0) {
                skipElement = !regex.matches(c + "(?>[^\\\\" + c + "]+|\\\\.)+" + c + "[imsuxADSUX]*");
            }
        }

        if (skipElement) {
            return;
        }

        int pos = 0;
        // skip leading whitespaces
        while (pos < length && isspace(regex.charAt(pos))) {
            pos++;
        }

        // get delimiters
        final char startDelimiter = regex.charAt(pos++);
        if (isalnum(startDelimiter) || startDelimiter == '\\') {
            return;
        }

        final int startPos = pos;

        final char endDelimiter;
        switch (startDelimiter) {
            case '(':
                endDelimiter = ')';
                break;
            case '[':
                endDelimiter = ']';
                break;
            case '{':
                endDelimiter = '}';
                break;
            case '<':
                endDelimiter = '>';
                break;
            default:
                endDelimiter = startDelimiter;
        }

        // get pattern
        int brackets = 1;
        while (pos < length) {
            final char c = regex.charAt(pos);
            if (c == '\\') {
                pos++;
            } else if (c == endDelimiter && --brackets <= 0) {
                break;
            } else if (c == startDelimiter) {
                brackets++;
            }
            pos++;
        }

        if (pos >= length) {
            return;
        }

        final int endPos = pos;
        final boolean commentMode = regex.indexOf('x', endPos + 1) >= 0;
        final int offset = ((StringLiteralExpression) element).getValueRange().getStartOffset();

        registrar
                .startInjecting(commentMode ? PhpRegexpCommentModeLanguage.INSTANCE : PhpRegexpLanguage.INSTANCE)
                .addPlace(null, null, (PsiLanguageInjectionHost) element,
                        TextRange.create(offset + startPos, offset + endPos))
                .doneInjecting();
    }

    private static boolean isspace(final char c) {
        return c == ' ' || c == '\t' || c == '\n' || c == '\13' || c == '\f' || c == '\r';
    }

    private static boolean isalnum(final char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9');
    }
}
