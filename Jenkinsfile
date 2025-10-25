pipeline {
  agent any

  tools {
    jdk 'jdk17'
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

  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Backend Build & Test') {
      steps {
        script {
          def command = 'mvn clean verify -Dflyway.skip=true'
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
              "-Dsonar.projectName=Maavooripachadi"
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
        timeout(time: 5, unit: 'MINUTES') {
          script {
            def gate = waitForQualityGate abortPipeline: true, installationName: 'local-sonar'
            echo "Quality gate status: ${gate.status}"
          }
        }
      }
    }
  }

  post {
    always {
      cleanWs()
    }
  }
}
