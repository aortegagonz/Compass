// kNN localizar en un path la fila de un grid usando la media ponderando las distancias
function [knnData, result, aciertosGrid, erroresGrid] = busca(xapp, yapp, sensor, k, valY, nrows, ncolumns, gridId)
    knnData=[]; grids=[]; rows=[]; columns=[];
    erroresGrid=0; aciertosGrid=0;
    result=zeros(nrows, ncolumns);
    n=size(sensor.m,"r");
    for i=1:n
        knnData(i) = struct("ypred" , 0, "tabkppv", [], "distance", []);
        v = sensor.m(i);
        [ypred,tabkppv,distance] = knn(xapp, yapp, valY,v, k);
        knnData(i).ypred=ypred;
        knnData(i).tabkppv=tabkppv;
        knnData(i).distance=distance;
        
        kdistance = gsort(distance,"g","i");
        kdistance = kdistance(1:k);
        tdistance = sum(kdistance);
        for j=1:size(tabkppv, "r")
            pondera=(tdistance-kdistance(j))/((k-1)*tdistance)
            ret=tabkppv(j);
            g = floor(ret/10000);
            grids(i)=g;
            ret = ret-grids(i)*10000;
            r = floor(ret/100);
            rows(i)=r;
            c = ret-rows(i)*100
            columns(i)=c;
            if g*10000==gridId then
                result(r,c)= result(r,c)+pondera;
                aciertosGrid = aciertosGrid +1;
                //printf("Acierto de grid pos=%d v=%.2f result=%d grid=%d row=%d column=%d\n", i, v, tabkppv(j), grids(i), rows(i), columns(i));
            else
                //printf("Error de grid pos=%d v=%.2f result=%d grid=%d row=%d column=%d\n", i, v, tabkppv(j), grids(i), rows(i), columns(i));
                erroresGrid = erroresGrid +1;
            end
        end
    end
    printf("Grid: %s -> aciertos:%d errores:%d porcentaje:%.2f\n", sensor.name, aciertosGrid, erroresGrid, 100*aciertosGrid/(aciertosGrid+erroresGrid));
    clf();
    surf(result);
    xtitle(sensor.name, "column", "row", "hits");
    a=get("current_axes");
    export_path="export/path/cell";
    sufix = sprintf("k_%d_r1",k);
    exportPlot(fileName(magneticField, sufix), export_path);
    sufix = sprintf("k_%d_r2",k);
    a.rotation_angles=[68,10];
    exportPlot(fileName(magneticField, sufix), export_path);

endfunction

exec("lib/lib.sce");
data = csvRead("compass_data/path_data.csv");
k=3;

head = csvRead("compass_data/path_vestibulo.csv", ",", ".", "string");
recorridos = [4 6 8 10 12 14 16 18 20];

for i=1:size(recorridos,"c")
    //break;
    taciertos=0;
    terrores=0;
    pathId=recorridos(1,i);
    magneticField = readPathSamples(head, data, pathId, 2);
    [knnData, result, aciertos, errores]=busca(xapp, yapp, magneticField, k, valY, 12,3,0);
    taciertos = taciertos + aciertos;
    terrores = terrores + errores;
    disp(round(result*100)/100);
    //return;
end
printf("Porcentaje Acierto en grid: %.2f\n", 100*taciertos/(taciertos+terrores));
    
head = csvRead("compass_data/path_pasillo.csv", ",", ".", "string");
//recorridos = [106 108 110 112 114 116 118 120 122];
recorridos = [107 109 111 113 115 117 119 121 123];
for i=1:size(recorridos,"c")
    taciertos=0;
    terrores=0;
    pathId=recorridos(1,i);
    magneticField = readPathSamples(head, data, pathId, 2);
    [knnData, result, aciertos, errores]=busca(xapp, yapp, magneticField, k, valY, 5,2,10000);
    taciertos = taciertos + aciertos;
    terrores = terrores + errores;
    disp(round(result*100)/100);
    //break;
end
printf("Porcentaje Acierto en path: %.2f\n", 100*taciertos/(taciertos+terrores))

