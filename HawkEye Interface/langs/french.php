<?php

	///////////////////////////////////////////////////
	//         HawkEye Interface Lang File           //
	//                 by oliverw92                  //
	///////////////////////////////////////////////////			
	//       Original translation by oliverw92       //	IT IS IMPORTANT TO SAVE THE FILE IN UTF-8 with BOM
	//          Modifications by Gabigabigo          //
	//       Traduction originale de oliverw92       //	IL EST IMPORTANT DE SAUVEGARDER LE FICHIER EN UTF-8 with (avec) BOM
	//          Modifications de Gabigabigo          //
	///////////////////////////////////////////////////
	$lang = array(
					
					"pageTitle"  => "HawkEye - Outil d'administration",
					"title" => "HawkEye",
					
					"filter" => array("title" => "Options de filtrage",
									  "players" => "Joueurs",
									  "xyz" => "XYZ",
									  "range" => "Rayon",
									  "keys" => "Mots-clés",
									  "worlds" => "Monde(s)",
									  "dFrom" => "Date de début",
									  "dTo" => "Date de fin",
									  "block" => "Bloc",
									  "search" => "Rechercher",
									  "exclude" => "Exclure les filtres",
									  "selectall" => "Tout sélectionner"),
					
					"tips" => array("hideFilter" => "Afficher / Masquer les options de filtrage",
									"hideResults" => "Afficher / Masquer les résultats",
									"actions" => "Actions à rechercher. Vous devez en sélectionner au moins une",
									"password" => "Mot de passe pour utiliser le navigateur. Requis seulement s'il a été défini",
									"players" => "(Facultatif) Liste des joueurs que vous souhaitez rechercher, séparés par des virgules",
									"xyz" => "(Facultatif) Coordonnées auxquelles vous souhaitez débuter la recherche.",
									"range" => "(Facultatif) Rayon de la recherche.",
									"keys" => "(Facultatif) Liste des mots-clés à rechercher, séparés par des virgules.",
									"worlds" => "(Facultatif) Monde ou liste des mondes - séparés par des virgules - où rechercher les actions. Laissez vide pour sélectionner tous les mondes.",
									"dFrom" => "(Facultatif) Date et heure de début de la période de recherche des actions.",
									"dTo" => "(Facultatif) Date et heure de fin de la période de recherche des actions.",
									"block" => "(Facultatif) Blocs à rechercher dans les événements 'Bloc cassé' et 'Bloc posé'",
									"reverse" => "Si cette case est cochée est cochee, les résultats seront dans l'ordre chronologique inverse. Décochez la case pour afficher les journaux de conversation.",
									"exclude" => "(Facultatif) Liste des mots clés à exclure des résultats, séparés par des virgules",
									"selectall" => "Cliquez pour sélectionner toutes les actions, cliquer à nouveau pour toutes les desélectionner."),
									
					"actions" => array("0" => "Bloc cassé",
									   "1" => "Bloc posé",
									   "2" => "Panneau placé",
									   "3" => "Chat",
									   "4" => "Commande",
									   "5" => "Connexion",
									   "6" => "Déconnexion",
									   "7" => "Téléportation",
									   "8" => "Seau de Lave",
									   "9" => "Seau d'eau",
									   "10" => "Ouverture coffre",
									   "11" => "Interaction porte",
									   "12" => "Mort PVP",
									   "13" => "Briquet",
									   "14" => "Levier",
									   "15" => "Bouton",
									   "16" => "Autres",
									   "17" => "Explosions",
									   "18" => "Combustion de bloc",
									   "19" => "Formation de bloc",
									   "20" => "Chute de feuilles",
									   "21" => "Mort Monstre",
									   "22" => "Mort Autre",
									   "23" => "Dépôt item",
									   "24" => "Ramassage item",
									   "25" => "Bloc Fade",
									   "26" => "Ecoulement Lave",
									   "27" => "Ecoulement eau",
									   "28" => "Transaction coffre",
									   "29" => "Panneau posé",
									   "30" => "Peinture posée",
									   "31" => "Peinture cassée",
									   "32" => "Enderman Pickup",
									   "33" => "Enderman Place",
									   "34" => "Tree Grow",
									   "35" => "Mushroom Grow",
									   "36" => "Mob Kill",
									   "37" => "Spawn Egg",
									   "38" => "HeroChat",	
									   "39" => "Entity Modify",
                                       "40" => "Block Inhabit",
                                       "41" => "Super-Pickaxe",
                                       "42" => "WorldEdit-Break",
                                       "43" => "WorldEdit-Place",
									   "44" => "Crop-Trample",
                                       "45" => "Block-Ignite",
									   "46" => "FallingBlock-Place"),
					
					"results" => array("title" => "Résultats",
									   "id" => "ID",
									   "date" => "Date",
									   "player" => "Joueur",
									   "action" => "Action",
									   "world" => "Monde",
									   "xyz" => "XYZ",
									   "data" => "Données"),
									   
					"login" => array("password" => "Mot de Passe : ",
									 "login" => "Connexion"),
					
					"messages" => array("clickTo" => "Cliquez sur Rechercher pour récupérer des données",
										"breakMe" => "Arrêtez d'essayer de m\'arrêter !",
									    "invalidPass" => "Mot de passe invalide !",
									    "noActions" => "Vous devez sélectionner au moins 1 action à rechercher !",
									    "noResults" => "Aucun résultat correspondant à ces options",
									    "error" => "Erreur !",
									    "notLoggedIn" => "Vous n'êtes pas connecté !")
									    
					
					);
	
	//Convert foreign characters to entities
	array_walk_recursive($lang, "ents");
	function ents(&$item, $key) {
		$item = htmlentities($item);
	}

?>
