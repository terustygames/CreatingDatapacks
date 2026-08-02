tag @s add obw.joined

# ВАЖНО: tick вызывает эту функцию через "execute as", без "at",
# поэтому позиция выполнения НЕ игрока. Каждой команде нужен "at @s".

# X и Z оставляем ванильные — куда игра сама поставила игрока.
# Меняем только высоту на 64.
execute at @s run tp @s ~ 64 ~

# Разовая настройка мира: блок воды, биом, мировой спавн.
# Только для ПЕРВОГО игрока. Остальные попадут на уже готовый спавн,
# и вода заново создаваться не будет.
execute at @s unless data storage oneblockwater:state {world_setup:1b} run function oneblockwater:world_setup

# Личная точка возрождения — у каждого игрока своя, это нормально.
execute at @s run spawnpoint @s ~ 64 ~

give @s fishing_rod[enchantments={mending:1}] 1

tellraw @s {"text":"Добро пожаловать в One Block Water!","color":"aqua"}
