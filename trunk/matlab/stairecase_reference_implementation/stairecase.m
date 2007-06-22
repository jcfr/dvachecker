function convergence=stairecase(resp,stim_int)
%Fonction qui calcule le prochain stimuli selon un algo de staircase
%resp:r�ponse du sujet
%stim_int: r�ponse juste
global stimulus	                                        %=1 if heard it, =-1 if didn't hear it, =0 if change stimulus, no lastresp
global rundir                                           %La direction montant = 1,descendant =-1
global step                                             %Le pas courant
global CurrVar                                          %La variable dur staircase
global run                                              %Le nombre de segment montant et descendant
global peak                                             %Tableau contenant les pics
global valley                                           %Tableau contenant les vall�e
global limite_up                                        %Limite sup�rieur pour la variable du staircase (700 us par exemple)
global limit_down                                       %Limite inf�rieur pour la variable du staircase (0 us par exemple)
global minstep;                                         %Pas minimum accord�
global no                                               %num�ro du stimuli courant
global indice_faux                                      %Tableau contenant les indice des r�ponses fausses
global indice_juste                                     %Tableau contenant les indice des r�ponses justes
global juste                                            %Tableau comportant les CurrVar juste
global faux                                             %Tableau comportant les CurrVar fausse
%global counter                                          %Base de temps du graphique

%Variable locale

convergence=0;                                          %Flag: 1 -> convergence, 0 -> non, -1 -> divergence
nbofrun=12;                                              %Nb de segment totale d�sir� (condition de sortie)

%Algorithme de staircase

switch(rundir)                                          %Run dir vaut 1 ou -1 (monte ou descend)
   case 1 					                            %On monte
   if stim_int~=resp;	                                %R�ponse fausse
        %counter=counter+1;                             %Base de temps du graphique
        faux=[faux,CurrVar];                            %Sauvgarde des CirrVarr faux
        indice_faux=[indice_faux,no];                   %Indice de cette faute relativement � la base de temps
        CurrVar=CurrVar+step;                           %On augmente la variable
        if CurrVar>limite_up                           %Mais elle ne dois pas d�passer cette limite
            'convergence=-1'
            convergence=-1;
        end
        stimulus=0; 		                            %Flag indiquant que le stimuli a �t� chang�
    elseif stimulus==1; 	                            %La r�ponse pr�c�dante et la r�ponse actuelle sont justes
        %counter=counter+1;                              %Base de temps du graphique
        juste=[juste,CurrVar];                          %Sauvgarde des CirrVarr juste
        indice_juste=[indice_juste,no];                 %Indice de cette r�ponse juste
        peak=[peak,CurrVar];                            %On prend note du fait que l'on a un pic
        rundir=-1;                                      %Car la direction a chang�
        run=run+1;                                      %On augmente le nombre de segment
        switch (run)                                    %Changement du step
            case 3                                      %Au troisi�me segment
            if step/2<=minstep;                          %On diminue de moitier mais on ne doit pas etre au dessous du seuil minimum
                step=minstep;
            else step=step/2;
            end
            case 5                                      %Au 5�me segment descendant
            if step/2<=minstep;                          %On diminue de moitier mais on ne doit pas etre au dessous du seuil minimum
                step=minstep;
            else step=step/2;
            end
        end
        CurrVar=CurrVar-step;                           %On diminue la varialble
        if CurrVar<limit_down                                    
            CurrVar=limit_down;                         %La variable ne doit pas se situ� en dessous de cette limite
        end
        stimulus=0;                                     %Flag indiquant que le stimuli a �t� chang�
   else stimulus=1;  	                                %La r�ponse pr�c�dante a �t� fausse mais la courante juste
     	                                                %Flag indiquant que le stimuli n'a pas �t� chang�
        juste=[juste,CurrVar];                          %Sauvgarde des CirrVarr juste
        indice_juste=[indice_juste,no];                 %Indice de cette r�ponse juste
   end
     
   case -1 						                        %On descend
        if stim_int~=resp; 	                            %La r�ponse est fausse
            %counter=counter+1;                          %Base de temps du graphique
            faux=[faux,CurrVar];                        %Sauvgarde des CirrVarr faux
            indice_faux=[indice_faux,no];               %Indice de cette faute relativement � la base de temps
            valley=[valley,CurrVar];                    %Comme on monte, on prend note de la vall�e            
            CurrVar=CurrVar+step;                       %On augmente la variable
            if CurrVar>limite_up                       %Mais elle ne dois pas d�passer cette limite
                'convergence=-1'
                convergence=-1;                         %Divergence
            end
            rundir=1;                                   %On change de direction
            run=run+1;                                  %On augmente le nombre de segment
            if run>=nbofrun;                          %Si le nombre de segment maximum a �t� atteint (condition de sortie)
                'Convergence=1'
	            convergence=1;
	 
                        
            %minima=valley(4:6);                         %Calcule de la valeur moyenn et d�viation standard sur les                        
            %maxima=peak(4:6);                           %trois dernier pics/vall�e
            %mean=sum(minima,maxima)/6;
            %stdev=sqrt(1/5*(sum((minima-mean).^2)+sum((maxima-mean).^2)));
            
            end
            stimulus=0;                                 %On doit chang� de direction (pas besoin de restimuler puisque l'on descend)
        elseif stimulus==1;	                            %R�ponse pr�c�dante et actuelle juste
            %counter=counter+1;                         %Base de temps du graphique
            juste=[juste,CurrVar];                      %Sauvgarde des CirrVarr juste
            indice_juste=[indice_juste,no];             %Indice de cette r�ponse juste
            CurrVar=CurrVar-step;                       %On diminue mais pas au del� de la valeur limite
            if CurrVar<limit_down
                CurrVar=limit_down;
            end 			                            
            stimulus=0; 			                        %Flag indiquant que le stimuli a �t� chang�
            
        else 
            stimulus=1; 		                        %R�ponse pr�c�dante fausse mais actuel juste
                                                            %On envoie le m^eme stimui pour confirmer
            juste=[juste,CurrVar];                          %Sauvgarde des CirrVarr juste
            indice_juste=[indice_juste,no];                 %Indice de cette r�ponse juste
                                             
        end
end                                                     %Fin du switch
run
      

