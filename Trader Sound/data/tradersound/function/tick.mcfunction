# Ищем торговцев, которых ещё не отметили, и помечаем их.
# Тег нужен, чтобы звук сыграл ОДИН раз на торговца, а не каждый тик.
execute as @e[type=minecraft:wandering_trader,tag=!ts.seen] run function tradersound:on_appear

# Отдельно ламы торговца — они появляются вместе с ним,
# но звук на них играть не нужно, просто помечаем.
tag @e[type=minecraft:trader_llama,tag=!ts.seen] add ts.seen
