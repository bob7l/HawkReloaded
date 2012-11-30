<?php

	///////////////////////////////////////////////////
	//         HawkEye Interface Lang File           //
	//                 by oliverw92                  //
	///////////////////////////////////////////////////
	//      German Lang File by untergrundbiber      //
	///////////////////////////////////////////////////	
	$lang = array(
					
					"pageTitle"  => "HawkEye Browser",
					"title" => "HawkEye",
					
					"filter" => array("title" => "Filter-Optionen",
									  "players" => "Spieler",
									  "xyz" => "XYZ",
									  "range" => "Reichweite",
									  "keys" => "Stichw�rter",
									  "worlds" => "Welten",
									  "dFrom" => "Von Datum",
									  "dTo" => "Bis Datum",
									  "block" => "Block",
									  "search" => "Suche",
									  "exclude" => "Ausschluss-Filter",
									  "selectall" => "Alles ausw�hlen"),
					
					"tips" => array("hideFilter" => "Zeige / Verstecke Filter-Optionen",
									"hideResults" => "Zeige / Verstecke Ergebnisse",
									"actions" => "Aktionen die du suchen willst. Es muss mind. eine ausgew�hlt werden.",
									"password" => "Passwort um die Suche zu benutzen. Wird nur gebraucht wenn gesetzt.",
									"players" => "(Optional) Liste von Spieler nach denen gesucht werden soll, getrennt durch Kommas.",
									"xyz" => "(Optional) Koordinaten in dessen Umkreis du suchen willst",
									"range" => "(Optional) Suchreichweite um die Koordinaten",
									"keys" => "(Optional) Liste von Stichw�rter, getrennt durch Kommas.",
									"worlds" => "(Optional) Liste der Welten, getrennt durch Kommas. Leeres Feld entspricht alle Welten",
									"dFrom" => "(Optional) Start Zeit und Datum f�r Suchzeitraum",
									"dTo" => "(Optional) Ende Zeit und Datum f�r Suchzeitraum",
									"block" => "(Optional) Block nach dem gesucht wird bei 'Block zerst�rt' und 'Block plaziert'",
									"reverse" => "Wenn diese Option aktiviert, wird der Log in chronologischer Reihenfolge angezeigt. Deaktiviere die Option zum Anzeigen von Chat-Protokollen",
									"exclude" => "(Optional) Liste der Stichw�rte die aus der Suche ausgeschlossen werden sollen, getrennt durch Kommas.",
									"selectall" => "Klicke hier um alle Aktionen an- oder abzuw�hlen"),
									
					"actions" => array("0" => "Block zerst�rt",
									   "1" => "Block plaziert",
									   "2" => "Schild platziert",
									   "3" => "Chat",
									   "4" => "Kommando",
									   "5" => "Login",
									   "6" => "Logout",
									   "7" => "Teleport",
									   "8" => "Lava-Eimer",
									   "9" => "Wasser-Eimer",
									   "10" => "Kiste ge�ffnet",
									   "11" => "Tuer benutzt",
									   "12" => "Tod durch PVP",
									   "13" => "Feuerzeug",
									   "14" => "Hebel benutzt",
									   "15" => "Taste benutzt",
									   "16" => "Sonstiges",
									   "17" => "Explosion",
									   "18" => "Feuer",
									   "19" => "Block entsteht",
									   "20" => "Bl�tter-Zerfall",
									   "21" => "Tod durch Mob",
									   "22" => "Sonstiger Tod",
									   "23" => "Item gedroppt",
									   "24" => "Item aufgehoben",
									   "25" => "Block verschwindet",
									   "26" => "Lavafluss",
									   "27" => "Wasserfluss",
									   "28" => "Kisten-Aktion",
									   "29" => "Schild zerst�rt",
									   "30" => "Bild zerst�rt",
									   "31" => "Bild platziert",
									   "32" => "Enderman aufgehoben",
									   "33" => "Enderman platziert",
									   "34" => "Baumwuchs",
									   "35" => "Pilzwuchs",
									   "36" => "Mob Kill",
									   "37" => "Spawn Egg",
									   "38" => "HeroChat",	
									   "39" => "Entity Modify",
                                                                           "40" => "Block Inhabit"),

					"results" => array("title" => "Ergebnisse",
									   "id" => "ID",
									   "date" => "Datum",
									   "player" => "Spieler",
									   "action" => "Aktion",
									   "world" => "Welt",
									   "xyz" => "XYZ",
									   "data" => "Daten"),
									   
					"login" => array("password" => "Passwort: ",
									 "login" => "Login"),

					"messages" => array("clickTo" => "Klicke auf Suche um Ergenisse zu erhalten",
										"breakMe" => "Mach mich nicht kaputt!",
									    "invalidPass" => "Falsches Passwort!",
									    "noActions" => "Du musst mind. eine Aktion ausw�hlen nach der gesucht werden soll!",
									    "noResults" => "Keine Ergebnisse gefunden mit dieser Auswahl",
									    "error" => "Fehler!",
									    "notLoggedIn" => "Du bist nicht angemeldet!")
					);
	
	//Convert foreign characters to entities
	array_walk_recursive($lang, "ents");
	function ents(&$item, $key) {
		$item = htmlentities($item);
	}

?>