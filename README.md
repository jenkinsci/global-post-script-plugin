# global-post-script-plugin [![Build Status](https://jenkins.ci.cloudbees.com/buildStatus/icon?job=plugins/global-post-script-plugin)](https://jenkins.ci.cloudbees.com/job/plugins/job/global-post-script-plugin/)
Execute a global configured groovy/python script after each build of each job managed by the Jenkins

See also: https://wiki.jenkins-ci.org/display/JENKINS/Global+Post+Script+Plugin

## Variables that could be used in the script file
### Jenkins Built-in Variables
| Variable | Description | Sample Data |
| -------- | ----------- | ------ |
| BUILD_ID | Build timestamp as ID | 2014-06-26_07-16-51 |
| BUILD_NUMBER | Build No# | 16 |
| BUILD_RESULT | Build result | SUCCESS / UNSTABLE / FAILURE ... |
| BUILD_TAG | Job Name + Build No# | jenkins-test-job-16 |
| BUILD_URL | The URL of this build | http://localhost:8080/job/test-job/16/ |
| JENKINS_HOME | The path of the root folder of Jenkins | ~/workspace-idea/global-post-script-plugin/./work |
| JENKINS_URL | The root URL of Jenkins | http://localhost:8080/ |
| JOB_NAME | Name of the job | test-job |
| JOB_URL | URL of the job | http://localhost:8080/job/test-job/ |
| MAVEN_CMD_LINE_ARGS | Maven command args | clean install |
| NODE_LABELS | Lables of the nodes where the build could be executed | master |
| NODE_NAME | Name of the node where the build executed | master |
| SVN_REVISION | SVN redeploy_targets?.trivision | 185214 |
| SVN_URL | SVN URL |  |
| WORKSPACE | The path of the workspace | deploy_targets?.tri~/workspace-idea/global-post-script-plugin/work/workspace/LOGANALYZE |

### Extra variables
Parameters of `parameterized build` or parameters been passed in by `-Dparameter_name=parameter_value` are also available

### `manager`
An extra object is available as groovy variables: `manager`, provided 4 methods:

| Method | Description |
| -------- | ----------- |
| `isVar(String name)` | Check if a variable is defined and usable in the script |
| `isNotBlankVar(String name)` | Check if a variable is defined and usable in the script, and with a non-blank value |
| `addBadge(String icon, String text)` | Add a badge to the build |
| `addShortText(String text)` | Add a text label to the build |
| `triggerJob(String jobName)` | Trigger a job managed by the same Jenkins |
| `triggerJob(String jobName, HashMap params)` | Trigger a job managed by the same Jenkins with parameters|
| `triggerRemoteJob(String url)` | Trigger a job by URL |

## Supported Scripts
### Groovy
Sample:
```groovy
out.println("deploy to: $deploy_targets")
```

Sample:
```groovy
out.println("deploy to: " + deploy_targets)
```

Sample:
```groovy
if (binding.variables.containsKey("variable_name")) {
    ...
}
```

Sample:
```groovy
def triggers = [
        wwwsqs8: {
            def params = [
                PARENT_BUILD_NUMBER: '$BUILD_NUMBER',
                PARENT_JOB_NAME: '$JOB_NAME',
                any_param_name: '$deploy_targets'
            ]
            manager.triggerJob("WWW_JBEHAVE_TEST", params)
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

if (manager.isVar("deploy") && manager.isNotBlankVar("deploy_targets") && "true" == deploy) {
    dropped = false
    deploy_targets.split(',').each {
        trigger = triggers[it]
        if (trigger) {
            trigger()
            dropped = true
        }
    }
    if (dropped) {
        manager.addBadge("server.png", "[SQ: " + deploy_targets + "]")
    }
}
```

### Python (Jython)
Sample:
```python
print 'deploy to: ' + deploy_targets + ", " +  manager.getCause()
```

Sample:
```python
if 'variable_name' in locals():
    ...
```

Sample:
```python
str = 'deploy to: '
if manager.isVar('deploy_targets'):
    str += deploy_targets
str += ", " + manager.getCause()
print str
```

### bat/sh
**NO** variables will passed into the script
