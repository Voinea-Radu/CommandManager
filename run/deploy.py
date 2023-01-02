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


def publish(artifact, version, project, file):
    runShell(f"cd ..; mvn deploy:deploy-file " +
             f"-DgroupId=dev.lightdream " +
             f"-DartifactId={artifact} " +
             f"-Dversion={version} " +
             f"-Dpackaging=jar " +
             f"-Dfile=\"{project}/build/libs/{file}\" " +
             f"-DrepositoryId=lightdream " +
             f"-Durl=https://repo.lightdream.dev")


runShell("rm ../Forge-1.19.3/build/libs/*")
runShell("rm ../Spigot/build/libs/*")
runShell("rm ../Sponge/build/libs/*")

runShell("cd ..; powershell.exe -Command gradle build")

version_forge_1_19_3 = getVersion("Forge_1_19_3")
version_spigot = getVersion("Spigot")
version_sponge = getVersion("Sponge")

print(f"Forge 1.19.3 version: {version_forge_1_19_3} with file Forge-1.19.3.jar")
print(f"Spigot version: {version_spigot} with file Spigot.jar")
print(f"Sponge version: {version_sponge} with file Sponge.jar")

publish("CommandManager-Forge-1.19.3", version_forge_1_19_3, "Forge_1_19_3", "Forge-1.19.3.jar")
publish("CommandManager-Spigot", version_spigot, "Spigot", "Spigot.jar")
publish("CommandManager-Sponge", version_sponge, "Sponge", "Sponge.jar")
