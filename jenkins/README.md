# Bank Microservices - Локальное развертывание

## Предварительные требования

Перед началом убедитесь, что установлены:
- Docker Desktop
- Kubernetes (включен в Docker Desktop)
- Git

## 1. Настройка Kubernetes в Docker Desktop

### Включение Kubernetes:
1. Откройте Docker Desktop
2. Перейдите в Settings → Kubernetes
3. Отметьте галочку "Enable Kubernetes"
4. Нажмите "Apply & Restart"

### Проверка установки:
```bash
kubectl get nodes
kubectl config current-context
```

## 2. Установка Ingress Controller

Для доступа к сервисам через доменные имена установите NGINX Ingress Controller:

```bash
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/cloud/deploy.yaml

# Дождитесь готовности
kubectl wait --namespace ingress-nginx --for=condition=ready pod --selector=app.kubernetes.io/component=controller --timeout=120s
```

## 3. Настройка локального DNS

Добавьте в файл hosts запись для доступа к приложению:

**Linux/Mac:**
```bash
sudo echo "127.0.0.1 bank.local" >> /etc/hosts
```

**Windows:**
Добавьте в `C:\Windows\System32\drivers\etc\hosts`:
```
127.0.0.1 bank.local
```

## 4. Запуск Jenkins Pipeline

### Создайте Jenkins pipeline со следующим содержимым:

1. В Jenkins создайте новый проект типа "Pipeline"
2. В разделе "Pipeline" выберите "Pipeline script"
3. Вставьте предоставленный Jenkinsfile
4. Сохраните и запустите сборку

### Pipeline выполняет следующие этапы:

1. **Checkout** - Получение кода из репозитория
2. **Build and Test** - Сборка проекта с помощью Gradle
3. **Docker Build** - Сборка Docker образов всех сервисов
4. **K8s Deploy** - Деплой всех компонентов в Kubernetes:
    - Создание namespace bank-dev
    - Развертывание PostgreSQL
    - Развертывание Keycloak
    - Развертывание микросервисов (accounts, blocker, cash, exchange, notifications, transfer, front-ui)
    - Настройка Ingress
5. **Wait for Services** - Ожидание запуска ключевых сервисов
6. **Health Check** - Проверка статуса развертывания

## 5. Доступ к приложению

После успешного выполнения pipeline:

### Через Ingress (рекомендуется):
```bash
# Откройте в браузере
http://bank.local
```

### Через Port-forward (альтернативный способ):
```bash
# Для frontend
kubectl port-forward -n bank-dev service/front-ui-service 8080:8086
# Откройте http://localhost:8080

# Для Keycloak админки
kubectl port-forward -n bank-dev service/keycloak 8180:8180
# Откройте http://localhost:8180
```

## 6. Проверка развертывания

```bash
# Проверить статус подов
kubectl get pods -n bank-dev

# Проверить сервисы
kubectl get svc -n bank-dev

# Проверить ingress
kubectl get ingress -n bank-dev

# Просмотреть логи конкретного сервиса
kubectl logs -n bank-dev deployment/accounts-service --tail=50
```

## 7. Устранение неполадок

Если возникают проблемы:

1. **Проверьте, что все поды в состоянии Running:**
   ```bash
   kubectl get pods -n bank-dev
   ```

2. **Просмотрите логи упавших подов:**
   ```bash
   kubectl get pods -n bank-dev --no-headers | grep -v Running | awk '{print $1}' | xargs -I {} kubectl logs {} -n bank-dev --tail=20
   ```

3. **Перезапустите деплоймент:**
   ```bash
   kubectl rollout restart deployment -n bank-dev
   ```

Приложение готово к использованию!