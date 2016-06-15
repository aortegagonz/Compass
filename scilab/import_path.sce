// Lee los datos de los recorridos y exporta las gráficas
function exportPathData(pathId)
    [pathName samplingRate] = getPathInfo(head, pathId);
    printf("Procesando path: %s\n", pathName);
    accelerometer = readPathSamples(head, data, pathId, 1);
    magneticField = readPathSamples(head, data, pathId, 2);
    gyroscope = readPathSamples(head, data, pathId, 4);
    globalMagneticField = readPathSamples(head, data, pathId, 100);
    
    pathTitle = sprintf("%s (%.2f hz)", pathName, samplingRate);
    exportPath(magneticField, pathTitle, "time (ms)", "Mafnetic Field (µT) ", "MF", "");
    exportPath(globalMagneticField, pathTitle, "time (ms)", "Magnetic Field in global coordinates (µT) ", "GMF", "");
    exportPath(accelerometer, pathTitle, "time (ms)", "Accelerometer (m/s2) ", "A", "");
    exportPath(gyroscope, pathTitle, "time (ms)", "Gyroscope (degrees) ", "G", "");
    
    exportPathSamplingRate([accelerometer], pathTitle + ". Sampling rate", ["A"] );
    exportPathSamplingRate([magneticField gyroscope], pathTitle + ". Sampling rate", ["MF" "G"] );
    
    timeRange=getTimeRange(accelerometer);
    intMagField=interpolateSamples(magneticField, timeRange);
    intAccelerometer=interpolateSamples(accelerometer, timeRange);
    intGyroscope=interpolateSamples(gyroscope, timeRange);
    intGlobalMagField=interpolateSamples(globalMagneticField, timeRange);
    
    exportInterpolatedPath(intMagField, pathTitle, "time (ms)", "Magnetic Field (µT)", "MF", "");
    exportInterpolatedPath(intGlobalMagField, pathTitle, "time (ms)", "Magnetic Field in global coordinates (µT) ", "GMF", "");
    exportInterpolatedPath(intAccelerometer, pathTitle, "time (ms)", "Accelerometer (m/s2)", "A", "");
    exportInterpolatedPath(intGyroscope, pathTitle, "time (ms)", "Gyroscope (degrees)", "G", "");
    
    exportPathComparation(magneticField, intMagField, pathTitle + ". Magnetic field vs Interpolated", "time (ms)", "Magnetic Field (µT)", "MF", "MFI", 0);
    exportPathComparation(accelerometer, intAccelerometer, pathTitle + ". Accelerometer vs Interpolated", "time (ms)", "Accelerometer (m/s2)", "AC", "ACI", 0);
    exportPathComparation(gyroscope, intGyroscope, pathTitle + ". Gyroscope vs Interpolated", "time (ms)", "Gyroscope (degrees)", "GI", "GII", 0);
    exportPathComparation(globalMagneticField, intGlobalMagField, pathTitle + ". Gobal reference magnetic field vs Interpolated", "time (ms)", "Magnetic Field (µT)", "GMF", "GMFI", 0);
    exportPathComparation(intMagField, intGlobalMagField, pathTitle + ". Interpolated magnetic field vs Interpolated", "time (ms)", "Magnetic Field (µT)", "MFI", "GMFI", 0);
    exportPathComparation(intGlobalMagField, intAccelerometer, pathTitle + ". Interpolated global magnetic field vs Interpolated accelerometer", "time (ms)", ["Magnetic Field (µT)" "Accelerometer (m/s2)"], "GMFI", "ACI", 1);
    exportPathComparation(intMagField, intAccelerometer, pathTitle + ". Interpolated magnetic field vs Interpolated accelerometer", "time (ms)", ["Magnetic Field (µT)" "Accelerometer (m/s2)"], "MFI", "ACI", 1);  
endfunction

exec("lib/lib.sce");
data = csvRead("compass_data/path_data.csv");
head = csvRead("compass_data/path.csv", ",", ".", "string");
win = scf();
win.figure_position = [0 0];
//win.figure_size = [2560 1440];
win.figure_size = [800 600];

ids=getPathIds(head)
n=size(ids,"r");
for i=1:n
for i=
    pathId=ids(i);
    exportPathData(pathId);
end

close(win);
