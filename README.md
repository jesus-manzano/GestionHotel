# 🏨 GestionHotel

**GestionHotel** es una aplicación desarrollada en **Spring Boot** que expone una API REST para la gestión de una cadena de hoteles.  
Permite administrar localidades, clientes, hoteles y reservas, así como consultar disponibilidad en función de fechas y número de habitaciones.

---

## 🚀 Características principales

- Registro y gestión de **localidades**, **clientes** y **hoteles**.  
- Asignación de **roles de administrador** mediante token.  
- Consulta de **hoteles disponibles** según localidad, rango de fechas y número de habitaciones.  
- Creación y gestión de **reservas** asociadas a clientes y hoteles.  

---

## ⚙️ Tecnologías utilizadas

- **Java 17**  
- **Spring Boot**  
- **Maven**  
- **REST API**  

---

## 📡 Endpoints principales

### 🔹 Localidades
- **POST** `/cadenaHoteles/localidades` → Crear una nueva localidad.  

### 🔹 Usuarios
- **POST** `/cadenaHoteles/clientes` → Crear un nuevo cliente.  
- **GET** `/cadenaHoteles/clientes/{dni}` → Obtener detalles de un cliente.  
- **GET** `/cadenaHoteles/makeAdmin/{dni}/{token}` → Convertir un cliente en administrador (requiere token).  

### 🔹 Hoteles
- **POST** `/cadenaHoteles/hoteles` → Crear un nuevo hotel.  
- **GET** `/cadenaHoteles/hoteles?localidad=...&fechaInicio=...&fechaFin=...&habSimple=...&habDoble=...`  
  → Consultar hoteles disponibles en una localidad.  

### 🔹 Reservas
- **POST** `/cadenaHoteles/hoteles/{hotel}/reservas` → Crear una nueva reserva en un hotel.  

---

## ▶️ Ejecución

1. Clonar el repositorio:  
   ```bash
   git clone https://github.com/tuusuario/GestionHotel.git
   cd GestionHotel

   mvn spring-boot:run
   ```

Finalmente accede a: http://localhost:8080/cadenaHoteles

## 🧪 Ejemplos de uso
- Crear un cliente:
    ```
    POST /cadenaHoteles/clientes
    Content-Type: application/json

    {
        "dni": "12345678A",
        "nombre": "Juan Pérez",
        "email": "juan@example.com"
    }
    ```
    <br>

- Obtener detalles de un cliente:
    ```
    GET /cadenaHoteles/clientes/12345678A
    ```
    <br>

- Convertir un cliente en administrador:
    ```
    GET /cadenaHoteles/makeAdmin/12345678A/DAEn22-23
    ```
    <br>

- Consultar disponibilidad de hoteles:
    ```
    GET /cadenaHoteles/hoteles?localidad=Jaen&fechaInicio=2023-07-01&fechaFin=2023-07-10&habSimple=1&habDoble=0
    ```
    <br>

- Crear una reserva:
    ```
    POST /cadenaHoteles/hoteles/1/reservas?fechaInicio=2023-07-01&fechaFin=2023-07-05&habSimple=1&habDoble=0
    Content-Type: application/json

    {
        "dni": "12345678A",
        "nombre": "Juan Pérez",
        "email": "juan@example.com"
    }
    ```
    <br>
