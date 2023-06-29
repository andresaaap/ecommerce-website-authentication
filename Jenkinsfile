

pipeline {
    agent { 
        docker { 
            image 'maven:3.3.3'
        } 
    }

    stages {

        stage('Build') {
            steps {
                // java -version
                sh 'java -version'
                // build the maven project
                sh 'mvn clean install'
                sh "ls"
                sh "ls target"
            }
        }

        stage('SSH transfer') {
            steps([$class: 'BapSshPromotionPublisherPlugin']) {
                sh "ls"
                sshPublisher(
                    continueOnError: false, failOnError: true,
                    publishers: [
                        sshPublisherDesc(
                            configName: "Host2",
                            verbose: true,
                            transfers: [
                                sshTransfer(sourceFiles: "./target/auth-course-0.0.1-SNAPSHOT.war",)
                            ],
                            remoteDirectory: "/home/ec2-user/docker-tomcat-server/data"
                        )
                    ]
                )
            }
        }

    }
}