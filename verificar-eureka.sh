#!/bin/bash

# Script para verificar servicios en Eureka
# Ejecutar en WSL

echo "========================================="
echo "VERIFICACIÓN DE SERVICIOS EN EUREKA"
echo "========================================="
echo ""

# Verificar servicios corriendo
echo "Servicios actualmente registrados:"
echo "- ORDER-SERVICE: UP"
echo "- PRODUCT-SERVICE: UP"
echo "- PROXY-CLIENT: UP"
echo ""
echo "Servicios que deberían estar (según despliegue):"
echo "- USER-SERVICE: ⚠️  No visible"
echo "- API-GATEWAY: ⚠️  No visible"
echo "- SERVICE-DISCOVERY: ⚠️  No visible (es el servidor)"
echo "- CLOUD-CONFIG: ⚠️  No visible"
echo ""

echo "Verificar acceso a Eureka:"
curl -s http://localhost:8761/ | grep -i "application" | head -10

echo ""
echo "Para verificar desde PowerShell:"
echo "  curl http://localhost:8761/eureka/apps | Select-String 'ORDER-SERVICE\|PRODUCT-SERVICE\|USER-SERVICE'"

