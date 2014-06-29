package com.orctom.jenkins.plugin.globalpostscript;

import groovy.lang.GroovyShell;
import hudson.Util;
import hudson.model.TaskListener;
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
        } else {
            throw new UnsupportedOperationException("Script type not supported: " + ext);
        }
    }

    public void executeGroovy(File script) {
        System.out.println("executeGroovy");
        try {
            String scriptContent = getScriptContent(script);
            GroovyShell shell = new GroovyShell();
            for (Map.Entry<String, String> entry : variables.entrySet()) {
                shell.setVariable(entry.getKey(), entry.getValue());
            }
            shell.setVariable("out", listener.getLogger());
            shell.setVariable("manager", manager);
            shell.evaluate(scriptContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void executePython(File script) {
        try {
            ScriptEngine engine = new ScriptEngineManager().getEngineByExtension("py");
            if (null != engine) {
                ScriptContext context = new SimpleScriptContext();
                StringWriter writer = new StringWriter();
                for (Map.Entry<String, String> entry : variables.entrySet()) {
                    context.setAttribute(entry.getKey(), entry.getValue(), ScriptContext.ENGINE_SCOPE);
                }
                context.setWriter(writer);
                engine.eval(getScriptContent(script), context);
                listener.getLogger().println(writer.toString());
            } else {
                executeScript("python", script);
            }
        } catch (Throwable e) {
            e.printStackTrace(listener.getLogger());
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
            e.printStackTrace(listener.getLogger());
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

}
