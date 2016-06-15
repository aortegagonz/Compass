// Media y desviación tipica los componentes del campo magnético

function procesa(sensorType, sensor)
    disp(sensorType + ". " + sensor.name);
    printf("Media X: %.2f\n", mean(sensor.x));
    printf("Desviación X: %.2f\n", stdev(sensor.x));
    printf("Media Y: %.2f\n", mean(sensor.y));
    printf("Desviación Y: %.2f\n", stdev(sensor.y));
    printf("Media Z: %.2f\n", mean(sensor.z));
    printf("Desviación Z: %.2f\n", stdev(sensor.z));
    printf("Media M: %.2f\n", mean(sensor.m));
    printf("Desviación M: %.2f\n", stdev(sensor.m));
endfunction

function procesaPath(pathId)
    magneticField = readPathSamples(head, data, pathId, 2);
    gmf = readPathSamples(head, data, pathId, 100);
    procesa("Campo Magnético en coordenadas globales", gmf);    
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
