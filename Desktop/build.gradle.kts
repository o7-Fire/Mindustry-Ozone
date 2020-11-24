plugins {
    base
    java
    `java-library`
}
repositories {
    mavenLocal()
    mavenCentral()
    jcenter()
    maven ( "https://oss.sonatype.org/content/repositories/snapshots/" )
    maven ( "https://oss.sonatype.org/content/repositories/releases/" )
    maven ("https://jitpack.io")
}

tasks.withType(Jar::class.java) {
    dependsOn(":makeJar")
    archiveFileName.set("Ozone-Desktop.jar")
    archiveClassifier.set("all")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(configurations.getByName("runtimeClasspath").map { if (it.isDirectory) it else zipTree(it) })
    manifest {
        attributes["Main-Class"] = "Premain.InstallerEntryPoint"
    }
}

dependencies {
    val atomHash = findProperty("atomHash")
    val mindustryVersion = findProperty("mindustryVersion")

    testImplementation(group="junit", name="junit", version="4.12")


    implementation(":Core")
    implementation( "io.sentry:sentry:3.1.0")

    compileOnly( "com.github.o7-Fire.Atomic-Library:Desktop:$atomHash"               )
    compileOnly( "com.github.Anuken.Mindustry:desktop:$mindustryVersion"             )
    compileOnly( "com.github.Anuken.Mindustry:annotations:$mindustryVersion"         )
    compileOnly( "com.github.Anuken.Arc:backend-headless:$mindustryVersion"          )



    compileOnly( "io.github.vincenzopalazzo:material-ui-swing:1.1.2-rc1"       )
    compileOnly( group="com.miglayout", name="miglayout-core", version="5.2"   )
    compileOnly( group="com.miglayout", name="miglayout-swing", version="5.2"  )
    compileOnly( "com.github.MinnDevelopment:java-discord-rpc:v2.0.1"          )
}

tasks.withType(JavaCompile::class.java) {
    sourceCompatibility = "14"
    targetCompatibility = "14"
    options.encoding = "UTF-8"
    options.compilerArgs.add("-Xlint:deprecation")
}