package dev.lumix.astatine.engine

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import ktx.assets.getAsset
import ktx.assets.load

enum class SoundAssets(val path: String) {
    Break("sounds/break.wav"),
    Pick("sounds/pick.wav"),
    Pop("sounds/pop.ogg"),
    Stone("sounds/stone.ogg"),
    Yippee("sounds/yippee.mp3"),
    Nuke("sounds/nuke.wav"),
    Dick("sounds/dick.mp3")
}

fun AssetManager.load(asset: SoundAssets) = load<Sound>(asset.path)
operator fun AssetManager.get(asset: SoundAssets) = getAsset<Sound>(asset.path)

enum class TextureAtlasAssets(val path: String) {
    Game("textures/pack.atlas")
}

fun AssetManager.load(asset: TextureAtlasAssets) = load<TextureAtlas>(asset.path)
operator fun AssetManager.get(asset: TextureAtlasAssets) = getAsset<TextureAtlas>(asset.path)

enum class TextureAssets(val path: String) {
    Yippert("textures/yippert.png")
}

fun AssetManager.load(asset: TextureAssets) = load<Texture>(asset.path)
operator fun AssetManager.get(asset: TextureAssets) = getAsset<Texture>(asset.path)