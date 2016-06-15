// Busca el campo magnético en los picos del acelerómetro (pasos)

function result = procesa(pathId, threshold)
    magneticField = readPathSamples(head, data, pathId, 2);
    accelerometer = readPathSamples(head, data, pathId, 1);
    timeRange=getTimeRange(accelerometer);
    intMagField=interpolateSamples(magneticField, timeRange);
    intAccelerometer=interpolateSamples(accelerometer, timeRange);
    clf();
    plot(intAccelerometer.t, intAccelerometer.m);

    dt=intAccelerometer.m;
    peaks=peak_detect(dt,threshold);
    m=intAccelerometer.m(peaks);
    t=intAccelerometer.t(peaks);
    plot(t,m, "*r");

    //m=intMagField.m(peaks);
    //plot(t,m);
    xtitle(magneticField.name, "time (ms)", "Accelerometer (m/s2)")
    export_path="export/path/peak/A";
    exportPlot(fileName(magneticField, "A"), export_path);
    result=peaks;
endfunction

exec("lib/lib.sce");
data = csvRead("compass_data/path_data.csv");
head = csvRead("compass_data/path_pasillo.csv", ",", ".", "string");
threshold=10.5;
ids=getPathIds(head)

n=size(ids,"r");
for i=1:n
    pathId=ids(i);
    procesa(pathId, threshold);
end
