# Taller 2: Pruebas y Release
**Estudiante:** Santiago Arboleda
**Curso:** Ingenieria de Software 5

---

## Video Demo

**Video completo de la implementacion y demostracion:**

[Ver Demo.mp4](capturas/Demo.mp4)

> **Nota:** El video muestra la ejecucion completa de todos los componentes, pruebas y funcionalidad del sistema.

---

## Tabla de Contenidos

1. [Configuracion de Entorno](#1-configuracion-de-entorno)
2. [Microservicios Desplegados](#2-microservicios-desplegados)
3. [Pruebas Implementadas](#3-pruebas-implementadas)
4. [Arquitectura del Sistema](#4-arquitectura-del-sistema)
5. [Comandos de Ejecucion](#5-comandos-de-ejecucion)
6. [Evidencias y Capturas](#6-evidencias-y-capturas)

---

## 1. Configuracion de Entorno

### 1.1 Docker Desktop

**Estado:** ✅ Operacional

![Docker Desktop - Contenedores](capturas/DockerDesktop1.png)
*Docker Desktop mostrando todos los contenedores en ejecucion*

![Docker Desktop - Imagenes](capturas/DockerDesktop2.png)
*Imagenes Docker construidas localmente*

**Contenedores en ejecucion:**
- Jenkins (puerto 8090)
- Service Discovery / Eureka (puerto 8761)
- Cloud Config Server (puerto 9296)
- API Gateway (puerto 8080)
- User Service (puerto 8700)
- Product Service (puerto 8500)
- Order Service (puerto 8300)
- Proxy Client (puerto 8900)
- Zipkin (puerto 9411)

**Verificar contenedores:**
```powershell
docker ps
```

![Docker Console](capturas/DockerConsole.png)
*Salida de `docker ps` mostrando todos los contenedores activos*

---

### 1.2 Docker Hub - Imagenes Publicadas

**Registry:** https://hub.docker.com/u/santi1761

![Docker Hub](capturas/DockerHub.png)
*Repositorio en Docker Hub con las 7 imagenes publicadas*

**Imagenes disponibles:**
- `santi1761/service-discovery-ecommerce-boot:0.1.0`
- `santi1761/cloud-config-ecommerce-boot:0.1.0`
- `santi1761/api-gateway-ecommerce-boot:0.1.0`
- `santi1761/proxy-client-ecommerce-boot:0.1.0`
- `santi1761/user-service-ecommerce-boot:0.1.0`
- `santi1761/product-service-ecommerce-boot:0.1.0`
- `santi1761/order-service-ecommerce-boot:0.1.0`

---

### 1.3 Jenkins

**URL:** http://localhost:8090

![Jenkins Dashboard](capturas/JenkinsNavegador.png)
*Jenkins Dashboard - Interfaz principal*

![Jenkins Console](capturas/JenkinsConsole.png)
*Jenkins - Consola de ejecucion de builds*

**Configuracion:**
- Plugins instalados: Docker Pipeline, Kubernetes CLI, Git
- Credenciales configuradas: `dockerhub-creds`
- Pipelines creados: ecommerce-dev-pipeline

**Acceso:**
```powershell
docker ps | findstr jenkins

# Abrir en navegador
start http://localhost:8090
```

---

### 1.4 Minikube - Kubernetes

**Iniciar Minikube:**
```powershell
minikube start --driver=docker --cpus=4 --memory=6144
```

![Minikube Console](capturas/MinikubeConsole.png)
*Minikube iniciado y pods desplegados en namespace dev*

**Comandos de verificacion:**
```powershell
# Ver estado de Minikube
minikube status

# Ver pods en namespace dev
kubectl -n dev get pods

# Ver servicios
kubectl -n dev get services

# Ver todos los namespaces
kubectl get namespaces
```

**Namespaces configurados:**
- `dev` - Desarrollo
- `stage` - Pre-produccion
- `prod` - Produccion

---

## 2. Microservicios Desplegados

### 2.1 Service Discovery (Eureka)

**Puerto:** 8761
**URL:** http://localhost:8761

![Eureka Dashboard](capturas/Eureka.png)
*Eureka mostrando todos los servicios registrados*

**Servicios registrados:**
- API-GATEWAY
- USER-SERVICE
- PRODUCT-SERVICE
- ORDER-SERVICE
- PROXY-CLIENT

**Funcion:**
- Registro automatico de servicios
- Descubrimiento de servicios
- Health checking
- Load balancing

---

### 2.2 API Gateway

**Puerto:** 8080
**Endpoints:** `/actuator/health`

**Health Check:**
```powershell
curl http://localhost:8080/actuator/health
```

![API Gateway Health](capturas/HealthNavegador.png)
*Endpoint /actuator/health mostrando estado UP*

**Funcion:**
- Punto de entrada unico para todos los servicios
- Routing dinamico basado en Eureka
- Circuit breaker con Resilience4j
- Rate limiting

---

### 2.3 User Service

**Puerto:** 8700
**Endpoints:** `/user-service/api/users`

**Probar en navegador:**
```
http://localhost:8700/user-service/api/users
```

![User Service API](capturas/Api-usersNavegador.png)
*GET /api/users retornando lista de usuarios en formato JSON*

**Funcionalidad:**
- CRUD de usuarios
- Gestion de credenciales
- Integracion con base de datos H2

**Comandos:**
```powershell
# Obtener todos los usuarios
curl http://localhost:8700/user-service/api/users

# Obtener usuario por ID
curl http://localhost:8700/user-service/api/users/1
```

---

### 2.4 Product Service

**Puerto:** 8500
**Endpoints:** `/product-service/api/products`

**Probar en navegador:**
```
http://localhost:8500/product-service/api/products
```

![Product Service API](capturas/Api-ProductsNavegador.png)
*GET /api/products retornando catalogo de productos*

**Funcionalidad:**
- CRUD de productos
- Gestion de categorias
- Control de inventario (stock)
- Validacion de SKU unico

**Comandos:**
```powershell
# Obtener todos los productos
curl http://localhost:8500/product-service/api/products

# Obtener producto por ID
curl http://localhost:8500/product-service/api/products/1
```

---

### 2.5 Order Service

**Puerto:** 8300
**Endpoints:** `/order-service/api/orders`

**Funcionalidad:**
- Gestion de ordenes
- Integracion con User Service
- Integracion con Product Service
- Validacion de stock antes de crear orden

**Comandos:**
```powershell
curl http://localhost:8300/order-service/api/orders
```

---

### 2.6 Zipkin - Distributed Tracing

**Puerto:** 9411
**URL:** http://localhost:9411

**Funcionalidad:**
- Trazabilidad distribuida entre microservicios
- Visualizacion de latencia por servicio
- Deteccion de cuellos de botella

---

## 3. Pruebas Implementadas

### 3.1 Pruebas Unitarias (15 tests)

**Ubicacion:**
- `user-service/src/test/java/com/selimhorri/app/helper/UserMappingHelperTest.java`
- `product-service/src/test/java/com/selimhorri/app/helper/ProductMappingHelperTest.java`

![Unit Tests Execution](capturas/UnitTest.png)
*Ejecucion de pruebas unitarias - 15 tests PASSED*

**Ejecutar:**
```bash
# En WSL2
cd user-service
../mvnw test -Dtest=UserMappingHelperTest

cd ../product-service
../mvnw test -Dtest=ProductMappingHelperTest
```

**Resultados:**
```
Tests run: 15
Failures: 0
Errors: 0
Success Rate: 100%
```

**Cobertura:**
- Mapeo de entidades a DTOs
- Manejo de valores nulos
- Validacion de relaciones entre objetos
- Transformaciones de datos

---

### 3.2 Pruebas de Integracion (5 tests)

**Ubicacion:**
- `user-service/src/test/java/com/selimhorri/app/resource/UserResourceIntegrationTest.java`
- `product-service/src/test/java/com/selimhorri/app/resource/ProductResourceIntegrationTest.java`

![Integration Test - User Service](capturas/IntegrationTest.png)
*Pruebas de integracion User Service - 3/3 PASSED*

![Integration Test - Product Service](capturas/IntegrationTest2.png)
*Pruebas de integracion Product Service - 2/2 PASSED*

**Ejecutar:**
```bash
cd user-service
../mvnw test -Dtest=UserResourceIntegrationTest

cd ../product-service
../mvnw test -Dtest=ProductResourceIntegrationTest
```

**Validaciones:**
- REST Controllers (@SpringBootTest + MockMvc)
- Serializacion/Deserializacion JSON
- HTTP Status Codes (200, 201, 404)
- Content-Type headers
- Integracion con base de datos H2

---

### 3.3 Pruebas End-to-End (7 tests)

**Ubicacion:** `tests/e2e/run-e2e-tests.sh`

![E2E Tests Execution](capturas/E2Etest.png)
*Pruebas E2E - 7/7 PASSED*

**Ejecutar:**
```bash
cd tests/e2e
chmod +x run-e2e-tests.sh
./run-e2e-tests.sh
```

**Pruebas:**
1. Health Check API Gateway
2. Get All Products
3. Get All Users
4. Get All Orders
5. Check Eureka Registration
6. Zipkin Health Check
7. Response Time Check (<1s) - 79ms

**Script:**
```bash
#!/bin/bash
echo "E2E TESTS - E-Commerce Microservices"

# E2E-1: Health Check API Gateway
curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/actuator/health | grep -q "200"

# E2E-2: Get All Products
curl -s -o /dev/null -w "%{http_code}" http://localhost:8500/product-service/api/products | grep -q "200"

```

---

### 3.4 Pruebas de Rendimiento (Locust)

**Ubicacion:** `tests/performance/simple_load_test.py`

![Locust Performance Test](capturas/LocustTest.png)
*Resultados de pruebas de rendimiento con Locust*

**Configuracion:**
- Usuarios concurrentes: 25
- Requests por usuario: 10
- Total requests: 250

**Resultados:**
```
Total Requests: 250
Successful: 250 (100.0%)
Failed: 0 (0.0%)
Throughput: 20.57 req/s

Response Times (ms):
  Average: 39.08
  Median: 19.34
  Min: 7.61
  Max: 298.22
  P95: 197.22
  P99: 293.16
```

**Metricas Clave:**

| Metrica | Valor | Estado |
|---------|-------|--------|
| **Tasa de Exito** | 100% | ✅ Excelente |
| **Throughput** | 20.57 req/s | ✅ Bueno |
| **Tiempo Promedio** | 39.08 ms | ✅ Excelente |
| **P95** | 197.22 ms | ✅ < 200ms |
| **P99** | 293.16 ms | ✅ < 300ms |

**Ejecutar:**
```bash
cd tests/performance
python3 simple_load_test.py
cat results_summary.txt
```

**Analisis:**
- ✅ **Estabilidad perfecta:** 0% de errores en 250 requests
- ✅ **Latencia excelente:** Promedio de 39ms
- ✅ **Consistencia:** P95 < 200ms
- ✅ **Sistema estable bajo carga concurrente**

---

## 4. Arquitectura del Sistema

### 4.1 Estructura del Proyecto

![Estructura de Carpetas](capturas/ProyectoCarpetas.png)
*Organizacion del proyecto - Microservicios, K8s manifests, Tests, Pipelines*

```
ecommerce-microservice-backend-app/
├── service-discovery/          # Eureka Server (8761)
├── cloud-config/               # Config Server (9296)
├── api-gateway/                # Gateway (8080)
├── proxy-client/               # Auth + Swagger (8900)
├── user-service/               # Users (8700)
│   └── src/test/java/
│       ├── helper/             # Unit tests
│       └── resource/           # Integration tests
├── product-service/            # Products (8500)
│   └── src/test/java/
│       ├── helper/             # Unit tests
│       └── resource/           # Integration tests
├── order-service/              # Orders (8300)
├── k8s/
│   ├── namespaces.yaml
│   ├── infra/                  # Infrastructure services
│   │   ├── zipkin.yaml
│   │   ├── eureka.yaml
│   │   └── config-server.yaml
│   └── apps/                   # Application services
│       ├── api-gateway.yaml
│       ├── user-service.yaml
│       ├── product-service.yaml
│       └── order-service.yaml
├── tests/
│   ├── e2e/
│   │   └── run-e2e-tests.sh   # 7 E2E tests
│   └── performance/
│       ├── simple_load_test.py
│       └── results_summary.txt
├── Jenkinsfile                 # DEV pipeline
├── Jenkinsfile.stage           # STAGE pipeline
├── Jenkinsfile.master          # MASTER pipeline
├── deploy-k8s-dev.ps1          # K8s deployment script
├── capturas/                   # Screenshots
│   ├── Demo.mp4               # Video demostracion
│   └── *.png                  # Evidencias
└── README.md                   # Esta documentacion
```


---

### 4.3 Flujo de Request

**Ejemplo: GET /api/products**

```
1. Cliente → http://localhost:8080/product-service/api/products
2. API Gateway recibe request
3. Gateway consulta Eureka: ¿donde esta PRODUCT-SERVICE?
4. Eureka responde: product-service:8500
5. Gateway → Forward request → Product Service
6. Product Service procesa y responde
7. Gateway → Retorna respuesta al cliente
8. Zipkin registra toda la traza
```

---

## 5. Comandos de Ejecucion

### 5.1 Iniciar Entorno Completo

**Paso 1: Docker Desktop**
```powershell
# Abrir Docker Desktop (GUI)
# Esperar que muestre "Running"
```

**Paso 2: Iniciar servicios con Docker Compose**
```bash
# En WSL2
cd /mnt/c/Users/santiago/Desktop/Universidad/Doce/Ingenieria\ de\ Software\ 5/Taller\ 2/ecommerce-microservice-backend-app

docker-compose -f compose.yml up -d

# Verificar
docker ps
```

**Servicios disponibles:**
- Eureka: http://localhost:8761
- API Gateway: http://localhost:8080
- User Service: http://localhost:8700
- Product Service: http://localhost:8500
- Order Service: http://localhost:8300
- Zipkin: http://localhost:9411
- Jenkins: http://localhost:8090

---

### 5.2 Ejecutar Todas las Pruebas

**Paso 2: Minikube**
```powershell
minikube start --driver=docker --cpus=4 --memory=6144
minikube status
```

**Paso 3: Desplegar en Kubernetes**
```powershell
# Opcion 1: Script automatizado
.\deploy-k8s-dev.ps1

# Opcion 2: Manual
kubectl apply -f k8s/namespaces.yaml
kubectl apply -f k8s/infra/
kubectl apply -f k8s/apps/

# Verificar
kubectl -n dev get pods
kubectl -n dev get services
```

**Paso 4: Port-forward para acceder a servicios**
```powershell
# En diferentes ventanas de PowerShell
kubectl -n dev port-forward svc/api-gateway 8080:8080
kubectl -n dev port-forward svc/user-service 8700:8700
kubectl -n dev port-forward svc/product-service 8500:8500
```

---

### 5.2 Ejecutar Todas las Pruebas

**Pruebas Unitarias:**
```bash
# En WSL2
cd user-service
../mvnw test -Dtest=UserMappingHelperTest

cd ../product-service
../mvnw test -Dtest=ProductMappingHelperTest
```

**Pruebas de Integracion:**
```bash
cd user-service
../mvnw test -Dtest=UserResourceIntegrationTest

cd ../product-service
../mvnw test -Dtest=ProductResourceIntegrationTest
```

**Pruebas E2E:**
```bash
cd tests/e2e
chmod +x run-e2e-tests.sh
./run-e2e-tests.sh
```

**Pruebas de Rendimiento:**
```bash
cd tests/performance
python3 simple_load_test.py
cat results_summary.txt
```

---

### 5.3 Comandos Utiles

**Docker:**
```powershell
# Ver contenedores
docker ps

# Ver logs
docker logs jenkins --tail=100
docker logs service-discovery --tail=100

# Reiniciar contenedor
docker restart <container-name>

# Detener todos
docker-compose -f compose.yml down
```

**Kubernetes:**
```powershell
# Ver pods
kubectl -n dev get pods

# Ver logs de un pod
kubectl -n dev logs <pod-name>

# Describir pod (troubleshooting)
kubectl -n dev describe pod <pod-name>

# Eliminar pod (se recrea automaticamente)
kubectl -n dev delete pod <pod-name>

# Detener Minikube
minikube stop
```

---

## 6. Evidencias y Capturas

### 6.1 Infraestructura

| Captura | Descripcion |
|---------|-------------|
| ![Docker Desktop 1](capturas/DockerDesktop1.png) | Docker Desktop - Contenedores en ejecucion |
| ![Docker Desktop 2](capturas/DockerDesktop2.png) | Docker Desktop - Imagenes construidas |
| ![Docker Console](capturas/DockerConsole.png) | Comando `docker ps` - Lista de contenedores |
| ![Docker Hub](capturas/DockerHub.png) | Registry Docker Hub con imagenes publicadas |
| ![Jenkins Navegador](capturas/JenkinsNavegador.png) | Jenkins Dashboard - Interfaz web |
| ![Jenkins Console](capturas/JenkinsConsole.png) | Jenkins - Consola de builds |
| ![Minikube Console](capturas/MinikubeConsole.png) | Minikube - Pods desplegados |

---

### 6.2 Microservicios en Ejecucion

| Captura | Descripcion |
|---------|-------------|
| ![Eureka](capturas/Eureka.png) | Eureka Dashboard - Servicios registrados |
| ![Health Check](capturas/HealthNavegador.png) | API Gateway - Endpoint /actuator/health |
| ![Users API](capturas/Api-usersNavegador.png) | User Service - GET /api/users |
| ![Products API](capturas/Api-ProductsNavegador.png) | Product Service - GET /api/products |

---

### 6.3 Pruebas

| Captura | Descripcion |
|---------|-------------|
| ![Unit Tests](capturas/UnitTest.png) | Ejecucion de 15 pruebas unitarias - 100% exito |
| ![Integration Test 1](capturas/IntegrationTest.png) | User Service - 3 pruebas de integracion PASSED |
| ![Integration Test 2](capturas/IntegrationTest2.png) | Product Service - 2 pruebas de integracion PASSED |
| ![E2E Tests](capturas/E2Etest.png) | Pruebas End-to-End - 7/7 PASSED |
| ![Locust Test](capturas/LocustTest.png) | Pruebas de rendimiento - 250 requests, 100% exito |

---

### 6.4 Estructura del Proyecto

| Captura | Descripcion |
|---------|-------------|
| ![Proyecto Carpetas](capturas/ProyectoCarpetas.png) | Organizacion completa del proyecto |

---

## 📹 Video Demostracion Completa

**Archivo:** `capturas/Demo.mp4`

El video demuestra:
1. ✅ Docker Desktop con todos los contenedores corriendo
2. ✅ Jenkins Dashboard y configuracion
3. ✅ Minikube y pods desplegados en Kubernetes
4. ✅ Eureka mostrando servicios registrados
5. ✅ APIs funcionando (Users, Products, Health checks)
6. ✅ Ejecucion de pruebas unitarias
7. ✅ Ejecucion de pruebas de integracion
8. ✅ Ejecucion de pruebas E2E
9. ✅ Resultados de pruebas de rendimiento
10. ✅ Estructura completa del proyecto

**Ver video:** [Demo.mp4](capturas/Demo.mp4)

---

## Resumen de Cumplimiento

### Requisitos del Enunciado

| Requisito | Estado | Evidencia |
|-----------|--------|-----------|
| **1. Configurar Jenkins, Docker, Kubernetes (10%)** | ✅ | Capturas: JenkinsNavegador.png, DockerDesktop1.png, MinikubeConsole.png |
| **2. Pipelines build (dev) >=6 microservicios (15%)** | ✅ | 7 servicios: Eureka, Config, Gateway, Proxy, User, Product, Order |
| **3a. >=5 Pruebas Unitarias (30%)** | ✅ | 15 pruebas - UnitTest.png |
| **3b. >=5 Pruebas Integracion (30%)** | ✅ | 5 pruebas - IntegrationTest.png, IntegrationTest2.png |
| **3c. >=5 Pruebas E2E (30%)** | ✅ | 7 pruebas - E2Etest.png |
| **3d. Pruebas Rendimiento Locust (30%)** | ✅ | 250 requests - LocustTest.png |
| **4. Pipeline STAGE en K8s (15%)** | ✅ | Jenkinsfile.stage + manifiestos K8s |
| **5. Pipeline MASTER + Release Notes (15%)** | ✅ | Jenkinsfile.master con generacion automatica |
| **6. Documentacion + Reporte + ZIP (15%)** | ✅ | README.md + Demo.mp4 + Capturas |

**Total:** ✅ **Todos los requisitos cumplidos**

---

## Endpoints de Prueba

### Servicios en Docker Compose

```bash
# Service Discovery (Eureka)
http://localhost:8761

# API Gateway
http://localhost:8080/actuator/health

# User Service
http://localhost:8700/user-service/api/users

# Product Service
http://localhost:8500/product-service/api/products

# Order Service
http://localhost:8300/order-service/api/orders

# Zipkin
http://localhost:9411

# Jenkins
http://localhost:8090
```

### Servicios en Kubernetes (con port-forward)

```bash
# Despues de ejecutar port-forward
http://localhost:8080/actuator/health
http://localhost:8700/user-service/api/users
http://localhost:8500/product-service/api/products
```

---

## Tecnologias Utilizadas

**Backend:**
- Java 11
- Spring Boot 2.5.7
- Spring Cloud 2020.0.4
- Maven

**Infraestructura:**
- Docker & Docker Compose
- Kubernetes (Minikube)
- Jenkins

**Testing:**
- JUnit 5
- Mockito
- Spring Boot Test
- MockMvc
- Locust (Python)

**Observabilidad:**
- Zipkin
- Spring Boot Actuator
- Eureka Dashboard

---

## Conclusiones

### Logros

1. ✅ **Arquitectura de Microservicios completa** con 7 servicios comunicandose
2. ✅ **Containerizacion exitosa** con Docker
3. ✅ **Orquestacion en Kubernetes** con Minikube
4. ✅ **CI/CD con Jenkins** implementado
5. ✅ **Testing exhaustivo** (15 unitarias + 5 integracion + 7 E2E + rendimiento)
6. ✅ **100% de pruebas pasando** sin errores
7. ✅ **Observabilidad** con Zipkin y Actuator
8. ✅ **Documentacion completa** con evidencias

### Metricas Destacadas

- **Estabilidad:** 100% de exito en pruebas de carga (250 requests)
- **Rendimiento:** Tiempo de respuesta promedio de 39.08 ms
- **Throughput:** 20.57 requests/segundo
- **Calidad:** 0% tasa de errores

### Aprendizajes

1. Configuracion de Service Discovery con Eureka
2. Implementacion de API Gateway con Spring Cloud Gateway
3. Despliegue de microservicios en Kubernetes
4. Estrategias de testing multinivel
5. Analisis de metricas de rendimiento

---

## Autor

**Santiago Arboleda Velasco**
Ingenieria de Software 5
Universidad
Noviembre 2025

---
