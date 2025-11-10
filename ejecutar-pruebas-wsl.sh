#!/bin/bash

# Script para ejecutar todas las pruebas en WSL
# Taller 2 - Pruebas y Lanzamiento
# Ejecutar en WSL: bash ejecutar-pruebas-wsl.sh

echo "========================================="
echo "EJECUTANDO PRUEBAS - TALLER 2"
echo "Fecha: $(date)"
echo "========================================="
echo ""

# Cambiar al directorio del proyecto
PROJECT_DIR="/mnt/c/Users/santiago/Desktop/Universidad/Doce/Ingenieria de Software 5/Taller 2/ecommerce-microservice-backend-app"
cd "$PROJECT_DIR" || exit 1

echo "[1/4] PRUEBAS UNITARIAS - USER SERVICE"
echo "========================================="
cd user-service
../mvnw test -Dtest=UserMappingHelperTest -q
USER_HELPER_RESULT=$?
../mvnw test -Dtest=UserServiceImplTest -q
USER_SERVICE_RESULT=$?
cd ..

echo ""
echo "[2/4] PRUEBAS UNITARIAS - PRODUCT SERVICE"
echo "========================================="
cd product-service
../mvnw test -Dtest=ProductMappingHelperTest -q
PRODUCT_HELPER_RESULT=$?
../mvnw test -Dtest=ProductServiceImplTest -q
PRODUCT_SERVICE_RESULT=$?
cd ..

echo ""
echo "[3/4] PRUEBAS DE INTEGRACIÓN"
echo "========================================="
cd user-service
../mvnw test -Dtest=UserResourceIntegrationTest -q
USER_INTEGRATION_RESULT=$?
cd ../product-service
../mvnw test -Dtest=ProductResourceIntegrationTest -q
PRODUCT_INTEGRATION_RESULT=$?
cd ..

echo ""
echo "[4/4] RESUMEN DE RESULTADOS"
echo "========================================="
TOTAL_TESTS=0
PASSED_TESTS=0

if [ $USER_HELPER_RESULT -eq 0 ]; then
    echo "✅ UserMappingHelperTest: PASSED"
    ((PASSED_TESTS++))
else
    echo "❌ UserMappingHelperTest: FAILED"
fi
((TOTAL_TESTS++))

if [ $USER_SERVICE_RESULT -eq 0 ]; then
    echo "✅ UserServiceImplTest: PASSED"
    ((PASSED_TESTS++))
else
    echo "❌ UserServiceImplTest: FAILED"
fi
((TOTAL_TESTS++))

if [ $PRODUCT_HELPER_RESULT -eq 0 ]; then
    echo "✅ ProductMappingHelperTest: PASSED"
    ((PASSED_TESTS++))
else
    echo "❌ ProductMappingHelperTest: FAILED"
fi
((TOTAL_TESTS++))

if [ $PRODUCT_SERVICE_RESULT -eq 0 ]; then
    echo "✅ ProductServiceImplTest: PASSED"
    ((PASSED_TESTS++))
else
    echo "❌ ProductServiceImplTest: FAILED"
fi
((TOTAL_TESTS++))

if [ $USER_INTEGRATION_RESULT -eq 0 ]; then
    echo "✅ UserResourceIntegrationTest: PASSED"
    ((PASSED_TESTS++))
else
    echo "❌ UserResourceIntegrationTest: FAILED"
fi
((TOTAL_TESTS++))

if [ $PRODUCT_INTEGRATION_RESULT -eq 0 ]; then
    echo "✅ ProductResourceIntegrationTest: PASSED"
    ((PASSED_TESTS++))
else
    echo "❌ ProductResourceIntegrationTest: FAILED"
fi
((TOTAL_TESTS++))

echo ""
echo "========================================="
echo "RESULTADO FINAL: $PASSED_TESTS/$TOTAL_TESTS pruebas pasaron"
echo "========================================="

if [ $PASSED_TESTS -eq $TOTAL_TESTS ]; then
    echo "🎉 ¡TODAS LAS PRUEBAS PASARON!"
    exit 0
else
    echo "⚠️  Algunas pruebas fallaron"
    exit 1
fi

