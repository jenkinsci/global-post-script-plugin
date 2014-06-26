## global-post-script-plugin

### Variables that could be used in the script file
| Variable | Description | Example |
| -------- | ----------- | ------ |
| BUILD_ID | Build timestamp as ID | 2014-06-26_07-16-51 |
| BUILD_NUMBER | Build No# | 16 |
| BUILD_TAG | Job Name + Build No# | jenkins-LOGANALYZER-16 |
| BUILD_URL | The URL of this build | http://localhost:8080/job/LOGANALYZER/16/ |
| JENKINS_HOME | The path of the root folder of Jenkins | ~/workspace-idea/global-post-script-plugin/./work |
| JENKINS_URL | The root URL of Jenkins | http://localhost:8080/ |
| JOB_NAME | Name of the job | LOGANALYZER |
| JOB_URL | URL of the job | http://localhost:8080/job/LOGANALYZER/ |
| MAVEN_CMD_LINE_ARGS | Maven command args | clean install |
| NODE_LABELS | Lables of the nodes where the build could be executed | master |
| NODE_NAME | Name of the node where the build executed | master |
| SVN_REVISION | SVN revision | 185214 |
| SVN_URL | SVN URL | https://ecomsvn.officedepot.com/svn/ECOM/trunk/tools/loganalyzer |
| WORKSPACE | The path of the workspace | ~/workspace-idea/global-post-script-plugin/work/workspace/LOGANALYZE |R| 