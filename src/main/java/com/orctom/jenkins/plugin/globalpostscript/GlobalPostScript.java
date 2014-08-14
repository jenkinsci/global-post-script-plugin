package com.orctom.jenkins.plugin.globalpostscript;

import hudson.EnvVars;
import hudson.Extension;
import hudson.model.*;
import hudson.model.listeners.RunListener;
import hudson.util.FormValidation;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.apache.commons.codec.StringEncoder;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.wagon.providers.http.httpclient.HttpResponse;
import org.apache.maven.wagon.providers.http.httpclient.client.methods.HttpGet;
import org.codehaus.plexus.classworlds.UrlUtils;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import javax.servlet.ServletException;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
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
                    BadgeManager manager = new BadgeManager(run, listener);
                    ScriptExecutor executor = new ScriptExecutor(envVars, listener, manager);
                    executor.execute(file);
                } catch (Throwable e) {
                    e.printStackTrace(listener.getLogger());
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static class BadgeManager {

        private Run run;
        private TaskListener listener;

        public BadgeManager(Run run, TaskListener listener) {
            this.run = run;
            this.listener = listener;
        }

        public void addBadge(String icon, String text) {
            run.addAction(GlobalPostScriptAction.createBadge(icon, text));
        }

        public void addShortText(String text) {
            run.addAction(GlobalPostScriptAction.addShortText(text));
        }

        public void triggerJob(String jobName) {
            AbstractProject job = Jenkins.getInstance().getItem(jobName, run.getParent().getParent(), AbstractProject.class);
            if (null != job) {
                Cause cause = new Cause.UpstreamCause(run);
                job.scheduleBuild(cause);
                listener.getLogger().println("Triggered downstream job: " + jobName);
            } else {
                listener.getLogger().println("Downstream job not found: " + jobName);
            }
        }

        public void triggerRemoteJob(String jobUrl) {
            String url = jobUrl;
            try {
                URL jobURL = new URL(jobUrl);
                jobURL.appendToParamValue("cause", new URLCodec().encode(getCause(), "UTF-8"));
                url = jobURL.getURL();
            } catch (Exception e) {
            }

            HttpClient client = new HttpClient();
            HttpMethod method = new GetMethod(url);
            try {
                client.executeMethod(method);
                int statusCode = method.getStatusCode();
                if (statusCode < 400) {
                    listener.getLogger().println("Triggered: " + url);
                } else {
                    listener.getLogger().println("Failed to triggered: " + url + " | " + statusCode);
                }
            } catch (Exception e) {
                listener.getLogger().println("Failed to triggered: " + url + " | " + e.getMessage());
            } finally {
                method.releaseConnection();
            }
        }

        public String getCause() {
            List<Cause> causes = run.getCauses();
            StringBuilder cause = new StringBuilder(50);
            for (Cause c : causes) {
                String desc = c.getShortDescription();
                if (StringUtils.isNotEmpty(desc)) {
                    cause.append(c.getShortDescription()).append(" ");
                }
            }

            String rootUrl = Jenkins.getInstance().getRootUrl();
            if (StringUtils.isNotEmpty(rootUrl)) {
                cause.append("on ").append(rootUrl).append(" ");
            }

            cause.append("[").append(run.getParent().getName()).append("]");
            return cause.toString();
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

        private String script = "downstream_job_trigger.groovy";

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
