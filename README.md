# Deprecated
This project has been moved to [jenkinsci/global-post-script-plugin!](https://github.com/jenkinsci/global-post-script-plugin)

# global-post-script-plugin

Execute a global configured groovy/python script after each build of each job managed by the Jenkins

## Variables that could be used in the script file
| Variable | Description | Sample Data |
| -------- | ----------- | ------ |
| BUILD_ID | Build timestamp as ID | 2014-06-26_07-16-51 |
| BUILD_NUMBER | Build No# | 16 |
| BUILD_TAG | Job Name + Build No# | jenkins-test-job-16 |
| BUILD_URL | The URL of this build | http://localhost:8080/job/test-job/16/ |
| JENKINS_HOME | The path of the root folder of Jenkins | ~/workspace-idea/global-post-script-plugin/./work |
| JENKINS_URL | The root URL of Jenkins | http://localhost:8080/ |
| JOB_NAME | Name of the job | test-job |
| JOB_URL | URL of the job | http://localhost:8080/job/test-job/ |
| MAVEN_CMD_LINE_ARGS | Maven command args | clean install |
| NODE_LABELS | Lables of the nodes where the build could be executed | master |
| NODE_NAME | Name of the node where the build executed | master |
| SVN_REVISION | SVN revision | 185214 |
| SVN_URL | SVN URL |  |
| WORKSPACE | The path of the workspace | ~/workspace-idea/global-post-script-plugin/work/workspace/LOGANALYZE |

### Extra variables
Parameters of `parameterized build` or parameters been passed in by `-Dparameter_name=parameter_value` are also available

## Groovy Script
Variables can be used in two ways:
 * `$variable` quoted as string, will be replaced by avaialbe variable's value, will keep not changed if no variable matches.
 * groovy variables

### `manager`
An extra object is available as groovy variables: `manager`, provided 4 methods:

| Method | Description |
| -------- | ----------- |
| `addBadge(String icon, String text)` | Add a badge to the build |
| `addShortText(String text)` | Add a text label to the build |
| `triggerJob(String jobName)` | Trigger a job managed by the same Jenkins |
| `triggerRemoteJob(String url)` | Trigger a job by URL |

### Extra variables
Should be checked if available before use, by 
```groovy
binding.variables.containsKey("variable_name")
```

### Sample
```groovy
def triggers = [
        wwwsqs8: {
            manager.triggerJob("WWW_JBEHAVE_TEST")
            manager.triggerJob("WWW_MOBILE_API_TEST")
            manager.triggerRemoteJob("http://localhost/job/Dev_Launch_WWW_SQS_REGRESSION/build?token=88e4b5fd1d28949710a9c4924775ce40&delay=1800sec")
        },
        wwwsqm8: {
            manager.triggerRemoteJob("http://localhost/job/Dev_Launch_WWW_SQM_REGRESSION/build?token=88e4b5fd1d28949710a9c4924775ce40&delay=1800sec")
        },
        bsdsqs8: {
            manager.triggerJob("BSD_JBEHAVE_TEST")
            manager.triggerJob("BSD_MOBILE_API_TEST")
            manager.triggerRemoteJob("http://localhost/job/Dev_Launch_BSD_SQS_REGRESSION/build?token=88e4b5fd1d28949710a9c4924775ce40&delay=1800sec")
        },
        bsdsqm8: {
            manager.triggerRemoteJob("http://localhost/job/Dev_Launch_BSD_SQM_REGRESSION/build?token=88e4b5fd1d28949710a9c4924775ce40&delay=1800sec")
        },
        gmlsqs8: {
            manager.triggerJob("GMIL_JBEHAVE_TEST")
            manager.triggerRemoteJob("http://localhost/job/Dev_Launch_GMIL_SQS_REGRESSION/build?token=88e4b5fd1d28949710a9c4924775ce40&delay=1800sec")
        },
        gmlsqm8: {
            manager.triggerRemoteJob("http://localhost/job/Dev_Launch_GMIL_SQM_REGRESSION/build?token=88e4b5fd1d28949710a9c4924775ce40&delay=1800sec")
        },
        basepom: {
            manager.triggerJob("basepom")
        }
]

if (binding.variables.containsKey("deploy") && binding.variables.containsKey("deploy_targets") &&
        "true" == deploy && deploy_targets?.trim()) {
    manager.addBadge("computer.png", "[DEV: " + deploy_targets + "]")
}
if (binding.variables.containsKey("dropdeploy") && binding.variables.containsKey("dropdeploy_targets") &&
        "true" == dropdeploy && dropdeploy_targets?.trim()) {
    dropped = false
    dropdeploy_targets.split(',').each {
        trigger = triggers[it]
        if (trigger) {
            trigger()
            dropped = true
        }
    }
    if (dropped) {
        manager.addBadge("server.png", "[SQ: " + dropdeploy_targets + "]")
    }
}
```

## Python Script
Variables only can be used as `$variable` as string, will be replaced by avaialbe variable's value, will keep not changed if no variable matches.

### Sample
```python
dropdeploy_targets = '$dropdeploy_targets'
print 'dropdeploy to: ' + dropdeploy_targets
```