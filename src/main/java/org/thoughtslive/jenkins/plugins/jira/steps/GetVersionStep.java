package org.thoughtslive.jenkins.plugins.jira.steps;

import static org.thoughtslive.jenkins.plugins.jira.util.Common.buildErrorResponse;

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
 * Step to query a JIRA Version.
 *
 * @author Naresh Rayapati
 */
public class GetVersionStep extends BasicJiraStep {

  private static final long serialVersionUID = -4252560961571411897L;
  @Getter
  private final int id;

  @DataBoundConstructor
  public GetVersionStep(final int id) {
    this.id = id;
  }

  @Extension
  public static class DescriptorImpl extends JiraStepDescriptorImpl {

    @Override
    public String getFunctionName() {
      return "jiraGetVersion";
    }

    @Override
    public String getDisplayName() {
      return getPrefix() + "Get Version";
    }

  }

  public static class Execution extends JiraStepExecution<ResponseData<Version>> {

    private static final long serialVersionUID = 325576266548671174L;

    private final GetVersionStep step;

    protected Execution(final GetVersionStep step, final StepContext context)
        throws IOException, InterruptedException {
      super(context);
      this.step = step;
    }

    @Override
    protected ResponseData<Version> run() throws Exception {

      ResponseData<Version> response = verifyInput();

      if (response == null) {
        logger.println(
            "JIRA: Site - " + siteName + " - Querying Project Version with id:" + step.getId());
        response = jiraService.getVersion(step.getId());
      }

      return logResponse(response);
    }

    @Override
    protected <T> ResponseData<T> verifyInput() throws Exception {
      String errorMessage = null;
      ResponseData<T> response = verifyCommon(step);

      if (response == null) {
        if (step.getId() <= 0) {
          errorMessage = "id less than or equals to zero.";
        }

        if (errorMessage != null) {
          response = buildErrorResponse(new RuntimeException(errorMessage));
        }
      }
      return response;
    }
  }

  @Override
  public StepExecution start(StepContext context) throws Exception {
    return new Execution(this, context);
  }
}
