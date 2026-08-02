# Разовая настройка мира. Вызывается ОДИН раз за всё существование мира,
# из first_join первого зашедшего игрока.
# Позиция берётся от него (функция вызвана через execute at @s).

# Стартовый блок воды.
setblock ~ 63 ~ minecraft:water

# Биом вокруг стартовой точки.
fillbiome ~-2 61 ~-2 ~2 65 ~2 minecraft:jungle

# Мировой спавн — сюда будут попадать все следующие игроки.
setworldspawn ~ 64 ~

# Флаг: больше не повторять.
data modify storage oneblockwater:state world_setup set value 1b
