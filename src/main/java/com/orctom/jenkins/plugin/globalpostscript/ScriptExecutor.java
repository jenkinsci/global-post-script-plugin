package com.orctom.jenkins.plugin.globalpostscript;

import com.orctom.jenkins.plugin.globalpostscript.runner.ScriptRunners;
import hudson.model.TaskListener;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.util.Map;

/**
 * script executor
 * Created by hao on 6/25/2014.
 */
public class ScriptExecutor {

  private TaskListener listener;
  private GlobalPostScript.BadgeManager manager;

  public ScriptExecutor(TaskListener listener, GlobalPostScript.BadgeManager manager) {
    this.listener = listener;
    this.manager = manager;
  }

  public void execute(File scriptFile, Map<String, String> variables) {
    println("[INFO]");
    println("[INFO] -----------------------------------------------------");
    println("[INFO] " + GlobalPostScriptPlugin.PLUGIN_NAME);
    println("[INFO] -----------------------------------------------------");
    String ext = FileUtils.getExtension(scriptFile.getAbsolutePath());
    if ("groovy".equalsIgnoreCase(ext) || "gvy".equalsIgnoreCase(ext) || "gs".equalsIgnoreCase(ext) || "gsh".equalsIgnoreCase(ext)) {
      ScriptRunners.GROOVY.run(scriptFile, variables, manager, listener);
    } else if ("sh".equalsIgnoreCase(ext) || "bat".equalsIgnoreCase(ext)) {
      ScriptRunners.SHELL.run(scriptFile, variables, manager, listener);
    } else {
      println("[ERROR] Script type not supported: " + ext + " | " + scriptFile.getName());
    }
    println("[INFO] -----------------------------------------------------");
  }

  private void println(String message) {
    listener.getLogger().println(message);
  }
}
