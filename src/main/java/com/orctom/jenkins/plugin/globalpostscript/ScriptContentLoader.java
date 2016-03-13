package com.orctom.jenkins.plugin.globalpostscript;

import com.orctom.jenkins.plugin.globalpostscript.model.ScriptContent;
import hudson.Util;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * load script content with cache
 * Created by hao on 3/9/16.
 */
public class ScriptContentLoader {

  private static String scriptFileName;
  private static long scriptLastModified;

  private static String scriptContent;

  public static ScriptContent getScriptContent(File script, Map<String, String> variables) throws IOException {
    long modified = script.lastModified();
    boolean isChanged = false;
    if (!script.getName().equals(scriptFileName) || scriptLastModified < modified) {
      scriptFileName = script.getName();
      scriptLastModified = modified;

      String content = Util.loadFile(script);
      scriptContent = Util.replaceMacro(content, variables);
      isChanged = true;
    }
    return new ScriptContent(scriptContent, isChanged);
  }
}
