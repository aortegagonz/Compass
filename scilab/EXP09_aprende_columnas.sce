// kNN localizar en un path la columna de un grid usando la media
function [xapp,yapp] =aprende(sensor, columnaInicial)
    gridMean=[];
    xapp=[];
    yapp=[];
    for row=1:sensor.rows
        for column=1:sensor.columns
            gridMean(row,column) = mean(sensor.data(row,column).m);
            i = (row-1)*sensor.columns+column;
            xapp(i) = gridMean(row,column);
            yapp(i)=columnaInicial+column;
        end
    end
endfunction

exec("lib/lib.sce");
data = csvRead("compass_data/grid_data.csv");
head = csvRead("compass_data/grid.csv", ",", ".", "string");
k=1;
magneticField1 = readGridSamplesv2(head, data, 1, 2);
magneticField2 = readGridSamplesv2(head, data, 2, 2);

[xapp1 yapp1] = aprende(magneticField1, 0);
valY1 = linspace(1, magneticField1.columns, magneticField1.columns) 
[xapp2 yapp2] = aprende(magneticField2, 1000);
valY2 = linspace(1001, 1000+magneticField2.columns, magneticField2.columns);

xapp=cat(1, xapp1, xapp2);
yapp=cat(1, yapp1, yapp2);
valY=cat(2, valY1, valY2);
