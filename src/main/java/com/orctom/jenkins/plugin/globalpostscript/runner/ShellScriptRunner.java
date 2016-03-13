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
      Process p = Runtime.getRuntime().exec(new String[]{getExecutable(script), temp.getAbsolutePath()});
      BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
      StringBuilder builder = new StringBuilder();
      String line;
      while ((line = br.readLine()) != null) {
        builder.append(line);
        builder.append(System.getProperty("line.separator"));
      }
      println(listener, builder.toString());
    } catch (Throwable e) {
      e.printStackTrace();
      println(listener, "[ERROR] Failed to execute: " + script.getName() + ", " + e.getMessage());
    } finally {
      if (null != temp) {
        temp.delete();
      }
    }
  }

  public String getExecutable(File script) {
    return FileUtils.getExtension(script.getAbsolutePath());
  }
}
