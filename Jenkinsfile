@Library('jenkins-shared-library@release') _

pipeline {
    agent {
        label 'podman'
    }

    tools {
        jdk 'JDK_1_8'
    }
    
    parameters {
        booleanParam(defaultValue: true, 
                     description: 'Flag whether quality checks should be performed.', 
                     name: 'RUN_QUALITY_CHECKS')
    }

    options {
        skipStagesAfterUnstable()
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '28'))
        timeout(time: 1, unit: 'HOURS')
    }

    triggers {
        // at least once a day
        cron('H H(0-7) * * *')
        pollSCM('H/15 * * * *')
    }

    stages {
        stage("SCM Checkout") {
            steps {
                notifyBitBucket state: "INPROGRESS"
                deleteDir()
                checkout scm
            }
        }

        stage("Maven") {
            steps {
                mavenbuild uploadArtifactsWithBranchnameInVersion: true
                
                script {
                    pomInfo = readMavenPom file: 'pom.xml'
                    currentBuild.description = "${pomInfo.version}"
                }
            }
        }

        stage("Quality assurance") {
            when {
                expression { return params.RUN_QUALITY_CHECKS }
            }

            steps {
                sonar()
            }
        }

        stage("Build Container") {
            steps{
                script {
                    currentBuild.description = GIT_COMMIT
                    pomInfo = readMavenPom file: 'pom.xml'
                }
                containerBuild registry: 'quay.balgroupit.com', repository: 'devopsselfservice/airlock-waf-eventlistener', tags: [GIT_COMMIT, pomInfo.version], dockerFileName: 'event-listener/src/main/resources/Dockerfile.eventlistener', path: '.'

            }

        }
    }
    post {
                success {
                    notifyBitBucket state: "SUCCESSFUL"
                }
                fixed {
                    mailTo status: "SUCCESS", actuator: true, recipients: [], logExtract: true
                }
                failure {
                    notifyBitBucket state: "FAILED"
                    mailTo status: "FAILURE", actuator: true, recipients: [], logExtract: true
                }
            }
}