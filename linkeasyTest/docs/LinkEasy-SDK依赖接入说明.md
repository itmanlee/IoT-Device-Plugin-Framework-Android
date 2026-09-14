# LinkEasy SDK 依赖配置

宿主工程如何把 SDK 拉下来并编过。不涉及业务初始化和调用。

当前可用版本：`1.0.0`（`compileSdk 35`，AGP `8.10.1`）。

---

## 1. 声明依赖

`gradle/libs.versions.toml`：

```toml
[versions]
linkeasySdk = "1.0.0"

[libraries]
linkeasy-sdk = { group = "io.enz.linkeasy", name = "linkeasysdk", version.ref = "linkeasySdk" }
linkeasy-featureAdddevice = { group = "io.enz.linkeasy", name = "linkeasyFeatureAdddevice", version.ref = "linkeasySdk" }
```

`app/build.gradle`：

```gradle
implementation libs.linkeasy.sdk
implementation libs.linkeasy.featureAdddevice
```

等价写法：

```gradle
implementation 'io.enz.linkeasy:linkeasysdk:1.0.0'
implementation 'io.enz.linkeasy:linkeasyFeatureAdddevice:1.0.0'
```

必须用 `implementation`，不要用已废弃的 `compile`。

---

## 2. 配置仓库（`settings.gradle`）

本工程使用 `dependencyResolutionManagement`，且：

```gradle
repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
```

仓库只能写在 `settings.gradle`，不要写在根 `build.gradle` 的 `allprojects { repositories }`。

`pluginManagement {}` 必须是该文件的**第一段代码**，前面不能有 `def`、读配置文件等语句。

完整示例：

```gradle
pluginManagement {
    repositories {
        maven { url 'https://maven.aliyun.com/repository/public' }
        google {
            url 'https://maven.aliyun.com/repository/google'
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral {
            url 'https://maven.aliyun.com/repository/central'
        }
        gradlePluginPortal()
        maven { url 'https://developer.huawei.com/repo/' }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven { url 'https://maven.aliyun.com/repository/public' }
        maven {
            url 'https://hjtcode-cn-hangzhou.devops.aliyuncs.com/packages/api/protocol/maven/repo-pxsqm'
            credentials {
                def mavenCredentials = new Properties()
                def mavenCredentialsFile = new File(settingsDir, "local.maven.properties")
                if (mavenCredentialsFile.exists()) {
                    mavenCredentialsFile.withInputStream { mavenCredentials.load(it) }
                }
                username mavenCredentials.getProperty("aliyun.maven.username", "")
                password mavenCredentials.getProperty("aliyun.maven.password", "")
            }
        }
        google {
            url 'https://maven.aliyun.com/repository/google'
        }
        mavenCentral {
            url 'https://maven.aliyun.com/repository/central'
        }
        maven { url 'https://developer.huawei.com/repo/' }
        maven { url 'https://jitpack.io' }
    }
}

rootProject.name = "linkeasyTest"
include ':app'
```

各仓库用途：

| 仓库 | 用途 |
| --- | --- |
| 私有 Maven `repo-pxsqm` | 拉 `io.enz.linkeasy:*` |
| 阿里云 public / google / central | 公共依赖；避免直连 `dl.google.com` 握手失败 |
| `https://developer.huawei.com/repo/` | 传递依赖 `com.huawei.hms:scanplus` |
| `https://jitpack.io` | SDK 中部分 GitHub 构件 |

凭证不要写进可提交脚本。工程根目录建 `local.maven.properties`（已在 `.gitignore`）：

```properties
aliyun.maven.username=<用户名>
aliyun.maven.password=<密码>
```

---

## 3. 宿主开启 DataBinding

SDK 内部使用 DataBinding。宿主 `app/build.gradle` 的 `android {}` 里必须打开，否则编不过（常见报错是找不到 `*Binding` 或 DataBinding 相关 task 失败）。

```gradle
android {
    compileSdk 35
    // ...
    buildFeatures {
        compose true
    }
    dataBinding {
        enabled = true
    }
}
```

也可以写成：

```gradle
android {
    buildFeatures {
        compose true
        dataBinding true
    }
}
```

两种写法等价，二选一即可。只开 Compose、不开 DataBinding 不够。

---

## 4. 宿主 AndroidX 版本

SDK 按 `compileSdk 35`、AGP `8.10.1` 发布。宿主不要用过高的 AndroidX，否则 `checkDebugAarMetadata` 会要求 compileSdk 36/37 或 AGP 9.x。

建议与 SDK 对齐：

```toml
[versions]
agp = "8.10.1"
coreKtx = "1.13.1"
lifecycleRuntimeKtx = "2.8.6"
activityCompose = "1.9.2"
composeBom = "2024.09.00"
```

`app/build.gradle` 中 `compileSdk 35`。

---

## 5. 配置完成后

Sync Project，或执行：

```bat
.\gradlew.bat :app:compileDebugKotlin
```
