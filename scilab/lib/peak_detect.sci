// Detecta los picos de una señal
// Basado en la implementación de Jean-Luc GOUDIER 11-2011
//
// signal: vector de valores de la señal
// threshold: parámetro opcional que indica el umbral a partir del cual deben localizarse los picos
function peaks=peak_detect(signal,threshold)
	[nargout,nargin] = argn(0);
	if nargin==2 then ts=threshold;
	end;
	if nargin==1 then ts=min(signal);
	end;

	[r c]=size(signal);
	if r>1 then
	    error("La señal no es un vector");
	end;

	Lg=c-1; 
	d=diff(signal); 

	// Calcula la derivada discreta
	d_s=diff(signal); 

	// Obtiene los desplazamientos
	dd_s=[d_s(1),d_s(1,:)];          
	d_s=[d_s(1,:),d_s(Lg)];          
	ddd_s=[dd_s(1),dd_s(1,1:Lg)];    
	Z=d_s.*dd_s;                     
	Z=abs(Z);
	
	// Busca los picos
	p1=find(d_s<0 & ddd_s>0 & signal>ts);

	// Elimina picos consecutivos
	s1=signal(p1);
	c=size(p1,"c");
	p2=[p1(2:c), p1(c)];
	s2=signal(p2);
	Xp3=[p1(1), p1(1:c-1)];
	s3=signal(p3);
	filtered = find((p2-p1>1 & p1-p3>1) | (p2-p1==1 & s1>s2) | (p1-p3 ==1 & s1>s3))
	peaks = p1(filtered);
endfunction



