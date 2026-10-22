# Práctica 1 - TSP

## Ejecución

Abre la carpeta del proyecto en VS Code y ejecuta `Main.java`. La configuración se lee de `config.txt` en la carpeta raíz.

## Configuración

`OtrosParametros` contiene cinco enteros, en este orden: `K iteraciones estancamiento% tenenciaTabu oscilacion%`.

`Algoritmos=GRE GRA BL TA` activa los cuatro algoritmos. `TA` y `TABU` son alias. `Semillas` debe contener tres semillas distintas para el estudio probabilístico; usa como primera la indicada por el profesor.

## Clases principales

- `Problema`: lee las coordenadas TSP y calcula distancias bajo demanda, sin reservar una matriz de distancias n*n.
- `Greedy`: ordena las ciudades por suma de distancias al resto.
- `GreedyAleatorizado`: selecciona una ciudad aleatoriamente entre las primeras K disponibles.
- `BusquedaLocal`: primer mejor con DLB cíclico, operador 2-opt y evaluación factorizada para TSP simétrico.
- `BusquedaTabu`: selección del mejor vecino admisible, MCP, MLP, estancamiento y oscilación estratégica.
- `MemoriaTabu`: representa MCP y MLP con mapas dispersos para no reservar dos matrices cuadradas enormes.
- `OscilacionEstrategica`: intensifica con BL desde la mejor solución global o diversifica favoreciendo arcos poco frecuentes en MLP.

## Tabu en instancias grandes

En instancias con hasta 20.000 movimientos 2-opt posibles, Tabu examina el vecindario completo. En instancias mayores muestrea hasta 20.000 propuestas por iteración. Para `d18512.tsp` esto reduce la exploración de unos 171 millones a 20.000 propuestas por iteración y permite respetar las 10.000 iteraciones sin el coste cuadrático completo. El muestreo es estocástico y se controla con la semilla de cada ejecución. Conviene declarar esta decisión en el informe.

## Logs

Al ejecutar `Main`, se crea un archivo `.log` independiente por ejecución dentro de `logs/` en la carpeta desde la que se inicia el programa. El nombre incluye instancia, algoritmo, semilla y fecha/hora. Para BL y TA se registra cada movimiento aceptado con las dos ciudades asociadas al 2-opt y el coste resultante. El archivo termina con el coste final y el tiempo total; nunca se imprime el recorrido completo. GRA y GRE no tienen intercambios, así que su log contiene sus datos de ejecución y el resultado final.
