plugins {
    java
    id("eu.cloudnetservice.juppiter") version "0.5.0-beta.1"
    id("net.kyori.blossom") version "1.3.1"
}

repositories {
    mavenCentral()
    maven("https://repo.cloudnetservice.eu/repository/snapshots/")
    maven("https://s01.oss.sonatype.org/content/repositories/releases/")
    maven("https://repository.derklaro.dev/snapshots/")
    maven("https://repository.derklaro.dev/releases/")
    maven("https://hub.spigotmc.org/nexus/content/groups/public/")
    maven("https://repo.codemc.io/repository/maven-public/")
    maven("https://libraries.minecraft.net/")
}

dependencies {
    compileOnly("dev.derklaro.aerogel:aerogel:2.1.0")

    compileOnly("org.spigotmc:spigot-api:1.20.1-R0.1-SNAPSHOT")
    compileOnly("net.md-5:bungeecord-api:1.20-R0.2-SNAPSHOT")

    compileOnly("eu.cloudnetservice.cloudnet:node-impl:4.0.0-RC12")
    compileOnly("eu.cloudnetservice.cloudnet:bridge:4.0.0-RC12")
    compileOnly("eu.cloudnetservice.cloudnet:platform-inject-api:4.0.0-RC12")

    compileOnly("org.projectlombok:lombok:1.18.38")
    annotationProcessor("org.projectlombok:lombok:1.18.38")
    annotationProcessor("eu.cloudnetservice.cloudnet:platform-inject-processor:4.0.0-RC12")

    compileOnly(fileTree("libs"))
}

tasks {
    withType<JavaCompile> {
        options.compilerArgs.add("--enable-preview")
    }
}

blossom {
    replaceToken("@project_name@", project.name)
    replaceToken("@project_version@", project.version.toString())
}

moduleJson {
    author = "Score2"
    name = project.name
    group = project.group.toString()
    main = "${rootProject.group}.TmuxServicesModule"
}
