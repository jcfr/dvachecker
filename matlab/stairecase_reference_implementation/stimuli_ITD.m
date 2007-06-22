function []=stimuli_ITD(reponse)
%Fonction stimuli mais adapt�e � la rib, elle utilise un algorithme de staircase
global no sound reponse_patient bonne_reponse delay handler ref_side
global CurrVar
global indice_faux                                      %Tableau contenant les indice des r�ponses fausses
global indice_juste                                     %Tableau contenant les indice des r�ponses justes
global juste                                            %Tableau comportant les CurrVar juste
global faux                                             %Tableau comportant les CurrVar fausse
global counter                                          %Base de temps du graphique
global init_intens_d                                    %Intensit� gauche utilius� pour la r�f�rence (canal droite)
global init_intens_g                                    %Intensit� gauche utilius� pour la r�f�rence (canal gauche)
global no_elec_g                                        %Num�ro de l'�lectrode gauche s�l�ction� au d�but de l'exp�rience
global no_elec_d                                        %Num�ro de l'�lectrode droite s�l�ction� au d�but de l'exp�rience

reponse_patient(no)=reponse;                        %Sauvgarde de toutes les r�ponses donn�es par le sujet

%Calcule du d�lai par l'algorithme du staircase
convergence=stairecase(reponse,bonne_reponse(no));              %Le nouveau delai est stocker dans la variable globale CurrVar
if convergence==0
    no=no+1;
    delay(no)=CurrVar;                                  %Sauvgarde du prochain d�lai
    %On doit arrondir delay vers un nombre impair (la stimulation st�r�o est entrelac� et le canal de r�f�rence 'commence' � 0)
    if mod(delay(no)+1,2)~=0                    %Si delay(no) n'est pas impair (-> soit pair, soit pas entier)
        delay(no)=(2*round((delay(no)-1)/2))+1
    end

%Choix gauche-droite al�atoire
    direction = rand (1,1);
    if ref_side==1                  %R�f�rence � gauche,seul le cot� droite peut changer        
        if direction <= 0.5
            DioBurst(['BB,',num2str(init_intens_d),',',num2str(init_intens_g),',',num2str(init_intens_d),',',num2str(init_intens_g),',200000,500000,25000,25000,',num2str(-delay(no)),',0,0,0,1000']);
            %Droite -> d�lais n�gatif � droite    
            bonne_reponse(no) = 1;       %1 pour droite
            'droite'
        else    
            DioBurst(['BB,',num2str(init_intens_d),',',num2str(init_intens_g),',',num2str(init_intens_d),',',num2str(init_intens_g),',200000,500000,25000,25000,',num2str(delay(no)),',0,0,0,1000']);
            %Gauche -> delay positif � droite
            bonne_reponse(no) = 2;       %2 pour gauche
            'gauche'
        end
    elseif ref_side==2              %R�f�rence � droite, seul le cot� gauche peut bouger
        if direction <= 0.5
            DioBurst(['BB,',num2str(init_intens_d),',',num2str(init_intens_g),',',num2str(init_intens_d),',',num2str(init_intens_g),',200000,500000,25000,25000,0,',num2str(delay(no)),',0,0,1000']);
            %Droite -> d�lais positif � gauche    
            bonne_reponse(no) = 1;       %1 pour droite
            'droite'
        else
            DioBurst(['BB,',num2str(init_intens_d),',',num2str(init_intens_g),',',num2str(init_intens_d),',',num2str(init_intens_g),',200000,500000,25000,25000,0,',num2str(-delay(no)),',0,0,1000']);
            %Gauche -> delay n�gatif � gauche
            bonne_reponse(no) = 2;       %2 pour gauche
            'gauche'
        end
    else 
        'Mode de r�f�rence non d�finit'
    end        
%Activation des boutons 'gauche' et 'droite'
    set(handler.pushbutton8,'Enable','on');
    set(handler.pushbutton9,'Enable','on');
    set(handler.rejouer_button,'Enable','on');
elseif convergence==1
    'fermer toutes les fen�tre etc'
    sauvegarde(indice_juste,indice_faux,delay);
    DioBurst('','');
    DioBurst('close');
    close(menu);
    clear all
    menu;
elseif convergence==-1
    'Attention, ITD initiale non idenetifi�e!'
    DioBurst('','');
    DioBurst('close');
    close(menu);
    clear all
    menu;
end
delay