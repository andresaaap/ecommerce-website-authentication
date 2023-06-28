

pipeline {
    agent any

    stages {

        stage('Build') {
            steps {
                // build the maven project
                sh 'mvn clean install'
                sh "ls"
            }
        }

    }
}