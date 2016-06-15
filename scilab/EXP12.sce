// Histograma del campo magnético

function [ini, fin] = procesa(pathId)
    clf();
    magneticField = readPathSamples(head, data, pathId, 2);
    //ini=65;
    //fin=105;
    ini=round(min(magneticField.m));
    fin=round(max(magneticField.m));
    v=linspace(ini,fin,fin-ini+1);
    v1=v(2:fin-ini+1);
    [a b] = dsearch(magneticField.m,v);
    bar(v1, b);
    xtitle(magneticField.name, "Magnetic Field (µT)")
    export_path="export/path/hist/MF";
    exportPlot(fileName(magneticField, "MF"), export_path);
endfunction

exec("lib/lib.sce");
data = csvRead("compass_data/path_data.csv");
head = csvRead("compass_data/path_pasillo.csv", ",", ".", "string");
ids=getPathIds(head)
n=size(ids,"r");
iniG=999999;
finG=0;

for i=1:n
    pathId=ids(i);
    [ini, fin] = procesa(pathId);
    iniG=min(ini,iniG);
    finG=max(fin,finG);
end
disp(iniG, finG);
