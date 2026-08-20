# 🚀 Быстрый старт — NukeAccount плагин

## Вариант 1: Сборка через Gradle (рекомендуется)

### Требования:
- Android SDK (API 33+)
- Gradle 7.0+
- JDK 11+

### Шаги:
```bash
# 1. Клонируй репо
git clone https://github.com/lekanava61-create/ihopeiddie.git
cd ihopeiddie

# 2. Подключи телефон по ADB (или запусти эмулятор с Aliucord)
adb devices

# 3. Собери и установи плагин
./gradlew make deployWithAdb

# 4. Перезагрузи Discord
# В приложении: Настройки → Aliucord → Plugins → включи NukeAccount
```

Плагин будет скопирован в `/sdcard/Aliucord/plugins/`.

---

## Вариант 2: Готовый ZIP (если сборка не работает)

Если у тебя нет Android SDK или Gradle:

1. Попроси собрать `classes.dex` у кого-то с полным тулчейном (или используй GitHub Actions workflow).
2. Упакуй в ZIP-архив:
   ```
   NukeAccount.zip
   ├── manifest.json
   └── classes.dex
   ```
3. Скопируй на телефон в `/sdcard/Aliucord/plugins/`

---

## Использование

В любом чате Discord напиши:
```
/nuke
```

Плагин:
- ✅ Выходит из всех серверов
- ✅ Удаляет всех друзей
- ✅ Соблюдает рейт-лимиты Discord
- ⚠️ **Действие необратимо!** Перед запуском убедись.

---

## Если ошибка при сборке

**Ошибка: `StoreStream.getAuthentication().token` не найден?**

Discord периодически переименовывает внутренние методы. Решение:
1. Открой `base.apk` в [JADX](https://github.com/skylot/jadx)
2. Найди класс `StoreAuthentication` (или похожий)
3. Скопируй актуальный путь до геттера токена
4. Замени в `NukeAccount.kt` строку 40

---

## Безопасность

- Плагин использует **официальный Discord API** (v9), не внутренние методы
- Токен берётся безопасно из Aliucord Store (не из памяти)
- Никаких сетевых запросов кроме Discord API
