// kNN para localizar la columna del grid usando la media
function [xapp,yapp] =aprende(sensor, baseCelda)
    gridMean=[];
    xapp=[];
    yapp=[];
    for row=1:sensor.rows
        for column=1:sensor.columns
            gridMean(row,column) = mean(sensor.data(row,column).m);
            i = (row-1)*sensor.columns+column;
            xapp(i) = gridMean(row,column);
            yapp(i)=baseCelda + column;
            printf("Huella(%d,%d,%d)=[%.2f,%d]\n", row, column, baseCelda, xapp(i), yapp(i));
        end
    end
endfunction


function ret=busca(xapp, yapp, valY, sensor, baseCelda, k)
    ret=struct("aciertos", 0, "errores", 0, "data", []);
    probAcierto=[];
    for r=1:sensor.rows
        for c=1:sensor.columns
            esperado = baseCelda+c;
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

[xapp1 yapp1] = aprende(magneticField1, 0);
valY1 = linspace(1, magneticField1.columns, magneticField1.columns);
valY2 = linspace(1001, 1000 + magneticField2.columns, magneticField2.columns);
[xapp2 yapp2] = aprende(magneticField2, 1000);

xapp=cat(1, xapp1, xapp2);
yapp=cat(1, yapp1, yapp2);
valY=cat(2, valY1, valY2);
ret1=busca(xapp, yapp, valY, magneticField1, 0, 1);
ret2=busca(xapp, yapp, valY, magneticField2, 1000, 1);

