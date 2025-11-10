pipeline {
  agent any

  environment {
    REGISTRY = 'docker.io'               
    DOCKER_IMAGE_PREFIX = 'santi1761'   
    VERSION = "0.1.${env.BUILD_NUMBER}"
  }

  stages {
    stage('Checkout') {
      steps { checkout scm }
    }

    stage('Build & Test') {
      steps {
        sh './mvnw -B -DskipTests=false test'
      }
      post {
        always { junit '**/target/surefire-reports/*.xml' }
      }
    }

    stage('Package (opcional)') {
      steps { sh './mvnw -B -DskipTests -Pprod package' }
    }

    stage('Build Images') {
      parallel {
        stage('user-service') {
          steps {
            sh "docker build -t $DOCKER_IMAGE_PREFIX/user-service-ecommerce-boot:${VERSION} user-service"
          }
        }
        stage('product-service') {
          steps {
            sh "docker build -t $DOCKER_IMAGE_PREFIX/product-service-ecommerce-boot:${VERSION} product-service"
          }
        }
        stage('order-service') {
          steps {
            sh "docker build -t $DOCKER_IMAGE_PREFIX/order-service-ecommerce-boot:${VERSION} order-service"
          }
        }
        stage('api-gateway') {
          steps {
            sh "docker build -t $DOCKER_IMAGE_PREFIX/api-gateway-ecommerce-boot:${VERSION} api-gateway"
          }
        }
        stage('service-discovery') {
          steps {
            sh "docker build -t $DOCKER_IMAGE_PREFIX/service-discovery-ecommerce-boot:${VERSION} service-discovery"
          }
        }
        stage('cloud-config') {
          steps {
            sh "docker build -t $DOCKER_IMAGE_PREFIX/cloud-config-ecommerce-boot:${VERSION} cloud-config"
          }
        }
        stage('proxy-client') {
          steps {
            sh "docker build -t $DOCKER_IMAGE_PREFIX/proxy-client-ecommerce-boot:${VERSION} proxy-client"
          }
        }
      }
    }

    stage('Login & Push') {
      steps {
        withCredentials([usernamePassword(credentialsId: 'dockerhub-creds', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
          sh "echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin"
        }
        sh """
          docker images --format '{{.Repository}}:{{.Tag}}' | grep $DOCKER_IMAGE_PREFIX | grep ${VERSION} | xargs -I {} docker push {}
        """
      }
    }

    stage('Deploy to Minikube (dev)') {
      when { expression { fileExists('k8s/namespaces.yaml') } }
      steps {
        sh """
          kubectl apply -f k8s/namespaces.yaml
          kubectl -n dev apply -f k8s/infra/zipkin.yaml
          kubectl -n dev apply -f k8s/infra/eureka.yaml
          kubectl -n dev apply -f k8s/infra/config-server.yaml
          kubectl -n dev apply -f k8s/apps/user-service.yaml
          kubectl -n dev apply -f k8s/apps/product-service.yaml
          kubectl -n dev apply -f k8s/apps/order-service.yaml
          kubectl -n dev apply -f k8s/apps/proxy-client.yaml
          kubectl -n dev apply -f k8s/apps/api-gateway.yaml
        """
      }
    }
  }
}
