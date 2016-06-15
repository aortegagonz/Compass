// Librería de funciones para la exportación de gráficas a ficheros en formato png

// Exporta las gráficas asociadas a las medidas de un sensor en un recorrido, 
// se exportan tanto las gráficas vectoriales como las de la magnitud
// los ficheros se crean en los directorios:
//       ./export/path/vector/data/<leyendPrefix>
//       ./export/path/magnitude/data/<leyendPrefix>
//
// samples: datos del recorrido en el formato documentado en la función readPathSamples
// pathTitle: título a mostrar en la gráfica
// xAxisTitle: título a mostrar en el eje X
// yAxisTitle: título a mostrar en el eje Y
// leyendPrefix: prefijo a mostrar en cada leyenda (indica el tipo de medida, por ejemplo "MF" para el campo magnético
// lineChar: caracter que indica el tipo de línea a dibujar (ver parámetro LineSpec de la función plot de scilab)
function exportPath(samples, pathTitle, xAxisTitle, yAxisTitle, leyendPrefix, lineChar)
    export_path="export/path/vector/data" + "/" + leyendPrefix;
    plotSensor(samples, pathTitle, xAxisTitle, yAxisTitle, leyendPrefix, lineChar, 0);
    exportPlot(fileName(samples, leyendPrefix), export_path);
    
    export_path="export/path/magnitude/data" + "/" + leyendPrefix;
    plotSensor(samples, pathTitle, xAxisTitle, yAxisTitle, leyendPrefix, lineChar, 1);
    exportPlot(fileName(samples, leyendPrefix), export_path);
endfunction

// Exporta las gráficas asociadas a las medidas interpoladas de un sensor en un recorrido, 
// se exportan tanto las gráficas vectoriales como las de la magnitud
// los ficheros se crean en los directorios:
//       ./export/path/vector/interpolated/<leyendPrefix>
//       ./export/path/magnitude/interpolated/<leyendPrefix>
//
// samples: datos del recorrido en el formato documentado en la función readPathSamples
// pathTitle: título a mostrar en la gráfica
// xAxisTitle: título a mostrar en el eje X
// yAxisTitle: título a mostrar en el eje Y
// leyendPrefix: prefijo a mostrar en cada leyenda (indica el tipo de medida, por ejemplo "MF" para el campo magnético
// lineChar: caracter que indica el tipo de línea a dibujar (ver parámetro LineSpec de la función plot de scilab)
function exportInterpolatedPath(samples, pathTitle, xAxisTitle, yAxisTitle, leyendPrefix, lineChar)
    pathTitle = pathTitle + " [interpolated]";

    export_path="export/path/vector/interpolated" + "/" + leyendPrefix;
    plotSensor(samples, pathTitle, xAxisTitle, yAxisTitle, leyendPrefix, lineChar,0);
    exportPlot(fileName(samples, leyendPrefix), export_path);


    export_path="export/path/magnitude/interpolated" + "/" + leyendPrefix;
    plotSensor(samples, pathTitle, xAxisTitle, yAxisTitle, leyendPrefix, lineChar, 1);
    exportPlot(fileName(samples, leyendPrefix), export_path);
endfunction

// Exporta las gráficas asociadas a la frecuencia de muestreo de las medidas de un sensor en un recorrido, 
// los ficheros se crean en los directorios:
//       ./export/path/sampling_rate/<sufix>
// Se especifica un array de muestras para poder superponer en la misma gráfica los datos de varios sensores

// samples: array datos del recorrido. Cada componente es una muestra en el formato documentado en la función readPathSamples
// pathTitle: título a mostrar en la gráfica
// pathLegends: array con os sufijos a mostrar en la leyenda para cada sensor
function exportPathSamplingRate(samples, pathTitle, pathLegends)
    sufix="SR";
    [n m] = size(samples);
    for i=1:n
        sufix = sufix + "_" + pathLegends(i);
    end
    
    export_path="export/path/sampling_rate" + "/" + sufix;
    plotSensorSamplingRate(samples, pathTitle, pathLegends);
    exportPlot(fileName(samples(1), sufix), export_path);
endfunction

// Exporta las gráficas para comparar los valores de dos sensores
// se exportan tanto las gráficas vectoriales como las de la magnitud
// los ficheros se crean en los directorios:
//       ./export/path/vector/compare/CMP_<prefix1>_<prefix2>
//       ./export/path/magnitude/compare/CMP_<prefix1>_<prefix2>

