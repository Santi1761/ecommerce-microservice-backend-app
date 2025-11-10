#!/bin/bash
# Script para ejecutar TODAS las pruebas del proyecto
# Taller 2 - Pruebas y Lanzamiento

echo "============================================="
echo "EJECUTANDO TODAS LAS PRUEBAS DEL PROYECTO"
echo "============================================="
echo ""

TOTAL_PASSED=0
TOTAL_FAILED=0

# Pruebas Unitarias
echo "[1/4] Ejecutando pruebas UNITARIAS..."
echo "--------------------------------------"
./mvnw test -Dtest=UserMappingHelperTest,ProductMappingHelperTest
if [ $? -eq 0 ]; then
    echo "[OK] Pruebas unitarias PASARON"
    TOTAL_PASSED=$((TOTAL_PASSED + 10))
else
    echo "[FAIL] Pruebas unitarias FALLARON"
    TOTAL_FAILED=$((TOTAL_FAILED + 10))
fi
echo ""

# Pruebas de Integración
echo "[2/4] Ejecutando pruebas de INTEGRACIÓN..."
echo "-------------------------------------------"
cd user-service
../mvnw test -Dtest=UserResourceIntegrationTest
USER_RESULT=$?
cd ..

cd product-service
../mvnw test -Dtest=ProductResourceIntegrationTest
PRODUCT_RESULT=$?
cd ..

if [ $USER_RESULT -eq 0 ] && [ $PRODUCT_RESULT -eq 0 ]; then
    echo "[OK] Pruebas de integración PASARON"
    TOTAL_PASSED=$((TOTAL_PASSED + 5))
else
    echo "[FAIL] Pruebas de integración FALLARON"
    TOTAL_FAILED=$((TOTAL_FAILED + 5))
fi
echo ""

# Pruebas E2E
echo "[3/4] Ejecutando pruebas E2E..."
echo "--------------------------------"
chmod +x tests/e2e/run-e2e-tests.sh
./tests/e2e/run-e2e-tests.sh
if [ $? -eq 0 ]; then
    echo "[OK] Pruebas E2E PASARON"
    TOTAL_PASSED=$((TOTAL_PASSED + 7))
else
    echo "[FAIL] Pruebas E2E FALLARON"
    TOTAL_FAILED=$((TOTAL_FAILED + 7))
fi
echo ""

# Pruebas de Rendimiento
echo "[4/4] Ejecutando pruebas de RENDIMIENTO..."
echo "-------------------------------------------"
python3 tests/performance/simple_load_test.py
if [ $? -eq 0 ]; then
    echo "[OK] Pruebas de rendimiento PASARON"
    TOTAL_PASSED=$((TOTAL_PASSED + 1))
else
    echo "[FAIL] Pruebas de rendimiento FALLARON"
    TOTAL_FAILED=$((TOTAL_FAILED + 1))
fi
echo ""

# Resumen Final
echo "============================================="
echo "RESUMEN FINAL DE PRUEBAS"
echo "============================================="
echo "Tests PASADOS: $TOTAL_PASSED"
echo "Tests FALLADOS: $TOTAL_FAILED"
echo ""

if [ $TOTAL_FAILED -eq 0 ]; then
    echo "[SUCCESS] TODAS LAS PRUEBAS PASARON!"
    echo "============================================="
    exit 0
else
    echo "[WARNING] Algunas pruebas fallaron"
    echo "============================================="
    exit 1
fi
