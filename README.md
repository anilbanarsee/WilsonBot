# WilsonBot
**A SoundBoard bot**

WilsonBot is a Java project based around the Discord4J wrapper api. The project comprises of a bot (named WilsonBot) that acts essentially as a discord based soundboard bot. This means that it will play clips by command, and allow users to add their own clips from discord chat itself. Along side this there are many other features.

This is a multi-platform project, works on both Windows and Linux (has not been tested on Mac). However it does require ffmpeg and youtube-dl to be installed to the path for this to function correctly.

I welcome any suggestions or bug reports.

This bot uses the following additional resources:

-youtube-dl
-ffmpeg
-various java libraries (detailed in pom file)


**Outstanding Milestones:**

  
**New Milestones :**

  -Implement exclude function
  -Implement verbose vs non-verbose options

  
**Hiatus Milestones**

  -Implement command tree structure (it works in theory, but it just creates needless complexity when creating commands)
  
**Recently Completed Milestones :**

Date : undated
  -Implement diagnostic redirects

  -Implement tag system
  
  -Fix downloading clips from same source
    
  -Implement BotServer (with method array structure)
  
  -Implement Ban system
  
  -Implement r9k mode
  
  -Add working isAdmin() method
  
  -Fix volume bug
