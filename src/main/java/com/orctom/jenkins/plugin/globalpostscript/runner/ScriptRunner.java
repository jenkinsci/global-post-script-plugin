package com.orctom.jenkins.plugin.globalpostscript.runner;

import com.orctom.jenkins.plugin.globalpostscript.GlobalPostScript;
import hudson.model.TaskListener;
import jenkins.model.Jenkins;

import java.io.File;
import java.util.Map;

/**
 * script runner interface
 * Created by hao on 3/13/16.
 */
public abstract class ScriptRunner {

  public abstract void run(File script,
                           Map<String, String> variables,
                           GlobalPostScript.BadgeManager manager,
                           TaskListener listener);

  protected void println(TaskListener listener, String message) {
    listener.getLogger().println(message);
  }

  protected ClassLoader getParentClassloader() {
    if (null != Jenkins.getInstance()) {
      return Jenkins.getInstance().getPluginManager().uberClassLoader;
    } else {
      return Thread.currentThread().getContextClassLoader();
    }
  }
}
