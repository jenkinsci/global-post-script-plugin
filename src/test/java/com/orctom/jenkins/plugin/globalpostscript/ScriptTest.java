package com.orctom.jenkins.plugin.globalpostscript;

import hudson.model.TaskListener;
import hudson.util.LogTaskListener;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by hao on 6/26/2014.
 */
public class ScriptTest {

    private ScriptExecutor executor;
    private TaskListener listener;

    @Before
    public void before() {
        Map<String, String> variables = new HashMap<String, String>();
        variables.put("dropdeploy_targets", "server1");
        listener = new LogTaskListener(Logger.getLogger(ScriptTest.class.getName()), Level.ALL);
        executor = new ScriptExecutor(variables, listener);
    }

    @Test
    public void testExecutePython() {
        File script = new File(ClassLoader.getSystemResource("test.py").getPath());
        System.out.println("script: " + script);
        String expected = "server1";
        executor.executePython(script);
        String actual = listener.getLogger().toString();
        System.out.println("======");
        System.out.println(actual);
        System.out.println("======");
        Assert.assertEquals(expected, actual);
    }
}
