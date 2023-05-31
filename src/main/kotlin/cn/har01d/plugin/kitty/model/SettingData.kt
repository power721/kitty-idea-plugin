package cn.har01d.plugin.kitty.model

import com.intellij.ide.util.PropertyName

data class SettingData(
    @PropertyName("kitty.enabled") var enabled: Boolean = false,
    @PropertyName("kitty.workTime") var workTime: Int = 60,
    @PropertyName("kitty.restTime") var restTime: Int = 5,
    @PropertyName("kitty.localImages") var localImages: Boolean = true,
    @PropertyName("kitty.imageApi") var imageApi: String = "https://cataas.com/cat",
    @PropertyName("kitty.imageFolder") var imageFolder: String = ""
)
