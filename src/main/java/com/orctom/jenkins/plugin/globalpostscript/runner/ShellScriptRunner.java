package com.orctom.jenkins.plugin.globalpostscript.runner;

import com.orctom.jenkins.plugin.globalpostscript.GlobalPostScript;
import hudson.model.TaskListener;
import org.codehaus.plexus.util.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Map;

import static com.orctom.jenkins.plugin.globalpostscript.ScriptContentLoader.getScriptContent;

/**
 * shell/bash script runner
 * Created by hao on 3/13/16.
 */
public class ShellScriptRunner extends ScriptRunner {

  public void run(File script,
                  Map<String, String> variables,
                  GlobalPostScript.BadgeManager manager,
                  TaskListener listener) {
    File temp = null;
    try {
      String extension = "." + FileUtils.getExtension(script.getPath());
      temp = FileUtils.createTempFile("global-post-script-", extension, null);
      FileUtils.fileWrite(temp, getScriptContent(script, variables).getContent());
      String[] commands;
      if (".sh".equals(extension)) {
        commands = new String[]{getExecutable(script), temp.getAbsolutePath()};
      } else {
        commands = new String[]{temp.getAbsolutePath()};
      }
      ProcessBuilder builder = new ProcessBuilder(commands);
      Map<String, String> env = builder.environment();
      for (Map.Entry<String, String> entry : variables.entrySet()) {
        env.put(entry.getKey(), entry.getValue());
      }
      Process process = builder.start();

      try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
        String line;
        while ((line = reader.readLine()) != null) {
          println(listener, line);
        }
      }

    } catch (Throwable e) {
      e.printStackTrace();
      println(listener, "[ERROR] Failed to execute: " + script.getName() + ", " + e.getMessage());
    } finally {
      if (null != temp) {
        temp.delete();
      }
    }
  }

  private String getExecutable(File script) {
    return FileUtils.getExtension(script.getAbsolutePath());
  }
}
