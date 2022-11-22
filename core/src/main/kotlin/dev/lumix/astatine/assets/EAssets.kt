package dev.lumix.astatine.assets

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import ktx.assets.getAsset
import ktx.assets.load

enum class SoundAssets(val path: String) {
    Yippee("yippee.mp3")
}

inline fun AssetManager.load(asset: SoundAssets) = load<Sound>(asset.path)
inline operator fun AssetManager.get(asset: SoundAssets) = getAsset<Sound>(asset.path)

enum class TextureAtlasAssets(val path: String) {
    Game("pack.atlas")
}

inline fun AssetManager.load(asset: TextureAtlasAssets) = load<TextureAtlas>(asset.path)
inline operator fun AssetManager.get(asset: TextureAtlasAssets) = getAsset<TextureAtlas>(asset.path)