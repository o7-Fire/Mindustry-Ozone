rootProject.name = "Ozone"
include("Desktop")
include("Tools")
fun use(vararg list: String) {
    for (name in list) {
        include(name)
        project(name).projectDir = File(settingsDir, "../${name.substring(1).replace(":", "/")}")
    }
}
if (File(settingsDir, "../Atom").exists()) {
    use(

            ":Atom",
            ":Atom:Atomic",
            ":Atom:Desktop"
    )
}
include("Core")
