def triggers = [
        basepom  : {
            manager.triggerJob("basepom")
            manager.triggerRemoteJob("http://localhost:8080/job/basepom85/build?delay=0sec")
        },
        basepom85: {
            manager.triggerRemoteJob("http://localhost:8080/job/basepom85/build?delay=0sec")
        }
]

if ("true" == deploy && deploy_targets?.trim()) {
    manager.addBadge("computer.png", "[DEV: " + deploy_targets + "]")
}
if ("true" == dropdeploy && dropdeploy_targets?.trim()) {
    dropdeploy_targets.split(',').each {
        triggers[it]()
    }
    manager.addBadge("server.png", "[SQ: " + dropdeploy_targets + "]")
}