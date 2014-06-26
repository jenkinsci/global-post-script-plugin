package com.orctom;

import com.orctom.jenkins.plugin.globalpostscript.ScriptExecutor;

import java.io.File;
import java.util.Collections;

/**
 * Created by hao on 6/26/2014.
 */
public class ScriptTest {

    public static void main(String[] args) {
        try {
            ScriptExecutor executor = new ScriptExecutor(Collections.<String, String>emptyMap(), null);
            executor.executePython(new File("D:\\workspace-idea\\global-post-script-plugin\\work\\global-post-script\\conditional_downstream_trigger.py"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
