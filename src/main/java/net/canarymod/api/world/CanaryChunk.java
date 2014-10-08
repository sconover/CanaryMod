package net.canarymod.api.world;

import net.canarymod.api.entity.Entity;
import net.canarymod.api.world.blocks.TileEntity;
import net.canarymod.api.world.position.Position;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Chunk implementation
 *
 * @author Chris (damagefilter)
 * @author Jason (darkdiplomat)
 * @author Jos Kuijpers
 */
public class CanaryChunk implements Chunk {
    private net.minecraft.world.chunk.Chunk handle;

    public CanaryChunk(net.minecraft.world.chunk.Chunk chunk) {
        this.handle = chunk;
    }

    public net.minecraft.world.chunk.Chunk getHandle() {
        return handle;
    }

    @Override
    public int getX() {
        return handle.a;
    }

    @Override
    public int getZ() {
        return handle.b;
    }

    @Override
    public int getBlockTypeAt(int x, int y, int z) {
        return net.minecraft.block.Block.a(handle.a(x, y, z));
    }

    @Override
    public void setBlockTypeAt(int x, int y, int z, int type) {
        // returns the new block state, do something with it?
        handle.a(new BlockPos(x, y, z), Block.d(type));
    }

    @Override
    public int getBlockDataAt(int x, int y, int z) {
        IBlockState state = handle.g(new BlockPos(x, y, z));
        return state.c().c(state);
    }

    @Override
    public void setBlockDataAt(int x, int y, int z, int data) {
        IBlockState state = handle.g(new BlockPos(x, y, z));
//        state.a()
//        handle.a(x, y, z, data);
    }

    @Override
    public int getMaxHeigth() {
        return 256;
    }

    @Override
    public boolean isLoaded() {
        return handle.k;
    }

    @Override
    public World getDimension() {
        return handle.i.getCanaryWorld();
    }

    @Override
    public byte[] getBiomeByteData() {
        return handle.k();
    }

    @Override
    public BiomeType[] getBiomeData() {
        return BiomeType.fromIdArray(getBiomeByteData());
    }

    @Override
    public void setBiomeData(BiomeType[] type) {
        if (type.length != 256) {
            return;
        }
        handle.a(BiomeType.fromTypeArray(type));
    }

    @Override
    public void setBiomeData(byte[] data) {
        if (data.length != 256) {
            return;
        }
        for (int index = 0; index < data.length; index++) {
            if (data[index] < 0 || data[index] > BiomeType.count()) { //Use BiomeType.count() so we don't forget to adjust this here
                data[index] = 0;
            }
        }
        handle.a(data);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<Position, TileEntity> getTileEntityMap() {
        HashMap<Position, TileEntity> toRet = new HashMap<Position, TileEntity>();
        synchronized (handle.r()) {
            for (BlockPos pos : (Set<BlockPos>) handle.r().keySet()) {
                Position cPos = new Position(pos.n(), pos.o(), pos.p());
                net.minecraft.tileentity.TileEntity te = (net.minecraft.tileentity.TileEntity) handle.r().get(pos);
                if (te.complexBlock != null) {
                    toRet.put(cPos, te.complexBlock);
                }
            }
        }
        return toRet;
    }

    @Override
    public boolean hasEntities() {
        return handle.hasEntities();
    }

    @SuppressWarnings("unchecked")
    @Deprecated
    public List<Entity>[] getEntityLists() {
        // FIXME: Chunk doesn't have a list of its entities anymore
//        List<Entity>[] toRet = new List[handle.j.length];
//        for (int index = 0; index < handle.j.length; index++) {
//            toRet[index] = new ArrayList<Entity>();
//            for (Object e : handle.j[index]) {
//                toRet[index].add(((net.minecraft.entity.Entity) e).getCanaryEntity());
//            }
//        }
//        return toRet;
        return null;
    }

    @Override
    public int[] getHeightMap() {
        return handle.q();
    }

    @Override
    public int[] getPrecipitationHeightMap() {
        return handle.f;
    }

    @Override
    public long getLastSaveTime() {
        return handle.getTimeSaved();
    }

    @Override
    public boolean isTerrainPopulated() {
        return handle.t();
    }

    @Override
    public boolean isModified() {
        return handle.u();
    }

    @Override
    public void generateSkyLightMap() {
        handle.b();
    }

    @Override
    public void updateSkyLightMap(boolean force) {
        if (force) {
            handle.n();
        } else {
            handle.m();
        }
    }

    @Override
    public void relightBlock(int x, int y, int z) {
        handle.d(x, y, z);
    }

    @Override
    public Biome getBiome(int x, int z) {
        return handle.a(new BlockPos(x, 0, z), handle.i.v()).getCanaryBiome();
    }
}
