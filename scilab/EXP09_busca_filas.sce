// kNN localizar en un path la fila de un grid usando la media
function [aciertos, errores, knnData]=busca(xapp, yapp, sensor, esperado, k, valY)

    aciertos=0;
    errores=0;
    knnData=[];
    n=size(sensor.m,"r");
    for i=1:n
        knnData(i) = struct("ypred" , 0, "tabkppv", [], "distance", []);
        v = sensor.m(i);
        [ypred,tabkppv,distance] = knn(xapp, yapp, valY,v, k);
        knnData(i).ypred=ypred;
        knnData(i).tabkppv=tabkppv;
        knnData(i).distance=distance;
        if knnData(i).ypred == esperado then
            aciertos = aciertos+1;
        else
            errores = errores+1;
        end
    end
     printf("%s-> aciertos:%d errores:%d prob.acierto: %.2f\n", sensor.name, aciertos, errores, aciertos*100/n);
endfunction

exec("lib/lib.sce");
data = csvRead("compass_data/path_data.csv");
head = csvRead("compass_data/path_vestibulo.csv", ",", ".", "string");
recorridos=[];
recorridos = [3 5 7; 9 11 13; 15 17 19];
for i=1:size(recorridos,"r")
    for j=1:size(recorridos,"c")
        pathId=recorridos(i,j);
        magneticField = readPathSamples(head, data, pathId, 2);
        [aciertos, errores, knnData]=busca(xapp, yapp, magneticField, 1000+i, k, valY);
    end
end


head = csvRead("compass_data/path_pasillo.csv", ",", ".", "string");
recorridos=[];
recorridos = [106 108 110; 112 114 116; 118 120 122];
for i=1:size(recorridos,"r")
    for j=1:size(recorridos,"c")
        pathId=recorridos(i,j);
        magneticField = readPathSamples(head, data, pathId, 2);
        [aciertos, errores, knnData]=busca(xapp, yapp, magneticField, i, k, valY);
    end
end



