package com.orctom.jenkins.plugin.globalpostscript.runner;

import com.orctom.jenkins.plugin.globalpostscript.GlobalPostScript;
import com.orctom.jenkins.plugin.globalpostscript.ScriptContentLoader;
import com.orctom.jenkins.plugin.globalpostscript.model.ScriptContent;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyShell;
import groovy.lang.MissingPropertyException;
import hudson.model.TaskListener;
import jenkins.model.Jenkins;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.Map;

/**
 * groovy script runner
 * Created by hao on 3/13/16.
 */
public class GroovyScriptRunner extends ScriptRunner {

  public void run(File scriptFile,
                  Map<String, String> variables,
                  GlobalPostScript.BadgeManager manager,
                  TaskListener listener) {
    GroovyShell shell = new GroovyShell(getGroovyClassloader());
    try {
      for (Map.Entry<String, String> entry : variables.entrySet()) {
        shell.setVariable(entry.getKey(), entry.getValue());
      }
      shell.setVariable("out", listener.getLogger());
      shell.setVariable("manager", manager);

      ScriptContent sc = ScriptContentLoader.getScriptContent(scriptFile, variables);
      shell.parse(sc.getContent()).run();
    } catch (MissingPropertyException e) {
      println(listener, "[ERROR] Failed to execute: " + scriptFile.getName() + ", " + e.getMessage());
    } catch (Throwable e) {
      e.printStackTrace();
      println(listener, "[ERROR] Failed to execute: " + scriptFile.getName() + ", " + e.getMessage());
    }
  }

  protected ClassLoader getGroovyClassloader() {
    if (null == Jenkins.getInstance()) {
      return getParentClassloader();
    }

    File libFolder = new File(Jenkins.getInstance().getRootDir().getAbsolutePath() + GlobalPostScript.SCRIPT_FOLDER, "lib");
    return getGroovyClassloader(libFolder);
  }

  protected ClassLoader getGroovyClassloader(File libFolder) {
    GroovyClassLoader cl = new GroovyClassLoader(getParentClassloader());
    if (!libFolder.exists() || !libFolder.isDirectory()) {
      return cl;
    }

    File[] files = libFolder.listFiles(new JarFilter());
    if (null == files || 0 == files.length) {
      return cl;
    }

    for (File file : files) {
      cl.addClasspath(file.getPath());
      System.out.println("[global-post-script] extra classpath: " + file.getPath());
    }

    return cl;
  }

  protected static class JarFilter implements FilenameFilter {
    private static final String JAR_FILE_SUFFIX = ".jar";
    public boolean accept(File dir, String name) {
      return name.toLowerCase().endsWith(JAR_FILE_SUFFIX);
    }
  }
}
