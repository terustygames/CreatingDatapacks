tag @s add obw.joined
setworldspawn 0 80 0
fillbiome -2 78 -2 2 82 2 minecraft:jungle
setblock 0 79 0 minecraft:water
spawnpoint @s 0 80 0
tp @s 0.5 80 0.5
give @s fishing_rod[enchantments={mending:1}] 1

tellraw @s {"text":"Добро пожаловать в One Block Water!","color":"aqua"}
