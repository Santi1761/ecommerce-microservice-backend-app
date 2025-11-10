"""
Pruebas de Rendimiento con Locust
Taller 2 - Pruebas y Lanzamiento
E-Commerce Microservices

Simula carga de usuarios concurrentes sobre los microservicios pasando por el API Gateway.
Descubre automáticamente la mejor ruta (path) disponible para cada servicio.
"""

from locust import HttpUser, task, between
import random
import json

CANDIDATES_PRODUCTS = [
    "/product-service/api/products",
    "/PRODUCT-SERVICE/api/products",
    "/api/products",
    "/products",
    "/product-service/products",
    "/api/v1/products",
]

CANDIDATES_USERS = [
    "/user-service/api/users",
    "/USER-SERVICE/api/users",
    "/api/users",
    "/users",
]

CANDIDATES_ORDERS = [
    "/order-service/api/orders",
    "/ORDER-SERVICE/api/orders",
    "/api/orders",
    "/orders",
]

JSON_HEADERS = {"Accept": "application/json"}

def pick_first_json_2xx(client, candidates, name_hint):
    """
    Devuelve el primer path que:
      - responde 2xx
      - y su cuerpo es JSON (list o dict)
    Si no hay ninguno válido, retorna None.
    """
    for path in candidates:
        with client.get(path, headers=JSON_HEADERS, name=f"PROBE {name_hint}", catch_response=True) as r:
            try:
                if 200 <= r.status_code < 300:
                    data = r.json()
                    if isinstance(data, (list, dict)):
                        r.success()
                        return path
                    else:
                        r.failure(f"Unexpected JSON type: {type(data)} for {path}")
                else:
                    r.failure(f"{path} -> {r.status_code}")
            except Exception as e:
                r.failure(f"{path} -> JSON parse error: {e}")
    return None


class ECommerceUser(HttpUser):
    """
    Simulación de usuarios en el sistema e-commerce.
    Descubre rutas y luego ejerce carga sobre endpoints clave.
    """
    wait_time = between(1, 3)

    # Rutas elegidas dinámicamente
    products_base = None
    users_base = None
    orders_base = None

    product_ids = None  # cache de IDs válidos

    def on_start(self):
        """Detecta rutas válidas y realiza warmup."""
        # 1) Descubrir rutas
        self.products_base = pick_first_json_2xx(self.client, CANDIDATES_PRODUCTS, "PRODUCTS")
        self.users_base    = pick_first_json_2xx(self.client, CANDIDATES_USERS, "USERS")
        self.orders_base   = pick_first_json_2xx(self.client, CANDIDATES_ORDERS, "ORDERS")

        # 2) Warmup de productos (si hay ruta)
        self.product_ids = []
        if self.products_base:
            with self.client.get(self.products_base, headers=JSON_HEADERS, name="GET All Products (warmup)", catch_response=True) as r:
                if r.status_code == 200:
                    try:
                        data = r.json()
                        # si es dict, intenta lista en 'content' o similar
                        if isinstance(data, dict):
                            data = data.get("content") or data.get("items") or data.get("data") or []
                        if isinstance(data, list):
                            self.product_ids = [
                                (p.get("productId") or p.get("id"))
                                for p in data
                                if isinstance(p, dict) and (p.get("productId") or p.get("id")) is not None
                            ]
                        r.success()
                    except Exception as ex:
                        r.failure(f"JSON parse error: {ex}")
                else:
                    r.failure(f"Failed with status {r.status_code}")

    @task(4)
    def get_all_products(self):
        """✅ PRUEBA 1: Obtener lista de productos (endpoint más consultado)."""
        if not self.products_base:
            return  # no hay ruta válida, se omite sin fallar

        with self.client.get(self.products_base, headers=JSON_HEADERS, name="GET All Products", catch_response=True) as r:
            if r.status_code == 200:
                try:
                    data = r.json()
                    if isinstance(data, dict):
                        data = data.get("content") or data.get("items") or data.get("data") or []
                    if isinstance(data, list):
                        ids = [
                            (p.get("productId") or p.get("id"))
                            for p in data
                            if isinstance(p, dict) and (p.get("productId") or p.get("id")) is not None
                        ]
                        if ids:
                            self.product_ids = ids
                    r.success()
                except Exception as ex:
                    r.failure(f"JSON parse error: {ex}")
            else:
                r.failure(f"Failed with status {r.status_code}")

    @task(2)
    def get_product_by_id(self):
        """✅ PRUEBA 2: Obtener producto por ID (usa IDs válidos si existen)."""
        if not self.products_base:
            return

        candidate_ids = self.product_ids or [random.randint(1, 10)]
        product_id = random.choice(candidate_ids)

        # Intenta <base>/<id> (forma más común)
        path = f"{self.products_base.rstrip('/')}/{product_id}"

        with self.client.get(path, headers=JSON_HEADERS, name="GET Product By ID", catch_response=True) as r:
            if r.status_code in (200, 404):
                r.success()
            else:
                r.failure(f"Failed with status {r.status_code}")

    @task(2)
    def get_all_users(self):
        """✅ PRUEBA 3: Obtener lista de usuarios."""
        if not self.users_base:
            return

        with self.client.get(self.users_base, headers=JSON_HEADERS, name="GET All Users", catch_response=True) as r:
            if r.status_code == 200:
                try:
                    _ = r.json()
                    r.success()
                except Exception as ex:
                    r.failure(f"JSON parse error: {ex}")
            else:
                r.failure(f"Failed with status {r.status_code}")

    @task(2)
    def get_all_orders(self):
        """✅ PRUEBA 4: Obtener lista de órdenes."""
        if not self.orders_base:
            return

        with self.client.get(self.orders_base, headers=JSON_HEADERS, name="GET All Orders", catch_response=True) as r:
            if r.status_code == 200:
                try:
                    _ = r.json()
                    r.success()
                except Exception as ex:
                    r.failure(f"JSON parse error: {ex}")
            else:
                r.failure(f"Failed with status {r.status_code}")

    @task(1)
    def health_check_api_gateway(self):
        """✅ PRUEBA 5: Health check del API Gateway."""
        with self.client.get("/actuator/health", headers=JSON_HEADERS, name="Health Check", catch_response=True) as r:
            if r.status_code == 200:
                try:
                    _ = r.json()
                    r.success()
                except Exception as ex:
                    r.failure(f"JSON parse error: {ex}")
            else:
                r.failure(f"Failed with status {r.status_code}")
