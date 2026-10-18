# Práctica 1 - TSP

## Ejecución

Abre la carpeta del proyecto en VS Code y ejecuta `Main.java`. La configuración se lee de `config.txt` en la carpeta raíz.

## Configuración

`OtrosParametros` debe contener exactamente cinco enteros, en este orden:

`OtrosParametros=5 10000 5 10 50`

1. K del Greedy Aleatorizado (GRA).
2. Número máximo de iteraciones de BL y TA.
3. Porcentaje de estancamiento de TA.
4. Tenencia tabú de TA.
5. Porcentaje de oscilación/intensificación de TA (0 a 100).

Los valores predeterminados si se omite `OtrosParametros` son 5, 10000, 5, 10 y 50.

`Algoritmos=GRE GRA BL TA` activa los cuatro algoritmos. `TA` y `TABU` son alias. `Semillas` debe contener tres semillas distintas para el estudio probabilístico; usa como primera la semilla inicial que os haya indicado el profesor.

## Clases principales

- `Problema`: lee las coordenadas TSP y calcula distancias bajo demanda, sin reservar una matriz de distancias n*n.
- `Greedy`: ordena las ciudades por suma de distancias al resto.
- `GreedyAleatorizado`: selecciona una ciudad aleatoriamente entre las primeras K disponibles.
- `BusquedaLocal`: primer mejor con DLB, operador 2-opt y evaluación factorizada para TSP simétrico.
- `BusquedaTabu`: selección del mejor vecino admisible, MCP, MLP, estancamiento y oscilación estratégica.
- `MemoriaTabu`: representa MCP y MLP con mapas dispersos para no reservar dos matrices cuadradas enormes.
- `OscilacionEstrategica`: intensifica con BL desde la mejor solución global o diversifica favoreciendo arcos poco frecuentes en MLP.

## Aviso de rendimiento

La búsqueda tabú evalúa exhaustivamente los vecinos 2-opt. En una instancia con 18.512 ciudades existen aproximadamente 171 millones de movimientos 2-opt por iteración; 10.000 iteraciones son prohibitivas en tiempo. Para validar TA, empieza con `ch130.tsp`, `pr144.tsp` o `a280.tsp`. Ejecutar TA en `d18512.tsp` requiere una estrategia de vecindario reducido, que cambia la exploración exhaustiva y debe acordarse con el profesor.
