#!/usr/bin/env groovy

class ClusterInput implements Serializable {
    //Required
    String targetApp

    //Optional - Platform
    String clusterUrl = ""
    String clusterAPI = ""
    String clusterToken = ""
    String projectName = ""
    Integer loglevel = 0
}

// verify deployment
def call(Map input) {
    call(new ClusterInput(input))
}

def call(ClusterInput input) {
    assert input.targetApp?.trim(): "Param targetApp should be defined."

    openshift.loglevel(input.loglevel)

    if (input.clusterUrl?.trim().length() > 0) {
        error "clusterUrl is deprecated and will be removed in the next release. Please use 'clusterAPI'"
    }

<<<<<<< HEAD
    rollout([
        clusterAPI         : input.clusterAPI,
        clusterToken       : input.clusterToken,
        projectName        : input.projectName,
        resourceKindAndName: "dc/${input.targetApp}",
        latest             : false
    ])
=======
    openshift.withCluster(input.clusterAPI, input.clusterToken) {
        openshift.withProject(input.projectName) {
            echo "Attempting to verify 'deploymentconfig/${input.targetApp}' in ${openshift.project()}"
            def deploymentConfig = openshift.selector("dc", input.targetApp)
            def rolloutManager   = deploymentConfig.rollout()
            try {
                rolloutManager.status("--watch=true")
            }
            catch (e) {
                echo "Error verifying deployment: ${e}"
                rolloutManager.history()
                deploymentConfig.describe()
                throw e
            }
        }
    }
>>>>>>> Update verify to look at dc rollout status
}
