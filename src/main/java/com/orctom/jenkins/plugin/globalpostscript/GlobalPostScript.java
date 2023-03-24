package com.orctom.jenkins.plugin.globalpostscript;

import com.google.common.collect.Lists;
import com.orctom.jenkins.plugin.globalpostscript.model.URL;
import hudson.EnvVars;
import hudson.Extension;
import hudson.console.ModelHyperlinkNote;
import hudson.model.*;
import hudson.model.listeners.RunListener;
import hudson.util.ComboBoxModel;
import hudson.util.FormValidation;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.apache.commons.codec.net.URLCodec;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import javax.servlet.ServletException;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Global Post Script that will be executed for all jobs
 * Created by hao on 6/25/2014.
 */
@Extension
public class GlobalPostScript extends RunListener<Run<?, ?>> implements Describable<GlobalPostScript> {

  public static final String SCRIPT_FOLDER = File.separator + "global-post-script" + File.separator;

  public static String getRemoteJobUrl(String jobUrl) {
    if (jobUrl.contains("buildByToken")) {
      return jobUrl.substring(0, jobUrl.indexOf("buildByToken")) + "job/" + jobUrl.replaceFirst(".*job=(\\w+)&.*", "$1");
    } else {
      return jobUrl.substring(0, jobUrl.lastIndexOf("/") + 1);
    }
  }

  @Override
  public void onCompleted(Run run, TaskListener listener) {
    EnvVars envVars = getEnvVars(run, listener);

    Result result = run.getResult();
    if (result == null || result.isWorseThan(getDescriptorImpl().getResultCondition())) {
      return;
    }

    String script = getDescriptorImpl().getScript();
    File file = new File(Jenkins.get().getRootDir().getAbsolutePath() + SCRIPT_FOLDER, script);
    if (file.exists()) {
      try {
        BadgeManager manager = new BadgeManager(run, listener);
        ScriptExecutor executor = new ScriptExecutor(listener, manager);
        executor.execute(file, envVars);
      } catch (Throwable e) {
        e.printStackTrace(listener.getLogger());
      }
    }
  }

  private EnvVars getEnvVars(Run run, TaskListener listener) {
    try {
      EnvVars envVars = run.getEnvironment(listener);
      Result result = run.getResult();
      envVars.put("BUILD_RESULT", result == null ? "ongoing" : result.toString());
      return envVars;
    } catch (Throwable e) {
      e.printStackTrace();
      return null;
    }
  }

  public Descriptor<GlobalPostScript> getDescriptor() {
    return getDescriptorImpl();
  }

  public DescriptorImpl getDescriptorImpl() {
    return (DescriptorImpl) Jenkins.get().getDescriptorOrDie(GlobalPostScript.class);
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
      AbstractProject job = Jenkins.get().getItem(jobName, run.getParent().getParent(), AbstractProject.class);
      if (null != job) {
        Cause cause = new Cause.UpstreamCause(run);
        boolean scheduled = job.scheduleBuild(job.getQuietPeriod(), cause, new ParametersAction(newParams));
        if (Jenkins.get().getItemByFullName(job.getFullName()) == job) {
          String name = ModelHyperlinkNote.encodeTo(job) + "  "
              + ModelHyperlinkNote.encodeTo(
              job.getAbsoluteUrl() + job.getNextBuildNumber() + "/",
              "#" + job.getNextBuildNumber());
          if (scheduled) {
            println("Triggering " + name);
          } else {
            println("In queue " + name);
          }
        }
      } else {
        println("[ERROR] Downstream job not found: " + jobName);
      }
    }

    public void triggerRemoteJob(String jobTriggerUrl) {
      String url = jobTriggerUrl;
      String jobUrl = getRemoteJobUrl(jobTriggerUrl);

      try {
        URL jobURL = new URL(jobTriggerUrl);
        jobURL.appendToParamValue("cause", new URLCodec().encode(getCause(), "UTF-8"));
        url = jobURL.getURL();
      } catch (Exception e) {
        println("[WARNING] ignoring URL exception for " + jobTriggerUrl);
      }

      CloseableHttpClient client = HttpClients.createDefault();
      HttpGet method = new HttpGet(url);
      try {
        CloseableHttpResponse response = client.execute(method);
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode < 400) {
          println("Triggering " + jobUrl);
        } else {
          println("[ERROR] Failed to trigger: " + jobUrl + " | " + statusCode);
        }
      } catch (Exception e) {
        e.printStackTrace();
        println("[ERROR] Failed to trigger: " + jobUrl + " | " + e.getMessage());
      } finally {
        try {
          client.close();
        } catch (IOException e) {
          println("[ERROR] Failed to close connection: " + jobUrl + " | " + e.getMessage());
        }
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

      String rootUrl = Jenkins.get().getRootUrl();
      if (StringUtils.isNotEmpty(rootUrl)) {
        cause.append("on ").append(rootUrl).append(" ");
      }

      cause.append("[").append(run.getParent().getName()).append("]");
      return cause.toString();
    }

    private void println(String message) {
      listener.getLogger().println(message);
    }
  }

  @Extension
  public static final class DescriptorImpl extends Descriptor<GlobalPostScript> {

    private String script = "downstream_job_trigger.groovy";
    private Result runCondition = Result.UNSTABLE;

    public DescriptorImpl() {
      load();
    }

    public FormValidation doCheckScript(@QueryParameter("script") String name) throws IOException, ServletException {
      Jenkins.get().checkPermission(Jenkins.ADMINISTER);
      if (StringUtils.isEmpty(name)) {
        return FormValidation.error("Please set the script name");
      }
      if (!name.matches("[a-zA-Z0-9_\\-]+\\.\\w+")) {
        return FormValidation.error("Please make sure it's a valid file name with extension");
      }
      return FormValidation.ok();
    }

    public ComboBoxModel doFillScriptItems() {
      ComboBoxModel items = new ComboBoxModel();

      File scriptFolder = new File(Jenkins.get().getRootDir().getAbsolutePath() + SCRIPT_FOLDER);
      FilenameFilter filter = new FilenameFilter() {
        public boolean accept(File dir, String name) {
          String fileName = name.toLowerCase();
          return new File(dir, name).isFile() && (
              fileName.endsWith(".groovy") ||
                  fileName.endsWith(".gvy") ||
                  fileName.endsWith(".gy") ||
                  fileName.endsWith(".gsh") ||
                  fileName.endsWith(".bat") ||
                  fileName.endsWith(".sh")
          );
        }
      };

      String [] filteredFiles = scriptFolder.list(filter);
      if (filteredFiles != null) {
        Collections.addAll(items, filteredFiles);
      }
      return items;
    }

    public boolean isApplicable(Class<? extends AbstractProject> aClass) {
      return true;
    }

    /**
     * This human readable name is used in the configuration screen.
     */
    public String getDisplayName() {
      return GlobalPostScriptPlugin.PLUGIN_NAME;
    }

    @Override
    public boolean configure(StaplerRequest req, JSONObject formData) throws Descriptor.FormException {
      script = formData.getString("script");
      runCondition = Result.fromString(formData.getString("runCondition"));
      save();
      return super.configure(req, formData);
    }

    public String getScript() {
      return script;
    }

    public Result getResultCondition() {
      return runCondition;
    }

    public String getRunCondition() {
      return runCondition.toString();
    }
  }
}
