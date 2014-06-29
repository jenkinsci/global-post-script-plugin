package com.orctom.jenkins.plugin.globalpostscript;

import hudson.model.BuildBadgeAction;

/**
 * Created by CH on 6/29/2014.
 */
public class GlobalPostScriptAction implements BuildBadgeAction {

    private String icon;
    private String text;

    private GlobalPostScriptAction(String icon, String text) {
        this.icon = icon;
        this.text = text;
    }

    public String getIconFileName() {
        return icon;
    }

    public String getDisplayName() {
        return text;
    }

    public String getUrlName() {
        return null;
    }

    public static GlobalPostScriptAction createBadge(String icon, String text) {
        return new GlobalPostScriptAction(icon, text);
    }

    public static GlobalPostScriptAction addShortText(String text) {
        return new GlobalPostScriptAction(null, text);
    }
}
