FilterIT
======

Filter files the way you need.

Supported file types:
-----------------------------
- .docx
- .xlsx
- .pdf
- every other file is opened as a text file

How to use:
-----------
1. Download <a href="https://github.com/Steffen93/filterit/releases">the latest version of FilterIT</a> and launch the software
2. Choose all the files you want to filter and press "Continue"
3. Enter filter rules (and if you need it, how many files before and after a hit should be printed) and apply them
4. "Continue" to view the results; places where rules apply are highlighted
5. Press "Save" to save the results to a file.


Build jar from the code:
------------------------
1. Run `mvn install` from the command line in the root folder (where the pom.xml file is located)
2. The jar file is located at `target/FilterIT-<VERSION>-jar-with-dependencies.jar`
