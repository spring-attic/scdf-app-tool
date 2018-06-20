@echo OFF
For /f "usebackq tokens=*" %%F in (`dir /b /a "target\scdf-app-tool-*.jar"`) do java -jar "target\%%F" %*