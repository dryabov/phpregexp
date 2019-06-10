package com.dryabov.phpStorm.phpregexp;

import org.intellij.lang.regexp.DefaultRegExpPropertiesProvider;
import org.intellij.lang.regexp.RegExpLanguageHost;
import org.intellij.lang.regexp.psi.RegExpChar;
import org.intellij.lang.regexp.psi.RegExpGroup;
import org.intellij.lang.regexp.psi.RegExpNamedGroupRef;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PhpRegexpHost implements RegExpLanguageHost {

    @NotNull
    private final DefaultRegExpPropertiesProvider m_propertiesProvider;

    public PhpRegexpHost() {
        m_propertiesProvider = DefaultRegExpPropertiesProvider.getInstance();
    }

    @Override
    public boolean characterNeedsEscaping(char c) {
        return false;
    }

    @Override
    public boolean supportsPerl5EmbeddedComments() {
        // Embedded comments (?#...)
        return true;
    }

    @Override
    public boolean supportsPossessiveQuantifiers() {
        return true;
    }

    @Override
    public boolean supportsPythonConditionalRefs() {
        return false;
    }

    @Override
    public boolean supportsNamedGroupSyntax(@NotNull RegExpGroup group) {
        return true;
    }

    @Override
    public boolean supportsNamedGroupRefSyntax(@NotNull RegExpNamedGroupRef regExpNamedGroupRef) {
        return true;
    }

    @Override
    public boolean supportsExtendedHexCharacter(@NotNull RegExpChar regExpChar) {
        return true;
    }

    @Override
    public boolean isValidCategory(@NotNull String category) {
        return m_propertiesProvider.isValidCategory(category);
    }

    @NotNull
    @Override
    public String[][] getAllKnownProperties() {
        return m_propertiesProvider.getAllKnownProperties();
    }

    @Nullable
    @Override
    public String getPropertyDescription(@Nullable final String name) {
        return m_propertiesProvider.getPropertyDescription(name);
    }

    @NotNull
    @Override
    public String[][] getKnownCharacterClasses() {
        return m_propertiesProvider.getKnownCharacterClasses();
    }
}