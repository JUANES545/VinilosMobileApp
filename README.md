# VinilosMobileApp

Este proyecto tiene como objetivo desarrollar una aplicación móvil para navegar y gestionar catálogos de vinilos basada en la versión web existente.

## 🍷 APK
A continuación proporcionamos el siguiente [APK](#) para la instalación de la aplicación.

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

4. **Permitir acceso a internet**

Asegúrate de que `AndroidManifest.xml` incluye:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

Ya está incluido por defecto en este proyecto.

5. **Compilar y ejecutar**

- Selecciona un dispositivo o emulador con Android Lollipop o superior
- Haz click en el botón **Run** ▶️ o presiona `Shift + F10`

---

### 🧪 Verificación rápida

Una vez instalada:

- La aplicación mostrará una pantalla inicial con una lista de álbumes simulados (con imágenes y texto)
- Podrás navegar entre secciones con la barra inferior
- Desde "Álbumes" puedes crear un nuevo álbum o ver detalles
