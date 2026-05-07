# Тестирование


> **Запуск Unit тестов**
> 
> ```powershell
> ./gradlew test
> ```

> **Kover анализ покрытия тестами**
>
> ```powershell
> ./gradlew koverHtmlReport
> ```

> **Allure отчет**
>
> ```powershell
> allure serve --results app/build/allure-results
> ```

> **Запуск UI тестов**
>
> ```powershell
> ./gradlew :app:connectedDebugAndroidTest
> ```
