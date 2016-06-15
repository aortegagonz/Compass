// Librerías con funciones de utilidad

// Devuelve un valor de tiempo en formato horas:minutos:segundos:milisegundos
//
// ms: tiempo en milisegundos
//
// ret cadena de caracteres con el tiempo formateado
function ret=miliseconds2time(ms)
    s = floor(ms/1000);
    m = floor(sg/60);
    h = floor(sg/3600);
    rest = ms - ((h*60 + m) * 60 + s) * 60;
    ret = sprintf("%d:%2d:%2d:%d", h, m, s, rest);
endfunction

// Calcula una interpolación spline cúbica para cada componente de un sensor a partir de una secuencia de tiempos de muestreo
//
// sampleData: estructura con los siguientes campos:
//      x: muestras del sensor x
//      y: muestras del sensor y
//      z: muestras del sensor z
//      m: muestras de la magnitud de cada vector
// timeRange: array con los tiempos en milisegundos en los que se tomó cada muestra
function sampleData = sampleSpling(sampleData, timeRange)
    sampleData.xs=splin(sampleData.t, sampleData.x);
    sampleData.ys=splin(sampleData.t, sampleData.y);
    sampleData.zs=splin(sampleData.t, sampleData.z);
    sampleData.ms=splin(sampleData.t, sampleData.m);
endfunction

// Calcula un rango temporal para interpolar las muestras de un recorrido, el rango se calcula al doble de la frecuencia de muestreo de la muestra original
//
// samples: muestras en el formato indicado por la función readPathSamples
//
// timeRange: valores de tiempo calculados
function timeRange = getTimeRange(samples)
    n=size(samples.t, "r");
    timeMax=samples.t(n);
    timeRange=linspace(0,timeMax,round(2*n));
endfunction

// Calcula un rango temporal para interpolar las muestras de una cuadrícula, el rango se calcula al doble de la frecuencia de muestreo de la muestra original
//
// grid: muestras en el formato indicado por la función readGridSamples
//
// timeRanges: matriz con las filas y columnas de la cuadrícula donde cada celda contiene los valores de tiempo calculados
function timeRanges = getGridTimeRanges(grid)
    timeRanges=[];
    for row=1:grid.rows
        for column = 1:grid.columns
            samples=grid.data(row,column);
            n=size(samples.t, "r");
            timeMax=samples.t(n);
            timeRange=linspace(0,timeMax,round(n/2));
            timeRanges(row,column) = struct("row", row, "column", column, "data", timeRange);
        end
    end                    
endfunction

// Interpola las muestras de un recorrido a para una secuencia temporal
// 
// samples: datos del recorrido en el formato documentado en la función readPathSamples
// timeRange: escala temporal
//
// result: datos del recorrido interpolados en el formato documentado en la función readPathSamples
function result = interpolateSamples(samples, timeRange)
    xs=splin(samples.t,samples.x);
    ys=splin(samples.t,samples.y);
    zs=splin(samples.t,samples.z);
    ms=splin(samples.t,samples.m);
    x= interp(timeRange, samples.t, samples.x, xs);
    y= interp(timeRange, samples.t, samples.y, ys);
    z= interp(timeRange, samples.t, samples.z, zs);
    m= interp(timeRange, samples.t, samples.m, ms);

    result = struct("name", samples.name, "samplingRate", samples.samplingRate, "x", x, "y", y, "z", z, "m", m, "t" , timeRange);
endfunction

// Interpola las muestras de una cuadrícula a para una secuencia temporal
// 
// grid: datos de la cuadrícula en el formato documentado en la función readGridSamples
// timeRange: escala temporal
//
// result: datos de la cuadrícula interpolados en el formato documentado en la función readPathSamples
function result = interpolateGridSamples(grid, timeRanges)
    result = struct("name", grid.name, "samplingRate", grid.samplingRate, "rows", grid.rows, "columns", grid.columns, "data", []);    
    for row=1:grid.rows
        for column = 1:grid.columns
            samples = grid.data(row,column);
            timeRange = timeRanges(row, column).data;
            xs=splin(samples.t,samples.x);
            ys=splin(samples.t,samples.y);
            zs=splin(samples.t,samples.z);
            ms=splin(samples.t,samples.m);
            x= interp(timeRange, samples.t, samples.x, xs);
            y= interp(timeRange, samples.t, samples.y, ys);
            z= interp(timeRange, samples.t, samples.z, zs);
            m= interp(timeRange, samples.t, samples.m, ms);

            result.data(row,column) = struct("row", row, "column", column, "x", x, "y", y, "z", z, "m", m, "t" , timeRange);
        end
    end                    
endfunction

// Normaliza dos vectores para que coincidan zus valores máximo y mínimo
//
// v1:vector base
// v2:vector a normalizar
//
// result: v2 normalizado
function result = normalize(v1, v2)
    min1 = min(v1);
    min2 = min(v2);
    max1 = max(v1);
    max2 = max(v2);
    c = (max1-min1)/(max2-min2);
    result = min1+ c*(v2-min2); 
endfunction

// Obtiene el nombre de un fichero añadiéndole un sufijo.
// si los datos son de una cuadrícula además se añade la fila y la columna
//
// data: pueden ser los datos de un recorrido o de una cuadrícula
// sufix: sufijo a añadir
//
// filename: nombre calculado para el fichero
function fileName = fileName(data, sufix)
    name=data.name + "_" + sufix;
    fileName= strsubst(name, " ", "_");

    if isfield(data, "row") then
        fileName = fileName + "_" + data.row + "_" + data.column;
    end
endfunction

// Calcula la moda de un vector con una precisión de dos decimales
//
// a: vector a procesar
//
// moda: calculo de la moda
function moda= moda2decimales(a)
    x=round(a*100)/100;
    t=tabul(x);
    [m,k]=max(t(:,2));
    moda = t(k,1);
endfunction