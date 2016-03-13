package com.orctom.jenkins.plugin.globalpostscript.model;

/**
 * script content model
 * Created by hao on 3/13/16.
 */
public class ScriptContent {

  private String content;
  private boolean isChanged;

  public ScriptContent(String content, boolean isChanged) {
    this.content = content;
    this.isChanged = isChanged;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public boolean isChanged() {
    return isChanged;
  }

  public void setChanged(boolean changed) {
    this.isChanged = changed;
  }
}
