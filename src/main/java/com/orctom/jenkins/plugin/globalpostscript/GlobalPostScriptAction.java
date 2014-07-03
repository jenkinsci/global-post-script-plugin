package com.orctom.jenkins.plugin.globalpostscript;

import hudson.PluginWrapper;
import hudson.model.BuildBadgeAction;
import hudson.model.Hudson;
import jenkins.model.Jenkins;

import java.io.File;

/**
 * Created by CH on 6/29/2014.
 */
public class GlobalPostScriptAction implements BuildBadgeAction {

    private String iconPath;
    private String text;

    private GlobalPostScriptAction(String iconPath, String text) {
        this.iconPath = iconPath;
        this.text = text;
    }

    public String getIconFileName() {
        return iconPath;
    }

    public String getDisplayName() {
        return text;
    }

    public String getUrlName() {
        return null;
    }

    public static GlobalPostScriptAction createBadge(String icon, String text) {
        return new GlobalPostScriptAction(getIconPath(icon), text);
    }

    public static GlobalPostScriptAction addShortText(String text) {
        return new GlobalPostScriptAction(null, text);
    }

    private static String getIconPath(String icon) {
        if (null == icon) {
            return null;
        }

        PluginWrapper wrapper = Hudson.getInstance().getPluginManager().getPlugin(GlobalPostScriptPlugin.class);
        boolean pluginIconExists = (wrapper != null) && new File(wrapper.baseResourceURL.getPath() + "/img/" + icon).exists();
        return pluginIconExists ? "/plugin/global-post-script/img/" + icon : Jenkins.RESOURCE_PATH + "/images/16x16/" + icon;
    }
}
