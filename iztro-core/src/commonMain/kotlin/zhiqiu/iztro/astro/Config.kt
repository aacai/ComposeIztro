package zhiqiu.iztro.astro

import zhiqiu.iztro.i18n.kot
import zhiqiu.iztro.model.Config

private var configState = Config()

fun config(cfg: Config) {
    val mutagens = cfg.mutagens.takeIf { it.isNotEmpty() }?.mapKeys { (key, _) ->
        kot<String>(key, "Heavenly")
    }?.mapValues { (_, value) ->
        value.map { item -> kot<String>(item) }
    }
    val brightness = cfg.brightness.takeIf { it.isNotEmpty() }?.mapKeys { (key, _) ->
        kot<String>(key)
    }?.mapValues { (_, value) ->
        value.map { item -> kot<String>(item) }
    }
    configState = configState.copy(
        mutagens = mutagens ?: configState.mutagens,
        brightness = brightness ?: configState.brightness,
        yearDivide = cfg.yearDivide,
        horoscopeDivide = cfg.horoscopeDivide,
        ageDivide = cfg.ageDivide,
        dayDivide = cfg.dayDivide,
        algorithm = cfg.algorithm,
    )
}

fun getConfig(): Config = configState
