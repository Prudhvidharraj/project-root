pipeline {
    agent any

    environment {
        SONARQUBE_SERVER = 'SonarQube'
        SONAR_PROJECT_KEY = 'prudhvi-boot'
        ECR_REPO_URI = '448049787674.dkr.ecr.us-west-1.amazonaws.com/prudhvi-boot'
        AWS_REGION = 'us-west-1'
        TRIVY_IMAGE = 'aquasec/trivy:0.45.0' 
        RECIPIENT_EMAIL = 'nandhuraj0303@gmail.com'
    }

    stages {
        stage('Checkout') {
            steps {
                cleanWs()
                git(
                    url: 'https://github.com/Prudhvidharraj/project-root.git',
                    branch: 'main',
                    poll: false
                )
                sh 'pwd && ls -al && test -f pom.xml'
            }
        }

        stage('Build & Test') {
            steps {
                sh '''
                echo "##[debug] Workspace Contents:"
                ls -al
                mvn clean install -DskipTests=true -f pom.xml
                '''
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv(env.SONARQUBE_SERVER) {
                    sh """
                    mvn sonar:sonar \
                    -Dsonar.projectKey=${SONAR_PROJECT_KEY} \
                    -Dsonar.host.url=http://54.183.40.137:9000 \
                    -f pom.xml
                    """
                }
            }
        }

        stage('Build & Scan') {
            steps {
                script {
                    def dockerImage = docker.build(
                        "${ECR_REPO_URI}:${BUILD_NUMBER}",
                        "--file Dockerfile ."
                    )
                    dockerImage.tag('latest')
                    
                    sh(script: """
                        docker run --rm \
                        -v /var/run/docker.sock:/var/run/docker.sock \
                        ${TRIVY_IMAGE} \
                        --exit-code 1 \
                        image \
                        --severity HIGH,CRITICAL \
                        --quiet \
                        ${ECR_REPO_URI}:${BUILD_NUMBER}
                    """, returnStatus: true)
                }
            }
        }

        stage('Push to ECR') {
            steps {
                script {
                    docker.withRegistry("https://${ECR_REPO_URI}", 'ecr:us-west-1:aws-credentials') {
                        docker.image("${ECR_REPO_URI}:${BUILD_NUMBER}").push()
                        docker.image("${ECR_REPO_URI}:latest").push()
                    }
                }
            }
        }
    }

    post {
        always {
            sh 'docker system prune -af || true'
            cleanWs()
        }
        success {
            emailext(
                subject: "SUCCESS: ${currentBuild.fullDisplayName}",
                body: """<p>Build succeeded!</p>
                <p>ECR Image: ${ECR_REPO_URI}:${BUILD_NUMBER}</p>
                <p>SonarQube Report: <a href="http://54.183.40.137:9000/dashboard?id=${SONAR_PROJECT_KEY}">Link</a></p>
                <p>Build URL: <a href="${BUILD_URL}">${BUILD_URL}</a></p>""",
                to: "${RECIPIENT_EMAIL}",
                replyTo: "${RECIPIENT_EMAIL}",
                mimeType: 'text/html',
                recipientProviders: [[$class: 'DevelopersRecipientProvider']]
            )
        }
        failure {
            emailext(
                subject: "FAILED: ${currentBuild.fullDisplayName}",
                body: """<p>Build failed! Check logs:</p>
                <p><a href="${BUILD_URL}">${BUILD_URL}</a></p>""",
                to: "${RECIPIENT_EMAIL}",
                replyTo: "${RECIPIENT_EMAIL}",
                mimeType: 'text/html',
                attachLog: true,
                recipientProviders: [[$class: 'DevelopersRecipientProvider']]
            )
        }
    }
}