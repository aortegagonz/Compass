// kNN para localizar el grid usando la media
function [xapp,yapp] =aprende(sensor, celda)
    gridMean=[];
    xapp=[];
    yapp=[];
    for row=1:sensor.rows
        for column=1:sensor.columns
            gridMean(row,column) = mean(sensor.data(row,column).m);
            i = (row-1)*sensor.columns+column;
            xapp(i, 1) = [gridMean(row,column)];
            yapp(i)=celda;
        end
    end
endfunction


function ret=busca(xapp, yapp, sensor, esperado, k)
    valY=linspace(1,2,2);

    ret=struct("aciertos", 0, "errores", 0, "data", []);
    probAcierto=[];
    for r=1:sensor.rows
        for c=1:sensor.columns
            printf("[%d,%d]\n", r,c);
            ret.data(r,c) = struct("aciertos", 0, "errores", 0, "knn", [])
            n= size(sensor.data(r,c).m,"r");
            for i=1:n
                v = sensor.data(r,c).m(i);
                ret.data(r,c).knn(i) = knn(xapp, yapp, valY,v, k)
                if ret.data(r,c).knn(i) == esperado then
                    ret.data(r,c).aciertos = ret.data(r,c).aciertos+1;
                else
                    ret.data(r,c).errores = ret.data(r,c).errores+1;
                end
            end
            ret.aciertos = ret.aciertos + ret.data(r,c).aciertos;
            ret.errores = ret.errores + ret.data(r,c).errores;
            probAcierto(r,c) = ret.data(r,c).aciertos/n
        end
    end
     disp("Probabilidad de acierto");
     disp(probAcierto);
endfunction

exec("lib/lib.sce");
data = csvRead("compass_data/grid_data.csv");
head = csvRead("compass_data/grid.csv", ",", ".", "string");

magneticField1 = readGridSamplesv2(head, data, 1, 2);
magneticField2 = readGridSamplesv2(head, data, 2, 2);

[xapp1 yapp1] = aprende(magneticField1, 1);
[xapp2 yapp2] = aprende(magneticField2, 2);

xapp=cat(1, xapp1, xapp2);
yapp=cat(1, yapp1, yapp2);
ret1=busca(xapp, yapp, magneticField1, 1, 1);
ret2=busca(xapp, yapp, magneticField2, 2, 1);

