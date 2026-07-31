execute as @a[tag=!obw.joined] run function oneblockwater:first_join
execute in minecraft:the_nether as @a[tag=!obw.nether_joined] at @s if dimension minecraft:the_nether run function oneblockwater:nether_join