// samples1: Muestras del primer sensor a comparar
// samples2: Muestras del segundo sensor a comparar
// pathTitle: título a mostrar en la gráfica
// xAxisTitle: título a mostrar en el eje X
// yAxisTitle: título a mostrar en el eje Y
// prefix1: prefijo a mostrar en la leyenda para el primer sensor
// prefix2: prefijo a mostrar en la leyenda para el segundo sensor
// normalizeMagnitude: indica si se deben normalizar las magnitudes para que coincidan los valores máximos y mínimos
function exportPathComparation(samples1, samples2, pathTitle, xAxisTitle, yAxisTitle, prefix1, prefix2, normalizeMagnitude)
    sufix="CMP_"+ prefix1+"_" + prefix2;

    export_path="export/path/vector/compare" + "/" + sufix;
    comparePlot(samples1, samples2, pathTitle, xAxisTitle, yAxisTitle, prefix1, prefix2, 0, 0);
    exportPlot(fileName(samples1, sufix), export_path);


    export_path="export/path/magnitude/compare" + "/" + sufix;
    comparePlot(samples1, samples2, pathTitle, xAxisTitle, yAxisTitle, prefix1, prefix2, 1, normalizeMagnitude);
    exportPlot(fileName(samples1, sufix), export_path);
endfunction

// Exporta las gráficas asociadas a las medidas de un sensor cada celda de una cuadrícula, 
// se exportan tanto las gráficas vectoriales como las de la magnitud
// los ficheros se crean en los directorios:
//       ./export/grid/vector/data/<leyendPrefix>
//       ./export/grid/magnitude/data/<leyendPrefix>
//
// samples: datos de la cuadrícula en el formato documentado en la función readGridSamples
// gridTitle: título a mostrar en la gráfica
// xAxisTitle: título a mostrar en el eje X
// yAxisTitle: título a mostrar en el eje Y
// leyendPrefix: prefijo a mostrar en cada leyenda (indica el tipo de medida, por ejemplo "MF" para el campo magnético
// lineChar: caracter que indica el tipo de línea a dibujar (ver parámetro LineSpec de la función plot de scilab)
function exportGrid(grid, gridTitle, xAxisTitle, yAxisTitle, leyendPrefix, lineChar)
    for row=1:grid.rows
        for column=1:grid.columns
            cellTitle = sprintf("%s [%d,%d]", gridTitle, row, column);
            sufix=sprintf("%s_%d_%d", leyendPrefix, row, column);
            
            export_path="export/grid/vector/data" + "/" + leyendPrefix;
            plotSensor(grid.data(row,column), cellTitle, xAxisTitle, yAxisTitle, leyendPrefix, lineChar, 0);
            exportPlot(fileName(grid, sufix), export_path);

            export_path="export/grid/magnitude/data" + "/" + leyendPrefix;
            plotSensor(grid.data(row,column), cellTitle, xAxisTitle, yAxisTitle, leyendPrefix, lineChar, 1);
            exportPlot(fileName(grid, sufix), export_path);
        end
    end
endfunction

// Exporta las gráficas para comparar los valores de dos sensores para cada celda de una cuadrícula
// se exportan tanto las gráficas vectoriales como las de la magnitud
// los ficheros se crean en los directorios:
//       ./export/grid/vector/compare/CMP_<prefix1>_<prefix2>
//       ./export/grid/magnitude/compare/CMP_<prefix1>_<prefix2>

