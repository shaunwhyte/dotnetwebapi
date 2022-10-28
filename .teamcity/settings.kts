import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.FileContentReplacer
import jetbrains.buildServer.configs.kotlin.buildFeatures.replaceContent
import jetbrains.buildServer.configs.kotlin.buildSteps.dotnetBuild
import jetbrains.buildServer.configs.kotlin.buildSteps.dotnetPack
import jetbrains.buildServer.configs.kotlin.buildSteps.dotnetPublish
import jetbrains.buildServer.configs.kotlin.buildSteps.dotnetTest
import jetbrains.buildServer.configs.kotlin.triggers.vcs

version = "2022.10"

project {

    var bts = sequential {
        buildType(Build)
        parallel {
            buildType(TestRunner("Run Unit Tests", "WebAPI/../UnitTests/UnitTests.csproj"))
            buildType(TestRunner("Run Integration Tests", "WebAPI/../IntegrationTests/IntegrationTests.csproj"))
        }
        buildType(Publish)
    }.buildTypes()

    bts.forEach{ buildType(it) }

    bts.last().triggers {

        vcs {
            watchChangesInDependencies = true
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

class TestRunner(name: String, projectToRun: String) : BuildType({

    id(name.toId())
    this.name = name

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
            projects = projectToRun
            sdk = "6"
            skipBuild
        }
    }
})

object Publish : BuildType({
    name = "Publish"

    artifactRules = "WebAPI/bin/Release/net6.0/publish => teamcity-%build.counter%.zip"

    vcs {
        root(DslContext.settingsRoot)
    }

    params {
        param("env.random-variable", "Hi from TC")
    }

    steps {
        dotnetPublish {
            name = "Publish"
            projects = "WebAPI/WebAPI.csproj"
            configuration = "Release"
            param("dotNetCoverage.dotCover.home.path", "%teamcity.tool.JetBrains.dotCover.CommandLineTools.DEFAULT%")
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