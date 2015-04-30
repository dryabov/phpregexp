package com.dryabov.phpStorm.phpregexp.settings;

import com.intellij.openapi.components.*;
import org.jetbrains.annotations.NotNull;

@State(
        name = "PhpRegexpProjectSettings",
        storages = {
                @Storage(id = "default",
                        file = "$PROJECT_FILE$"),
                @Storage(id = "dir",
                        file = "$PROJECT_CONFIG_DIR$/phpregexp_project.xml",
                        scheme = StorageScheme.DIRECTORY_BASED)}
)
public class PhpRegexpProjectSettingsComponent
        implements PersistentStateComponent<PhpRegexpProjectSettings>, ProjectComponent {

    private PhpRegexpProjectSettings settings = new PhpRegexpProjectSettings();

    @Override
    public PhpRegexpProjectSettings getState() {
        return settings;
    }

    @Override
    public void loadState(PhpRegexpProjectSettings state) {
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
