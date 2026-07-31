tag @s add obw.joined

# X и Z оставляем ванильные — куда игра сама поставила игрока.
# Меняем только высоту на 64.
# ВАЖНО: tick вызывает эту функцию через "execute as", без "at",
# поэтому позиция выполнения НЕ игрока. Каждой команде нужен "at @s".
execute at @s run tp @s ~ 64 ~

# Стартовый блок воды прямо под игроком, где бы он ни оказался.
execute at @s run setblock ~ 63 ~ minecraft:water
execute at @s run fillbiome ~-2 61 ~-2 ~2 65 ~2 minecraft:jungle

# Точка возрождения игрока — там же.
execute at @s run spawnpoint @s ~ 64 ~

# Мировой спавн ставим ОДИН раз, по первому игроку,
# иначе каждый новый игрок сдвигал бы его на себя.
execute at @s unless data storage oneblockwater:state {worldspawn_set:1b} run setworldspawn ~ 64 ~
data modify storage oneblockwater:state worldspawn_set set value 1b

give @s fishing_rod[enchantments={mending:1}] 1

tellraw @s {"text":"Добро пожаловать в One Block Water!","color":"aqua"}
