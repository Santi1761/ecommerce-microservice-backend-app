#!/bin/bash

# Script detallado para ejecutar pruebas con salida completa
# Taller 2 - Pruebas y Lanzamiento
# Ejecutar en WSL: bash ejecutar-pruebas-detalladas.sh

echo "========================================="
echo "EJECUTANDO PRUEBAS DETALLADAS - TALLER 2"
echo "Fecha: $(date)"
echo "========================================="
echo ""

PROJECT_DIR="/mnt/c/Users/santiago/Desktop/Universidad/Doce/Ingenieria de Software 5/Taller 2/ecommerce-microservice-backend-app"
cd "$PROJECT_DIR" || exit 1

# Crear directorio para resultados
mkdir -p resultados-pruebas
RESULTADOS_DIR="$PROJECT_DIR/resultados-pruebas"

echo "[1/6] PRUEBAS UNITARIAS - UserMappingHelperTest"
echo "========================================="
cd user-service
../mvnw test -Dtest=UserMappingHelperTest > "$RESULTADOS_DIR/user-mapping-helper.txt" 2>&1
if [ $? -eq 0 ]; then
    echo "✅ PASSED"
    grep -E "Tests run:|Failures:" "$RESULTADOS_DIR/user-mapping-helper.txt" | tail -2
else
    echo "❌ FAILED"
fi
cd ..

echo ""
echo "[2/6] PRUEBAS UNITARIAS - UserServiceImplTest"
echo "========================================="
cd user-service
../mvnw test -Dtest=UserServiceImplTest > "$RESULTADOS_DIR/user-service-impl.txt" 2>&1
if [ $? -eq 0 ]; then
    echo "✅ PASSED"
    grep -E "Tests run:|Failures:" "$RESULTADOS_DIR/user-service-impl.txt" | tail -2
else
    echo "❌ FAILED"
fi
cd ..

echo ""
echo "[3/6] PRUEBAS UNITARIAS - ProductMappingHelperTest"
echo "========================================="
cd product-service
../mvnw test -Dtest=ProductMappingHelperTest > "$RESULTADOS_DIR/product-mapping-helper.txt" 2>&1
if [ $? -eq 0 ]; then
    echo "✅ PASSED"
    grep -E "Tests run:|Failures:" "$RESULTADOS_DIR/product-mapping-helper.txt" | tail -2
else
    echo "❌ FAILED"
fi
cd ..

echo ""
echo "[4/6] PRUEBAS UNITARIAS - ProductServiceImplTest"
echo "========================================="
cd product-service
../mvnw test -Dtest=ProductServiceImplTest > "$RESULTADOS_DIR/product-service-impl.txt" 2>&1
if [ $? -eq 0 ]; then
    echo "✅ PASSED"
    grep -E "Tests run:|Failures:" "$RESULTADOS_DIR/product-service-impl.txt" | tail -2
else
    echo "❌ FAILED"
fi
cd ..

echo ""
echo "[5/6] PRUEBAS INTEGRACIÓN - UserResourceIntegrationTest"
echo "========================================="
cd user-service
../mvnw test -Dtest=UserResourceIntegrationTest > "$RESULTADOS_DIR/user-integration.txt" 2>&1
if [ $? -eq 0 ]; then
    echo "✅ PASSED"
    grep -E "Tests run:|Failures:" "$RESULTADOS_DIR/user-integration.txt" | tail -2
else
    echo "❌ FAILED"
fi
cd ..

echo ""
echo "[6/6] PRUEBAS INTEGRACIÓN - ProductResourceIntegrationTest"
echo "========================================="
cd product-service
../mvnw test -Dtest=ProductResourceIntegrationTest > "$RESULTADOS_DIR/product-integration.txt" 2>&1
if [ $? -eq 0 ]; then
    echo "✅ PASSED"
    grep -E "Tests run:|Failures:" "$RESULTADOS_DIR/product-integration.txt" | tail -2
else
    echo "❌ FAILED"
fi
cd ..

echo ""
echo "========================================="
echo "RESULTADOS GUARDADOS EN: resultados-pruebas/"
echo "========================================="
echo ""
echo "Archivos generados:"
ls -lh "$RESULTADOS_DIR"

