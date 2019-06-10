package com.dryabov.phpStorm.phpregexp.settings;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class PhpRegexpProjectSettingsConfigurable implements ProjectComponent, Configurable {
    @Nullable
    private PhpRegexpProjectSettingsPane settingsPanel;

    @NotNull
    private final Project project;

    private PhpRegexpProjectSettingsConfigurable(@NotNull Project project) {
        this.project = project;
    }

    @NotNull
    public String getComponentName() {
        return "com.dryabov.phpStorm.phpregexp.ProjectSettingsConfigurable";
    }

    public void initComponent() {
    }

    public void disposeComponent() {
        if (settingsPanel != null) {
            settingsPanel.dispose();
            settingsPanel = null;
        }
    }

    @NotNull
    @Nls
    public String getDisplayName() {
        return "PhpRegexp";
    }

    public String getHelpTopic() {
        return null;
    }

    public JComponent createComponent() {
        if (settingsPanel == null) {
            settingsPanel = new PhpRegexpProjectSettingsPane();
        }
        return settingsPanel.getPanel();
    }

    public boolean isModified() {
        if (settingsPanel == null) {
            return false;
        }
        return settingsPanel.isModified(PhpRegexpProjectSettings.storedSettings(project));
    }

    public void apply() {
        if (settingsPanel != null) {
            settingsPanel.storeSettings(PhpRegexpProjectSettings.storedSettings(project));
        }
    }

    public void reset() {
        if (settingsPanel != null) {
            settingsPanel.setData(PhpRegexpProjectSettings.storedSettings(project));
        }
    }

    public void disposeUIResources() {
    }

    public void projectOpened() {
    }

    public void projectClosed() {
    }
}
