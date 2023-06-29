

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
                sh "mv target/auth-course-0.0.1-SNAPSHOT.war target/ROOT.war"

                stash includes: 'target/auth-course-0.0.1-SNAPSHOT.war', name: 'myStash'

            }
        }

        stage('SSH transfer') {
            steps([$class: 'BapSshPromotionPublisherPlugin']) {
                sh "ls"
                sh "ls target"
                sshPublisher(
                    continueOnError: false, failOnError: true,
                    publishers: [
                        sshPublisherDesc(
                            configName: "Host2",
                            verbose: true,
                            transfers: [
                                sshTransfer(sourceFiles: "target/ROOT.war", remoteDirectory: "/docker-tomcat-server/data", removePrefix: "target")
                            ]

                        )
                    ]
                )
            }
        }

    }

    post {
        always {
            // Unstash the file(s) from the stash and move them to the artifacts folder
            unstash 'myStash'
            archiveArtifacts artifacts: 'target/ROOT.war', fingerprint: true
        }
    }
}