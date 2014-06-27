package com.orctom.jenkins.plugin.globalpostscript;

import hudson.Util;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.SimpleScriptContext;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hao on 6/26/2014.
 */
public class ScriptTest {

    Map<String, String> variables = new HashMap<String, String>();

    public ScriptTest() {
        variables = new HashMap<String, String>();
        variables.put("dropdeploy_targets", "wwwsqs8");
    }

    public void executePython(File script) {
        System.out.println("executePython");
        System.out.println("======================== 1");
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
            System.out.println("=");
            System.out.println(result);
        } catch (Throwable e) {
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

    public static void main(String[] args) {
        File script = new File(ClassLoader.getSystemResource("test.py").getPath());
        System.out.println("script: " + script);
        new ScriptTest().executePython(script);
    }
}