// grid1: Muestras del primer sensor a comparar
// grid2: Muestras del segundo sensor a comparar
// gridTitle: título a mostrar en la gráfica
// xAxisTitle: título a mostrar en el eje X
// yAxisTitle: título a mostrar en el eje Y
// prefix1: prefijo a mostrar en la leyenda para el primer sensor
// prefix2: prefijo a mostrar en la leyenda para el segundo sensor
// normalizeMagnitude: indica si se deben normalizar las magnitudes para que coincidan los valores máximos y mínimos
function exportGridComparation(grid1, grid2, gridTitle, xAxisTitle, yAxisTitle, prefix1, prefix2, normalizeMagnitude)
    sufix="CMP_"+ prefix1+"_" + prefix2;
    for row=1:grid1.rows
        for column=1:grid1.columns
            export_path="export/grid/vector/compare" + "/" + sufix;
            cellTitle = sprintf("%s [%d,%d]", gridTitle, row, column);
            cellSufix=sprintf("%s_%d_%d", sufix, row, column);
            
            comparePlot(grid1.data(row,column), grid2.data(row,column), cellTitle, xAxisTitle, yAxisTitle, prefix1, prefix2, 0, 0);
            exportPlot(fileName(grid1, cellSufix), export_path);
        
        
            export_path="export/grid/magnitude/compare" + "/" + sufix;
            comparePlot(grid1.data(row,column), grid2.data(row,column), cellTitle, xAxisTitle, yAxisTitle, prefix1, prefix2, 1, normalizeMagnitude);
            exportPlot(fileName(grid1, cellSufix), export_path);
        end
    end
endfunction

// Exporta las gráficas asociadas a las medidas interpoladas de un sensor en un recorrido, 
// se exportan tanto las gráficas vectoriales como las de la magnitud
// los ficheros se crean en los directorios:
//       ./export/grid/vector/interpolated/<leyendPrefix>
//       ./export/grid/magnitude/interpolated/<leyendPrefix>
//
// grid: datos de la cuadrícula en el formato documentado en la función readGridSamples
// gridTitle: título a mostrar en la gráfica
// xAxisTitle: título a mostrar en el eje X
// yAxisTitle: título a mostrar en el eje Y
// leyendPrefix: prefijo a mostrar en cada leyenda (indica el tipo de medida, por ejemplo "MF" para el campo magnético
// lineChar: caracter que indica el tipo de línea a dibujar (ver parámetro LineSpec de la función plot de scilab)
function exportInterpolatedGrid(grid, gridTitle, xAxisTitle, yAxisTitle, leyendPrefix, lineChar)
    for row=1:grid.rows
        for column=1:grid.columns
            sufix=sprintf("%s_%d_%d", leyendPrefix, row, column);
            cellTitle = sprintf("%s [interpolated] [%d,%d]", gridTitle, row, column);

            export_path="export/grid/vector/interpolated" + "/" + leyendPrefix;
            plotSensor(grid.data(row,column), cellTitle, xAxisTitle, yAxisTitle, leyendPrefix, lineChar, 0);
            exportPlot(fileName(grid, sufix), export_path);

            export_path="export/grid/magnitude/interpolated" + "/" + leyendPrefix;
            plotSensor(grid.data(row,column), cellTitle, xAxisTitle, yAxisTitle, leyendPrefix, lineChar, 1);
            exportPlot(fileName(grid, sufix), export_path);
        end
    end            
endfunction

// Exporta las gráficas asociadas a la frecuencia de muestreo de las medidas de un sensor en una cuadrícula, 
// los ficheros se crean en los directorios:
//       ./export/grid/sampling_rate/<sufix>
// Se especifica un array de cuadrículas para poder superponer en la misma gráfica los datos de varios sensores

// grids: array datos de las cuadrículas. Cada componente es una muestra en el formato documentado en la función readgridSamples
// pathTitle: título a mostrar en la gráfica
// gridLegends: array con os sufijos a mostrar en la leyenda para cada sensor
function exportGridSamplingRate(grids, gridTitle, gridLegends)
    sufix="SR";
    [n m] = size(gridLegends);
    for i=1:n
        sufix = sufix + "_" + gridLegends(i);
    end
    export_path="export/grid/sampling_rate" + "/" + sufix;;
    [n m] = size(grids);
    for row=1:grids(1).rows
        for column=1:grids(1).columns
            sufix="SR";
            data=[];
            for i=1:m
                sufix = sufix + "_" + gridLegends(i);
                data(1,i) = grids(i).data(row,column);
            end
            cellSufix=sprintf("%s_%d_%d", sufix, row, column);
            plotSensorSamplingRate(data, gridTitle, gridLegends);
            exportPlot(fileName(grids(1), cellSufix), export_path);
        end
    end 
endfunction

// Exporta el último gráfico que se ha generado
// 
// plotTitle: nombre del fichero a generar
// path: directorio en el que se creará el fichero
function exportPlot(plotTitle, path) 
    mkdir(path);
    win=gcf();
    fileName= path+"/"+plotTitle+".png";
    xs2png(win, fileName);
endfunction
