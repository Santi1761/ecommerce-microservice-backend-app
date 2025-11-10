#!/usr/bin/env python3
"""
Prueba de Carga Simple
Taller 2 - Pruebas y Lanzamiento

Simula múltiples usuarios haciendo requests concurrentes
"""

import requests
import time
import concurrent.futures
import statistics
from datetime import datetime

# Configuración
BASE_URL = "http://localhost"
ENDPOINTS = [
    ("product-service", f"{BASE_URL}:8500/product-service/api/products"),
    ("user-service", f"{BASE_URL}:8700/user-service/api/users"),
    ("order-service", f"{BASE_URL}:8300/order-service/api/orders"),
    ("api-gateway health", f"{BASE_URL}:8080/actuator/health"),
]

NUM_USERS = 25
NUM_REQUESTS_PER_USER = 10

# Métricas globales
total_requests = 0
successful_requests = 0
failed_requests = 0
response_times = []

def make_request(endpoint_name, url):
    """Hace un request y mide el tiempo de respuesta"""
    global total_requests, successful_requests, failed_requests

    try:
        start_time = time.time()
        response = requests.get(url, timeout=5)
        end_time = time.time()

        response_time = (end_time - start_time) * 1000  # en milisegundos

        total_requests += 1

        if response.status_code == 200:
            successful_requests += 1
            response_times.append(response_time)
            return {
                "success": True,
                "endpoint": endpoint_name,
                "status": response.status_code,
                "time_ms": response_time
            }
        else:
            failed_requests += 1
            return {
                "success": False,
                "endpoint": endpoint_name,
                "status": response.status_code,
                "time_ms": response_time
            }
    except Exception as e:
        failed_requests += 1
        return {
            "success": False,
            "endpoint": endpoint_name,
            "error": str(e)
        }

def simulate_user(user_id):
    """Simula un usuario haciendo múltiples requests"""
    print(f"[USER] Usuario {user_id} iniciado")
    results = []

    for _ in range(NUM_REQUESTS_PER_USER):
        # Elegir endpoint aleatorio
        import random
        endpoint_name, url = random.choice(ENDPOINTS)
        result = make_request(endpoint_name, url)
        results.append(result)
        time.sleep(random.uniform(0.5, 1.5))  # Esperar entre 0.5 y 1.5 segundos

    return results

def main():
    print("=" * 60)
    print("PRUEBAS DE RENDIMIENTO - E-Commerce Microservices")
    print("=" * 60)
    print(f"Fecha: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    print(f"Usuarios concurrentes: {NUM_USERS}")
    print(f"Requests por usuario: {NUM_REQUESTS_PER_USER}")
    print(f"Total requests esperados: {NUM_USERS * NUM_REQUESTS_PER_USER}")
    print("=" * 60)
    print()

    print("[START] Iniciando prueba de carga...")
    start_time = time.time()

    # Ejecutar usuarios concurrentemente
    with concurrent.futures.ThreadPoolExecutor(max_workers=NUM_USERS) as executor:
        futures = [executor.submit(simulate_user, i+1) for i in range(NUM_USERS)]
        concurrent.futures.wait(futures)

    end_time = time.time()
    total_duration = end_time - start_time

    # Calcular métricas
    print("\n" + "=" * 60)
    print("RESULTADOS")
    print("=" * 60)

    print(f"\n[RESUMEN]:")
    print(f"  Total requests: {total_requests}")
    print(f"  [OK] Exitosos: {successful_requests}")
    print(f"  [FAIL] Fallidos: {failed_requests}")
    print(f"  [RATE] Tasa de éxito: {(successful_requests/total_requests*100):.2f}%")
    print(f"  [TIME] Duración total: {total_duration:.2f} segundos")
    print(f"  [THROUGHPUT] Throughput: {total_requests/total_duration:.2f} req/s")

    if response_times:
        print(f"\n[RESPONSE TIMES] (ms):")
        print(f"  Promedio: {statistics.mean(response_times):.2f} ms")
        print(f"  Mínimo: {min(response_times):.2f} ms")
        print(f"  Máximo: {max(response_times):.2f} ms")
        print(f"  Mediana: {statistics.median(response_times):.2f} ms")

        if len(response_times) > 1:
            print(f"  Desv. Estándar: {statistics.stdev(response_times):.2f} ms")

        # Percentiles
        sorted_times = sorted(response_times)
        p95_index = int(len(sorted_times) * 0.95)
        p99_index = int(len(sorted_times) * 0.99)
        print(f"  P95: {sorted_times[p95_index]:.2f} ms")
        print(f"  P99: {sorted_times[p99_index]:.2f} ms")

    print("\n" + "=" * 60)

    if failed_requests == 0:
        print("[SUCCESS] TODAS LAS PRUEBAS DE RENDIMIENTO PASARON!")
    else:
        print(f"[WARNING] {failed_requests} requests fallaron")

    print("=" * 60)

    # Guardar resultados en archivo
    with open("tests/performance/results_summary.txt", "w") as f:
        f.write(f"PRUEBAS DE RENDIMIENTO - {datetime.now()}\n")
        f.write("=" * 60 + "\n\n")
        f.write(f"Total requests: {total_requests}\n")
        f.write(f"Exitosos: {successful_requests}\n")
        f.write(f"Fallidos: {failed_requests}\n")
        f.write(f"Tasa de éxito: {(successful_requests/total_requests*100):.2f}%\n")
        f.write(f"Throughput: {total_requests/total_duration:.2f} req/s\n\n")

        if response_times:
            f.write(f"Tiempo respuesta promedio: {statistics.mean(response_times):.2f} ms\n")
            f.write(f"Tiempo respuesta mínimo: {min(response_times):.2f} ms\n")
            f.write(f"Tiempo respuesta máximo: {max(response_times):.2f} ms\n")
            f.write(f"P95: {sorted_times[p95_index]:.2f} ms\n")
            f.write(f"P99: {sorted_times[p99_index]:.2f} ms\n")

    print("\n[SAVED] Resultados guardados en: tests/performance/results_summary.txt\n")

if __name__ == "__main__":
    main()
