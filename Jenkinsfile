pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
        skipDefaultCheckout(true)
    }

    environment {
        APP_NAME = 'student-management'
        GCP_PROJECT_ID = 'java-cicd-devops'
        GCP_REGION = 'asia-south1'
        ARTIFACT_REPO = 'java-app-repo'
        IMAGE_TAG = "${env.BUILD_NUMBER}"
        IMAGE_URI = "${env.GCP_REGION}-docker.pkg.dev/${env.GCP_PROJECT_ID}/${env.ARTIFACT_REPO}/${env.APP_NAME}"
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

        stage('Maven Compile & Test') {
            steps {
                sh '''
                    export JAVA_HOME=$(/usr/libexec/java_home -v 25)
                    export PATH="$JAVA_HOME/bin:/opt/homebrew/bin:/usr/local/bin:$PATH"
                    mvn clean test package -DskipTests=false
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

        stage('GCP Authentication & Docker Login') {
            steps {
                sh '''
                    export PATH="/opt/homebrew/bin:/usr/local/bin:$PATH"
                    # Authenticate Docker to GCP Artifact Registry
                    gcloud auth configure-docker asia-south1-docker.pkg.dev --quiet
                '''
            }
        }

        stage('Cross-Arch Docker Build & Push') {
            steps {
                sh '''
                    export PATH="/opt/homebrew/bin:/usr/local/bin:$PATH"
                    
                    # Ensure buildx builder is active
                    docker buildx create --use --name multiarch-builder || true

                    # Build for linux/amd64 (GKE node architecture) and push to Artifact Registry
                    docker buildx build --platform linux/amd64 \
                        -t ${IMAGE_URI}:${IMAGE_TAG} \
                        -t ${IMAGE_URI}:latest \
                        --push .
                '''
            }
        }

        stage('Deploy to GKE') {
            steps {
                sh '''
                    # Explicitly include Homebrew and the exact Google Cloud SDK bin directory in PATH
                    export PATH="/opt/homebrew/bin:/usr/local/bin:/usr/bin:/bin:/usr/sbin:/sbin:/opt/homebrew/share/google-cloud-sdk/bin"
                    
                    # Update Kubernetes deployment manifest image tag dynamically
                    sed -i.bak "s|image: .*|image: ${IMAGE_URI}:${IMAGE_TAG}|g" k8s/deployment.yaml
                    
                    # Apply manifests to GKE cluster
                    kubectl apply --validate=false -f k8s/
                '''
            }
        }
    }

    post {
        success {
            echo 'CI/CD PIPELINE COMPLETED SUCCESSFULLY - APP DEPLOYED TO GKE!'
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