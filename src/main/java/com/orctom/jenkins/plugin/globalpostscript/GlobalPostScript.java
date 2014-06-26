package com.orctom.jenkins.plugin.globalpostscript;

import hudson.EnvVars;
import hudson.Extension;
import hudson.model.*;
import hudson.model.listeners.RunListener;
import hudson.util.FormValidation;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import javax.servlet.ServletException;
import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Global Post Script that will be executed for all jobs
 * Created by hao on 6/25/2014.
 */
@Extension
public class GlobalPostScript extends RunListener<Run<?, ?>> implements Describable<GlobalPostScript> {

    @Override
    public void onCompleted(Run run, TaskListener listener) {
        if (run.getResult().isBetterOrEqualTo(Result.UNSTABLE)) {
            String script = getDescriptorImpl().getScript();
            File file = new File(Jenkins.getInstance().getRootDir().getAbsolutePath() + "/global-post-script/", script);
            if (file.exists()) {
                try {
                    EnvVars envVars = run.getEnvironment(listener);
                    for (Map.Entry<String, String> entry : envVars.entrySet()) {
                        listener.getLogger().println(entry.getKey() + " = " + entry.getValue());
                    }
                    ScriptExecutor executor = new ScriptExecutor(envVars, listener);
                    executor.execute(file);
                } catch (Throwable e) {
                    e.printStackTrace(listener.getLogger());
                }
            } else {
                System.out.println("file not exist: " + file.getAbsolutePath());
            }
        }
    }

    public Descriptor<GlobalPostScript> getDescriptor() {
        return getDescriptorImpl();
    }

    public DescriptorImpl getDescriptorImpl() {
        return (DescriptorImpl) Jenkins.getInstance().getDescriptorOrDie(GlobalPostScript.class);
    }

    @Extension
    public static final class DescriptorImpl extends Descriptor<GlobalPostScript> {

        private String script = "conditional_downstream_trigger.py";

        public DescriptorImpl() {
            load();
        }

        public FormValidation doCheckName(@QueryParameter String value) throws IOException, ServletException {
            if (StringUtils.isEmpty(value)) {
                return FormValidation.error("Please set the script name");
            }
            if (!value.matches("[a-zA-Z0-9_\\-]+\\.\\w+")) {
                return FormValidation.error("Please make sure it's a valid file name with extension. (matching '[a-zA-Z0-9_\\-]+\\.\\w+')");
            }
            return FormValidation.ok();
        }

        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        /**
         * This human readable name is used in the configuration screen.
         */
        public String getDisplayName() {
            return "Global Post Script";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws Descriptor.FormException {
            script = formData.getString("script");
            save();
            return super.configure(req, formData);
        }

        public String getScript() {
            return script;
        }
    }
}
