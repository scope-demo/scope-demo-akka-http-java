pipeline {
    agent none
    stages {
        stage('Execute Akka Java') {
            agent { docker 'openjdk:11-jdk' }
            steps {
                sh './mvnw clean verify -U -fae'
            }
        }
    }
}