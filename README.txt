Description

HawkEye reloaded is a continuation of the beloved former "Hawkeye", which is now completely inactive. It gives you the ability to log changes, search through them, roll edits back and much, much more.

features

Logging of over 30 different actions
Block filter to avoid logging unwanted material
Rollback commands with simple-to-use parameters
Advanced interactive web interface for viewing logs
Rollback previews - have the rollback only appear to you at first
WorldEdit selection rollbacks - rollback everything in your WE selection
Configurable search tool to quickly see edits on single blocks
Simple, and easy to learn parameters
API so other plugins can interact with the HawkEye database
Command list

Temp command list: http://i.imgur.com/0MnoQ.png
To roll back world edit regions, execute this command:
/he rollback r:we
Permissions

Nodes:

hawkeye.*	 [Access to all HawkEye commands]
hawkeye.page	 [Permission to view different pages]
hawkeye.search [Permission to search the HawkEye database]
hawkeye.search.<action>
hawkeye.tpto	 [Permission to teleport to the location of a search result]
hawkeye.rollback	[Permission to rollback actions]
hawkeye.tool	 [Permission to use the HawkEye tool]
hawkeye.tool.bind	[Permission to bind parameters to the tool]
hawkeye.preview	[Permission to preview a rollback before applying it]
hawkeye.rebuild	[Permission to rebuild actions]
Requirements

Latest RB of bukkit
MySQL database (Your host should provide you with one)
(optional) WebServer (if you want to run the Web Interface)
ToDo List

WorldEdit logging
SQLite, Maybe? (Tried it, it's just not meant for logging so much data)
Donate?

All the donations go directly to the former author oliverw92 Click here to donate!

Source

GitHub: https://github.com/bob7l/HawkReloaded

DevBuilds: 1.0.6.2.3.1 DEV

(Added HeroChat support)

Plugin not working correctly?

Here you can post your issue/error code or just simply suggest something!

http://dev.bukkit.org/server-mods/hawkeye-reload/pages/error/problems/

How to import my logblock logs?

Very easily, oliverw92 has created some very easy to use import scripts. Find out how here: Importing scripts

How the filter works

It's very simple to filter out unwanted items from the log. Things like sand, or grass are some things you might not want to be logged, this is how you filter them out!

block-filter:

- GRASS

- STONE

List of blocks/items: http://aoc.curseforge.com/paste/5395/