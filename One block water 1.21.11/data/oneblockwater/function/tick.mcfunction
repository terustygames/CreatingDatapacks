# Портал ставим один раз за мир, независимо от игроков.
# Раньше он висел в first_join — если первый игрок уже был помечен obw.joined
# (например, пак поставили в существующий мир), портал не появлялся никогда.
execute unless data storage oneblockwater:state {portal_placed:1b} run function oneblockwater:place_portal

execute as @a[tag=!obw.joined] run function oneblockwater:first_join
execute in minecraft:the_nether as @a[tag=!obw.nether_joined] at @s if dimension minecraft:the_nether run function oneblockwater:nether_join
