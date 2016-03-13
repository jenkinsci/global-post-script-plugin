package com.orctom.jenkins.plugin.globalpostscript.runner;

import com.orctom.jenkins.plugin.globalpostscript.GlobalPostScript;
import hudson.model.TaskListener;

import java.io.File;
import java.util.Map;

/**
 * executors registry
 * Created by hao on 3/13/16.
 */
public enum ScriptRunners {

  GROOVY(new GroovyScriptRunner()),
  SHELL(new ShellScriptRunner());

  private ScriptRunner scriptRunner;

  ScriptRunners(ScriptRunner scriptRunner) {
    this.scriptRunner = scriptRunner;
  }

  public void run(File scriptFile,
                  Map<String, String> variables,
                  GlobalPostScript.BadgeManager manager,
                  TaskListener listener) {
    scriptRunner.run(scriptFile, variables, manager, listener);
  }
}
