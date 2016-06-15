// Librería de funciones para la importación de los datos en formato CVS

// Obtiene el nombre y frecuencia de muestreo de un recorrido
//
// head: resultado de leer el fichero de cabecera de recorridos mediante csvread
// pathId: Identificador del recorrido
//
// name: nombre del recorrido (columna 2)
// samplingRate: frecuencia de muestreo del recorrido(columna 3)
function [name, samplingRate] = getPathInfo(head, pathId)
    name="unnamed";
    [rows, columns] = size(head);
    for i=2:rows
    	if strtod(head(i,1)) == pathId then
            name=head(i,2);
            samplingRate=strtod(head(i,3));
        end
    end
endfunction

// Obtiene los identificadores de todos los recorridos
//
// head: resultado de leer el fichero de cabecera de recorridos mediante csvread
//
// ids: array con todos los identificadores
function ids = getPathIds(head)
    ids=[];
    [rows, columns] = size(head);
    for i=2:rows
        ids(i-1) = strtod(head(i,1));
    end
endfunction

// Obtiene los identificadores de todas las cuadrículas
//
// head: resultado de leer el fichero de cabecera de cuadrículas mediante csvread
//
// ids: array con todos los identificadores
function ids = getGridIds(head)
    ids=[];
    [rows, columns] = size(head);
    for i=2:rows
        ids(i-1) = strtod(head(i,1));
    end
endfunction

// Obtiene el nombre, número de filas y culumnas, y frecuencia de muestreo de una cuadrícula
//
// head: resultado de leer el fichero de cabecera de cuadrículas mediante csvread
// gridId: Identificador de la cuadrícula
//
// name: nombre de la cuadrícula (columna 2)
// rows: número de columnas de la cuadrícula (columna 3)
// columns: número de filas de la cuadrícula (columna 4)
// samplingRate: frecuencia de muestreo de la cuadrícula(columna 5)
function [name, rows, columns, samplingRate] = getGridInfo(head, gridId)
    name="unnamed";
    n = size(head, "r");
    for i=2:n
        if strtod(head(i,1)) == gridId then
            name=head(i,2);
            rows=strtod(head(i,3));
            columns=strtod(head(i,4));
            samplingRate=strtod(head(i,5));
        end
    end
endfunction

// Obtiene los datos asociados a un recorrido
//
// head: resultado de leer el fichero de cabecera de recorridos mediante csvread
// data: resultado de leer el fichero de datos de recorridos mediante csvread
// pathId: Identificador del recorrido
// sensorType: Tipo de sensor 
//      1->     Acelerómetro
//      2->     Campo magnético
//      4->     Giroscopio
//      100->   Campo magnético en coordenadas globales
//
// result: estructura con los datos del recorrido con los siguientes campos: 
//      name: nombre del recorrido
//      samplingRate: frecuencia de muestreo
//      x: array con los valores sampleados del eje x
//      y: array con los valores sampleados del eje y
//      z: array con los valores sampleados del eje z
//      m: array con los las magnitudes de cada vector
//      t: array con los tiempos en milisegundos en los que se tomó cada muestra
//      tDif: array con los tiempos en milisegundos entre una muestra y la anterior
function result = readPathSamples(head, data, pathId, sensorType)
    t=[]; tDif =[]; 
    x=[]; y=[]; z=[]; m=[]; 
    [name, samplingRate] = getPathInfo(head, pathId)
    tIni=0;
    [rows, columns] = size(data);
    samples=0;
    for i=2:rows
	   if data(i,1) == pathId & data(i,2) == sensorType then
            samples=samples+1;
            if (samples == 1) then
                tIni = data(i,3);
            end;
    		t(samples)=data(i,3) - tIni;
    		x(samples)=data(i,4);
    		y(samples)=data(i,5);
    		z(samples)=data(i,6);
    		m(samples)=sqrt(x(samples)^2+y(samples)^2+z(samples)^2);
            if (samples>2) then
                tDif(samples-1) = 1000.0 / (t(samples) - t(samples-1));
            end;
        end
    end
    tDif(1) = tDif(2);
    tDif(samples) = tDif(samples-1);
    result = struct("name", name, "samplingRate", samplingRate, "x", x, "y", y, "z", z, "m", m, "t", t, "tDif", tDif);
endfunction

