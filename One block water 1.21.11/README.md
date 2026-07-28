# One Block Water - Void Worldgen 1.21.11

Датапак генерирует пустой оверворлд и пустой ад, но оставляет биомы и bounding box'ы структур, а энд остаётся ванильным.

## Основано на шаблоне skyvoid
Использован современный `skyvoid_worldgen_empty` (v2.0.11, для 1.21/26.1) от BPR02 как шаблон:

- `data/minecraft/worldgen/noise_settings/overworld.json` и `nether.json` - войд (default_block=jigsaw, surface_rule=air, + geode purge)
- `data/minecraft/worldgen/noise/iceberg_*` - отключает айсберги
- `data/minecraft/worldgen/biome/*.json` - все оверворлд и адские биомы переопределены: carvers=[], features очищены, оставлены только `geode_purge` для удаления блоков, тем самым мир пустой но карта биомов остаётся как в ваниле с тем же сидом
- `data/minecraft/structure/*/*.nbt` - все структуры оверворлда и ада (ancient_city, bastion, village, pillager_outpost, woodland_mansion, trial_chambers и т.д.) заменены на пустые (только jigsaw/air), поэтому блоки построек не генерируются, но bounding box сохраняется (фермы ведьм, гвардов, фортрессов работают)
- `data/skyvoid_worldgen/` - теги `final_purge`, `initial_purge`, `structure_void` и configured_feature `geode_purge` которые удаляют все блоки
- Энд специально оставлен ванильным:
  - НЕ копируется `noise_settings/end.json`
  - НЕ копируются биомы `the_end`, `end_highlands`, `end_midlands`, `end_barrens`, `small_end_islands`
  - НЕ копируются структуры `end_city` (города энда остаются ванильными)

## Что исправлено относительно оригинала
- Старый `empty_nether.json` с min_y 0 height 128 и final_density -1M заменён на современный войд из skyvoid (jigsaw + air + geode purge) - теперь корректно пусто в 1.21.11
- Старый `empty_overworld.json` аналогично заменён
- Добавлены `processor_list/empty_all` и `empty` для совместимости
- Добавлен `world_preset/normal.json` который явно указывает:
  - overworld -> minecraft:overworld (войд)
  - nether -> minecraft:nether (войд)
  - end -> minecraft:end (ваниль)
- Сохранены твои функции `oneblockwater:first_join`, `tick`, `place_portal` и структура `ender_portal.nbt`

## Как это работает (bounding box без построек)
1. Noise settings ставит default_block=jigsaw и surface_rule=air -> террейн воздух
2. Биомы очищены от всех features кроме geode_purge
3. Geode purge - это configured_feature типа geode который заменяет все блоки из тега `#skyvoid_worldgen:final_purge` (почти все блоки кроме jigsaw) на воздух в каждом чанке каждые 16 блоков по высоте
4. Структуры (village, mansion и т.д.) генерируются игрой, их bounding box считается, но их NBT файлы перезаписаны на пустые (только воздух и jigsaw), поэтому блоков нет, а рамка остаётся

В итоге:
- Overworld: полностью пусто, биомы как в ваниле, рамки структур есть
- Nether: полностью пусто, биомы ада как в ваниле, рамки бастионов/фортрессов есть
- End: ванильный (столбы, портал, острова, города)

## Установка
Положи папку `One block water 1.21.11` в `world/datapacks/` до создания мира или сделай `/datapack enable`

Формат пака 94 для 1.21.11
