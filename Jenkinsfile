pipeline {
  agent any

  tools {
    jdk 'jdk21'
    maven 'maven3.9'
  }

  environment {
    // Prevent Flyway from needing a real DB during CI scans
    MAVEN_OPTS = '-Dmaven.test.failure.ignore=false'
  }

  options {
    skipDefaultCheckout()
    buildDiscarder(logRotator(numToKeepStr: '20'))
    timestamps()
  }

  triggers {
    githubPush()
  }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Version Stamp') {
      steps {
        script {
          def timestamp = new Date().format("yyyyMMddHHmmss")
          env.APP_VERSION = "1.0.${env.BUILD_NUMBER}-${timestamp}"
          def command = "mvn versions:set -DnewVersion=${env.APP_VERSION} -DgenerateBackupPoms=false"

          if (isUnix()) {
            sh "cd maavooripachadi-backend && ${command}"
          } else {
            bat "cd /d maavooripachadi-backend && ${command}"
          }

          echo "Backend version set to ${env.APP_VERSION}"
        }
      }
    }

    stage('Flyway Repair & Migrate') {
      steps {
        script {
          if (isUnix()) {
            sh """
              cd maavooripachadi-backend
              mvn flyway:repair
              mvn flyway:migrate
            """
          } else {
            bat """
              cd /d maavooripachadi-backend
              mvn flyway:repair
              mvn flyway:migrate
            """
          }
        }
      }
    }

    stage('Backend Build & Test') {
      steps {
        script {
          def command = 'mvn clean install'
          if (isUnix()) {
            sh "cd maavooripachadi-backend && ${command}"
          } else {
            bat "cd /d maavooripachadi-backend && ${command}"
          }
        }
      }
    }

    stage('SonarQube Analysis') {
      when {
        expression { return env.SONARQUBE_ENABLED?.toBoolean() ?: true }
      }
      steps {
        withSonarQubeEnv('local-sonar') {
          script {
            def sonarCommand = [
              'mvn',
              'sonar:sonar',
              "-Dsonar.projectKey=Maavooripachadi",
              "-Dsonar.projectName=Maavooripachadi",
              "-Dsonar.token=${SONAR_AUTH_TOKEN}"
            ].join(' ')

            if (isUnix()) {
              sh "cd maavooripachadi-backend && ${sonarCommand}"
            } else {
              bat "cd /d maavooripachadi-backend && ${sonarCommand}"
            }
          }
        }
      }
    }

    stage('Quality Gate') {
      when {
        allOf {
          expression { return env.SONARQUBE_ENABLED?.toBoolean() ?: true }
          not { changeRequest() }
        }
      }
      steps {
        timeout(time: 10, unit: 'MINUTES') {
          script {
            def gate = waitForQualityGate()
            if (gate.status != 'OK') {
              error "Quality gate failed with status: ${gate.status}"
            }
            echo "Quality gate status: ${gate.status}"
          }
        }
      }
    }

    stage('Backend Package') {
      steps {
        script {
          def command = 'mvn -DskipTests package'
          if (isUnix()) {
            sh "cd maavooripachadi-backend && ${command}"
          } else {
            bat "cd /d maavooripachadi-backend && ${command}"
          }
        }
      }
    }
  }

  post {
    success {
      script {
        def backendTarget = 'maavooripachadi-backend/target'
        if (fileExists(backendTarget)) {
          dir('maavooripachadi-backend') {
            archiveArtifacts artifacts: 'target/*.jar', fingerprint: true, onlyIfSuccessful: true
          }
        } else {
          echo "Skipping artifact archive because ${backendTarget} was not found."
        }
      }
    }
    always {
      cleanWs()
    }
  }
}
