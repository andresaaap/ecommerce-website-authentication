

pipeline {
    agent { docker { image 'maven:3.3.3' } }

    stages {

        stage('Build') {
            steps {
                // java -version
                sh 'java -version'
                // build the maven project
                sh 'mvn clean install'
                sh "ls"
            }
        }

    }
}