package com.orctom.jenkins.plugin.globalpostscript;

import hudson.model.TaskListener;
import hudson.util.LogTaskListener;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
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
        listener = new LogTaskListener(Logger.getLogger(ScriptTest.class.getName()), Level.ALL) {

            private PrintStream logger = new PrintStream(new ByteArrayOutputStream()) {
                private StringBuilder logs = new StringBuilder();
                @Override
                public void println(String x) {
                    logs.append(x).append(System.getProperty("line.separator"));
                }

                @Override
                public void print(String x) {
                    logs.append(x);
                }

                @Override
                public String toString() {
                    return logs.toString();
                }
            };

            @Override
            public PrintStream getLogger() {
                return logger;
            }
        };
        executor = new ScriptExecutor(variables, listener, new GlobalPostScript.BadgeManager(null, null) {
            @Override
            public void addBadge(String icon, String text) {
                System.out.println("addBadge: " + icon + ", " + text);
            }

            @Override
            public void addShortText(String text) {
                System.out.println("addShortText: " + text);
            }

            @Override
            public void triggerJob(String jobName) {
                System.out.println("triggerJob: " + jobName);
            }

            @Override
            public void triggerRemoteJob(String jobUrl) {
                System.out.println("triggerRemoteJob: " + jobUrl);
            }

            @Override
            public String getCause() {
                return "dummy cause";
            }
        });
    }

    @Test
    public void testExecutePython() {
        File script = new File(ClassLoader.getSystemResource("test.py").getPath());
        System.out.println("script: " + script);
        String expected = "dropdeploy to: server1, dummy cause";
        executor.executePython(script);
        String actual = StringUtils.trim(listener.getLogger().toString());
        System.out.println("expected: " + expected);
        System.out.println("actual  : " + actual);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testExecutePython2() {
        File script = new File(ClassLoader.getSystemResource("test2.py").getPath());
        System.out.println("script: " + script);
        String expected = "dropdeploy to: server1, dummy cause";
        executor.executePython(script);
        String actual = StringUtils.trim(listener.getLogger().toString());
        System.out.println("expected: " + expected);
        System.out.println("actual  : " + actual);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testExecuteGroovy() {
        File script = new File(ClassLoader.getSystemResource("test.groovy").getPath());
        System.out.println("script: " + script);
        String expected = "dropdeploy to: server1";
        executor.executeGroovy(script);
        String actual = StringUtils.trim(listener.getLogger().toString());
        System.out.println("expected: " + expected);
        System.out.println("actual  : " + actual);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testExecuteGroovy2() {
        File script = new File(ClassLoader.getSystemResource("test2.groovy").getPath());
        System.out.println("script: " + script);
        String expected = "dropdeploy to: server1";
        executor.executeGroovy(script);
        String actual = StringUtils.trim(listener.getLogger().toString());
        System.out.println("expected: " + expected);
        System.out.println("actual  : " + actual);
        Assert.assertEquals(expected, actual);
    }
}
