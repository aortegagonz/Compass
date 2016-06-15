// Librería de funciones para dibujar gráficas


// Dibuja la gráfica asociadas a las medidas de un sensor en un recorrido, 
//
// samples: datos del recorrido en el formato documentado en la función readPathSamples
// plotTitle: título a mostrar en la gráfica
// xAxisTitle: título a mostrar en el eje X
// yAxisTitle: título a mostrar en el eje Y
// leyendPrefix: prefijo a mostrar en cada leyenda (indica el tipo de medida, por ejemplo "MF" para el campo magnético
// lineChar: caracter que indica el tipo de línea a dibujar (ver parámetro LineSpec de la función plot de scilab)
// onlyMagnitude: indica si se debe dibujar solo la magnitud o también los componentes vectoriales
function plotSensor(samples, plotTitle, xAxisTitle, yAxisTitle, legendPrefix, lineChar, onlyMagnitude) 
    if onlyMagnitude == 1 then
        decorations = ["r"];
        plotLegends = ["m"]
    else
        decorations = ["b" "g" "k" "r"];
        plotLegends = ["x" "y" "z" "m"]
    end
    for i=1:size(decorations, "c")
        decorations(i) = lineChar + decorations(i);
        plotLegends(i) = legendPrefix + "_" + plotLegends(i);
    end

    clf();
    xtitle(plotTitle, xAxisTitle, yAxisTitle);
    if onlyMagnitude == 1 then
        plot(samples.t, samples.m, decorations(1));
    else
        plot(samples.t, samples.x, decorations(1));
        plot(samples.t, samples.y, decorations(2));
        plot(samples.t, samples.z, decorations(3));
        plot(samples.t, samples.m, decorations(4));
    end
    legend(plotLegends, "out_upper_right");
endfunction

// Dibuja las gráficas para comparar los valores de dos sensores

// samples1: Muestras del primer sensor a comparar
// samples2: Muestras del segundo sensor a comparar
// plotTitle: título a mostrar en la gráfica
// xAxisTitle: título a mostrar en el eje X
// yAxisTitle: título a mostrar en el eje Y
// prefix1: prefijo a mostrar en la leyenda para el primer sensor
// prefix2: prefijo a mostrar en la leyenda para el segundo sensor
// onlyMagnitude: indica si se debe dibujar solo la magnitud o también los componentes vectoriales
// normalizeMagnitude: indica si se deben normalizar las magnitudes para que coincidan los valores máximos y mínimos
function comparePlot(samples1, samples2, plotTitle, xAxisTitle, yAxisTitle, prefix1, prefix2, onlyMagnitude, normalizeSamples) 
    clf();
    xtitle(plotTitle, xAxisTitle, yAxisTitle);
    if onlyMagnitude == 1 then
        if normalizeSamples then
            m2 = normalize(samples1.m, samples2.m);
        else
            m2 = samples2.m;
        end
        plot(samples1.t, samples1.m, "b");
        plot(samples2.t, m2, "r");
        plotLegends(1) = prefix1;
        plotLegends(2) = prefix2 + "(norm.)";
    else
        if normalizeSamples then
            x2 = normalize(samples1.x, samples2.x);
            y2 = normalize(samples1.y, samples2.y);
            z2 = normalize(samples1.z, samples2.z);
            m2 = normalize(samples1.m, samples2.m);
        else
            x2 = samples2.x;
            y2 = samples2.y;
            z2 = samples2.z;
            m2 = samples2.m;
        end
        decorations1= ["b" "g" "k" "r"];
        decorations2= ["c" "m" "y" "*r"];
        sufix = ["x" "y" "z" "m"]
        for i=1:4
            plotLegends(i) = prefix1 + "_" + sufix(i);
            plotLegends(i+4) = prefix2 + "_" + sufix(i);
        end
        plot(samples1.t, samples1.x, decorations1(1));
        plot(samples1.t, samples1.y, decorations1(2));
        plot(samples1.t, samples1.z, decorations1(3));
        plot(samples1.t, samples1.m, decorations1(4));
    
        plot(samples2.t, x2, decorations2(1));
        plot(samples2.t, y2, decorations2(2));
        plot(samples2.t, z2, decorations2(3));
        plot(samples2.t, m2, decorations2(4));
    end

    legend(plotLegends, "out_upper_right");
endfunction


// Dibuja las gráficas asociadas a la frecuencia de muestreo de las medidas de un sensor en un recorrido
// Se especifica un array de muestras para poder superponer en la misma gráfica los datos de varios sensores

// samples: array datos del recorrido. Cada componente es una muestra en el formato documentado en la función readPathSamples
// plotTitle: título a mostrar en la gráfica
// plotLegends: array con os sufijos a mostrar en la leyenda para cada sensor
function plotSensorSamplingRate(samples, plotTitle, plotLegends) 
    clf();
    decorations = ["b" "g" "k" "r"];
    [n m] = size(samples);
    for sensor=1:m
        plot(samples(sensor).t, samples(sensor).tDif, decorations(sensor));
    end
    xtitle(plotTitle, "time (ms)", "Sampling Rate (Hz)");
    legend(plotLegends, "out_upper_right");
endfunction
