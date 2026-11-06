
pipeline {
    agent any

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

        stage('Gitleaks Scan') {
            steps {
                echo 'Scanning repository for exposed secrets...'
                sh '''
                    gitleaks detect \
                    --source . \
                    --verbose \
                    --redact
                '''
            }
        }

        stage('Maven Compile') {
            steps {
                echo 'Compiling Java application...'
                sh 'mvn clean compile'
            }
        }

        stage('Unit Testing') {
            steps {
                echo 'Executing unit tests...'
                sh 'mvn test'
            }
        }

        stage('Maven Package') {
            steps {
                echo 'Packaging Java application...'
                sh 'mvn package -DskipTests'
            }
        }

        stage('Docker Build') {
            steps {
                echo 'Docker image build will be configured...'
                sh 'docker --version'
                sh 'docker build -t ${APP_NAME}:${BUILD_NUMBER} .'
            }
        }

        stage('Docker Image Verification') {
            steps {
                sh 'docker images ${APP_NAME}'
            }
        }
    }

    post {
        success {
            echo 'CI Pipeline completed successfully!'
        }

        failure {
            echo 'Pipeline failed. Check the failed stage.'
        }

        always {
            echo 'Pipeline execution finished.'
        }
    }
}
