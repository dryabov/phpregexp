package com.dryabov.phpstorm.phpregexp.settings;

import com.intellij.openapi.components.*;
import org.jetbrains.annotations.NotNull;

@State(name = "PhpRegexpProjectSettings", storages = @Storage("$PROJECT_CONFIG_DIR$/phpregexp.xml"))
public class PhpRegexpProjectSettingsComponent
        implements PersistentStateComponent<PhpRegexpProjectSettings>, ProjectComponent {

    @NotNull
    private PhpRegexpProjectSettings settings = new PhpRegexpProjectSettings();

    @NotNull
    @Override
    public PhpRegexpProjectSettings getState() {
        return settings;
    }

    @Override
    public void loadState(@NotNull PhpRegexpProjectSettings state) {
        settings = state;
    }

    @Override
    public void projectOpened() {
    }

    @Override
    public void projectClosed() {
    }

    @NotNull
    @Override
    public String getComponentName() {
        return "PhpRegexpProject";
    }

    @Override
    public void initComponent() {
    }

    @Override
    public void disposeComponent() {
    }
}
