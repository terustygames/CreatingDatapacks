# ЭТО НЕ ДАТАПАК — НЕ ГРУЗИТЬ В ИГРУ

Здесь нет `pack.mcmeta`, поэтому Minecraft эту папку не увидит.

Датапак лежит рядом и называется **`One block water 1.21.11`**.

---

Тут склад файлов для следующих этапов. Возвращать по одному,
проверяя игру после каждого.

- `tags/function/load.json`, `tags/function/tick.json` — ОБЯЗАТЕЛЬНАЯ пара,
  без них mcfunction не запускаются вообще. Класть в data/minecraft/tags/function/
- `function/*.mcfunction` — в data/oneblockwater/function/
- `structure/ender_portal.nbt` — в data/oneblockwater/structure/
- `empty_all.json` — processor_list, в data/minecraft/worldgen/processor_list/

Перед возвратом проверить команды на 1.21.11 (см. ПЛАН.md, Этап 2).
