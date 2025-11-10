#!/bin/bash

# ========================================
# E2E TESTS - E-Commerce Microservices
# Taller 2 - Pruebas y Lanzamiento
# ========================================

echo "========================================="
echo "E2E TESTS - E-Commerce Microservices"
echo "Fecha: $(date)"
echo "========================================="
echo ""

PASSED=0
FAILED=0

# ========================================
# ✅ E2E TEST 1: Health Check API Gateway
# ========================================
echo "[TEST 1/7] Health Check API Gateway..."
RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/actuator/health)
if [ "$RESPONSE" == "200" ]; then
    echo "✅ PASSED - API Gateway está funcionando"
    ((PASSED++))
else
    echo "❌ FAILED - API Gateway no responde (HTTP $RESPONSE)"
    ((FAILED++))
fi
echo ""

# ========================================
# ✅ E2E TEST 2: Get All Products
# ========================================
echo "[TEST 2/7] Get All Products desde product-service..."
RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8500/product-service/api/products)
if [ "$RESPONSE" == "200" ]; then
    echo "✅ PASSED - Product service responde correctamente"
    ((PASSED++))
else
    echo "❌ FAILED - Product service no responde (HTTP $RESPONSE)"
    ((FAILED++))
fi
echo ""

# ========================================
# ✅ E2E TEST 3: Get All Users
# ========================================
echo "[TEST 3/7] Get All Users desde user-service..."
RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8700/user-service/api/users)
if [ "$RESPONSE" == "200" ]; then
    echo "✅ PASSED - User service responde correctamente"
    ((PASSED++))
else
    echo "❌ FAILED - User service no responde (HTTP $RESPONSE)"
    ((FAILED++))
fi
echo ""

# ========================================
# ✅ E2E TEST 4: Get All Orders
# ========================================
echo "[TEST 4/7] Get All Orders desde order-service..."
RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8300/order-service/api/orders)
if [ "$RESPONSE" == "200" ]; then
    echo "✅ PASSED - Order service responde correctamente"
    ((PASSED++))
else
    echo "❌ FAILED - Order service no responde (HTTP $RESPONSE)"
    ((FAILED++))
fi
echo ""

# ========================================
# ✅ E2E TEST 5: Check Eureka Registration
# ========================================
echo "[TEST 5/7] Verificar servicios registrados en Eureka..."
EUREKA_PAGE=$(curl -s http://localhost:8761/)
if echo "$EUREKA_PAGE" | grep -q "USER-SERVICE" && \
   echo "$EUREKA_PAGE" | grep -q "PRODUCT-SERVICE" && \
   echo "$EUREKA_PAGE" | grep -q "ORDER-SERVICE"; then
    echo "✅ PASSED - Todos los servicios están registrados en Eureka"
    ((PASSED++))
else
    echo "❌ FAILED - No todos los servicios están en Eureka"
    ((FAILED++))
fi
echo ""

# ========================================
# ✅ E2E TEST 6: Zipkin Health Check
# ========================================
echo "[TEST 6/7] Health Check Zipkin (distributed tracing)..."
RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:9411/health)
if [ "$RESPONSE" == "200" ]; then
    echo "✅ PASSED - Zipkin está funcionando"
    ((PASSED++))
else
    echo "❌ FAILED - Zipkin no responde (HTTP $RESPONSE)"
    ((FAILED++))
fi
echo ""

# ========================================
# ✅ E2E TEST 7: Response Time Check
# ========================================
echo "[TEST 7/7] Validar tiempo de respuesta de product-service < 1s..."
START_TIME=$(date +%s%N)
curl -s http://localhost:8500/product-service/api/products > /dev/null
END_TIME=$(date +%s%N)
ELAPSED_MS=$(( ($END_TIME - $START_TIME) / 1000000 ))

if [ $ELAPSED_MS -lt 1000 ]; then
    echo "✅ PASSED - Tiempo de respuesta: ${ELAPSED_MS}ms (< 1000ms)"
    ((PASSED++))
else
    echo "❌ FAILED - Tiempo de respuesta: ${ELAPSED_MS}ms (>= 1000ms)"
    ((FAILED++))
fi
echo ""

# ========================================
# RESULTS SUMMARY
# ========================================
echo "========================================="
echo "RESULTADOS FINALES"
echo "========================================="
echo "✅ Tests PASSED: $PASSED"
echo "❌ Tests FAILED: $FAILED"
echo "📊 Total Tests: $(($PASSED + $FAILED))"
echo "========================================="
echo ""

if [ $FAILED -eq 0 ]; then
    echo "🎉 ¡TODOS LOS TESTS E2E PASARON!"
    exit 0
else
    echo "⚠️  Algunos tests fallaron. Revise los servicios."
    exit 1
fi
