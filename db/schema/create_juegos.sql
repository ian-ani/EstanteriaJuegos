-- juegos definition

CREATE TABLE juegos (
	id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	nombre TEXT NOT NULL,
	plataforma TEXT NOT NULL,
	estado TEXT NOT NULL,
	etiquetas TEXT,
	valoracion TEXT NOT NULL,
	notas TEXT
);