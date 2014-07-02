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