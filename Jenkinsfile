
pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
        skipDefaultCheckout(true)
    }

    environment {
        APP_NAME = 'student-management'

        // Add Homebrew locations to Jenkins PATH
        PATH+LOCAL_TOOLS = '/opt/homebrew/bin:/usr/local/bin'
    }

    stages {

        // STAGE 1: Download source code from GitHub
        stage('Checkout') {
            steps {
                echo 'Checking out source code from GitHub...'
                checkout scm
            }
        }

        // STAGE 2: Verify required tools
        stage('Tool Verification') {
            steps {
                sh '''
                    echo "Checking Java installation..."

                    export JAVA_HOME=$(/usr/libexec/java_home -v 25)
                    export PATH="$JAVA_HOME/bin:/opt/homebrew/bin:/usr/local/bin:$PATH"

                    java -version

                    echo "Checking Maven..."
                    command -v mvn
                    mvn -version

                    echo "Checking Gitleaks..."
                    command -v gitleaks
                    gitleaks version

                    echo "Tool verification completed."
                '''
            }
        }

        // STAGE 3: Scan repository for exposed secrets
        stage('Gitleaks Scan') {
            steps {
                echo 'Scanning repository for exposed secrets...'

                sh '''
                    gitleaks git \
                    --verbose \
                    --redact
                '''
            }
        }

        // STAGE 4: Compile Java application
        stage('Maven Compile') {
            steps {
                echo 'Compiling Java application...'

                sh '''
                    export JAVA_HOME=$(/usr/libexec/java_home -v 25)
                    export PATH="$JAVA_HOME/bin:/opt/homebrew/bin:/usr/local/bin:$PATH"

                    mvn clean compile
                '''
            }
        }

        // STAGE 5: Execute unit tests
        stage('Unit Testing') {
            steps {
                echo 'Executing automated unit tests...'

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

        // STAGE 6: Generate executable JAR
        stage('Maven Package') {
            steps {
                echo 'Packaging Java application...'

                sh '''
                    export JAVA_HOME=$(/usr/libexec/java_home -v 25)
                    export PATH="$JAVA_HOME/bin:/opt/homebrew/bin:/usr/local/bin:$PATH"

                    mvn package -DskipTests
                '''
            }
        }

        // STAGE 7: Build Docker image
        stage('Docker Build') {
            when {
                expression {
                    fileExists('Dockerfile')
                }
            }

            steps {
                echo 'Building Docker image...'

                sh '''
                    docker --version

                    docker build \
                    -t ${APP_NAME}:${BUILD_NUMBER} .
                '''
            }
        }

        // STAGE 8: Verify Docker image
        stage('Docker Image Verification') {
            when {
                expression {
                    fileExists('Dockerfile')
                }
            }

            steps {
                echo 'Verifying Docker image...'

                sh '''
                    docker image inspect \
                    ${APP_NAME}:${BUILD_NUMBER}

                    docker images ${APP_NAME}
                '''
            }
        }
    }

    post {

        success {
            echo '===================================='
            echo 'CI PIPELINE COMPLETED SUCCESSFULLY'
            echo '===================================='
        }

        failure {
            echo '===================================='
            echo 'PIPELINE FAILED'
            echo 'Check the failed stage in Console Output'
            echo '===================================='
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
