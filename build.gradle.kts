plugins {
    id(conventions.jvm.core)
}

val installGitHooks by tasks.registering {
    group = "git"
    description = "Copies all git hooks from .githooks/ to .git/hooks/"

    val srcDir = layout.projectDirectory.dir(".githooks")
    val dstDir = layout.projectDirectory.dir(".git/hooks")

    inputs.dir(srcDir)
    outputs.dir(dstDir)

    doLast {
        val src = srcDir.asFile
        val dst = dstDir.asFile

        if (!src.exists()) {
            logger.warn(".githooks directory not found — nothing to install.")
            return@doLast
        }

        if (!dst.exists()) {
            dst.mkdirs()
        }

        src.listFiles()?.forEach { hook ->
            val target = dst.resolve(hook.name)
            hook.copyTo(target, overwrite = true)
            target.setExecutable(true)
            logger.lifecycle("Installed hook: ${hook.name}")
        }
    }
}

tasks.build {
    dependsOn(installGitHooks)
}
