// Lee los datos de los grids y exporta las gráficas
function exportGridData(gridId)
    [gridName, rows, columns, samplingRate] = getGridInfo(head, gridId);
    printf("Procesando grid: %s\n", gridName);
    accelerometer = readGridSamplesv2(head, data, gridId, 1);
    magneticField = readGridSamplesv2(head, data, gridId, 2);
    gyroscope = readGridSamplesv2(head, data, gridId, 4);
    globalMagneticField = readGridSamplesv2(head, data, gridId, 100);
    gridTitle = sprintf("%s (%.2f hz)", gridName, samplingRate);

    exportGrid(magneticField, gridTitle, "time (ms)", "Mafnetic Field (µT)", "MF", "");
    exportGrid(globalMagneticField, gridTitle, "time (ms)", "Magnetic Field in global coordinates (µT) ", "GMF", "");
    exportGrid(accelerometer, gridTitle, "time (ms)", "Accelerometer (m/s2) ", "A", "");
    exportGrid(gyroscope, gridTitle, "time (ms)", "Gyroscope (degrees) ", "G", "");

    exportGridSamplingRate([magneticField gyroscope], gridTitle +". Sampling Rate", ["MF" "G"] );
    exportGridSamplingRate([accelerometer], gridTitle +". Sampling Rate", ["A"] );  
    
    timeRanges=zeros(rows, columns);
    intMagField = magneticField;
    timeRanges=getGridTimeRanges(accelerometer);
    intMagField=interpolateGridSamples(magneticField, timeRanges);
    intAccelerometer=interpolateGridSamples(accelerometer, timeRanges);
    intGyroscope=interpolateGridSamples(gyroscope, timeRanges);
    intGlobalMagField=interpolateGridSamples(globalMagneticField, timeRanges);
    
    exportInterpolatedGrid(intMagField, gridTitle, "time (ms)", "Magnetic Field (µT)", "MF", "");
    exportInterpolatedGrid(intGlobalMagField, gridTitle, "time (ms)", "Magnetic Field in global coordinates (µT) ", "GMF", "");
    exportInterpolatedGrid(intAccelerometer, gridTitle, "time (ms)", "Accelerometer (m/s2)", "A", "");
    exportInterpolatedGrid(intGyroscope, gridTitle, "time (ms)", "Gyroscope (degrees)", "G", "");


    exportGridComparation(magneticField, intMagField, gridTitle + ". Magnetic field vs Interpolated", "time (ms)", "Magnetic Field (µT)", "MF", "MFI", 0);
    exportGridComparation(accelerometer, intAccelerometer, gridTitle + ". Accelerometer vs Interpolated", "time (ms)", "Accelerometer (m/s2)", "AC", "ACI", 0);
    exportPathComparation(gyroscope, intGyroscope, gridTitle + ". Gyroscope vs Interpolated", "time (ms)", "Gyroscope (degrees)", "GI", "GII", 0);
    exportGridComparation(globalMagneticField, intGlobalMagField, gridTitle + ". Gobal reference magnetic field vs Interpolated", "time (ms)", "Magnetic Field (µT)", "GMF", "GMFI", 0);
    exportGridComparation(intMagField, intGlobalMagField, gridTitle + ". Interpolated magnetic field vs Interpolated", "time (ms)", "Magnetic Field (µT)", "MFI", "GMFI", 0);
    exportGridComparation(intGlobalMagField, intAccelerometer, gridTitle + ". Interpolated global magnetic field vs Interpolated accelerometer", "time (ms)", ["Magnetic Field (µT)" "Accelerometer (m/s2)"], "GMFI", "ACI", 1);
    exportGridComparation(intMagField, intAccelerometer, gridTitle + ". Interpolated magnetic field vs Interpolated accelerometer", "time (ms)", ["Magnetic Field (µT)" "Accelerometer (m/s2)"], "MFI", "ACI", 1);  
endfunction

exec("lib/lib.sce");
data = csvRead("compass_data/grid_data.csv");
head = csvRead("compass_data/grid.csv", ",", ".", "string");
win = scf();
win.figure_position = [0 0];
win.figure_size = [800 600];
ids=getGridIds(head);
n=size(ids,"r");
for i=1:n
    gridId = ids(i);
    exportGridData(gridId);
end

close(win);
