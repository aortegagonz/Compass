// Media y desviación tipica de la frecuencia de muestreo de los grids
function [medias,desviaciones] = procesa(sensor)
    medias=[];
    desviaciones=[];
    for row=1:sensor.rows
        for column=1:sensor.columns
            data = sensor.data(row,column).tDif;
            medias(row,column) = mean(data);
            desviaciones(row,column) = stdev(data);
        end
    end
endfunction


exec("lib/lib.sce");
data = csvRead("compass_data/grid_data.csv");
head = csvRead("compass_data/grid.csv", ",", ".", "string");
gridId = 1;

magneticField = readGridSamplesv2(head, data, gridId, 2);
[medias,desviaciones] = procesa(magneticField);
printf("%s. Media de la frecuencia de muestreo del campo magnético", magneticField.name);
disp(medias);
printf("%s. Desviación típica de la frecuencia de muestreo del campo magnético", magneticField.name);
disp(desviaciones)

accelerometer = readGridSamplesv2(head, data, gridId, 1);
[medias,desviaciones] = procesa(accelerometer);
printf("%s. Media de la frecuencia de muestreo del acelerometro", accelerometer.name);
disp(medias);
printf("%s. Desviación típica de la frecuencia de muestreo del acelerometro", accelerometer.name);
disp(desviaciones)
