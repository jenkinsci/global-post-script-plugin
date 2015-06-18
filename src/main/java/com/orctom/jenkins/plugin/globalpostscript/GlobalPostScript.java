package com.orctom.jenkins.plugin.globalpostscript;

import com.google.common.collect.Lists;
import hudson.EnvVars;
import hudson.Extension;
import hudson.console.ModelHyperlinkNote;
import hudson.model.*;
import hudson.model.listeners.RunListener;
import hudson.model.queue.QueueTaskFuture;
import hudson.util.FormValidation;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import javax.servlet.ServletException;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

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
		private EnvVars envVars;

		public BadgeManager(Run run, TaskListener listener) {
			this.run = run;
			this.listener = listener;

			if (null != run) {
				try {
					envVars = run.getEnvironment(listener);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		public boolean isVar(String name) {
			if (null == envVars || StringUtils.isBlank(name)) {
				return false;
			}

			return envVars.containsKey(name);
		}

		public boolean isNotBlankVar(String name) {
			if (null == envVars || StringUtils.isBlank(name)) {
				return false;
			}

			return StringUtils.isNotBlank(envVars.get(name));
		}

		public void addBadge(String icon, String text) {
			run.addAction(GlobalPostScriptAction.createBadge(icon, text));
		}

		public void addShortText(String text) {
			run.addAction(GlobalPostScriptAction.addShortText(text));
		}

		public void triggerJob(String jobName) {
			triggerJob(jobName, Collections.<String, String>emptyMap());
		}

		public void triggerJob(String jobName, Map<String, String> params) {
			List<ParameterValue> newParams = Lists.newArrayList();
			for (Map.Entry<String, String> entry : params.entrySet()) {
				newParams.add(new StringParameterValue(entry.getKey(), entry.getValue()));
			}
			AbstractProject job = Jenkins.getInstance().getItem(jobName, run.getParent().getParent(), AbstractProject.class);
			if (null != job) {
				Cause cause = new Cause.UpstreamCause(run);
				boolean scheduled = job.scheduleBuild(job.getQuietPeriod(), cause, new ParametersAction(newParams));
				if (Jenkins.getInstance().getItemByFullName(job.getFullName()) == job) {
					String name = ModelHyperlinkNote.encodeTo(job) + "  "
							+ ModelHyperlinkNote.encodeTo(
								job.getAbsoluteUrl() + job.getNextBuildNumber() + "/",
								"#" + job.getNextBuildNumber());
					if (scheduled) {
						listener.getLogger().println(hudson.tasks.Messages.BuildTrigger_Triggering(name));
					} else {
						listener.getLogger().println(hudson.tasks.Messages.BuildTrigger_InQueue(name));
					}
				}
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
