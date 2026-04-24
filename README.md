# xxx


## 修改版本号


版本号在 build.gradle.kts 文件中修改：

```kotlin
android {
    ...

    defaultConfig {
        applicationId = "com.example.vvesmartcity"
        minSdk = 24
        targetSdk = 36
        versionCode = 1      // 内部版本号，每次更新需要 +1
        versionName = "1.0"  // 显示给用户的版本号，如 "1.0"、"2.0"

        ...
    }
    ...
}
```

- versionCode - 整数，用于 Google Play 判断版本新旧，每次发布新版本需要递增
- versionName - 字符串，显示给用户看的版本号，如 "1.0"、"1.1"、"2.0"
