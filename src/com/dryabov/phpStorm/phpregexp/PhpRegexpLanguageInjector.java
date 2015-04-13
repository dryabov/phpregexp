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
    private static final List<String> pregMethodsStringArg = Arrays.asList(
            "\\preg_filter", // (string|array,...)
            "\\preg_grep", // (string,...)
            "\\preg_match", // (string,...)
            "\\preg_match_all", // (string,...)
            "\\preg_replace", // (string|array,...)
            "\\preg_replace_callback", // (string|array,...)
            "\\preg_split" // (string,...)
    );
    private static final List<String> pregMethodsArrayArg = Arrays.asList(
            "\\preg_filter", "\\preg_replace", "\\preg_replace_callback"
    );

    @NotNull
    @Override
    public List<? extends Class<? extends PsiElement>> elementsToInjectIn() {
        return Collections.singletonList(StringLiteralExpressionImpl.class);
    }

    @Override
    public void getLanguagesToInject(@NotNull final MultiHostRegistrar registrar, @NotNull final PsiElement element) {
        if (!(element instanceof StringLiteralExpression) || !((StringLiteralExpression) element).isValidHost()) {
            return;
        }

        final String regex = ((StringLiteralExpression) element).getContents();
        final int length = regex.length();
        if (length <= 2) {
            return;
        }

        PsiElement parent = element.getParent();
        boolean isRegexp = false;

        if (parent instanceof ParameterList) {
            parent = parent.getParent();
            if (parent instanceof FunctionReference) {
                String FQN = ((FunctionReference) parent).getFQN();
                if (pregMethodsStringArg.contains(FQN)) {
                    isRegexp = true;
                }
            }
        } else if (parent instanceof PhpPsiElement) { // array value element
            parent = parent.getParent();
            if (parent instanceof ArrayCreationExpression) {
                parent = parent.getParent();
                if (parent instanceof ParameterList) {
                    parent = parent.getParent();
                    if (parent instanceof FunctionReference) {
                        String FQN = ((FunctionReference) parent).getFQN();
                        if (pregMethodsArrayArg.contains(FQN)) {
                            isRegexp = true;
                        }
                    }
                }
            }
        }

        if (!isRegexp) {
            final char c = regex.charAt(0);
            // popular regexp delimiters
            if ("!#%&/=@_`|~".indexOf(c) >= 0) {
                isRegexp = regex.matches(c + "(?:[^\\\\" + c + "]+|\\\\.)+" + c + "[imsuxADSUX]*");
            }
        }

        if (!isRegexp) {
            return;
        }

        int pos = 0;
        // skip leading whitespaces
        while (pos < length && isspace(regex.charAt(pos))) {
            pos++;
        }

        // get delimiters
        final char start_delimiter = regex.charAt(pos++);
        if (isalnum(start_delimiter) || start_delimiter == '\\') {
            return;
        }

        final int start_pos = pos;

        char end_delimiter;
        switch (start_delimiter) {
            case '(':
                end_delimiter = ')';
                break;
            case '[':
                end_delimiter = ']';
                break;
            case '{':
                end_delimiter = '}';
                break;
            case '<':
                end_delimiter = '>';
                break;
            default:
                end_delimiter = start_delimiter;
        }

        // get pattern
        int brackets = 1;
        while (pos < length) {
            final char c = regex.charAt(pos);
            if (c == '\\') {
                pos++;
            } else if (c == end_delimiter && --brackets <= 0) {
                break;
            } else if (c == start_delimiter) {
                brackets++;
            }
            pos++;
        }

        if (pos >= length) {
            return;
        }

        final int end_pos = pos;
        final boolean commentMode = regex.indexOf('x', end_pos + 1) >= 0;
        final int offset = ((StringLiteralExpressionImpl) element).getValueRange().getStartOffset();

        registrar
                .startInjecting(commentMode ? PhpRegexpCommentModeLanguage.INSTANCE : PhpRegexpLanguage.INSTANCE)
                .addPlace(null, null, (PsiLanguageInjectionHost) element,
                        TextRange.create(offset + start_pos, offset + end_pos))
                .doneInjecting();
    }

    private static boolean isspace(final char c) {
        return c == ' ' || c == '\t' || c == '\n' || c == '\13' || c == '\f' || c == '\r';
    }

    private static boolean isalnum(final char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9');
    }
}
