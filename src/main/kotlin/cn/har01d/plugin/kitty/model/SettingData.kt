package cn.har01d.plugin.kitty.model

data class SettingData(
    var enabled: Boolean = false,
    var workTime: Int = 60,
    var restTime: Int = 5,
    var remoteImages: Boolean = false,
    var imageApi: String = "https://cataas.com/cat"
)
