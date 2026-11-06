plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.chaquopy)
}

// 必须与 :app 的 abiFilters 保持一致：Chaquopy 按本模块 abiFilters 生成 Python 运行时，
// 两边不一致会导致目标架构上缺少 libpython，Python 爬虫全部不可用。
val targetAbis = rootProject.extra["targetAbis"] as List<*>

android {
    namespace = "com.undcover.freedom.pyramid"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        ndk {
            abiFilters += targetAbis.map { it.toString() }
        }
    }

    buildTypes {
        debug {
        }
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

dependencies {
    coreLibraryDesugaring(libs.desugar.jdk.libs)
}

chaquopy {
    defaultConfig {
        version = "3.10"
        val explicitBuildPython = (project.findProperty("buildPython") as String?)
            ?: System.getenv("CHAQUOPY_BUILD_PYTHON")
        val defaultBuildPython = File(
            System.getProperty("user.home"),
            "AppData/Local/Programs/Python/Python310/python.exe",
        ).absolutePath
        listOfNotNull(explicitBuildPython, defaultBuildPython)
            .firstOrNull { file(it).exists() }
            ?.let { buildPython(it) }
        pip {
            options("-i", "https://repo.huaweicloud.com/repository/pypi/simple/")
            options("--extra-index-url", "https://chaquo.com/pypi-13.1")
            install("lxml")
            install("ujson")
            install("pyquery==2.0.2")
            install("requests")
            install(file("wheels/jsonpath-0.54-py3-none-any.whl").absolutePath)
            install("cachetools")
            install("pycryptodome")
            install("beautifulsoup4")
        }
    }
    sourceSets {
        getByName("main") {
            setSrcDirs(listOf("src/python"))
        }
    }
}
