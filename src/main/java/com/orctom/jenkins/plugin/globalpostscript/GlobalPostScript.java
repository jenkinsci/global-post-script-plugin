package com.orctom.jenkins.plugin.globalpostscript;

import hudson.EnvVars;
import hudson.Extension;
import hudson.model.*;
import hudson.model.listeners.RunListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * Global Post Script that will be executed for all jobs
 * Created by hao on 6/25/2014.
 */
@Extension
public class GlobalPostScript extends RunListener<Run<?, ?>> implements Describable<GlobalPostScript> {

    @Override
    public void onCompleted(Run r, TaskListener listener) {
        System.out.println("=============================");
        DescriptorImpl descriptor = (DescriptorImpl) Jenkins.getInstance().getDescriptorOrDie(GlobalPostScript.class);
        System.out.println("script: " + descriptor.getScript());
        try {
            EnvVars envVars = r.getEnvironment(listener);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String getStatus(Run r) {
        Result result = r.getResult();
        String status = null;
        if (result != null) {
            status = result.toString();
        }
        return status;
    }

    public Descriptor<GlobalPostScript> getDescriptor() {
        return (DescriptorImpl) Jenkins.getInstance().getDescriptorOrDie(GlobalPostScript.class);
    }

    @Extension
    public static final class DescriptorImpl extends Descriptor<GlobalPostScript> {

        private String script = "conditional_downstream_trigger.py";

        public DescriptorImpl() {
            load();
        }

        public FormValidation doCheckName(@QueryParameter String value) throws IOException, ServletException {
            if (value.length() == 0) {
                return FormValidation.error("Please set a name");
            }
            if (value.length() < 4) {
                return FormValidation.warning("Isn't the name too short?");
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
            return "Global Post Script :)";
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
