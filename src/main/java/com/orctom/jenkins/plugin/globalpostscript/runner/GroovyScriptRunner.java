package com.orctom.jenkins.plugin.globalpostscript.runner;

import com.orctom.jenkins.plugin.globalpostscript.GlobalPostScript;
import com.orctom.jenkins.plugin.globalpostscript.model.ScriptContent;
import com.orctom.jenkins.plugin.globalpostscript.ScriptContentLoader;
import groovy.lang.GroovyShell;
import groovy.lang.MissingPropertyException;
import groovy.lang.Script;
import hudson.model.TaskListener;

import java.io.File;
import java.util.Map;

/**
 * groovy script runner
 * Created by hao on 3/13/16.
 */
public class GroovyScriptRunner extends ScriptRunner {

  private GroovyShell shell = null;
  private Script script = null;

  public GroovyScriptRunner() {
    shell = new GroovyShell(getParentClassloader());
  }

  public void run(File scriptFile,
                  Map<String, String> variables,
                  GlobalPostScript.BadgeManager manager,
                  TaskListener listener) {
    try {
      for (Map.Entry<String, String> entry : variables.entrySet()) {
        shell.setVariable(entry.getKey(), entry.getValue());
      }
      shell.setVariable("out", listener.getLogger());
      shell.setVariable("manager", manager);

      ScriptContent sc = ScriptContentLoader.getScriptContent(scriptFile, variables);
      if (sc.isChanged()) {
        script = shell.parse(sc.getContent());
      }

      script.run();
    } catch (MissingPropertyException e) {
      println(listener, "[ERROR] Failed to execute: " + scriptFile.getName() + ", " + e.getMessage());
    } catch (Throwable e) {
      e.printStackTrace();
      println(listener, "[ERROR] Failed to execute: " + scriptFile.getName() + ", " + e.getMessage());
    }
  }
}
