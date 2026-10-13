# Sistema de Biblioteca – CRUD en JavaFX con ArrayList

Aplicación de escritorio para gestionar los administradores (y lectores) de una
biblioteca. Los datos se guardan **temporalmente en un `ArrayList`**: no hay
base de datos ni archivos de datos, y la información se pierde al cerrar la
aplicación.

**Autor:** Joshua Odin Montoya Flores  
**Institución:** Universidad Tecnológica de Jalisco (UTJ) – Programación Orientada a Objetos

## Clase elegida para el CRUD

La práctica del CRUD se realiza sobre la clase **`Administrador`**, que ya existía
en el proyecto: es la persona que opera el sistema de la biblioteca. El proyecto
incluye también la clase `Lector` (de la actividad del árbol de herencia), que
reutiliza la misma lógica.

## Tecnologías

- Java 17 o superior
- JavaFX (FXML, `TableView`, `ComboBox`, `DatePicker`, `FileChooser`, `Alert`)
- Maven
- NetBeans

## Estructura del proyecto

```
src/main/java/edu/utj/evnd/poo/bibliotecaproyec/
├── model/        Persona (abstract), Administrador (final), Lector (final)
├── enums/        EstadoAdministrador
├── repository/   PersonaRepository<T> (ArrayList), AdministradorRepository, LectorRepository
├── service/      ImagenService (manejo de imágenes)
├── controller/   PersonaCrudController<T> (lógica CRUD), AdministradorController,
│                 LectorController, MenuController
├── App.java      Aplicación JavaFX
└── Main.java     Punto de entrada
src/main/resources/
├── menu.fxml, administrador.fxml, lector.fxml
└── styles/app.css
capturas/         Evidencias (CRUD, imágenes, estados y validaciones)
```

**Separación de responsabilidades:** el *modelo* guarda los datos y sus reglas,
el *repositorio* almacena en el `ArrayList`, el *servicio* maneja las imágenes,
el *controlador* contiene la lógica de cada ventana y los archivos *FXML/CSS*
solo definen la presentación.

## Funcionalidades

### Registrar (Create)
1. Pulsar **Nuevo** para habilitar el formulario.
2. Llenar los datos y pulsar **Registrar**.
3. Se validan los campos; si hay un error se muestra un `Alert` y el cursor
   queda en el campo incorrecto.
4. Si todo es correcto, se agrega al `ArrayList`, se actualiza la tabla, se
   limpian los campos y se muestra un mensaje de confirmación.

### Consultar (Read)
La tabla (`TableView`) muestra los atributos principales: ID, CURP, nombre,
apellido, correo, usuario, estado y rol. Es **de solo lectura**: sirve para
consultar y seleccionar, no se pueden editar las celdas. Las columnas se pueden
ordenar.

### Buscar
Campo de búsqueda con el botón **Buscar** (también con Enter). Busca en el
`ArrayList` por **ID, CURP, nombre o correo**. El botón **Mostrar todos** vuelve a
listar todos los registros.

### Actualizar (Update)
Flujo obligatorio: **Buscar → Cargar → Editar → Modificar → Actualizar**.
- *Buscar* muestra las coincidencias y *carga* la primera en el formulario.
- *Editar* habilita los campos (el ID no se puede cambiar) y el botón cambia a **Actualizar**.
- Al actualizar se vuelve a validar, se reemplaza el objeto del `ArrayList`
  (no se crea uno nuevo), se actualiza la tabla, se limpian los campos y se confirma.

### Eliminar (Delete)
Flujo obligatorio: **Buscar → Cargar → Eliminar**. Antes de borrar se muestra un
`Alert` de confirmación («¿Está seguro de eliminar el registro seleccionado?»).
Si se cancela, no se modifica nada.

> Editar y Eliminar permanecen deshabilitados hasta realizar una búsqueda y
> seleccionar un registro.

### Validaciones
| Dato | Regla |
|---|---|
| ID | Obligatorio, número entero positivo y no repetido |
| CURP | Obligatoria y no repetida |
| Nombre y apellido | Obligatorios |
| Fecha de nacimiento | Obligatoria, anterior a hoy y edad mínima de 18 años (administrador) |
| Correo | Vacío o con formato `usuario@dominio` |
| Teléfono | Vacío o exactamente 10 dígitos |
| Usuario | Obligatorio |
| Contraseña | Mínimo 4 caracteres |
| Estado | Se elige de una lista; no se puede escribir |

Las reglas se comprueban en la ventana antes de registrar o actualizar y,
además, el modelo (setters y constructores) las vuelve a validar.

### Estados
El estado del administrador se elige con un `ComboBox` cargado con los valores
del enum `EstadoAdministrador`: **ACTIVO, BLOQUEADO, SUSPENDIDO y BAJA**.

### Imágenes
- Selección con `FileChooser` (png, jpg, jpeg, gif; máximo 5 MB) y vista previa.
- Se puede reemplazar al editar el registro.
- **Estrategia de almacenamiento:** la imagen se copia a la carpeta `imagenes/`
  con un nombre único (`id_marcaDeTiempo.ext`) y el registro guarda solo la ruta
  relativa. Así no depende de la ubicación original, no se guardan bytes en
  memoria y al reemplazar o eliminar se borra la imagen anterior.
  La carpeta `imagenes/` se crea automáticamente y no se sube al repositorio.

### Navegación
Un menú superior permite pasar entre los módulos **Administradores** y
**Lectores** con presentación uniforme (`styles/app.css`). Los datos se
conservan al cambiar de módulo. Se muestra el nombre del autor en la barra superior.

## Ejecución

1. Instalar JDK 17 o superior.
2. Abrir el proyecto en NetBeans (o cualquier IDE con Maven).
3. Hacer **Clean and Build** y ejecutar la clase `Main`
   (clic derecho → *Run File*).

## Evidencias

Las capturas de pantalla están en la carpeta [`capturas/`](capturas/): registro,
validaciones, búsqueda, edición, eliminación con confirmación, imágenes y estados.
