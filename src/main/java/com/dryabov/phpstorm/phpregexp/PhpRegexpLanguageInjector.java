package com.dryabov.phpStorm.phpregexp;

import com.dryabov.phpStorm.phpregexp.settings.PhpRegexpProjectSettings;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PhpRegexpLanguageInjector implements MultiHostInjector {
    @NotNull
    private static final List<String> PREG_METHODS_STRING_ARG = Arrays.asList(
            "\\preg_filter", // (string|array,...)
            "\\preg_grep", // (string,...)
            "\\preg_match", // (string,...)
            "\\preg_match_all", // (string,...)
            "\\preg_replace", // (string|array,...)
            "\\preg_replace_callback", // (string|array,...)
            "\\preg_split" // (string,...)
    );

    @NotNull
    private static final List<String> PREG_METHODS_ARRAY_ARG = Arrays.asList(
            "\\preg_filter",
            "\\preg_replace",
            "\\preg_replace_callback"
    );

    @NotNull
    private static final String phpNamePattern = "[A-Z_a-z\u007F-\uFFFF][0-9A-Z_a-z\u007F-\uFFFF]*";

    @NotNull
    private static final Pattern phpExprPattern = Pattern.compile("^" + phpNamePattern + "(?:\\[.*?]|(?:->" + phpNamePattern + ")+)?");

    @NotNull
    private static final String SKIP = "__SKIP__";


    public PhpRegexpLanguageInjector() {
    }

    @NotNull
    @Override
    public List<? extends Class<? extends PsiElement>> elementsToInjectIn() {
        return Collections.singletonList(StringLiteralExpressionImpl.class);
    }

    private static class Sections {
        private final int maxSections = 20;
        int nSections = 0;

        @NotNull
        final int[] rangeLeft = new int[maxSections];

        @NotNull
        final String[] rangeText = new String[maxSections];

        @NotNull
        private final String regex;

        Sections(@NotNull final String regex) {
            this.regex = regex;
        }

        boolean addSectionsPair(final int pos1, final int pos2, @NotNull final String skip) {
            if (nSections >= maxSections) return false;

            rangeLeft[nSections] = pos1;
            rangeText[nSections] = (pos2 > pos1) ? regex.substring(pos1, pos2) : "";
            nSections++;

            rangeLeft[nSections] = pos2;
            rangeText[nSections] = skip;
            nSections++;

            return true;
        }
    }

    @Override
    public void getLanguagesToInject(@NotNull final MultiHostRegistrar registrar, @NotNull final PsiElement element) {
        if (!(element instanceof StringLiteralExpression) || !((PsiLanguageInjectionHost) element).isValidHost()) {
            return;
        }

        final StringLiteralExpressionImpl expr = (StringLiteralExpressionImpl) element;

        final String regex = expr.getContents();
        final int length = regex.length();
        if (length <= 2) {
            return;
        }

        boolean skipElement = true;

        PsiElement parent = expr.getParent();

        if (parent instanceof ParameterList && expr.getPrevPsiSibling() == null) {
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
            final PhpRegexpProjectSettings settings = PhpRegexpProjectSettings.storedSettings(expr.getProject());
            if (settings.isParseAllStrings()) {
                final char c = regex.charAt(0);
                // popular regexp delimiters
                // @TODO: consider to remove "%" from this list (a popular delimiter in placeholders, e.g. %USER%)
                if ("!#%&/=@_|~".indexOf(c) >= 0) {
                    skipElement = !regex.matches(c + "(?>[^\\\\" + c + "]+|\\\\.)+" + c + "[imsuxADSUX]*");
                }
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

        final boolean doubleQuotes = expr.isHeredoc() ? (expr.getText().charAt(3) != '\'') : !expr.isSingleQuote();

        final char qDelimiter = doubleQuotes ? '"' : '\'';

        // get pattern

        final Sections sections = new Sections(regex);

        int prevPos = startPos;

        int brackets = 1;
        while (pos < length) {
            final int pos0 = pos;
            char c = regex.charAt(pos);

            if (c == '\\') {

                pos++;
                if (pos >= length) return;

                c = regex.charAt(pos);
                if (c == '\\' || c == qDelimiter || c == endDelimiter || c == startDelimiter ||
                        (doubleQuotes && (c == '$' || c == '{'))
                ) {
                    if (!sections.addSectionsPair(prevPos, pos0, "")) return;
                    prevPos = pos0 + 1;
                }
                pos++;

            } else if (c == '$' && doubleQuotes && (pos + 1) < length) {

                c = regex.charAt(pos + 1);
                if (c == '{') {
                    pos += 2;
                    while (pos < length && regex.charAt(pos) != '}') {
                        pos++;
                    }
                    if (pos >= length) return;

                    // @TODO Is it possible to resolve expression instead of using SKIP???
                    if (!sections.addSectionsPair(prevPos, pos0, SKIP)) return;

                    pos++;
                    prevPos = pos;
                } else if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_') {
                    pos++;
                    final Matcher matcher = phpExprPattern.matcher(regex.substring(pos));
                    if (matcher.find() && matcher.start() == 0) {
                        pos += matcher.end();

                        // @TODO Is it possible to resolve expression instead of using SKIP???
                        if (!sections.addSectionsPair(prevPos, pos0, SKIP)) return;

                        prevPos = pos;
                    }
                } else {
                    pos++;
                }

            } else if (c == '{' && doubleQuotes && (pos + 1) < length && regex.charAt(pos + 1) == '$') {

                pos += 2;
                // skip up to "}" (with nested {})
                int level = 1;
                while (pos < length) {
                    c = regex.charAt(pos);
                    if (c == '}' && --level <= 0) {
                        break;
                    } else if (c == '{') {
                        level++;
                    } else if (c == '\'' || c == '"') {
                        while (pos < length && regex.charAt(pos) != c) {
                            pos++;
                        }
                    }
                    pos++;
                }
                if (pos >= length) return;

                // @TODO Is it possible to resolve expression instead of using SKIP???
                if (!sections.addSectionsPair(prevPos, pos0, SKIP)) return;

                pos++;
                prevPos = pos;

            } else if (c == endDelimiter && --brackets <= 0) {

                break;

            } else if (c == startDelimiter) {

                brackets++;
                pos++;

            } else {

                pos++;

            }
        }

        if (pos >= length) return;

        final int endPos = pos;

        // @TODO Is it possible to resolve expression instead of using SKIP???
        if (!sections.addSectionsPair(prevPos, endPos, "")) return;

        final boolean commentMode = regex.indexOf('x', endPos + 1) >= 0;

        final int startOffset = expr.getValueRange().getStartOffset();

        for (int i = 0; i < sections.nSections; i += 2) {
            if (sections.rangeLeft[i] == sections.rangeLeft[i + 1]) continue;

            StringBuilder prefix = new StringBuilder();
            for (int j = 0; j < i; ++j) {
                prefix.append(sections.rangeText[j]);
            }

            StringBuilder suffix = new StringBuilder();
            for (int j = i + 1; j < sections.nSections; ++j) {
                suffix.append(sections.rangeText[j]);
            }

            final TextRange range = TextRange.create(sections.rangeLeft[i], sections.rangeLeft[i + 1]).shiftRight(startOffset);

            registrar
                    .startInjecting(commentMode ? PhpRegexpCommentModeLanguage.INSTANCE : PhpRegexpLanguage.INSTANCE)
                    .addPlace(prefix.toString(), suffix.toString(), (PsiLanguageInjectionHost) element, range)
                    .doneInjecting();
        }
    }

    private static boolean isspace(final char c) {
        return c == ' ' || c == '\t' || c == '\n' || c == '\13' || c == '\f' || c == '\r';
    }

    private static boolean isalnum(final char c) {
        return c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z' || c >= '0' && c <= '9';
    }

}
