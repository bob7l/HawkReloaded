<?php

// Check if required functions are here
if (!function_exists("json_decode")) require('json.php');

// Report all errors
error_reporting(E_ALL);

// Include config and lang pack
include("config.php");
include("langs/" . $hawkConfig["langFile"]);

// This method checks if the connection is https with CloudFlare in mind.
// Both the Standard connection and the CloudFlare one must be https.
function isHttps()
{
	// Standard
	if (empty($_SERVER["HTTPS"]) || $_SERVER["HTTPS"] !== "on") return false;
	
	// CloudFlare
	if (!empty($_SERVER["HTTP_CF_VISITOR"]) && strpos($_SERVER["HTTP_CF_VISITOR"], 'https') === false) return false;
	
	return true;
}

// Force SSL
if (isset($hawkConfig["forceSsl"]) && $hawkConfig["forceSsl"] && !isHttps())
{
	header('Location: https://' . $_SERVER['HTTP_HOST'] . $_SERVER['REQUEST_URI']);
	exit();
}

// Start session
session_start();

// The user wants to use forumAuth
if ($hawkConfig["forumAuth"])
{
	//We need to require the specified forumType. Hopefully we support it (Or the user is using their own file)
	require('./forumAuth/'.$hawkConfig['forumType'].'.php');
}

$mysqli = new mysqli($hawkConfig["dbHost"], $hawkConfig["dbUser"], $hawkConfig["dbPass"], $hawkConfig["dbDbase"]);
if ($mysqli->connect_errno) die("Could not connect to MySQL Database!");

if (!$mysqli->set_charset("utf8")) die("Error loading character set utf8.");

function handleError($errno, $errstr, $errfile, $errline, array $errcontext)
{
	// error was suppressed with the @-operator
	if (0 === error_reporting()) return false;
	throw new ErrorException($errstr, 0, $errno, $errfile, $errline);
}
