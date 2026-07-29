# Файлы для следующих этапов

Вынуто из минимального пака намеренно, чтобы Этап 1 грузился гарантированно.
Возвращать по одному, проверяя игру после каждого.

- `tags/function/load.json`, `tags/function/tick.json` — ОБЯЗАТЕЛЬНАЯ пара,
  без них mcfunction не запускаются вообще. Класть в data/minecraft/tags/function/
- `function/*.mcfunction` — в data/oneblockwater/function/
- `structure/ender_portal.nbt` — в data/oneblockwater/structure/
- `empty_all.json` — processor_list, в data/minecraft/worldgen/processor_list/

Перед возвратом проверить команды на 1.21.11 (см. ПЛАН.md, Этап 2).
