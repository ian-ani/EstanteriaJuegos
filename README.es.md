[![Lang en](https://img.shields.io/badge/lang-en-blue?style=flat)](https://github.com/ian-ani/EstanteriaJuegos/blob/main/README.md)
[![Lang es](https://img.shields.io/badge/lang-es-red?style=flat)](https://github.com/ian-ani/EstanteriaJuegos/blob/main/README.es.md)

## Tabla de contenidos

- [Acerca de este repositorio](#acerca-de-este-repositorio)
- [Capturas](#capturas)
- [Funcionalidades](#funcionalidades)
- [Instalación y ejecución](#instalación-y-ejecución)
- [Almacenamiento de datos](#almacenamiento-de-datos)
- [Tecnologías utilizadas](#tecnologías-utilizadas)
- [Estado del proyecto](#estado-del-proyecto)
- [Problemas conocidos](#problemas-conocidos)

## Acerca de este repositorio

Programa de escritorio desarrollado en Java que permite registrar los juegos que tienes físicos (¡o digitales!) en tu colección.  
Probado en **Windows 10**.

## Capturas

¡Por añadir!

## Funcionalidades

- **Añadir** juegos a la colección
- **Ver y editar** juegos de la colección
- **Borrar** juegos de la colección
- **Buscar** juegos por nombre o plataforma
- **Ordenar alfabéticamente** por nombre, plataforma, estado, etiquetas (género), valoración personal o comentarios
- **No admite duplicados**: se considera duplicado si coincide nombre y plataforma
- Máximo **tres etiquetas** por juego
- Interfaz **disponible en castellano e inglés**
- **Exportar** la colección a un CSV

## Instalación y ejecución

Por el momento no existe ninguna *release*, pero ampliaré esta sección una vez sea publicada.

## Almacenamiento de datos

- Utiliza **SQLite** por requerir muy poca configuración y ser liviana
- El esquema de la base de datos está en castellano
- Los datos se almacenan localmente
- La colección puede exportarse a CSV desde el propio programa

## Tecnologías utilizadas

- **JDK 24** (Oracle OpenJDK 24.0.2)
- **Java Swing**
- **SQLite**
- **Driver JDBC** de SQLite (versión 3.53.2.0)
- **GSON 2.10.1**

Estas tecnologías solamente son pertinentes para quien desee compilar o desarrollar más el programa.

## Estado del proyecto

Lanzamiento pendiente.

## Problemas conocidos

Sin problemas conocidos. Si encuentras alguno, ¡por favor házmelo saber abriendo una *Issue*!
