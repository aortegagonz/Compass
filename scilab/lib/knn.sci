//
// Algoritmo k-NN
// Basado en la omplementación de Vincent Guigue 08/01/03
//
// USO : [ypred,tabkppv,distance]=knn(xapp,yapp,valY,X,k)
//
// xapp, yapp : datos de entrada correspondientes a los ejes X e Y
// valY : Todos los posibles valores de Y
// X : dato a clasificar
// k : número de vecinos más cercanos a buscar
//
// ypred : localización de X
// tabkppv : [nbpts x nbpts] índices de los vecinos más cercanos
// distance : [nbpts x nbpts] distancias desde X a cada huella
//
function [ypred,tabkppv,distance]=knn(xapp,yapp,valY,X,k)

	// Comprobación de parámetros de entrada
	[nargout,nargin] = argn();
	if nargin < 4 then
	  error('faltan parámetros');
	elseif nargin < 5 then
	  k = 3;
	else
	  if modulo(k,2)==0 then
	    error('k debe ser impar');
	  end
	end

	if size(xapp,2)~=size(X,2) then
	  error('dimensiones incompatibles');
	end

	ndim    = size(xapp,2);
	nptxapp = size(xapp,1);
	nptX    = size(X,1);

	// distancia de X a xapp :
	mat1  = repmat(xapp, nptX,1);
	mat22 = repmat(X,1,nptxapp)';
	mat2  = matrix(mat22 ,ndim, nptxapp*nptX)';
	distance = mat1 - mat2 ;

	distance = sum(distance.^2,2);
	distance = matrix(distance,nptxapp,nptX);
	//[val kppv] = gsort(distance,1);
	[val kppv] = gsort(distance,"g","i")

	// balance en las k primeras líneas
	kppv  = matrix(kppv(1:k,:),k*nptX,1);
	//Ykppv = yapp(kppv,1);
	Ykppv = yapp(kppv);
	Ykppv = matrix(Ykppv,k,nptX);

	// encontrar más respuestas idénticas para la columna
	tabkppv = Ykppv;

	vote = [];
	for i=1:nptX
	  for j=1:length(valY)
	    vote(j,i) = size(find(Ykppv(:,i)==valY(j)),1);
	  end
	end

	[val ind] = max(vote,'r');
	ypred     = valY(ind);
endfunction