package org.thoughtslive.jenkins.plugins.jira.steps;

import java.io.IOException;

import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.kohsuke.stapler.DataBoundConstructor;
import org.thoughtslive.jenkins.plugins.jira.api.ResponseData;
import org.thoughtslive.jenkins.plugins.jira.api.Version;
import org.thoughtslive.jenkins.plugins.jira.util.JiraStepDescriptorImpl;
import org.thoughtslive.jenkins.plugins.jira.util.JiraStepExecution;

import hudson.Extension;
import lombok.Getter;

/**
 * Step to create a new JIRA Version.
 * 
 * @author Naresh Rayapati
 *
 */
public class NewVersionStep extends BasicJiraStep {

  private static final long serialVersionUID = -528328534268615694L;

  @Getter
  private final Version version;

  @DataBoundConstructor
  public NewVersionStep(final Version version) {
    this.version = version;
  }

  @Extension
  public static class DescriptorImpl extends JiraStepDescriptorImpl {

    @Override
    public String getFunctionName() {
      return "jiraNewVersion";
    }

    @Override
    public String getDisplayName() {
      return getPrefix() + "Create New Version";
    }

    @Override
    public boolean isMetaStep() {
      return true;
    }
  }

  public static class Execution extends JiraStepExecution<ResponseData<Version>> {

    private static final long serialVersionUID = 7109283776054289821L;

    private final NewVersionStep step;

    protected Execution(final NewVersionStep step, final StepContext context)
        throws IOException, InterruptedException {
      super(context);
      this.step = step;
    }

    @Override
    protected ResponseData<Version> run() throws Exception {

      ResponseData<Version> response = verifyInput();

      if (response == null) {
        logger
            .println("JIRA: Site - " + siteName + " - Creating new version: " + step.getVersion());
        final String description = addMeta(step.getVersion().getDescription());
        step.getVersion().setDescription(description);
        response = jiraService.createVersion(step.getVersion());
      }

      return logResponse(response);
    }

    @Override
    protected <T> ResponseData<T> verifyInput() throws Exception {
      // TODO Add validation - Or change the input type here ?
      return verifyCommon(step);
    }
  }

  @Override
  public StepExecution start(StepContext context) throws Exception {
    return new Execution(this, context);
  }
}
