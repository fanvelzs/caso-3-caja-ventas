# Caso 3: Caja de ventas

## Ejecutar

Se necesita JDK 21. JavaFX 21.0.6 se descarga con Maven la primera vez.

En Windows, abrir `ejecutar.cmd`. En IntelliJ, abrir el `pom.xml`, seleccionar JDK 21 y ejecutar el método `main` de `App.java` con el botón verde. También se puede ejecutar el objetivo Maven `javafx:run`.

Desde una terminal con `JAVA_HOME` apuntando al JDK 21:

```powershell
.\mvnw.cmd javafx:run
```

## Código

El proyecto separa la ventana de la lógica:

- `src/main/resources/edu/uam/caso3/caja-ventas.fxml`: diseño de la ventana, colores, fuentes, botones y barras. No usa CSS.
- `src/main/java/edu/uam/caso3/App.java`: punto de entrada para iniciar JavaFX desde IntelliJ o Maven.
- `src/main/java/edu/uam/caso3/Ventana.java`: carga el FXML y abre la ventana.
- `src/main/java/edu/uam/caso3/CajaController.java`: maneja los botones, las ventas y los hilos.

Para cambiar la apariencia, edita `caja-ventas.fxml` dentro de `resources`. Los atributos `fx:id` conectan los controles con los campos `@FXML` del controlador. Los atributos `onAction` conectan los botones con los métodos `iniciar` y `cancelar`.

Después de cambiar el FXML, cierra y vuelve a ejecutar la aplicación para ver los cambios. Si IntelliJ no reconoce la dependencia `javafx-fxml`, recarga el proyecto Maven.

Ejecuta `App`, no `Ventana`. El lanzador separado evita el error «JavaFX runtime components are missing» al iniciar directamente desde el IDE con las dependencias en el classpath.

Tres hilos realizan cinco ventas de C$100 cada uno. Se usa synchronized para cambiar juntos el contador, la recaudación y el registro. Al completar deben existir 15 ventas y C$1500.

`Platform.runLater` actualiza la ventana desde el hilo de JavaFX. El botón Iniciar se desactiva mientras se trabaja. Otro hilo espera con `join` a que terminen los trabajadores antes de permitir un nuevo inicio. Al cerrar, se interrumpe el trabajo pendiente.

## Equipo

- Integrante 1: ____________________
- Integrante 2: ____________________
- Integrante 3: ____________________
- Repositorio: ____________________
