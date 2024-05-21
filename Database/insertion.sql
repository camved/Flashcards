INSERT INTO chapitres (matiereNom, chapitreNom, year)
 VALUES
 ('Verification', NULL, 3),
 ('IA', NULL, 3);
 
 INSERT INTO flashcards (nbWin, nbLoose, idChap, type, isFirstOfSuite, nextCard, lastseen, takesInput, front, back)
 VALUES
 (0, 0,
 (SELECT idChap from chapitres WHERE matiereNom='IA' and chapitreNom is NULL),
 'Enigme',
 'yes',
 NULL,
 julianday('now'),
 'no',
 'La traversée du LAC:
Vous devez amener les trois loups et les trois poussins sur l autre rive. Attention à bien respecter les règles suivantes :

Le radeau ne peut accueillir plus de deux animaux.
Vous ne pouvez déplacer le radeau s il est vide.
S il y a plus de loups que de poussins sur une des rives, les loups mangeront les pauvres volatiles sans défense et il vous faudra recommencer depuis le début.
Vous n avez pas de limite de déplacement, mais il est possible de résoudre cette énigme en 11 déplacements.',
'DESCRIPTION DE LA SOLUTION'
),

 (0,0,
 (SELECT idChap from chapitres WHERE matiereNom='Verification' and chapitreNom is NULL),
 'Enigme',
 'yes',
 NULL,
 julianday('now'),
 'no',
 'La traversée du LAC:
Vous devez amener les trois loups et les trois poussins sur l autre rive. Attention à bien respecter les règles suivantes :

Le radeau ne peut accueillir plus de deux animaux.
Vous ne pouvez déplacer le radeau s il est vide.
S il y a plus de loups que de poussins sur une des rives, les loups mangeront les pauvres volatiles sans défense et il vous faudra recommencer depuis le début.
Vous n avez pas de limite de déplacement, mais il est possible de résoudre cette énigme en 11 déplacements.',
'DESCRIPTION DE LA SOLUTION'
),


 (0, 0,
 (SELECT idChap from chapitres WHERE matiereNom='IA' and chapitreNom is NULL),
 'Enigme',
 'yes',
 NULL,
 julianday('now'),
 'no',
 'LA CHANDELEUR DE GRAY:
On imagine que l on possède une pyramide de 5 crêpes et 3 assiettes.
Trouvez un moyen de passer toutes les crêpes ordonnée par taille de la 1ère assiette à la même configuration, mais dans la dernière assiette 
, en sachant qu il est impossible de placer une crêpe plus grosse au dessus d une plus petite.
 Déterminez également le nombre de coups minimum nécessaire pour effectuer cette opération',
'DESCRIPTION DE LA SOLUTION'
),

 (0,0,
 (SELECT idChap from chapitres WHERE matiereNom='Verification' and chapitreNom is NULL),
 'Enigme',
 'yes',
 NULL,
 julianday('now'),
 'no',
 'LA CHANDELEUR DE GRAY:
On imagine que l on possède une pyramide de 5 crêpes et 3 assiettes.
Trouvez un moyen de passer toutes les crêpes ordonnée par taille de la 1ère assiette à la même configuration, mais dans la dernière assiette 
, en sachant qu il est impossible de placer une crêpe plus grosse au dessus d une plus petite.
 Déterminez également le nombre de coups minimum nécessaire pour effectuer cette opération',
'DESCRIPTION DE LA SOLUTION'
),



 (0, 0,
 (SELECT idChap from chapitres WHERE matiereNom='Verification' and chapitreNom is NULL),
 'Enigme',
 'yes',
 NULL,
 julianday('now'),
 'no',
 'Le rouge et le noir: 
Tout paquet 52 cartes est décomposé en 26 cartes rouges et 26 cartes noires. 
Après avoir mélangé notre de jeu de cartes, on le coupe en deux tas, 1 à gauche et 1 à droite. On sait que le paquet de Gauche possède 23 cartes. 
Notre question est la suivante : 
Combien y a t-il de cartes noires dans le paquet de droite, par rapport au nombre de cartes rouges dans le paquet de gauche ?
',
' à résoudre avec une équation, mais la réponse est : Il y aura toujours 3 cartes noires de + dans la pile droite que de cartes rouges dans la pile gauche'
),



 (0, 0,
 (SELECT idChap from chapitres WHERE matiereNom='Verification' and chapitreNom is NULL),
 'Enigme',
 'yes',
 NULL,
 julianday('now'),
 'no',
 ' Zou Express
Il faut 15 minutes pour voyager de la gare A à la gare B.
Il faut 5 minutes pour voyager de la gare B à la gare C.
Il faut 10 minutes pour voyager de la gare C à la gare D.
Toutefois, il ne faut pas 30 minutes pour voyager de la gare A à la gare D…
La gare A est la première gare de la ligne.
La gare D est son terminus.

Sachant que la ligne ne comporte aucun aiguillage, combien de minutes faut-il pour voyager de la gare A à la gare D ?',
' 20 min'
)