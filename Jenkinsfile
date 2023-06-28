

pipeline {
    agent { 
        docker { 
            image 'maven:3.3.3'
            args '-v /var/run/docker.sock:/var/run/docker.sock'
        } 
        

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