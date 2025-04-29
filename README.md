# VinilosMobileApp

Este proyecto tiene como objetivo desarrollar una aplicación móvil para navegar y gestionar catálogos de vinilos basada en la versión web existente.

## 🍷 APK
A continuación proporcionamos el siguiente [APK](https://github.com/JUANES545/VinilosMobileApp/releases/download/2.0.0/VinilosMobileApp.apk) para la instalación de la aplicación.

## 🛠️ Instrucciones para construir la aplicación localmente

Sigue estos pasos para clonar y correr el proyecto en tu máquina local:

### ✅ Requisitos previos

- Android Studio **Hedgehog o superior**
- JDK **17 o superior**
- Gradle **compatibilidad automática desde Android Studio**
- Dispositivo físico o emulador con **Android Lollipop (API 21)** o superior
- Conexión a internet (el proyecto carga imágenes desde URLs externas)

---

### 🧾 Pasos

1. **Clonar el repositorio**

```bash
git clone https://github.com/JUANES545/VinilosMobileApp.git
cd VinilosMobileApp
```

2. **Abrir en Android Studio**

- Abrir Android Studio
- Click en **"Open an existing project"**
- Selecciona la carpeta del repositorio clonado

3. **Sincronizar el proyecto**

Android Studio detectará automáticamente el archivo `libs.versions.toml` y descargará las dependencias.

4. Desplegar el [Backend](https://github.com/MISW-4104-Web/BackVynils) Local/Remoto

Ya está incluido por defecto en la aplicación, consumiendo desde un [servidor](https://dev2.gestionhq5.com.co/) dedicado a este proyecto. 
Pero en caso de fallar, se recomienda ejecutar en local el servidor proveniente del [repositorio](https://github.com/MISW-4104-Web/BackVynils) provisto en clase y ajustar manualmente el BASE_URL del proyecto dentro del archivo *RetrofitClient.kt*.

5. **Compilar y ejecutar**

- Selecciona un dispositivo o emulador con Android Lollipop o superior
- Haz click en el botón **Run** ▶️

---

### 🧪 Verificación rápida

Una vez instalada:

- La aplicación mostrará una pantalla inicial con la lista de álbumes  (con imágenes y texto), en caso de existir, de lo contrario se pueden crear siguiendo las idicaciones de la App.
- Podrás navegar entre secciones con la barra inferior
- Desde "Álbumes" puedes crear un nuevo álbum o ver detalles
- En la sección de crear álbum también podrás agregar comentarios y listas de musica, entre otras funcionalidades.
