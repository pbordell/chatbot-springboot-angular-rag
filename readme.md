## Local RAG Chatbot (Spring Boot + Angular + Pgvector) 🚀

Este proyecto es un chatbot autónomo y full-stack diseñado para funcionar como base de conocimiento interactiva para mi portafolio profesional. Implementa una arquitectura **RAG (Generación Aumentada por Recuperación) 100% local**, lo que significa que el sistema procesa, vectoriza y consulta la información de forma gratuita y privada sin depender de APIs de terceros (como OpenAI).

Cualquier reclutador o desarrollador puede clonar este repositorio y levantar todo el ecosistema (Frontend, Backend, Base de Datos e IA) con **un solo comando** gracias a Docker Compose.

---

## 🏗️ Arquitectura del Sistema

La aplicación está completamente contenedorizada y dividida en tres capas principales que se comunican dentro de una red aislada de Docker:

1.  **Frontend (Angular):** Una interfaz de usuario moderna (SPA) con una pantalla de chat fluida, scroll automático y gestión de estados para enviar peticiones HTTP al servidor. Servida de forma eficiente mediante **Nginx**.
2.  **Backend (Spring Boot 3 & Spring AI):** El núcleo del sistema. Utiliza la suite de **Spring AI** para gestionar el flujo de datos. Al arrancar, lee un archivo de texto plano (`conocimiento.txt`), lo fragmenta (*chunking*) y automatiza el proceso de embeddings.
3.  **Base de Datos Vectorial (Pgvector):** Una base de datos PostgreSQL con la extensión `pgvector` que almacena los fragmentos de texto indexados por su significado matemático.
4.  **Motor de IA Local (Ollama):** Ejecuta de forma nativa el modelo de embeddings `all-minilm` dentro de Docker para transformar las consultas del usuario en vectores semánticos.

---

## 🛠️ Tecnologías Utilizadas

*   **Frontend:** Angular, TypeScript, Nginx (Producción)
*   **Backend:** Java 17, Spring Boot 3, Spring AI, Maven
*   **Base de Datos:** PostgreSQL 16 + Pgvector
*   **Inteligencia Artificial:** Ollama (`all-minilm` embeddings)
*   **DevOps:** Docker, Docker Compose (Multi-stage builds)

---

## 🚀 Requisitos Previos

Solo necesitas tener instalado en tu sistema:
*   [Docker Desktop](https://docker.com) (que incluye Docker Compose).

*Nota: No necesitas instalar Java, Node.js, PostgreSQL ni Ollama en tu máquina local. Todo se ejecutará aislado dentro de los contenedores.*

---

## 📦 Despliegue Rápido (Clonar y Listo)

Sigue estos pasos en tu terminal para arrancar el proyecto:

1.  **Clonar el repositorio:**
    ```bash
    git clone https://github.com
    cd TU_REPOSITORIO
    ```

2.  **Levantar el ecosistema con Docker Compose:**
    ```bash
    docker-compose up --build
    ```

3.  **Acceder a la aplicación:**
    *   **Frontend (Chatbot UI):** Abre tu navegador en [http://localhost:4200](http://localhost:4200)
    *   **Backend (API Rest):** Disponible en [http://localhost:8080](http://localhost:8080)

---

## 📥 ¿Cómo se puebla la Base de Datos?

El proyecto está diseñado para ser totalmente autónomo. En la ruta `backend/src/main/resources/conocimiento.txt` puedes escribir tu biografía, experiencia, proyectos o habilidades en texto plano.

Una vez tengas levantada la apliación y hayas accedido al frontend, tendrás un botón en el apartado lateral para cargar el fichero y realizar la ingesta (puede tardar bastante). con eso ya podrás interactuar con el bot.

Es posible que tengas que instalar manualmente el modelo Llama 3.2 dentro de un contenedor de Docker con este comando: docker exec -it ollama-ai ollama pull llama3.2

---

## 🎯 Demostración de Habilidades Técnicas

Este proyecto de portafolio demuestra conocimientos sólidos en:
*   **Arquitectura de Software:** Separación de responsabilidades en capas (Client-Server Architecture).
*   **Ingeniería de IA Moderna:** Implementación práctica de RAG, Vector Stores y Embeddings.
*   **Ecosistema Java:** Uso avanzado de Spring Boot 4 y la nueva suite de Spring AI.
*   **Desarrollo Frontend:** Creación de servicios reactivos y componentes en Angular.
