def triggers = [
        wwwsqs8                : {
            manager.triggerJob("WWW_JBEHAVE_TEST")
            manager.triggerJob("WWW_MOBILE_API_TEST")
            manager.triggerRemoteJob("http://10.94.0.137:8006/job/Dev_Launch_WWW_SQS_REGRESSION/build?token=88e4b5fd1d28949710a9c4924775ce40&delay=1800sec")
        },
        wwwsqm8                : {
            manager.triggerRemoteJob("http://10.94.0.137:8006/job/Dev_Launch_WWW_SQM_REGRESSION/build?token=88e4b5fd1d28949710a9c4924775ce40&delay=1800sec")
        },
        bsdsqs8                : {
            manager.triggerJob("BSD_JBEHAVE_TEST")
            manager.triggerJob("BSD_MOBILE_API_TEST")
            manager.triggerRemoteJob("http://10.94.0.137:8006/job/Dev_Launch_BSD_SQS_REGRESSION/build?token=88e4b5fd1d28949710a9c4924775ce40&delay=1800sec")
        },
        bsdsqm8                : {
            manager.triggerRemoteJob("http://10.94.0.137:8006/job/Dev_Launch_BSD_SQM_REGRESSION/build?token=88e4b5fd1d28949710a9c4924775ce40&delay=1800sec")
        },
        gmlsqs8                : {
            manager.triggerJob("GMIL_JBEHAVE_TEST")
            manager.triggerRemoteJob("http://10.94.0.137:8006/job/Dev_Launch_GMIL_SQS_REGRESSION/build?token=88e4b5fd1d28949710a9c4924775ce40&delay=1800sec")
        },
        gmlsqm8                : {
            manager.triggerRemoteJob("http://10.94.0.137:8006/job/Dev_Launch_GMIL_SQM_REGRESSION/build?token=88e4b5fd1d28949710a9c4924775ce40&delay=1800sec")
        },
        cpdsqs_configurator_www: {
            manager.triggerRemoteJob("http://ecopsci.uschecomrnd.net/buildByToken/buildWithParameters?token=8a36b668396e7aed7b4576f90bbbdc37&job=sync_cpd_content_with_sq&targets=chvwwwsqscmb01,chvwwwsqscmb02")
        },
        cpdsqs_configurator_bsd: {
            manager.triggerRemoteJob("http://ecopsci.uschecomrnd.net/buildByToken/buildWithParameters?token=8a36b668396e7aed7b4576f90bbbdc37&job=sync_cpd_content_with_sq&targets=chvbsdsqscmb01,chvbsdsqscmb02")
        },
        cpdsqm_configurator    : {
            manager.triggerRemoteJob("http://ecopsci.uschecomrnd.net/buildByToken/buildWithParameters?token=8a36b668396e7aed7b4576f90bbbdc37&job=sync_cpd_content_with_sq&targets=chvwwwsqmcmb01,chvwwwsqmcmb02")
        },
        cpdsqm_configurator_bsd: {
            manager.triggerRemoteJob("http://ecopsci.uschecomrnd.net/buildByToken/buildWithParameters?token=8a36b668396e7aed7b4576f90bbbdc37&job=sync_cpd_content_with_sq&targets=chvbsdsqmcmb01,chvbsdsqmcmb02")
        },
        cpdsqp_configurator_www: {
            manager.triggerRemoteJob("http://ecopsci.uschecomrnd.net/buildByToken/buildWithParameters?token=8a36b668396e7aed7b4576f90bbbdc37&job=sync_cpd_content_with_sq&targets=chvwwwsqpcmb01,chvwwwsqpcmb02")
        },
        cpdsqp_configurator_bsd: {
            manager.triggerRemoteJob("http://ecopsci.uschecomrnd.net/buildByToken/buildWithParameters?token=8a36b668396e7aed7b4576f90bbbdc37&job=sync_cpd_content_with_sq&targets=chvbsdsqpcmb01,chvbsdsqpcmb02")
        },
        configurator_www_dev   : {
            manager.triggerRemoteJob("http://ecopsci.uschecomrnd.net/buildByToken/buildWithParameters?token=8a36b668396e7aed7b4576f90bbbdc37&job=sync_ag2content_to_dev&targets=chvwwwdevcmb01,chvwwwdevcmb02")
        },
        configurator_bsd_dev   : {
            manager.triggerRemoteJob("http://ecopsci.uschecomrnd.net/buildByToken/buildWithParameters?token=8a36b668396e7aed7b4576f90bbbdc37&job=sync_ag2content_to_dev&targets=chvbsddevcmb01,chvbsddevcmb02")
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
        String entry = it;
        trigger = triggers[entry.replaceAll(/\W/, "_")]
        if (trigger) {
            trigger()
            dropped = true
        }
    }
    if (dropped) {
        manager.addBadge("server.png", "[SQ: " + dropdeploy_targets + "]")
    }
}

