
pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
        skipDefaultCheckout(true)
    }

    environment {
        APP_NAME = 'student-management'
    }

    stages {

        stage('Checkout') {
            steps {
                echo 'Checking out source code...'
                checkout scm
            }
        }

        stage('Tool Verification') {
            steps {
                sh '''
                    export JAVA_HOME=$(/usr/libexec/java_home -v 25)
                    export PATH="$JAVA_HOME/bin:/opt/homebrew/bin:/usr/local/bin:$PATH"

                    echo "Java version:"
                    java -version

                    echo "Maven version:"
                    mvn -version

                    echo "Gitleaks version:"
                    /opt/homebrew/bin/gitleaks version

                    echo "Docker version:"
                    docker --version
                '''
            }
        }

        stage('Gitleaks Scan') {
            steps {
                echo 'Scanning for exposed secrets...'
                sh '''
                    /opt/homebrew/bin/gitleaks git \
                    --verbose \
                    --redact
                '''
            }
        }

        stage('Maven Compile') {
            steps {
                sh '''
                    export JAVA_HOME=$(/usr/libexec/java_home -v 25)
                    export PATH="$JAVA_HOME/bin:/opt/homebrew/bin:/usr/local/bin:$PATH"
                    mvn clean compile
                '''
            }
        }

        stage('Unit Testing') {
            steps {
                sh '''
                    export JAVA_HOME=$(/usr/libexec/java_home -v 25)
                    export PATH="$JAVA_HOME/bin:/opt/homebrew/bin:/usr/local/bin:$PATH"
                    mvn test
                '''
            }
            post {
                always {
                    junit(
                        testResults: 'target/surefire-reports/*.xml',
                        allowEmptyResults: true
                    )
                }
            }
        }

        stage('Maven Package') {
            steps {
                sh '''
                    export JAVA_HOME=$(/usr/libexec/java_home -v 25)
                    export PATH="$JAVA_HOME/bin:/opt/homebrew/bin:/usr/local/bin:$PATH"
                    mvn package -DskipTests
                '''
            }
        }

        stage('Docker Build') {
            when {
                expression {
                    fileExists('Dockerfile')
                }
            }
            steps {
                sh '''
                    export PATH="/opt/homebrew/bin:/usr/local/bin:$PATH"
                    docker --version
                    docker build -t ${APP_NAME}:${BUILD_NUMBER} .
                '''
            }
        }

        stage('Docker Image Verification') {
            when {
                expression {
                    fileExists('Dockerfile')
                }
            }
            steps {
                sh '''
                    export PATH="/opt/homebrew/bin:/usr/local/bin:$PATH"
                    docker image inspect ${APP_NAME}:${BUILD_NUMBER}
                    docker images ${APP_NAME}
                '''
            }
        }
    }

    post {
        success {
            echo 'CI PIPELINE COMPLETED SUCCESSFULLY'
        }

        failure {
            echo 'PIPELINE FAILED - CHECK CONSOLE OUTPUT'
        }

        always {
            archiveArtifacts(
                artifacts: 'target/*.jar',
                allowEmptyArchive: true
            )
            echo 'Pipeline execution finished.'
        }
    }
}
