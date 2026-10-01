# Post-contenido — Unidad 8: Patrones Arquitectónicos II

## Descripción
Repositorio del post-contenido de la Unidad 8 de Patrones de Diseño
de Software — Sexto Semestre. Sistema de seguimiento de hallazgos
de auditoria interna implementado con Clean Architecture (Parte 1)
y extendido con dashboard agregado y bitacora de trazabilidad
(Parte 2), sobre el mismo proyecto Spring Boot.

## Parte 1 — Clean Architecture (Hallazgos de Auditoria)
El proyecto organiza los cuatro circulos concentricos: Entities
(domain/, con el Aggregate Root HallazgoAuditoria y su maquina de
estados EstadoHallazgo), Use Cases (usecase/, con los puertos y sus
implementaciones), Interface Adapters (adapter/, con
HallazgoController y HallazgoRepositoryAdapter) y Frameworks &
Drivers (Spring Boot + JPA). La dependencia del codigo siempre
apunta hacia adentro, hacia domain/.

## Parte 2 — Analisis costo-beneficio de CQRS/Event Sourcing
| **Escala y carga** | El sistema operará con un volumen de usuarios sumamente reducido y sin concurrencia masiva real (sistema de ámbito académico / uso interno). No existe un desbalance de carga que exija infraestructura, bases de datos o stacks separados para lecturas y escrituras. |
| **Complejidad de las consultas** | Las métricas del dashboard (agrupaciones y promedios) son perfectamente alcanzables mediante proyecciones dinámicas e interface projections de Spring Data JPA con `GROUP BY` y `AVG` sobre el mismo esquema relacional. |
| **Consistencia** | El comité de auditoría no requiere datos proyectados en tiempo real con latencias de milisegundos. Es adecuado y esperado que el dashboard procese los cálculos bajo demanda al momento de la consulta. |
| **Naturaleza de la trazabilidad exigida** | Cumplimiento legal requiere una bitácora cronológica e inalterable de los cambios de estado. No se exige reconstruir el estado entero a partir de un "replay" de eventos (Event Sourcing), por lo que basta con una tabla auditada append-only. |
| **Señales de sobre-ingeniería (Sección 7.2)** | Implementar CQRS con dos bases de datos o Event Store completo añadiría sincronización asíncrona, eventual consistency y complejidad operativa desproporcionada para un equipo unipersonal. |

## Decisiones de diseño
1. Severidad como enum simple vs. EstadoHallazgo como enum con
   maquina de estados — `Severidad` representa una categoría estática sin transiciones ni reglas de negocio asociadas. Por el contrario, `EstadoHallazgo` controla la máquina de estados del ciclo de vida del hallazgo (`ABIERTO` -> `EN_REMEDIACION` -> `CERRADO` -> `REABIERTO`). Se encapsuló la regla de transición dentro del propio enum para garantizar que ninguna entidad viole las transiciones válidas.
2. PlanRemediacion como Value Object embebido vs. agregado separado
   — Siguiendo el criterio de *Límite de Consistencia Transaccional*, un hallazgo no puede pasar a `EN_REMEDIACION` ni `CERRADO` sin que exista un plan de remediación válido. Modelarlo como Value Object dentro del mismo Agregado (`HallazgoAuditoria`) garantiza que el plan y el hallazgo se persistan en la misma transacción sin inconsistencias.
3. CQRS/Event Sourcing completos vs. extension liviana del
   repositorio existente — Aplicando los criterios de la Sección 7 de la guía, un stack CQRS separado (con dos modelos y dos BD) representaría una clara *sobre-ingeniería*. La escala y carga del laboratorio no justifican la complejidad. Se optó por una extensión liviana en el puerto existente mediante proyecciones de lectura sobre JPA.
4. Bitacora simple (HistorialCambioEstado) vs. Event Store completo
   — Event Sourcing obligaría a eliminar la persistencia de estado del Aggregate Root y reconstruir `HallazgoAuditoria` reejecutando eventos. La necesidad del área de Cumplimiento se satisface al 100% mediante una tabla de auditoría de solo lectura (*append-only*), escrita en la misma transacción del cambio de estado.

## Cómo ejecutar
```
$ mvn spring-boot:run
```

## Herramientas utilizadas
- Java 17, Spring Boot 3.x, Spring Data JPA, H2
- Apache Maven, Postman/curl, Git, GitHub

## Conclusiones
Clean Architecture garantiza el desacoplamiento de todas las reglas de dominio presentadas por los framework en esta caso Spring Boot, otros patrones vistos en la unidad como CQRS y Event Sourcing realizan todo lo contrario añadiendo sobreingenieria en el codigo. Por esa razon es primordial analizar y saber la utilidad de los patrones principalmente en arquitectura.