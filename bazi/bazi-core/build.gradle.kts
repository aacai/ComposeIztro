import org.gradle.api.publish.maven.MavenPublication
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    `maven-publish`
}

group = "io.github.zhiqiu"
version = "0.1.0-SNAPSHOT"

kotlin {
    android {
        namespace = "zhiqiu.iztro.bazi.core"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
    }

    jvm {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
    }

    iosArm64()
    iosSimulatorArm64()

    // tyme4kt / compose 均有 wasm-js 变体，web 端可用
    wasmJs()

    sourceSets {
        commonMain.dependencies {
            // SixtyCycle 等类型出现在公开 API（FlowChart/DecadeOption…），必须 api 传递
            api(libs.tyme4kt)
            implementation(libs.kotlinx.datetime)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        val jvmTest by getting {
            dependencies {
                implementation(libs.kotlin.testJunit)
            }
        }
    }
}

publishing {
    publications.withType<MavenPublication> {
        pom {
            name.set("bazi-core")
            description.set("Kotlin Multiplatform 八字排盘库（四柱/神煞/大运流年/四柱反查）")
            url.set("https://github.com/zhiqiu/bazi-kmp")
            licenses {
                license {
                    name.set("MIT")
                    url.set("https://opensource.org/licenses/MIT")
                }
            }
        }
    }
}
