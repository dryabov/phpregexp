package com.dryabov.phpStorm.phpregexp.settings;

import com.intellij.openapi.Disposable;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class PhpRegexpProjectSettingsPane implements Disposable {
    private JPanel settingsPane;
    private JCheckBox parseAllStrings;

    PhpRegexpProjectSettingsPane() {
    }

    public void dispose() {
    }

    void setData(@NotNull final PhpRegexpProjectSettings settings) {
        if (parseAllStrings != null) {
            parseAllStrings.setSelected(settings.isParseAllStrings());
        }
    }

    void storeSettings(@NotNull PhpRegexpProjectSettings settings) {
        settings.setParseAllStrings(parseAllStrings.isSelected());
    }

    boolean isModified(@NotNull final PhpRegexpProjectSettings settings) {
        return settings.isParseAllStrings() != parseAllStrings.isSelected();
    }

    @NotNull
    JPanel getPanel() {
        return settingsPane;
    }
}
