package dev.lumix.astatine.world.block

import dev.lumix.astatine.engine.Static
import dev.lumix.astatine.engine.TextureAtlasAssets
import dev.lumix.astatine.engine.get

class BlockManager {
    companion object {
        private val blocks = HashMap<BlockType, Block>()

        init {
            blocks[BlockType.AIR]   = Block(BlockType.AIR   , null)
            blocks[BlockType.GRASS] = Block(BlockType.GRASS , Static.assets[TextureAtlasAssets.Game].findRegion("grass"))
            blocks[BlockType.DIRT]  = Block(BlockType.DIRT  , Static.assets[TextureAtlasAssets.Game].findRegion("dirt"))
            blocks[BlockType.STONE] = Block(BlockType.STONE , Static.assets[TextureAtlasAssets.Game].findRegion("stone"))
            blocks[BlockType.ORE]   = Block(BlockType.ORE   , Static.assets[TextureAtlasAssets.Game].findRegion("ore"))
        }

        fun getBlock(type: BlockType): Block? {
            return blocks[type]
        }
    }
}