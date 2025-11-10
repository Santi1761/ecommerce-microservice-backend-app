# Script de despliegue a Kubernetes - Namespace DEV
# Taller 2 - Pruebas y Lanzamiento
# Ejecutar en PowerShell

Write-Host "============================================" -ForegroundColor Cyan
Write-Host "DESPLIEGUE A KUBERNETES - NAMESPACE DEV" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""

# Verificar que Minikube está corriendo
Write-Host "[1/7] Verificando Minikube..." -ForegroundColor Yellow
$minikubeStatus = minikube status 2>&1
if ($LASTEXITCODE -ne 0) {
    Write-Host "[ERROR] Minikube no esta corriendo!" -ForegroundColor Red
    Write-Host "Ejecuta primero: minikube start --driver=docker --cpus=4 --memory=8192" -ForegroundColor Red
    exit 1
}
Write-Host "[OK] Minikube corriendo" -ForegroundColor Green
Write-Host ""

# Crear namespaces
Write-Host "[2/7] Creando namespaces..." -ForegroundColor Yellow
kubectl apply -f k8s/namespaces.yaml
if ($LASTEXITCODE -eq 0) {
    Write-Host "[OK] Namespaces creados: dev, stage, prod" -ForegroundColor Green
} else {
    Write-Host "[WARNING] Error creando namespaces (puede que ya existan)" -ForegroundColor Yellow
}
Write-Host ""

# Desplegar infraestructura
Write-Host "[3/7] Desplegando infraestructura (Zipkin, Eureka, Config Server)..." -ForegroundColor Yellow
kubectl -n dev apply -f k8s/infra/zipkin.yaml
kubectl -n dev apply -f k8s/infra/eureka.yaml
kubectl -n dev apply -f k8s/infra/config-server.yaml

Write-Host "Esperando que Eureka este listo (timeout 3 minutos)..." -ForegroundColor Yellow
kubectl -n dev wait --for=condition=ready pod -l app=service-discovery --timeout=180s
if ($LASTEXITCODE -eq 0) {
    Write-Host "[OK] Infraestructura desplegada" -ForegroundColor Green
} else {
    Write-Host "[WARNING] Timeout esperando Eureka, continuando de todas formas..." -ForegroundColor Yellow
}
Write-Host ""

# Desplegar aplicaciones
Write-Host "[4/7] Desplegando aplicaciones..." -ForegroundColor Yellow
kubectl -n dev apply -f k8s/apps/user-service.yaml
kubectl -n dev apply -f k8s/apps/product-service.yaml
kubectl -n dev apply -f k8s/apps/order-service.yaml
kubectl -n dev apply -f k8s/apps/proxy-client.yaml
kubectl -n dev apply -f k8s/apps/api-gateway.yaml

Write-Host "[OK] Aplicaciones desplegadas" -ForegroundColor Green
Write-Host ""

# Esperar a que los pods estén listos
Write-Host "[5/7] Esperando que los pods esten listos..." -ForegroundColor Yellow
Start-Sleep -Seconds 30

# Mostrar estado
Write-Host "[6/7] Estado de los pods:" -ForegroundColor Yellow
kubectl -n dev get pods
Write-Host ""

Write-Host "[7/7] Estado de los servicios:" -ForegroundColor Yellow
kubectl -n dev get services
Write-Host ""

# Resumen final
Write-Host "============================================" -ForegroundColor Cyan
Write-Host "DESPLIEGUE COMPLETADO" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Para acceder a los servicios:" -ForegroundColor Yellow
Write-Host ""
Write-Host "Opcion 1 - Port Forward (recomendado para desarrollo):" -ForegroundColor White
Write-Host "  kubectl -n dev port-forward svc/api-gateway 8080:8080" -ForegroundColor Gray
Write-Host "  kubectl -n dev port-forward svc/user-service 8700:8700" -ForegroundColor Gray
Write-Host "  kubectl -n dev port-forward svc/product-service 8500:8500" -ForegroundColor Gray
Write-Host ""
Write-Host "Opcion 2 - NodePort:" -ForegroundColor White
Write-Host "  minikube service api-gateway -n dev --url" -ForegroundColor Gray
Write-Host ""
Write-Host "Ver logs de un servicio:" -ForegroundColor Yellow
Write-Host "  kubectl -n dev logs deployment/user-service --tail=50" -ForegroundColor Gray
Write-Host ""
Write-Host "Dashboard de Minikube:" -ForegroundColor Yellow
Write-Host "  minikube dashboard" -ForegroundColor Gray
Write-Host ""
