# Портал Края на 512 80 -2.
# Чанк 32,-1 далеко от спавна и НЕ загружен, а place template в незагруженном
# чанке молча не срабатывает. Поэтому форсим чанк, ставим, снимаем форс.
execute in minecraft:overworld run forceload add 512 -2
execute in minecraft:overworld positioned 512.0 80.0 -2.0 run place template oneblockwater:ender_portal 512 80 -2
execute in minecraft:overworld run forceload remove 512 -2

# Флаг, чтобы не ставить повторно при каждом заходе.
data modify storage oneblockwater:state portal_placed set value 1b

tellraw @a {"text":"[One Block Water] Портал Края создан на 512 80 -2","color":"light_purple"}
