package dev.lumix.astatine.world.block

import dev.lumix.astatine.engine.SoundAssets
import dev.lumix.astatine.engine.Static
import dev.lumix.astatine.engine.TextureAtlasAssets
import dev.lumix.astatine.engine.get

class BlockManager {
    companion object {
        private val blocks = HashMap<BlockType, Block>()

        // todo: make this uhh better? somehow.. use some design pattern or some shit idk
        init {
            val bAtlas = Static.assets[TextureAtlasAssets.Game]
            val wAtlas = Static.assets[TextureAtlasAssets.Walls]

            blocks[BlockType.AIR]       = Block(BlockType.AIR      , null                          , null,                           null, false)
            blocks[BlockType.GRASS]     = Block(BlockType.GRASS    , bAtlas.findRegion("grass")    , wAtlas.findRegion("grass"),     Static.assets[SoundAssets.Break], false)
            blocks[BlockType.DIRT]      = Block(BlockType.DIRT     , bAtlas.findRegion("dirt")     , wAtlas.findRegion("dirt"),      Static.assets[SoundAssets.Break], true)
            blocks[BlockType.STONE]     = Block(BlockType.STONE    , bAtlas.findRegion("stone")    , wAtlas.findRegion("stone"),     Static.assets[SoundAssets.Stone], true)
            blocks[BlockType.ORE]       = Block(BlockType.ORE      , bAtlas.findRegion("ore")      , wAtlas.findRegion("ore"),       Static.assets[SoundAssets.Stone], true)
            blocks[BlockType.DEEPSLATE] = Block(BlockType.DEEPSLATE, bAtlas.findRegion("deepslate"), wAtlas.findRegion("deepslate"), Static.assets[SoundAssets.Stone], true)
        }

        fun getBlock(type: BlockType): Block? {
            return blocks[type]
        }
    }
}