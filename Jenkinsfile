#!/usr/bin/env groovy

pipeline {
    agent any

    parameters {
        choice(name: 'ENV', choices: ['dev', 'test', 'prod'], description: 'Окружение')
    }

    tools {
        jdk 'jdk-21'
    }

    environment {
        // Общие
        K8S_NAMESPACE = "bank-${params.ENV}"
        HELM_CHART_DIR = 'k8s/helm'

        // Kafka
        KAFKA_NAMESPACE = 'kafka'
        KAFKA_HELM_RELEASE = 'kafka'
        KAFKA_VALUES_FILE = "infra/kafka/values-${params.ENV}.yaml"
    }

    stages {
        // === 1. Получение кода ===
        stage('Checkout') {
            steps {
                echo 'Получение кода из репозитория...'
                git branch: 'feature/k8s', url: 'https://github.com/alexanderkhakimov/bank-microservices.git'
            }
        }

        // === 2. Kafka: Установка ===
        stage('Deploy Kafka') {
            steps {
                echo "Развёртывание Kafka в ${KAFKA_NAMESPACE}..."

                sh """
                helm repo add bitnami https://charts.bitnami.com/bitnami --force-update
                helm repo update

                helm upgrade --install ${KAFKA_HELM_RELEASE} bitnami/kafka \
                  --namespace ${KAFKA_NAMESPACE} \
                  --create-namespace \
                  --wait --timeout=5m \
                  -f ${KAFKA_VALUES_FILE} \
                  --set auth.clientProtocol=plaintext
                """
            }
        }

        // === 3. Kafka: Ожидание готовности ===
        stage('Wait for Kafka') {
            steps {
                sh """
                echo "Ожидание запуска Kafka..."
                kubectl wait --for=condition=Ready pod -l app.kubernetes.io/name=kafka -n ${KAFKA_NAMESPACE} --timeout=3m
                """
            }
        }

        // === 4. Kafka: Создание топиков ===
        stage('Create Kafka Topics') {
            steps {
                sh """
                kubectl apply -f infra/kafka/topics/create-topics-job.yaml
                kubectl wait --for=condition=Complete job/kafka-create-topics -n ${KAFKA_NAMESPACE} --timeout=120s
                """
            }
        }

        // === 5. Проверка топиков ===
        stage('Verify Kafka Topics') {
            steps {
                sh """
                echo "Проверка топиков..."
                kubectl exec -n ${KAFKA_NAMESPACE} deploy/kafka -- \
                  bin/kafka-topics.sh --list --bootstrap-server localhost:9092 | grep -E "notifications|exchange-"
                """
            }
        }

        // === 6. Сборка микросервисов ===
        stage('Build and Test') {
            steps {
                echo 'Сборка проекта...'
                sh './gradlew build -x test --no-daemon --parallel'
            }
        }

        // === 7. Docker образы ===
        stage('Docker Build') {
            steps {
                echo 'Сборка Docker образов...'
                sh './gradlew dockerBuildAll --no-daemon'
            }
        }

        // === 8. Деплой микросервисов ===
        stage('Deploy Microservices') {
            steps {
                echo "Деплой в ${K8S_NAMESPACE}..."

                sh "kubectl create namespace $K8S_NAMESPACE --dry-run=client -o yaml | kubectl apply -f -"

                // Инфраструктура
                sh "kubectl apply -f k8s/postgresql/ -n $K8S_NAMESPACE"
                sh "kubectl apply -f k8s/keycloak/ -n $K8S_NAMESPACE"

                // Микросервисы
                sh """
                kubectl apply -f k8s/accounts-service/ -n $K8S_NAMESPACE
                kubectl apply -f k8s/blocker-service/ -n $K8S_NAMESPACE
                kubectl apply -f k8s/cash-service/ -n $K8S_NAMESPACE
                kubectl apply -f k8s/exchange-service/ -n $K8S_NAMESPACE
                kubectl apply -f k8s/exchange-generator-service/ -n $K8S_NAMESPACE
                kubectl apply -f k8s/notifications-service/ -n $K8S_NAMESPACE
                kubectl apply -f k8s/transfer-service/ -n $K8S_NAMESPACE
                kubectl apply -f k8s/front-ui-service/ -n $K8S_NAMESPACE
                """

                sh "kubectl apply -f k8s/ingress.yaml -n $K8S_NAMESPACE"
            }
        }

        // === 9. Ожидание сервисов ===
        stage('Wait for Services') {
            steps {
                echo 'Ожидание запуска сервисов...'
                sh """
                kubectl wait --for=condition=ready pod -l app=keycloak -n $K8S_NAMESPACE --timeout=180s
                kubectl wait --for=condition=ready pod -l app=exchange-service -n $K8S_NAMESPACE --timeout=120s
                kubectl wait --for=condition=ready pod -l app=exchange-generator-service -n $K8S_NAMESPACE --timeout=120s
                kubectl wait --for=condition=ready pod -l app=notifications-service -n $K8S_NAMESPACE --timeout=120s
                """
            }
        }

        // === 10. Health Check ===
        stage('Health Check') {
            steps {
                sh """
                echo "=== Статус подов ==="
                kubectl get pods -n $K8S_NAMESPACE

                echo "=== Доступ ==="
                echo "Frontend: kubectl port-forward -n $K8S_NAMESPACE service/front-ui-service 8080:8086"
                echo "Keycloak: kubectl port-forward -n $K8S_NAMESPACE service/keycloak 8180:8180"
                """
            }
        }
    }

    post {
        success {
            echo "Всё успешно: Kafka + микросервисы в ${params.ENV}!"
        }
        failure {
            echo "ОШИБКА!"
            sh """
            kubectl get pods -n $K8S_NAMESPACE
            kubectl get pods -n $KAFKA_NAMESPACE
            """
        }
        always {
            cleanWs()
        }
    }
}