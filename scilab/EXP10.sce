// Elimina los picos del campo magnético usando la componente y del giroscopio

function result = procesa(pathId, R)
    magneticField = readPathSamples(head, data, pathId, 2);
    gyroscope = readPathSamples(head, data, pathId, 4);
    timeRange=getTimeRange(gyroscope);
    intMagField=interpolateSamples(magneticField, timeRange);
    intGyroscope=interpolateSamples(gyroscope, timeRange);
    
    am=intGyroscope.y;
    maxA=max(am);
    minA=min(am);
    center=minA+(maxA-minA)/2;
    range=R*(maxA-minA)/100;
    filterA = am<minA+range;
    t=intMagField.t(filterA);
    x=intMagField.x(filterA);
    y=intMagField.y(filterA);
    z=intMagField.z(filterA);
    m=intMagField.m(filterA);
    result = struct("name", magneticField.name, "samplingRate", magneticField.samplingRate, "x", x, "y", y, "z", z, "m", m, "t", t);
    exportPath(result, result.name, "time (ms)", "Magnetic Field (µT) ", "MFS", "");
endfunction
exec("lib/lib.sce");
data = csvRead("compass_data/path_data.csv");
head = csvRead("compass_data/path_pasillo.csv", ",", ".", "string");
R=40;
ids=getPathIds(head)
n=size(ids,"r");
for i=1:n
    pathId=ids(i);
    procesa(pathId, R);
end
