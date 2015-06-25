package com.orctom.jenkins.plugin.globalpostscript;

import groovy.lang.GroovyShell;
import groovy.lang.MissingPropertyException;
import hudson.Util;
import hudson.model.TaskListener;
import jenkins.model.Jenkins;
import org.codehaus.plexus.util.FileUtils;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.SimpleScriptContext;
import java.io.*;
import java.util.Map;

/**
 * Created by hao on 6/25/2014.
 */
public class ScriptExecutor {

    private Map<String, String> variables;
    private TaskListener listener;
    private GlobalPostScript.BadgeManager manager;

    public ScriptExecutor(Map<String, String> variables, TaskListener listener, GlobalPostScript.BadgeManager manager) {
        this.variables = variables;
        this.listener = listener;
        this.manager = manager;
    }

    public void execute(File script) {
        String ext = FileUtils.getExtension(script.getAbsolutePath());
        if ("groovy".equalsIgnoreCase(ext) || "gvy".equalsIgnoreCase(ext) || "gs".equalsIgnoreCase(ext) || "gsh".equalsIgnoreCase(ext)) {
            executeGroovy(script);
        } else if ("py".equalsIgnoreCase(ext) || "jy".equalsIgnoreCase(ext)) {
            executePython(script);
        } else if ("sh".equalsIgnoreCase(ext)) {
            executeScript("sh", script);
        } else if ("bat".equalsIgnoreCase(ext)) {
            executeScript("bat", script);
        } else {
            listener.getLogger().println("=============================");
            listener.getLogger().println("Script type not supported: " + ext + " | " + script.getName());
            listener.getLogger().println("=============================");
        }
    }

    public void executeGroovy(File script) {
        try {
            String scriptContent = getScriptContent(script);
            GroovyShell shell = new GroovyShell(getParentClassloader());
            for (Map.Entry<String, String> entry : variables.entrySet()) {
                shell.setVariable(entry.getKey(), entry.getValue());
            }
            shell.setVariable("out", listener.getLogger());
            shell.setVariable("manager", manager);
            shell.evaluate(scriptContent);
        } catch (MissingPropertyException e) {
            listener.getLogger().println("Failed to execute: " + script.getName() + ", " + e.getMessage());
        } catch (Throwable e) {
            e.printStackTrace();
            listener.getLogger().println("Failed to execute: " + script.getName() + ", " + e.getMessage());
        }
    }

    public void executePython(File script) {
        ScriptEngineManager sem = null;
        ScriptEngine engine = null;
        try {
            sem = new ScriptEngineManager(getParentClassloader());
            engine = sem.getEngineByExtension("py");
            if (null != engine) {
                ScriptContext context = new SimpleScriptContext();
                StringWriter writer = new StringWriter();
                for (Map.Entry<String, String> entry : variables.entrySet()) {
                    context.setAttribute(entry.getKey(), entry.getValue(), ScriptContext.ENGINE_SCOPE);
                }
                context.setAttribute("manager", manager, ScriptContext.ENGINE_SCOPE);
                context.setWriter(writer);
                engine.eval(getScriptContent(script), context);
                listener.getLogger().println(writer.toString());
            } else {
                listener.getLogger().println("[ERROR] Failed to load python interpreter, please use Grovvy script in the meanwhile");
            }
        } catch (Throwable e) {
            e.printStackTrace();
            listener.getLogger().println("Failed to execute: " + script.getName() + ", " + e.getMessage());
        }
    }

    public void executeScript(String executable, File script) {
        File temp = null;
        try {
            String extension = "." + FileUtils.getExtension(script.getPath());
            temp = FileUtils.createTempFile("global-post-script-", extension, null);
            FileUtils.fileWrite(temp, getScriptContent(script));
            Process p = Runtime.getRuntime().exec(new String[]{executable, temp.getAbsolutePath()});
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line = null;
            while ((line = br.readLine()) != null) {
                builder.append(line);
                builder.append(System.getProperty("line.separator"));
            }
            listener.getLogger().println(builder.toString());
        } catch (Throwable e) {
            e.printStackTrace();
            listener.getLogger().println("[global-post-script] Failed to execute: " + script.getName() + ", " + e.getMessage());
        } finally {
            if (null != temp) {
                temp.delete();
            }
        }
    }

    private String getScriptContent(File script) throws IOException {
        String scriptContent = Util.loadFile(script);
        return Util.replaceMacro(scriptContent, variables);
    }

    private ClassLoader getParentClassloader() {
        if (null != Jenkins.getInstance()) {
            return Jenkins.getInstance().getPluginManager().uberClassLoader;
        } else {
            return this.getClass().getClassLoader();
        }
    }

}
