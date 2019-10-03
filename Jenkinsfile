pipeline {
    agent any

    stages {
       stage('Build') {
            steps {
                sh 'echo build'
                sh 'COMMIT=${GIT_COMMIT} docker-compose -p ${GIT_COMMIT} build'
            }
        }

        stage('Test'){
            steps {
                sh 'echo test'
                sh 'COMMIT=${GIT_COMMIT} docker-compose -p ${GIT_COMMIT} up --exit-code-from=scope-demo-akka-http-java scope-demo-akka-http-java'
            }
        }
    }

    post {
        always {
            sh 'COMMIT=${GIT_COMMIT} docker-compose -p ${GIT_COMMIT} down -v'
        }
    }

}