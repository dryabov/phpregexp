package com.dryabov.phpStorm.phpregexp.settings;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

public class PhpRegexpProjectSettings implements Serializable {
    private boolean parseAllStrings = true;

    public boolean isParseAllStrings() {
        return parseAllStrings;
    }

    public void setParseAllStrings(final boolean parseAllStrings) {
        this.parseAllStrings = parseAllStrings;
    }

    @Nullable
    public static PhpRegexpProjectSettings storedSettings(@NotNull final Project project) {
        return project.getComponent(PhpRegexpProjectSettingsComponent.class).getState();
    }


}
