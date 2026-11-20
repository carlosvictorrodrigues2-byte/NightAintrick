# NIGHT AINTRICK
Painel de desempenho e perfis de configuração para Android (Kotlin + Jetpack Compose).

## Gerar o APK
1. Instale o Android Studio (Koala ou mais novo) com JDK 17.
2. File > Open > pasta NightAintrick. Aguarde o Gradle sincronizar (precisa de internet na 1ª vez).
3. Para testar: conecte o celular com Depuração USB e clique em Run.
4. Para o APK: Build > Build Bundle(s)/APK(s) > Build APK(s).
   Saída: app/build/outputs/apk/debug/app-debug.apk
5. Linha de comando (após o Android Studio gerar o wrapper): ./gradlew assembleDebug
