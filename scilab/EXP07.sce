// Media y desviación tipica de la frecuencia de muestreo de los paths con diferentes frecuencias de muestreo

function procesa(sensorType, sensor)
    disp(sensorType + ". " + sensor.name);
    printf("Media: %.2f\n", mean(sensor.tDif));
    printf("Desviación: %.2f\n", stdev(sensor.tDif));
endfunction

function procesaPath(pathId)
    accelerometer = readPathSamples(head, data, pathId, 1);
    magneticField = readPathSamples(head, data, pathId, 2);
    procesa("Accelerometro", accelerometer);    
    procesa("Campo Magnético", magneticField);    
endfunction


exec("lib/lib.sce");
data = csvRead("compass_data/path_data.csv");
head = csvRead("compass_data/path.csv", ",", ".", "string");
disp("Medias y desviaciones típicas de las frecuencias de muestreo");
ids=getPathIds(head)
n=size(ids,"r");
for i=1:n
    pathId=ids(i);
    procesaPath(pathId);
end