// Obtiene los datos asociados a una cuadrícula
//
// head: resultado de leer el fichero de cabecera de cuadrículas mediante csvread
// data: resultado de leer el fichero de datos de cuadrículas mediante csvread
// gridId: Identificador de la cuadrícula
// sensorType: Tipo de sensor 
//      1->     Acelerómetro
//      2->     Campo magnético
//      4->     Giroscopio
//      100->   Campo magnético en coordenadas globales
//
// result: estructura con los datos del recorrido con los siguientes campos: 
//      name: nombre del recorrido
//      samplingRate: frecuencia de muestreo
//      rows: numero de filas
//      columumns: número de cuadrículas
//      data: array de tamaño rowsxcolumn con los datos. Cada valor es una estructura con los siguientes campos:
//          x: array con los valores sampleados del eje x
//          y: array con los valores sampleados del eje y
//          z: array con los valores sampleados del eje z
//          m: array con los las magnitudes de cada vector
//          t: array con los tiempos en milisegundos en los que se tomó cada muestra
//          tDif: array con los tiempos en milisegundos entre una muestra y la anterior
function result = readGridSamples(head, data, gridId, sensorType)
    [name, rows, columns, samplingRate] = getGridInfo(head, gridId);
    result = struct("name", name, "samplingRate", samplingRate, "rows", rows, "columns", columns, "data", []);
    samples = []; 
    tIni = [];
    for i=1:rows         
        for j=1:columns
            samples(i,j)=0;
            tIni(i,j)=0;
            result.data(i,j) = struct("row", i, "column", j, "x", [], "y", [], "z", [], "m", [], "t", [], "tDif", []);
        end
    end
     
    n = size(data, "r");
    for i=2:n
        disp(n-i);
       if data(i,1) == gridId & data(i,2) == sensorType then
            row=data(i,4);
            column=data(i,5);
            samples(row,column)=samples(row,column)+1;
            samp = samples(row,column);
            if (samp == 1) then
                tIni(row,column) = data(i,3);
            end;
            result.data(row,column).t(samp)=data(i,3) - tIni(row,column);
            result.data(row,column).x(samp)=data(i,6);
            result.data(row,column).y(samp)=data(i,7);
            result.data(row,column).z(samp)=data(i,8);
            result.data(row,column).m(samp)=sqrt(result.data(row,column).x(samp)^2+result.data(row,column).y(samp)^2+result.data(row,column).z(samp)^2);
            if (samp>2) then
                result.data(row,column).tDif(samp-1) = 1000.0 / (result.data(row,column).t(samp) - result.data(row,column).t(samp-1));
            end;
        end
    end

    for i=1:rows
        for j=1:columns
            samp = samples(i,j);
            result.data(i,j).tDif(1) = result.data(i,j).tDif(2);
            result.data(i,j).tDif(samp) = result.data(i,j).tDif(samp-1);
        end
    end
endfunction

// Obtiene los datos asociados a una cuadrícula. Esta función es una optimización de la función readGridSamples 
// pero utilizando cálculos vectoriales en lugar de bucles
//
// head: resultado de leer el fichero de cabecera de cuadrículas mediante csvread
// data: resultado de leer el fichero de datos de cuadrículas mediante csvread
// gridId: Identificador de la cuadrícula
// sensorType: Tipo de sensor 
//      1->     Acelerómetro
//      2->     Campo magnético
//      4->     Giroscopio
//      100->   Campo magnético en coordenadas globales
//
// result: estructura con los datos del recorrido con los siguientes campos: 
//      name: nombre del recorrido
//      samplingRate: frecuencia de muestreo
//      rows: numero de filas
//      columumns: número de cuadrículas
//      data: array de tamaño rowsxcolumn con los datos. Cada valor es una estructura con los siguientes campos:
//          x: array con los valores sampleados del eje x
//          y: array con los valores sampleados del eje y
//          z: array con los valores sampleados del eje z
//          m: array con los las magnitudes de cada vector
//          t: array con los tiempos en milisegundos en los que se tomó cada muestra
//          tDif: array con los tiempos en milisegundos entre una muestra y la anterior
function result = readGridSamplesv2(head, data, gridId, sensorType)
    [name, rows, columns, samplingRate] = getGridInfo(head, gridId);
    result = struct("name", name, "samplingRate", samplingRate, "rows", rows, "columns", columns, "data", []);
    for row=1:rows         
        for column=1:columns
            cellDataFilter = data(data(:,1) == gridId & data(:,2) == sensorType & data(:,4) == row & data(:,5) == column, :);
            result.data(row,column) = struct("row", row, "column", column, "x", [], "y", [], "z", [], "m", [], "t", [], "tDif", []);
            result.data(row,column).t = cellDataFilter(:,3);
            tIni = result.data(row,column).t(1);
            result.data(row,column).t = result.data(row,column).t - tIni;
            result.data(row,column).x = cellDataFilter(:,6);
            result.data(row,column).y = cellDataFilter(:,7);
            result.data(row,column).z = cellDataFilter(:,8);
            x=result.data(row,column).x;
            y=result.data(row,column).y;
            z=result.data(row,column).z;
            result.data(row,column).m = sqrt(x.^2+y.^2+z.^2);
            temp = diff(result.data(row,column).t);
            temp = temp';
            temp = 1000 ./ temp;
            temp = [0 temp];
            result.data(row,column).tDif = temp;
        end
    end     
endfunction
