# Выполняется ОДИН раз для каждого нового торговца.
# @s — сам торговец, tag=!ts.seen уже проверен в tick.

tag @s add ts.seen

# Звук слышат только игроки в радиусе 64 блоков от торговца.
# Позиция звука — координаты торговца, поэтому слышно направление.
execute at @s run playsound minecraft:entity.wandering_trader.reappeared master @a[distance=..64] ~ ~ ~ 1.0 1.0

# Сообщение тем же игрокам.
execute at @s run tellraw @a[distance=..64] {"text":"Рядом появился странствующий торговец","color":"aqua","italic":true}
