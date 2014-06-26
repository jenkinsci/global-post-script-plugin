package com.orctom.jenkins.plugin.globalpostscript;

import groovy.lang.GroovyShell;
import hudson.Util;
import hudson.model.TaskListener;
import hudson.util.LogTaskListener;
import org.codehaus.plexus.util.FileUtils;
import org.python.core.PyInteger;
import org.python.core.PyString;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.SimpleScriptContext;
import java.io.*;
import java.util.Collections;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by hao on 6/25/2014.
 */
public class ScriptExecutor {

    private Map<String, String> variables;
    private TaskListener listener;

    public ScriptExecutor(Map<String, String> variables, TaskListener listener) {
        this.variables = variables;
        this.listener = listener;
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
            shell.evaluate(scriptContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void executePython(File script) {
        System.out.println("executePython");
        try {
            Process p = Runtime.getRuntime().exec(new String[]{"python", script.getAbsolutePath()});
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line = null;
            while ((line = br.readLine()) != null) {
                builder.append(line);
                builder.append(System.getProperty("line.separator"));
            }
            String result = builder.toString();
            System.out.println("========================");
            System.out.println(result);
        } catch (Throwable e) {
            //e.printStackTrace(listener.getLogger());
            e.printStackTrace();
        }

        System.out.println("======================== 2");

        try {
            ScriptEngine engine = new ScriptEngineManager().getEngineByExtension("py");
            if (null != engine) {
                ScriptContext context = new SimpleScriptContext();
                StringWriter writer = new StringWriter();
                for (Map.Entry<String, String> entry : variables.entrySet()) {
                    context.setAttribute(entry.getKey(), entry.getValue(), ScriptContext.ENGINE_SCOPE);
                }
                context.setWriter(writer);
                engine.eval(new FileReader(script), context);
                System.out.println(writer.toString());
            } else {
                System.out.println("engine null");
            }
        } catch (Throwable e) {
            //e.printStackTrace(listener.getLogger());
            e.printStackTrace();
        }
        System.out.println("======================== 3");


        try {
            PythonInterpreter interpreter = new PythonInterpreter(null, new PySystemState());
            StringWriter out = new StringWriter();
            StringWriter err = new StringWriter();
            interpreter.setOut(out);
            interpreter.setErr(err);
            interpreter.eval(getScriptContent(script));
            System.out.println("output:\n" + out.toString());
            System.out.println("error:\n" + err.toString());
        } catch (Throwable e) {
            e.printStackTrace();
        }

        System.out.println("======================== 4");

        try {
            PythonInterpreter.initialize(System.getProperties(), System.getProperties(), new String[0]);
            PythonInterpreter interpreter = new PythonInterpreter();
            StringWriter out = new StringWriter();
            StringWriter err = new StringWriter();
            interpreter.setOut(out);
            interpreter.setErr(err);
            System.out.println("output:\n" + out.toString());
            System.out.println("error:\n" + err.toString());
        } catch (Throwable e) {
            e.printStackTrace();
        }

        System.out.println("======================== end");

    }

    private String getScriptContent(File script) throws IOException {
        String scriptContent = Util.loadFile(script);
        return Util.replaceMacro(scriptContent, variables);
    }

}
