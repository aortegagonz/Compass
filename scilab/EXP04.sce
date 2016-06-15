// kNN para buscar la celda dentro del grid usando la moda
function [xapp,yapp] =aprende(sensor)
    gridMean=[];
    xapp=[];
    yapp=[];
    
    for row=1:sensor.rows
        for column=1:sensor.columns
            gridMean(row,column) = moda2decimales(sensor.data(row,column).m);
            i = (row-1)*sensor.columns+column;
            xapp(i, 1) = [gridMean(row,column)];
            yapp(i)=i;
        end
    end
endfunction

function ret=busca(xapp, yapp, sensor, k)
    valY=linspace(1,sensor.rows*sensor.columns,sensor.rows*sensor.columns);
    ret=struct("aciertos", 0, "errores", 0, "data", []);
    probAcierto=[];
    for r=1:sensor.rows
        for c=1:sensor.columns
            printf("Buscando en %s [%d,%d]\n", sensor.name, r,c);
            ret.data(r,c) = struct("aciertos", 0, "errores", 0, "knn", []);
            esperado=(r-1)*sensor.columns+c;
            huellas(r,c)= xapp(esperado);
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
    disp("Huellas con la moda");
    disp(huellas);
    disp("Probabilidad de acierto");
    disp(probAcierto);
endfunction

exec("lib/lib.sce");
data = csvRead("compass_data/grid_data.csv");
head = csvRead("compass_data/grid.csv", ",", ".", "string");

k=1;
magneticField1 = readGridSamplesv2(head, data, 1, 2);
magneticField2 = readGridSamplesv2(head, data, 2, 2);

[xapp yapp] = aprende(magneticField2);
ret=busca(xapp, yapp, magneticField2, k);

[xapp yapp] = aprende(magneticField1);
ret=busca(xapp, yapp, magneticField1, k);
