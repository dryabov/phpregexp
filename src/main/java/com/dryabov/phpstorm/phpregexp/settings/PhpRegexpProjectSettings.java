package com.dryabov.phpStorm.phpregexp.settings;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public class PhpRegexpProjectSettings implements Serializable {
    private boolean parseAllStrings = false;

    public boolean isParseAllStrings() {
        return parseAllStrings;
    }

    void setParseAllStrings(final boolean parseAllStrings) {
        this.parseAllStrings = parseAllStrings;
    }

    @NotNull
    public static PhpRegexpProjectSettings storedSettings(@NotNull final Project project) {
        return project.getComponent(PhpRegexpProjectSettingsComponent.class).getState();
    }
}
