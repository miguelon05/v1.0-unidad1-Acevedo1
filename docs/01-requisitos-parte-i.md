# 01. Parte I: lista de cumplimiento

Fuente: Examen Parcial U1, caso AndinaSalud, 13 páginas. Alcance actual: Parte I y preparación de Git. Responsable: Miguel Acevedo. Por indicación expresa del estudiante, el trabajo se adapta a modalidad individual y se omiten los requisitos grupales. Las solicitudes SC-A a SC-D pertenecen a la Parte II.

## 1. Requerimientos funcionales

| Código | Resultado verificable |
|---|---|
| RF-01 | Inicio: saludo, próxima cita programada y accesos a citas y solicitud. |
| RF-02 | Citas ordenadas por fecha/hora ascendente y filtro de estado. |
| RF-03 | Detalle completo y cancelación con confirmación. |
| RF-04 | Especialidad, sede, fecha, hora y motivo; errores por campo. |
| RF-05 | Buscar especialidad o médico sin distinguir mayúsculas ni tildes. |
| RF-06 | Datos del paciente y tema claro/oscuro inmediato. |
| RF-07 | Barra inferior Inicio/Citas/Perfil, detalle, formulario y regreso del sistema. |
| RF-08 | Carga de 800 ms, contenido, vacío y error recuperable. |

## 2. Reglas en dominio

| Código | Regla y límites |
|---|---|
| RN-01 | Rechazar fecha/hora anterior al instante actual. |
| RN-02 | Máximo tres citas Programadas por paciente. |
| RN-03 | Solo cancelar Programadas a más de 24 horas; exactamente 24 no permite. |
| RN-04 | Motivo entre 10 y 200 caracteres, inclusive. |
| RN-05 | No duplicar día y hora entre Programadas del mismo paciente. |

## 3. Datos y arquitectura

- Paciente fijo con documento, correo y teléfono.
- Sedes: Ñaña, Chosica, Chaclacayo y Santa Anita.
- Cinco especialidades y dos médicos por especialidad, con sedes asignadas.
- Seis citas: tres futuras Programadas, dos Atendidas y una Cancelada.
- Paquete `pe.upeu.andinasalud`: domain, data, presentation y di en commonMain.
- EstadoCita como sealed class con los datos específicos indicados en la hoja.
- Clean + MVVM, StateFlow, UiState, casos de uso y repositorio por interfaz.
- Compose, LazyColumn, Scaffold, Material 3 propio, Koin y corrutinas.
- Sin red, base de datos ni persistencia. Los datos se reinician al cerrar el proceso.

## 4. Observaciones de lectura

1. La hoja exige seis pantallas, aunque enumera cinco principales. Se separan Perfil y Ajustes: Inicio, Citas, Detalle, Solicitud, Perfil y Ajustes.
2. El anexo omite el teléfono del paciente: se incluye porque la sección 3.2 sí lo exige.
3. Las fechas fijas del anexo caducan. Se generarán en relación con el reloj actual.
4. Las tres citas iniciales alcanzan RN-02: primero cancelar una elegible para probar una solicitud válida.
5. La ejecución real en iOS exige una verificación en macOS/Xcode. Configurar el target no acredita ejecución.

## 5. Git individual: Miguel Acevedo

- Registrar tareas y avances del desarrollo individual.
- `main`: estable; `develop`: integración; `feature/<funcionalidad>-<apellido>` y `fix/<descripcion>-<apellido>`.
- Cada rama feature nace desde develop. No desarrollar directamente en main.
- Commits pequeños, en español y presente: `feat`, `fix`, `refactor`, `style`, `docs`.
- Al menos un commit por sesión; la hoja no fija un total mínimo para la Parte I.
- Usar ramas de funcionalidad con sufijo `-acevedo`. Omitir reparto grupal y revisión cruzada por indicación del estudiante.
- Integrar después de verificar. Etiquetar `v1.0-unidad1` solo al cierre sobre el commit evaluado.
- Tres commits propios durante el bloque de 120 minutos es un requisito de la Parte II, no sustituye el historial de la Parte I.
- No se fabrican autores, fechas de trabajo, revisiones ni evidencias de dispositivos.

## 6. Entrega

README con arquitectura, ejecución y autor individual; repositorio etiquetado; PDF con seis pantallas en cada plataforma y evidencias de Git. Las ramas `sc-<letra>-acevedo` se trabajan en la Parte II.
