import os
import re


def runShell(command):
    print(command)
    os.system(command)


def getVersion(project):
    gradle_file = open(f"../{project}/build.gradle.kts", "r")
    gradle_contents = gradle_file.read()
    version = re.findall("^version = .*", gradle_contents, flags=re.M)[0]
    version = version.replace("version = \"", "")
    version = version.replace("\"", "")
    return version


def publish(artifact, version,project, file):
    runShell(f"cd ..; mvn deploy:deploy-file " +
             f"-DgroupId=dev.lightdream " +
             f"-DartifactId={project} " +
             f"-Dversion={version} " +
             f"-Dpackaging=jar " +
             f"-Dfile=\"{project}/build/libs/{file}\" " +
             f"-DrepositoryId=lightdream " +
             f"-Durl=https://repo.lightdream.dev")


def getFile(project):
    return  os.listdir(f"../{project}/build/libs")[0]
    # os.rename(f"../{project}/build/libs/{oldName}", f"../{project}/build/libs/Upload.jar")
    # return "Upload.jar"


runShell("rm ../Forge-1.19.3/build/libs/*.jar")
runShell("rm ../Spigot/build/libs/*.jar")
runShell("rm ../Sponge/build/libs/*.jar")

runShell("cd ..; powershell.exe -Command gradle build")

version_forge_1_19_3 = getVersion("Forge_1_19_3")
version_spigot = getVersion("Spigot")
version_sponge = getVersion("Sponge")

file_forge_1_19_3 = getFile("Forge_1_19_3")
file_spigot = getFile("Spigot")
file_sponge = getFile("Sponge")

print(f"Forge 1.19.3 version: {version_forge_1_19_3} with file {file_forge_1_19_3}")
print(f"Spigot version: {version_spigot} with file {file_spigot}")
print(f"Sponge version: {version_sponge} with file {file_sponge}")

publish("CommandManager-Forge-1.19.3",version_forge_1_19_3,"Forge_1_19_3", file_forge_1_19_3)
publish("CommandManager-Spigot", version_spigot,"Spigot", file_spigot)
publish("CommandManager-Sponge", version_sponge,"Sponge", file_sponge)
