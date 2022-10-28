import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.FileContentReplacer
import jetbrains.buildServer.configs.kotlin.buildFeatures.replaceContent
import jetbrains.buildServer.configs.kotlin.buildSteps.dotnetBuild
import jetbrains.buildServer.configs.kotlin.buildSteps.dotnetPack
import jetbrains.buildServer.configs.kotlin.buildSteps.dotnetPublish
import jetbrains.buildServer.configs.kotlin.buildSteps.dotnetTest
import jetbrains.buildServer.configs.kotlin.triggers.vcs

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2022.10"

project {

    buildType(Build)
    buildType(RunTests)
    buildType(RunIntegrationTests)

    sequential {
        buildType(Build)
        parallel {
            buildType(RunTests)
            buildType(RunIntegrationTests)
        }

    }
}

object Build : BuildType({
    name = "Build From Kotlin Yay"

    artifactRules = "WebAPI/bin/Release/net6.0/publish => teamcity-%build.counter%.zip"

    vcs {
        root(DslContext.settingsRoot)
    }

    params {
        param("env.random-variable", "Hi from TC")
    }

    steps {
        dotnetBuild {
            enabled = true
            projects = "WebAPI/WebAPI.sln"
            sdk = "6"
        }
    }

    features {
        replaceContent {
            fileRules = "**/appsettings.json"
            pattern = "secret-value-for-tc"
            regexMode = FileContentReplacer.RegexMode.FIXED_STRINGS
            replacement = "team city"
            customEncodingName = ""
        }
    }
})

object RunTests : BuildType({
    name = "Run Tests"

    artifactRules = "WebAPI/bin/Release/net6.0/publish => teamcity-%build.counter%.zip"

    vcs {
        root(DslContext.settingsRoot)
    }

    params {
        param("env.random-variable", "Hi from TC")
    }

    steps {
        dotnetTest {
            enabled = true
            projects = "WebAPI/../UnitTests/UnitTests.csproj"
            sdk = "6"
            skipBuild
        }
    }

    triggers {
        vcs {
        }
    }
})

object RunIntegrationTests : BuildType({
    name = "Run Integration Tests"

    artifactRules = "WebAPI/bin/Release/net6.0/publish => teamcity-%build.counter%.zip"

    vcs {
        root(DslContext.settingsRoot)
    }

    params {
        param("env.random-variable", "Hi from TC")
    }

    steps {
        dotnetTest {
            enabled = true
            projects = "WebAPI/../IntegrationTests/IntegrationTests.csproj"
            sdk = "6"
            skipBuild
        }
    }

    triggers {
        vcs {
        }
    }
})