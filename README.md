# WilsonBot
**A SoundBoard bot**

WilsonBot is a Java project based around the Discord4J wrapper api. The project comprises of a bot (named WilsonBot) that acts essentially as a discord based soundboard bot. This means that it will play clips by command, and allow users to add their own clips from discord chat itself. Along side this there are many other features.

This is a multi-platform project, works on both Windows and Linux (has not been tested on Mac). However it does require ffmpeg and youtube-dl to be installed to the path for this to function correctly.

Currently the project is in the wild development stage, i.e I am not overly worried about structure. However at some point in the near future this will be addressed.

I welcome any suggestions or bug reports.

This bot uses the following additional resources:

-youtube-dl
-ffmpeg
-various java libraries (detailed in pom file)


**Outstanding Milestones:**

  -Implement DjDog
  -Implement looping to djdog
  
**New Milestones :**

  -Implement exclude function
  -Implement verbose vs non-verbose options
  -Implement diagnostic redirects
  
**Hiatus Milestones**

  -Implement command tree structure (it works in theory, but it just creates needless complexity when creating commands)
  -Implement module system (Currently, I see little use for this as my bot is a contained unit, if I decide to work on BotServer more as a base, this may come up again)
  
**Recently Completed Milestones :**

Date : undated

  -Implement tag system
  
  -Fix downloading clips from same source
    
  -Implement BotServer (with method array structure)
  
  -Implement Ban system
  
  -Implement r9k mode
  
  -Add working isAdmin() method
  
  -Fix volume bug
