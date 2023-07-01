# JAPatch
A tool to convert your static modifications into agent patcher

how to use:

1. put all the class files you modified into a zip
2. run agent-patcher.jar [path/to/agent.jar] [classes.zip], this will create a jar called "patcher.jar"
3. run your program with the arg: -javaagent:"path/to/patcher.jar=path/to/patcher.jar"
