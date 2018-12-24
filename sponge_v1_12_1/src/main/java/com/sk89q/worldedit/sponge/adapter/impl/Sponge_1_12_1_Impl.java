package com.sk89q.worldedit.sponge.adapter.impl;

import com.google.common.base.MoreObjects;
import com.sk89q.jnbt.*;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.blocks.BaseItemStack;
import com.sk89q.worldedit.entity.BaseEntity;
import com.sk89q.worldedit.internal.Constants;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.registry.state.Property;
import com.sk89q.worldedit.sponge.SpongeWorld;
import com.sk89q.worldedit.sponge.adapter.SpongeImplAdapter;
import com.sk89q.worldedit.util.TreeGenerator;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import com.sk89q.worldedit.world.entity.EntityType;
import com.sk89q.worldedit.world.entity.EntityTypes;
import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.block.*;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.nbt.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.*;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.biome.BiomeType;

import javax.annotation.Nullable;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

public class Sponge_1_12_1_Impl implements SpongeImplAdapter {

    private static final IBlockState JUNGLE_LOG = Blocks.LOG.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.JUNGLE);
    private static final IBlockState JUNGLE_LEAF = Blocks.LEAVES.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.JUNGLE).withProperty(BlockLeaves.CHECK_DECAY, Boolean.FALSE);
    private static final IBlockState JUNGLE_SHRUB = Blocks.LEAVES.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.OAK).withProperty(BlockLeaves.CHECK_DECAY, Boolean.FALSE);

    public int resolve(ItemType type) {
        return Item.getIdFromItem((Item) type);
    }

    public int resolve(BlockType type) {
        return Block.getIdFromBlock((Block) type);
    }

    @Override
    public int resolve(BiomeType type) {
        return Biome.getIdForBiome((Biome) type);
    }

    public ItemType resolveItem(int intID) {
        return (ItemType) Item.getItemById(intID);
    }

    public BlockType resolveBlock(int intID) {
        return (BlockType) Block.getBlockById(intID);
    }

    @Override
    public BiomeType resolveBiome(int intID) {
        return (BiomeType) Biome.getBiome(intID);
    }

    public NBTBase toNative(Tag tag) {
        if (tag instanceof IntArrayTag) {
            return toNative((IntArrayTag) tag);

        } else if (tag instanceof ListTag) {
            return toNative((ListTag) tag);

        } else if (tag instanceof LongTag) {
            return toNative((LongTag) tag);

        } else if (tag instanceof StringTag) {
            return toNative((StringTag) tag);

        } else if (tag instanceof IntTag) {
            return toNative((IntTag) tag);

        } else if (tag instanceof ByteTag) {
            return toNative((ByteTag) tag);

        } else if (tag instanceof ByteArrayTag) {
            return toNative((ByteArrayTag) tag);

        } else if (tag instanceof CompoundTag) {
            return toNative((CompoundTag) tag);

        } else if (tag instanceof FloatTag) {
            return toNative((FloatTag) tag);

        } else if (tag instanceof ShortTag) {
            return toNative((ShortTag) tag);

        } else if (tag instanceof DoubleTag) {
            return toNative((DoubleTag) tag);
        } else {
            throw new IllegalArgumentException("Can't convert tag of type " + tag.getClass().getCanonicalName());
        }
    }

    public NBTTagIntArray toNative(IntArrayTag tag) {
        int[] value = tag.getValue();
        return new NBTTagIntArray(Arrays.copyOf(value, value.length));
    }

    public NBTTagList toNative(ListTag tag) {
        NBTTagList list = new NBTTagList();
        for (Tag child : tag.getValue()) {
            if (child instanceof EndTag) {
                continue;
            }
            list.appendTag(toNative(child));
        }
        return list;
    }

    public NBTTagLong toNative(LongTag tag) {
        return new NBTTagLong(tag.getValue());
    }

    public NBTTagString toNative(StringTag tag) {
        return new NBTTagString(tag.getValue());
    }

    public NBTTagInt toNative(IntTag tag) {
        return new NBTTagInt(tag.getValue());
    }

    public NBTTagByte toNative(ByteTag tag) {
        return new NBTTagByte(tag.getValue());
    }

    public NBTTagByteArray toNative(ByteArrayTag tag) {
        byte[] value = tag.getValue();
        return new NBTTagByteArray(Arrays.copyOf(value, value.length));
    }

    public NBTTagCompound toNative(CompoundTag tag) {
        NBTTagCompound compound = new NBTTagCompound();
        for (Map.Entry<String, Tag> child : tag.getValue().entrySet()) {
            compound.setTag(child.getKey(), toNative(child.getValue()));
        }
        return compound;
    }

    public NBTTagFloat toNative(FloatTag tag) {
        return new NBTTagFloat(tag.getValue());
    }

    public NBTTagShort toNative(ShortTag tag) {
        return new NBTTagShort(tag.getValue());
    }

    public NBTTagDouble toNative(DoubleTag tag) {
        return new NBTTagDouble(tag.getValue());
    }

    public Tag fromNative(NBTBase other) {
        if (other instanceof NBTTagIntArray) {
            return fromNative((NBTTagIntArray) other);

        } else if (other instanceof NBTTagList) {
            return fromNative((NBTTagList) other);

        } else if (other instanceof NBTTagEnd) {
            return fromNative((NBTTagEnd) other);

        } else if (other instanceof NBTTagLong) {
            return fromNative((NBTTagLong) other);

        } else if (other instanceof NBTTagString) {
            return fromNative((NBTTagString) other);

        } else if (other instanceof NBTTagInt) {
            return fromNative((NBTTagInt) other);

        } else if (other instanceof NBTTagByte) {
            return fromNative((NBTTagByte) other);

        } else if (other instanceof NBTTagByteArray) {
            return fromNative((NBTTagByteArray) other);

        } else if (other instanceof NBTTagCompound) {
            return fromNative((NBTTagCompound) other);

        } else if (other instanceof NBTTagFloat) {
            return fromNative((NBTTagFloat) other);

        } else if (other instanceof NBTTagShort) {
            return fromNative((NBTTagShort) other);

        } else if (other instanceof NBTTagDouble) {
            return fromNative((NBTTagDouble) other);
        } else {
            throw new IllegalArgumentException("Can't convert other of type " + other.getClass().getCanonicalName());
        }
    }

    public IntArrayTag fromNative(NBTTagIntArray other) {
        int[] value = other.getIntArray();
        return new IntArrayTag(Arrays.copyOf(value, value.length));
    }

    public ListTag fromNative(NBTTagList other) {
        other = (NBTTagList) other.copy();
        List<Tag> list = new ArrayList<>();
        Class<? extends Tag> listClass = StringTag.class;
        int tags = other.tagCount();
        for (int i = 0; i < tags; i++) {
            Tag child = fromNative(other.removeTag(0));
            list.add(child);
            listClass = child.getClass();
        }
        return new ListTag(listClass, list);
    }

    public EndTag fromNative(NBTTagEnd other) {
        return new EndTag();
    }

    public LongTag fromNative(NBTTagLong other) {
        return new LongTag(other.getLong());
    }

    public StringTag fromNative(NBTTagString other) {
        return new StringTag(other.getString());
    }

    public IntTag fromNative(NBTTagInt other) {
        return new IntTag(other.getInt());
    }

    public ByteTag fromNative(NBTTagByte other) {
        return new ByteTag(other.getByte());
    }

    public ByteArrayTag fromNative(NBTTagByteArray other) {
        byte[] value = other.getByteArray();
        return new ByteArrayTag(Arrays.copyOf(value, value.length));
    }

    public CompoundTag fromNative(NBTTagCompound other) {
        @SuppressWarnings("unchecked") Set<String> tags = other.getKeySet();
        Map<String, Tag> map = new HashMap<>();
        for (String tagName : tags) {
            map.put(tagName, fromNative(other.getTag(tagName)));
        }
        return new CompoundTag(map);
    }

    public FloatTag fromNative(NBTTagFloat other) {
        return new FloatTag(other.getFloat());
    }

    public ShortTag fromNative(NBTTagShort other) {
        return new ShortTag(other.getShort());
    }

    public DoubleTag fromNative(NBTTagDouble other) {
        return new DoubleTag(other.getDouble());
    }

    public com.sk89q.worldedit.world.block.BlockType fromNative(Block block) {
        if (block == null || block == Blocks.AIR) {
            return com.sk89q.worldedit.world.block.BlockTypes.AIR;
        }

        String id = Block.REGISTRY.getNameForObject(block).toString();
        return MoreObjects.firstNonNull(com.sk89q.worldedit.world.block.BlockTypes.get(id), com.sk89q.worldedit.world.block.BlockTypes.AIR);
    }

    @Override
    public ItemStack makeSpongeStack(BaseItemStack itemStack) {
        Item item = Item.getByNameOrId(itemStack.getType().getId());
        if (item == null) {
            return (ItemStack) (Object) net.minecraft.item.ItemStack.EMPTY;
        }

        net.minecraft.item.ItemStack newStack = new net.minecraft.item.ItemStack(item, itemStack.getAmount());
        if (itemStack.hasNbtData()) {
            newStack.setTagCompound(toNative(itemStack.getNbtData()));
        }

        return (ItemStack) (Object) newStack;
    }

    @Override
    public BaseEntity createBaseEntity(Entity entity) {
        String id = entity.getType().getId();
        EntityType entityType = EntityTypes.get(id);
        if (entityType == null) {
            return null;
        }

        NBTTagCompound tag = new NBTTagCompound();
        ((net.minecraft.entity.Entity) entity).writeToNBT(tag);
        return new BaseEntity(entityType, fromNative(tag));
    }

    @Override
    public SpongeWorld getWorld(World world) {
        return new SpongeNMSWorld(world);
    }

    @Override
    public boolean isBest() {
        return Sponge.getPlatform().getMinecraftVersion().getName().contains("1.12.1");
    }

    private class SpongeNMSWorld extends SpongeWorld {

        /**
         * Construct a new world.
         *
         * @param world the world
         */
        public SpongeNMSWorld(World world) {
            super(world);
        }

        @Override
        protected BlockState getBlockState(BlockStateHolder<?> block) {
            Block nmsBlock = Block.getBlockFromName(block.getBlockType().getId());
            if (nmsBlock == null || nmsBlock == Blocks.AIR) {
                return BlockTypes.AIR.getDefaultState();
            }

            IBlockState nmsState = nmsBlock.getDefaultState();
            for (Map.Entry<Property<?>, Object> entry : block.getStates().entrySet()) {
                // TODO Convert across states
            }

            return (BlockState) nmsState;
        }

        private NBTTagCompound updateForSet(NBTTagCompound tag, BlockVector3 position) {
            checkNotNull(tag);
            checkNotNull(position);

            tag.setTag("x", new NBTTagInt(position.getBlockX()));
            tag.setTag("y", new NBTTagInt(position.getBlockY()));
            tag.setTag("z", new NBTTagInt(position.getBlockZ()));

            return tag;
        }

        @Override
        protected void applyTileEntityData(org.spongepowered.api.block.tileentity.TileEntity entity, BaseBlock block) {
            NBTTagCompound tag = toNative(block.getNbtData());

            org.spongepowered.api.world.Location<World> loc = entity.getLocation();

            updateForSet(tag, BlockVector3.at(loc.getX(), loc.getY(), loc.getZ()));
            ((net.minecraft.tileentity.TileEntity) entity).readFromNBT(tag);
        }

        @Override
        protected void applyEntityData(Entity entity, BaseEntity data) {
            NBTTagCompound tag = toNative(data.getNbtData());
            for (String name : Constants.NO_COPY_ENTITY_NBT_FIELDS) {
                tag.removeTag(name);
            }
            ((net.minecraft.entity.Entity) entity).readFromNBT(tag);
        }

        @Override
        public boolean clearContainerBlockContents(BlockVector3 position) {
            BlockPos pos = new BlockPos(position.getBlockX(), position.getBlockY(), position.getBlockZ());
            net.minecraft.tileentity.TileEntity tile =((net.minecraft.world.World) getWorld()).getTileEntity(pos);
            if (tile instanceof IInventory) {
                IInventory inv = (IInventory) tile;
                int size = inv.getSizeInventory();
                for (int i = 0; i < size; i++) {
                    inv.setInventorySlotContents(i, net.minecraft.item.ItemStack.EMPTY);
                }
                return true;
            }
            return false;
        }

        @Nullable
        private WorldGenerator createWorldGenerator(TreeGenerator.TreeType type) {
            switch (type) {
                case TREE: return new WorldGenTrees(true);
                case BIG_TREE: return new WorldGenBigTree(true);
                case REDWOOD: return new WorldGenTaiga2(true);
                case TALL_REDWOOD: return new WorldGenTaiga1();
                case BIRCH: return new WorldGenBirchTree(true, false);
                case JUNGLE: return new WorldGenMegaJungle(true, 10, 20, JUNGLE_LOG, JUNGLE_LEAF);
                case SMALL_JUNGLE: return new WorldGenTrees(true, 4 + ThreadLocalRandom.current().nextInt(7), JUNGLE_LOG, JUNGLE_LEAF, false);
                case SHORT_JUNGLE: return new WorldGenTrees(true, 4 + ThreadLocalRandom.current().nextInt(7), JUNGLE_LOG, JUNGLE_LEAF, true);
                case JUNGLE_BUSH: return new WorldGenShrub(JUNGLE_LOG, JUNGLE_SHRUB);
                case RED_MUSHROOM: return new WorldGenBigMushroom(Blocks.BROWN_MUSHROOM_BLOCK);
                case BROWN_MUSHROOM: return new WorldGenBigMushroom(Blocks.RED_MUSHROOM_BLOCK);
                case SWAMP: return new WorldGenSwamp();
                case ACACIA: return new WorldGenSavannaTree(true);
                case DARK_OAK: return new WorldGenCanopyTree(true);
                case MEGA_REDWOOD: return new WorldGenMegaPineTree(false, ThreadLocalRandom.current().nextBoolean());
                case TALL_BIRCH: return new WorldGenBirchTree(true, true);
                case RANDOM:
                case PINE:
                case RANDOM_REDWOOD:
                case RANDOM_BIRCH:
                case RANDOM_JUNGLE:
                case RANDOM_MUSHROOM:
                default:
                    return null;
            }
        }

        @Override
        public boolean generateTree(TreeGenerator.TreeType type, EditSession editSession, BlockVector3 pos) throws MaxChangedBlocksException {
            WorldGenerator generator = createWorldGenerator(type);
            return generator != null && generator.generate((net.minecraft.world.World) getWorld(), ThreadLocalRandom.current(), new BlockPos(pos.getX(), pos.getY(), pos.getZ()));
        }

        @Override
        public com.sk89q.worldedit.world.block.BlockState getBlock(BlockVector3 position) {
            return getFullBlock(position).toImmutableState();
        }

        @Override
        public BaseBlock getFullBlock(BlockVector3 position) {
            World world = getWorld();
            BlockPos pos = new BlockPos(position.getBlockX(), position.getBlockY(), position.getBlockZ());
            IBlockState state = ((net.minecraft.world.World) world).getBlockState(pos);

            com.sk89q.worldedit.world.block.BlockType blockType = fromNative(state.getBlock());
            com.sk89q.worldedit.world.block.BlockState blockState = blockType.getDefaultState();

            if (blockType != com.sk89q.worldedit.world.block.BlockTypes.AIR) {
                net.minecraft.tileentity.TileEntity tile = ((net.minecraft.world.World) world).getTileEntity(pos);
                if (tile != null) {
                    BaseBlock baseBlock = blockState.toBaseBlock(fromNative(tile.writeToNBT(new NBTTagCompound())));

                    for (Map.Entry<IProperty<?>, Comparable<?>> entry : state.getProperties().entrySet()) {
                        // TODO Convert across states
                    }

                    return baseBlock;
                }
            }

            return blockState.toBaseBlock();
        }
    }
}
