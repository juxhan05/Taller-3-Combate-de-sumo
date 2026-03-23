# Control de Versiones con Git

## 1. Estrategia de Ramas
- **main**: Rama principal donde se integran las funcionalidades ya probadas y decididas.
- **master**: Rama de desarrollo donde se realizan pequeños cambios, pruebas y ajustes antes de pasar a `main`.

## 2. Flujo de Trabajo
1. Se crean nuevas funcionalidades en la rama `master`.
2. Una vez probadas y validadas, se realiza un **merge** hacia la rama `main`.
3. La rama `main` contiene siempre la versión estable del proyecto.

## 3. Historial de Cambios

### Commit inicial
- **Autor**: Ziheaus
- **Rama**: main
- **Acción**: Push
- **Descripción**: Se definieron los estándares de paquetes   
  Se agregó el archivo `.properties` con las **82 técnicas oficiales de kimarite** para ser cargadas desde la carpeta `data`.

- **Commit inicial**: Creación del proyecto base con estructura MVC.
- **Commit X**: [Aquí escribimos el cambio que hiciste, por ejemplo: "Implementación de la clase Luchador con atributos básicos"].
- **Commit Y**: [Ejemplo: "Configuración del archivo de propiedades para conexión a BD"].
- **Commit Z**: [Ejemplo: "Merge de master a main con la funcionalidad de combates"].

## 4. Fusiones (Merge)
- Fecha: [dd/mm/aaaa]
- Rama origen: `master`
- Rama destino: `main`
- Descripción: [Ejemplo: "Se integró la lógica de combate con selección aleatoria de kimarites"].

## 5. Versiones del Proyecto
- **Versión 0.1**: Proyecto inicial con estructura básica.
- **Versión 0.2**: Se añade conexión a BD y DAO.
- **Versión 0.3**: Se implementa lógica de combates concurrentes.
- **Versión final (1.0)**: Proyecto completo con interfaz gráfica y persistencia en archivos.


