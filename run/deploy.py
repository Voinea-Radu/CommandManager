import os
import re
import sys

argsSize = len(sys.argv)
deployList = []

if argsSize != 1:
    for i in range(1, argsSize):
        deployList.append(sys.argv[i])


def runShell(command):
    os.system(command)

def getVersion(project):
    gradle_file = open(f"../build.gradle.kts", "r")
    gradle_contents = gradle_file.read()

    version = re.findall(f"^extra..{project}.. = .*", gradle_contents, flags=re.M)[0]

    version = version.replace("extra", "") \
        .replace(project, "") \
        .replace("\"", "") \
        .replace(" ", "") \
        .replace("=", "") \
        .replace("[", "") \
        .replace("]", "")

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


def deployForge1_19_3():
    publish("CommandManager-Forge-1.19.3", version_forge_1_19_3, "Forge_1_19_3", "Forge-1.19.3.jar")


def deploySpigot():
    publish("CommandManager-Spigot", version_spigot, "Spigot", "Spigot.jar")


def deploySponge():
    publish("CommandManager-Sponge", version_sponge, "Sponge", "Sponge.jar")


def deployVelocity():
    publish("CommandManager-Velocity", version_velocity, "Velocity", "Velocity.jar")


runShell("rm ../Forge_1_19_3/build/libs/*")
runShell("rm ../Spigot/build/libs/*")
runShell("rm ../Sponge/build/libs/*")
runShell("rm ../Velocity/build/libs/*")

runShell("cd ..; powershell.exe -Command gradle build")

version_forge_1_19_3 = getVersion("Forge_1_19_3")
version_spigot = getVersion("Spigot")
version_sponge = getVersion("Sponge")
version_velocity = getVersion("Velocity")

print(f"Forge 1.19.3 version: {version_forge_1_19_3} with file Forge-1.19.3.jar")
print(f"Spigot version: {version_spigot} with file Spigot.jar")
print(f"Sponge version: {version_sponge} with file Sponge.jar")
print(f"Velocity version: {version_velocity} with file Velocity.jar")

if len(deployList) == 0:
    deployForge1_19_3()
    deploySpigot()
    deploySponge()
    deployVelocity()
else:
    if "forge_1_19_3" in deployList:
        deployForge1_19_3()
    if "spigot" in deployList:
        deploySpigot()
    if "sponge" in deployList:
        deploySponge()
    if "velocity" in deployList:
        deployVelocity()
