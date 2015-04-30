package com.dryabov.phpStorm.phpregexp.settings;

import com.intellij.openapi.Disposable;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class PhpRegexpProjectSettingsPane implements Disposable {
    private JPanel settingsPane;
    private JCheckBox parseAllStrings;

    public PhpRegexpProjectSettingsPane() {
    }

    public void dispose() {
    }

    public void setData(@NotNull final PhpRegexpProjectSettings settings) {
        parseAllStrings.setSelected(settings.isParseAllStrings());
    }

    public void storeSettings(@NotNull PhpRegexpProjectSettings settings) {
        settings.setParseAllStrings(parseAllStrings.isSelected());
    }

    public boolean isModified(@NotNull final PhpRegexpProjectSettings settings) {
        return settings.isParseAllStrings() != parseAllStrings.isSelected();
    }

    public JPanel getPanel() {
        return settingsPane;
    }
}
