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
dependencies {
    val atomHash = findProperty("atomHash")
    val mindustryVersion = findProperty("mindustryVersion")
    testImplementation ("com.github.javaparser:javaparser-symbol-solver-core:3.16.1"                          )
    testImplementation (group="com.google.googlejavaformat", name="google-java-format", version="1.7"      )
    testImplementation( group="junit", name="junit", version="4.12"         )
    testImplementation( "com.github.o7-Fire.Atomic-Library:Desktop:$atomHash"  )
    testImplementation( "com.github.o7-Fire.Atomic-Library:Atomic:$atomHash"   )
    implementation( project(":Desktop")                  )
    implementation( rootProject                          )


}