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
        MONITORING_NAMESPACE = "monitoring-${params.ENV}"
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

        // === 2. Сборка микросервисов ===
        stage('Build and Test') {
            steps {
                echo 'Сборка проекта...'
                sh './gradlew build -x test --no-daemon --parallel'
            }
        }

        // === 3. Docker образы ===
        stage('Docker Build') {
            steps {
                echo 'Сборка Docker образов...'
                sh './gradlew dockerBuildAll --no-daemon'
            }
        }

        // === 4. Kafka: Установка ===
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

        // === 5. Kafka: Ожидание готовности ===
        stage('Wait for Kafka') {
            steps {
                sh """
                echo "Ожидание запуска Kafka..."
                kubectl wait --for=condition=Ready pod -l app.kubernetes.io/name=kafka -n ${KAFKA_NAMESPACE} --timeout=3m
                """
            }
        }

        // === 6. Kafka: Создание топиков ===
        stage('Create Kafka Topics') {
            steps {
                sh """
                kubectl apply -f infra/kafka/topics/create-topics-job.yaml
                kubectl wait --for=condition=Complete job/kafka-create-topics -n ${KAFKA_NAMESPACE} --timeout=120s
                """
            }
        }

        // === 7. Деплой стека мониторинга ===
        stage('Deploy Monitoring Stack') {
            steps {
                echo "Развёртывание стека мониторинга в ${MONITORING_NAMESPACE}..."

                sh """
                kubectl create namespace ${MONITORING_NAMESPACE} --dry-run=client -o yaml | kubectl apply -f -
                """

                // Zipkin
                sh """
                helm upgrade --install zipkin ./zipkin \
                  --namespace ${MONITORING_NAMESPACE} \
                  --wait --timeout=3m
                """

                // Prometheus
                sh """
                helm upgrade --install prometheus ./prometheus \
                  --namespace ${MONITORING_NAMESPACE} \
                  --wait --timeout=3m
                """

                // Grafana
                sh """
                helm upgrade --install grafana ./grafana \
                  --namespace ${MONITORING_NAMESPACE} \
                  --wait --timeout=3m
                """

                // ELK Stack
                sh """
                helm upgrade --install elasticsearch ./elasticsearch \
                  --namespace ${MONITORING_NAMESPACE} \
                  --wait --timeout=5m
                """

                sh """
                helm upgrade --install kibana ./kibana \
                  --namespace ${MONITORING_NAMESPACE} \
                  --wait --timeout=3m
                """

                sh """
                helm upgrade --install logstash ./logstash \
                  --namespace ${MONITORING_NAMESPACE} \
                  --wait --timeout=3m
                """
            }
        }

        // === 8. Деплой инфраструктуры ===
        stage('Deploy Infrastructure') {
            steps {
                echo "Деплой инфраструктуры в ${K8S_NAMESPACE}..."

                sh "kubectl create namespace $K8S_NAMESPACE --dry-run=client -o yaml | kubectl apply -f -"

                // Инфраструктура через Helm (консистентность!)
                sh """
                helm upgrade --install postgresql ./postgresql \
                  --namespace ${K8S_NAMESPACE} \
                  --wait --timeout=3m
                """

                sh """
                helm upgrade --install keycloak ./keycloak \
                  --namespace ${K8S_NAMESPACE} \
                  --wait --timeout=3m
                """
            }
        }

        // === 9. Деплой микросервисов ===
        stage('Deploy Microservices') {
            steps {
                echo "Деплой микросервисов в ${K8S_NAMESPACE}..."

                // Все микросервисы через Helm для консистентности
                sh """
                helm upgrade --install accounts-service ./accounts-service \
                  --namespace ${K8S_NAMESPACE} \
                  --wait --timeout=3m

                helm upgrade --install blocker-service ./blocker-service \
                  --namespace ${K8S_NAMESPACE} \
                  --wait --timeout=3m

                helm upgrade --install cash-service ./cash-service \
                  --namespace ${K8S_NAMESPACE} \
                  --wait --timeout=3m

                helm upgrade --install exchange-service ./exchange-service \
                  --namespace ${K8S_NAMESPACE} \
                  --wait --timeout=3m

                helm upgrade --install exchange-generator-service ./exchange-generator-service \
                  --namespace ${K8S_NAMESPACE} \
                  --wait --timeout=3m

                helm upgrade --install notifications-service ./notifications-service \
                  --namespace ${K8S_NAMESPACE} \
                  --wait --timeout=3m

                helm upgrade --install transfer-service ./transfer-service \
                  --namespace ${K8S_NAMESPACE} \
                  --wait --timeout=3m

                helm upgrade --install front-ui-service ./front-ui-service \
                  --namespace ${K8S_NAMESPACE} \
                  --wait --timeout=3m
                """
            }
        }

        // === 10. Ingress ===
        stage('Deploy Ingress') {
            steps {
                sh "kubectl apply -f k8s/ingress.yaml -n $K8S_NAMESPACE"
            }
        }

        // === 11. Health Check ===
        stage('Health Check') {
            steps {
                sh """
                echo "=== Статус подов ==="
                kubectl get pods -n $K8S_NAMESPACE
                kubectl get pods -n $MONITORING_NAMESPACE

                echo "=== Мониторинг ==="
                echo "Prometheus: kubectl port-forward -n $MONITORING_NAMESPACE service/prometheus 9090:9090"
                echo "Grafana: kubectl port-forward -n $MONITORING_NAMESPACE service/grafana 3000:3000"
                echo "Zipkin: kubectl port-forward -n $MONITORING_NAMESPACE service/zipkin 9411:9411"
                echo "Kibana: kubectl port-forward -n $MONITORING_NAMESPACE service/kibana 5601:5601"
                """
            }
        }
    }

    post {
        success {
            echo "Всё успешно: Kafka + микросервисы + мониторинг в ${params.ENV}!"
        }
        failure {
            echo "ОШИБКА!"
            sh """
            kubectl get pods -n $K8S_NAMESPACE
            kubectl get pods -n $MONITORING_NAMESPACE
            kubectl get pods -n $KAFKA_NAMESPACE
            """
        }
        always {
            cleanWs()
        }
    }
}