pipeline{
    agent any

    tools{
        jdk 'jdk-21'
    }

    environment {
        K8S_NAMESPACE = 'bank-dev'
        HELM_CHART_DIR = 'k8s/helm'
    }

    stages{
        stage('Checkout'){
            steps{
                echo 'Получение кода из репозитория...'
                git branch: 'feature/k8s', url: 'https://github.com/alexanderkhakimov/bank-microservices.git'
            }
        }

        stage('Build and Test') {
            steps {
                echo 'Сборка проекта...'
                sh './gradlew build -x test --no-daemon --parallel'
            }
        }

        stage('Docker Build') {
            steps {
                echo 'Сборка Docker образов...'
                sh './gradlew dockerBuildAll --no-daemon'
            }
        }

        stage('K8s Deploy') {
            steps {
                echo 'Деплой в локальный Kubernetes...'


                sh "kubectl create namespace $K8S_NAMESPACE --dry-run=client -o yaml | kubectl apply -f -"

                sh """
                kubectl apply -f k8s/postgresql/ -n $K8S_NAMESPACE
                """

                sh """
                kubectl apply -f k8s/keycloak/ -n $K8S_NAMESPACE
                """

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

                sh """
                kubectl apply -f k8s/ingress.yaml -n $K8S_NAMESPACE
                """
            }
        }

        stage('Wait for Services') {
            steps {
                echo 'Ожидание запуска всех сервисов...'
                sh """
                kubectl wait --for=condition=ready pod -l app=keycloak -n $K8S_NAMESPACE --timeout=180s
                kubectl wait --for=condition=ready pod -l app=accounts-service -n $K8S_NAMESPACE --timeout=120s
                kubectl wait --for=condition=ready pod -l app=exchange-service -n $K8S_NAMESPACE --timeout=120s
                """
            }
        }

        stage('Health Check') {
            steps {
                echo 'Проверка статуса приложения...'

                sh "kubectl get pods -n $K8S_NAMESPACE"

                sh "kubectl get svc -n $K8S_NAMESPACE"

                sh "kubectl get ingress -n $K8S_NAMESPACE"

                sh """
                echo "Для доступа к приложению выполни:"
                echo "kubectl port-forward -n $K8S_NAMESPACE service/front-ui-service 8080:8086"
                echo "И открой: http://localhost:8080"
                """
            }
        }
    }

    post {
        always {
            echo 'Пайплайн завершен!'
        }
        success {
            echo 'Сборка и деплой прошли успешно!'
            archiveArtifacts '**/build/libs/*.jar'

            sh """
            echo "=== Деплой завершен ==="
            echo "Поды:"
            kubectl get pods -n $K8S_NAMESPACE
            echo ""
            echo "Для доступа к Keycloak: kubectl port-forward -n $K8S_NAMESPACE service/keycloak 8180:8180"
            echo "Для доступа к Frontend: kubectl port-forward -n $K8S_NAMESPACE service/front-ui-service 8080:8086"
            """
        }
        failure {
            echo 'Пайплайн завершился с ошибкой!'

            sh """
            echo "=== Статус подов ==="
            kubectl get pods -n $K8S_NAMESPACE

            echo "=== Логи проблемных подов ==="
            kubectl get pods -n $K8S_NAMESPACE --no-headers | grep -v Running | awk '{print \$1}' | xargs -I {} kubectl logs {} -n $K8S_NAMESPACE --tail=20
            """
        }
    }
}