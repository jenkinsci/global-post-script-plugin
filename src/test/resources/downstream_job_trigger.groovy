def triggers = [
        wwwsqs8                : {
            manager.triggerJob("WWW_JBEHAVE_TEST")
            manager.triggerJob("WWW_MOBILE_API_TEST")
            manager.triggerRemoteJob("http://10.94.0.137:8006/job/Dev_Launch_WWW_SQS_REGRESSION/build?token=88e4b5fd1d28949710a9c4924775ce40&delay=1800sec")
        },
        wwwsqm8                : {
            manager.triggerRemoteJob("http://10.94.0.137:8006/job/Dev_Launch_WWW_SQM_REGRESSION/build?token=88e4b5fd1d28949710a9c4924775ce40&delay=1800sec")
        },
        wwwsqp8                : {
            manager.triggerRemoteJob("http://ecopsci.uschecomrnd.net/job/wwwsqp_deploy/build?token=wwwsqp_deploy")
        },
        bsdsqs8                : {
            manager.triggerJob("BSD_JBEHAVE_TEST")
            manager.triggerJob("BSD_MOBILE_API_TEST")
            manager.triggerRemoteJob("http://ecopsci.uschecomrnd.net/job/BSD_DEPLOY/buildWithParameters?token=BSD_DEPLOY&target=bsdsqs8")
            manager.triggerRemoteJob("http://10.94.0.137:8006/job/Dev_Launch_BSD_SQS_REGRESSION/build?token=88e4b5fd1d28949710a9c4924775ce40&delay=1800sec")
        },
        bsdsqm8                : {
            manager.triggerRemoteJob("http://ecopsci.uschecomrnd.net/job/BSD_DEPLOY/buildWithParameters?token=BSD_DEPLOY&target=bsdsqm8")
            manager.triggerRemoteJob("http://10.94.0.137:8006/job/Dev_Launch_BSD_SQM_REGRESSION/build?token=88e4b5fd1d28949710a9c4924775ce40&delay=1800sec")
        },
        bsdsqd8                : {
            manager.triggerRemoteJob("http://ecopsci.uschecomrnd.net/job/BSD_DEPLOY/buildWithParameters?token=BSD_DEPLOY&target=bsdsqd")
        },
        bsdsqe8                : {
            manager.triggerRemoteJob("http://ecopsci.uschecomrnd.net/job/BSD_DEPLOY/buildWithParameters?token=BSD_DEPLOY&target=bsdsqe8")
        },
        bsdsqf8                : {
            manager.triggerRemoteJob("http://ecopsci.uschecomrnd.net/job/BSD_DEPLOY/buildWithParameters?token=BSD_DEPLOY&target=bsdsqf8")
        },
        bsdsqp8                : {
            manager.triggerRemoteJob("http://ecopsci.uschecomrnd.net/job/BSD_DEPLOY/buildWithParameters?token=BSD_DEPLOY&target=bsdsqp8")
        },
        bsdperf8               : {
            manager.triggerRemoteJob("http://ecopsci.uschecomrnd.net/job/BSD_DEPLOY/buildWithParameters?token=BSD_DEPLOY&target=bsdperf8")
        },
        bsdprfp8               : {
            manager.triggerRemoteJob("http://ecopsci.uschecomrnd.net/job/BSD_DEPLOY/buildWithParameters?token=BSD_DEPLOY&target=bsdprfp")
        },
        bsd1sqs8               : {
            manager.triggerRemoteJob("http://ecopsci.uschecomrnd.net/job/BSD_DEPLOY/buildWithParameters?token=BSD_DEPLOY&target=bsd1sqs8")
        },
        bsd1sqm8               : {
            manager.triggerRemoteJob("http://ecopsci.uschecomrnd.net/job/BSD_DEPLOY/buildWithParameters?token=BSD_DEPLOY&target=bsd1sqm8")
        },
        bsd1sqp8               : {
            manager.triggerRemoteJob("http://ecopsci.uschecomrnd.net/job/BSD_DEPLOY/buildWithParameters?token=BSD_DEPLOY&target=bsd1sqp8")
        },
        gmlsqs8                : {
            manager.triggerJob("GMIL_JBEHAVE_TEST")
            manager.triggerRemoteJob("http://10.94.0.137:8006/job/Dev_Launch_GMIL_SQS_REGRESSION/build?token=88e4b5fd1d28949710a9c4924775ce40&delay=1800sec")
        },
        gmlsqm8                : {
            manager.triggerRemoteJob("http://10.94.0.137:8006/job/Dev_Launch_GMIL_SQM_REGRESSION/build?token=88e4b5fd1d28949710a9c4924775ce40&delay=1800sec")
        },
        odensqs8               : {
            manager.triggerRemoteJob("http://ecopsci.uschecomrnd.net/job/odnsqs_deploy/build?token=odnsqs_deploy")
        },
        odensqm8               : {
            manager.triggerRemoteJob("http://ecopsci.uschecomrnd.net/job/odnsqm_deploy/build?token=odnsqm_deploy")
        },
        odensqp8               : {
            manager.triggerRemoteJob("http://ecopsci.uschecomrnd.net/job/odnsqp_deploy/build?token=odnsqp_deploy")
        },
        cpdperf                : {
            manager.triggerRemoteJob("http://ecopsci.uschecomrnd.net/job/CPD_DEPLOY/buildWithParameters?token=CPD_DEPLOY&target=cpdperf")
        },
        cpdpoc                 : {
            manager.triggerRemoteJob("http://ecopsci.uschecomrnd.net/job/CPD_DEPLOY/buildWithParameters?token=CPD_DEPLOY&target=cpdpoc")
        },
        cpdprf_service         : {
            manager.triggerRemoteJob("http://ecopsci.uschecomrnd.net/job/CPD_DEPLOY/buildWithParameters?token=CPD_DEPLOY&target=cpdprf-service")
        },
        cpdsqs_configurator_www: {
            manager.triggerRemoteJob("http://ecopsci.uschecomrnd.net/job/CPD_DEPLOY/buildWithParameters?token=CPD_DEPLOY&target=cpdsqs-configurator-www")
        },
        cpdsqs_configurator_bsd: {
            manager.triggerRemoteJob("http://ecopsci.uschecomrnd.net/job/CPD_DEPLOY/buildWithParameters?token=CPD_DEPLOY&target=cpdsqs-configurator-bsd")
        },
        cpdsqs_service         : {
            manager.triggerRemoteJob("http://ecopsci.uschecomrnd.net/job/CPD_DEPLOY/buildWithParameters?token=CPD_DEPLOY&target=cpdsqs-service")
        },
        cpdsqm                 : {
            manager.triggerRemoteJob("http://ecopsci.uschecomrnd.net/job/CPD_DEPLOY/buildWithParameters?token=CPD_DEPLOY&target=cpdsqm")
        },
        cpdsqm_configurator    : {
            manager.triggerRemoteJob("http://ecopsci.uschecomrnd.net/job/CPD_DEPLOY/buildWithParameters?token=CPD_DEPLOY&target=cpdsqm-configurator")
        },
        cpdsqm_configurator_bsd: {
            manager.triggerRemoteJob("http://ecopsci.uschecomrnd.net/job/CPD_DEPLOY/buildWithParameters?token=CPD_DEPLOY&target=cpdsqm-configurator-bsd")
        },
        cpdsqm_service         : {
            manager.triggerRemoteJob("http://ecopsci.uschecomrnd.net/job/CPD_DEPLOY/buildWithParameters?token=CPD_DEPLOY&target=cpdsqm-service")
        },
        cpdsqp_configurator_www: {
            manager.triggerRemoteJob("http://ecopsci.uschecomrnd.net/job/CPD_DEPLOY/buildWithParameters?token=CPD_DEPLOY&target=cpdsqp-configurator-www")
        },
        cpdsqp_configurator_bsd: {
            manager.triggerRemoteJob("http://ecopsci.uschecomrnd.net/job/CPD_DEPLOY/buildWithParameters?token=CPD_DEPLOY&target=cpdsqp-configurator-bsd")
        },
        cpdsqp_service         : {
            manager.triggerRemoteJob("http://ecopsci.uschecomrnd.net/job/CPD_DEPLOY/buildWithParameters?token=CPD_DEPLOY&target=cpdsqp-service")
        },
        configurator_www_dev   : {
            manager.triggerRemoteJob("http://ecopsci.uschecomrnd.net/buildByToken/buildWithParameters?token=8a36b668396e7aed7b4576f90bbbdc37&job=sync_ag2content_to_dev&targets=chvwwwdevcmb01,chvwwwdevcmb02")
        },
        configurator_bsd_dev   : {
            manager.triggerRemoteJob("http://ecopsci.uschecomrnd.net/buildByToken/buildWithParameters?token=8a36b668396e7aed7b4576f90bbbdc37&job=sync_ag2content_to_dev&targets=chvbsddevcmb01,chvbsddevcmb02")
        },
        admdev                 : {
            manager.triggerRemoteJob("http://ecopsci.uschecomrnd.net/buildByToken/buildWithParameters?token=8a36b668396e7aed7b4576f90bbbdc37&job=sync_adam_build_to_s3&targets=admdev")
            Thread.sleep(60000)
            manager.triggerRemoteJob("http://awsecopsci.uschecomrnd.net/buildByToken/build?token=45ca521be27644a6d4b6f3eae4486e5a&job=deploy_adam_dev")
        },
        admsqs                 : {
            manager.triggerRemoteJob("http://ecopsci.uschecomrnd.net/buildByToken/buildWithParameters?token=8a36b668396e7aed7b4576f90bbbdc37&job=sync_adam_build_to_s3&targets=admsqs")
            Thread.sleep(60000)
            manager.triggerRemoteJob("http://awsecopsci.uschecomrnd.net/buildByToken/build?token=45ca521be27644a6d4b6f3eae4486e5a&job=deploy_adam_sqs")
        },
        admsqm                 : {
            manager.triggerRemoteJob("http://ecopsci.uschecomrnd.net/buildByToken/buildWithParameters?token=8a36b668396e7aed7b4576f90bbbdc37&job=sync_adam_build_to_s3&targets=admsqm")
            Thread.sleep(60000)
            manager.triggerRemoteJob("http://awsecopsci.uschecomrnd.net/buildByToken/build?token=45ca521be27644a6d4b6f3eae4486e5a&job=deploy_adam_sqm")
        },
        admsqp                 : {
            manager.triggerRemoteJob("http://ecopsci.uschecomrnd.net/buildByToken/buildWithParameters?token=8a36b668396e7aed7b4576f90bbbdc37&job=sync_adam_build_to_s3&targets=admsqp")
            Thread.sleep(60000)
            manager.triggerRemoteJob("http://awsecopsci.uschecomrnd.net/buildByToken/build?token=45ca521be27644a6d4b6f3eae4486e5a&job=deploy_adam_sqp")
        }
]


if (binding.variables.containsKey("deploy") && binding.variables.containsKey("deploy_targets") &&
        "true" == deploy && deploy_targets?.trim()) {
    manager.addBadge("computer.png", "[DEV: " + deploy_targets + "]")
    deploy_targets.split(',').each {
        String entry = it;
        trigger = triggers[entry.replaceAll(/\W/, "_")]
        if (trigger) {
            trigger()
        }
    }
}
if (binding.variables.containsKey("dropdeploy") && binding.variables.containsKey("dropdeploy_targets") &&
        "true" == dropdeploy && dropdeploy_targets?.trim()) {
    dropped = false
    dropdeploy_targets.split(',').each {
        dropped = true
        String entry = it;
        trigger = triggers[entry.replaceAll(/\W/, "_")]
        if (trigger) {
            trigger()
        }
    }
    if (dropped) {
        manager.addBadge("server.png", "[SQ: " + dropdeploy_targets + "]")
    }
}
