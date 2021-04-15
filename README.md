<p align="center">
<img width="20%" src="https://mhcoin.s3.filebase.com/avatar.jpg">
<br>

<h1 align="center"> mhapp </h1>

## Компиляция mhapp

### `0.` Зависимости

* PowerShell (Windows) или Terminal (Linux).
> ⚠️ Коммандная строка Windows может не работать, лучше используйте PowerShell!
* Вам нужно иметь [Java Development Kit](https://adoptopenjdk.net/) установленной на вашей машине. Минимальная версия для компиляции и запуска mhapp это JDK 15.
* Вам нужно иметь Git установленной на вашей машине.
* Проверьте чтобы `JAVA_HOME` был установлен правильно, новые JDK версии скачанные с AdoptOpenJDK могут уже иметь правильно установленную переменную. Вы можете проверить это командой `echo $env:JAVA_HOME` в PowerShell.

### `1.` 🧹 Подготовка среды

* Сколинуйте репозиторий с помощью git:
```bash
git clone https://github.com/Ivanhai/mhapp.git
```

### `2.` 💻 Компиляция
* Зайдите в папку с исходниками и откройте PowerShell или терминал внутри.
* Билд mhapp с Gradle:
```bash
./gradlew :app:assembleDebug
```

* Если билд успешный, то поздравляю 🎉! Вы успешно скомпилировали mhapp!
* Финальный билд будет в папке `app/build/outputs/apk/debug/app-debug.apk`.

**Вы можете найти уже готовые здесь:** https://github.com/Ivanhai/mhapp/actions
