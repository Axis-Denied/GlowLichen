package com.roguetictac.glowlichen.block;

import com.google.common.collect.Lists;
import com.roguetictac.glowlichen.Tags;
import com.roguetictac.glowlichen.config.GlowLichenConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.stream.Collectors;

import static com.roguetictac.glowlichen.config.GlowLichenConfig.blobSize;

@ParametersAreNonnullByDefault
public class BlockGlowLichen extends Block implements net.minecraftforge.common.IShearable, IGrowable {


    public BlockGlowLichen(String id){
        super(Material.VINE, MapColor.AIR);
        setDefaultState(this.blockState.getBaseState()
                .withProperty(UP, Boolean.FALSE)
                .withProperty(DOWN, Boolean.FALSE)
                .withProperty(NORTH, Boolean.FALSE)
                .withProperty(EAST, Boolean.FALSE)
                .withProperty(SOUTH, Boolean.FALSE)
                .withProperty(WEST, Boolean.FALSE));
        setCreativeTab(CreativeTabs.DECORATIONS);
        setHardness(0.2F);
        setLightLevel(0.47f);
        setSoundType(SoundType.PLANT);
        setRegistryName(Tags.MOD_ID, id);
        setTranslationKey(Tags.MOD_ID+"."+id);
    }
    public static final PropertyBool UP = PropertyBool.create("up");
    public static final PropertyBool DOWN = PropertyBool.create("down");
    public static final PropertyBool NORTH = PropertyBool.create("north");
    public static final PropertyBool EAST = PropertyBool.create("east");
    public static final PropertyBool SOUTH = PropertyBool.create("south");
    public static final PropertyBool WEST = PropertyBool.create("west");
    public static final PropertyBool[] ALL_FACES = new PropertyBool[] {UP, DOWN, NORTH, SOUTH, WEST, EAST};
    protected static final AxisAlignedBB UP_AABB = new AxisAlignedBB(0.0D, 0.9375D, 0.0D, 1.0D, 1.0D, 1.0D);
    protected static final AxisAlignedBB DOWN_AABB = new AxisAlignedBB(0.0D, 0, 0.0D, 1.0D, 0.0625, 1.0D);
    protected static final AxisAlignedBB WEST_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0625D, 1.0D, 1.0D);
    protected static final AxisAlignedBB EAST_AABB = new AxisAlignedBB(0.9375D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
    protected static final AxisAlignedBB NORTH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.0625D);
    protected static final AxisAlignedBB SOUTH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.9375D, 1.0D, 1.0D, 1.0D);




    public boolean placeGlowLichenIfPossible(World world, BlockPos pos, Random rand, List<EnumFacing> possible) {
        if(world.getBlockState(pos).getMaterial() != Material.ROCK) return false;
        for(EnumFacing facing : possible) {
            BlockPos offpos = pos.offset(facing);
            if (canAttachTo(world, pos, facing) && world.isAirBlock(offpos)) {
                IBlockState newState = getActualState(getDefaultState(), world, offpos);
                world.setBlockState(offpos, newState, 2);
                if(rand.nextFloat() < GlowLichenConfig.chanceOfSpreading) {
                    List<EnumFacing> collect = mutateFacingList(possible, facing, true);
                    Collections.shuffle(collect);
                    spreadFromFaceTowardRandomDirection(world, getOffset(pos, rand, facing), rand, collect, rand.nextInt(Math.max(1, blobSize+1)));
                }
            }
        }

        return false;
    }



    private void spreadFromFaceTowardRandomDirection(World world, BlockPos pos, Random rand, List<EnumFacing> possible, int branch) {
        if(branch <= 0 || possible.isEmpty()) return;
        if(world.getBlockState(pos).getMaterial() != Material.ROCK) return;
        for(EnumFacing facing : possible) {
            BlockPos offpos = pos.offset(facing);
            if (canAttachTo(world, pos, facing) && world.isAirBlock(offpos)) {
                IBlockState newState = getActualState(getDefaultState(), world, offpos);
                world.setBlockState(offpos, newState, 2);
                spreadFromFaceTowardRandomDirection(world, getOffset(pos, rand, facing), rand, mutateFacingList(possible, facing, true), branch--);
            }
        }
    }

    private static BlockPos getOffset(BlockPos pos, Random rand, EnumFacing facing) {
        return pos.offset(facing.getAxis().getPlane().random(rand));
    }

    private static List<EnumFacing> mutateFacingList(List<EnumFacing> possible, EnumFacing facing, boolean recreate) {
        if(recreate){
            return possible.stream().filter(f -> f != facing.getOpposite()).collect(Collectors.toList());
        }
        possible.removeIf(f -> f == facing.getOpposite());
        return possible;
    }
    private static EnumFacing getLast(List<EnumFacing> possible){
        return possible.get(possible.size()-1);
    }



    /**
     * @deprecated call via {@link IBlockState#getCollisionBoundingBox(IBlockAccess,BlockPos)} whenever possible.
     * Implementing/overriding is fine.
     */
    @Nullable
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
    {
        return NULL_AABB;
    }

    /**
     * @deprecated call via {@link IBlockState#getBoundingBox(IBlockAccess,BlockPos)} whenever possible.
     * Implementing/overriding is fine.
     */
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        state = state.getActualState(source, pos);
        int i = 0;
        AxisAlignedBB axisalignedbb = FULL_BLOCK_AABB;

        if (state.getValue(UP))
        {
            axisalignedbb = UP_AABB;
            ++i;
        }
        if (state.getValue(DOWN))
        {
            axisalignedbb = DOWN_AABB;
            ++i;
        }

        if ( state.getValue(NORTH))
        {
            axisalignedbb = NORTH_AABB;
            ++i;
        }

        if ((state.getValue(EAST)))
        {
            axisalignedbb = EAST_AABB;
            ++i;
        }

        if ((state.getValue(SOUTH)))
        {
            axisalignedbb = SOUTH_AABB;
            ++i;
        }

        if ((state.getValue(WEST)))
        {
            axisalignedbb = WEST_AABB;
            ++i;
        }

        return i == 1 ? axisalignedbb : FULL_BLOCK_AABB;
    }
    /**
     * Get the actual Block state of this Block at the given position. This applies properties not visible in the
     * metadata, such as fence connections.
     */
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        for(EnumFacing facing : EnumFacing.VALUES){
            PropertyBool prop = getPropertyFor(facing);
            BlockPos other = pos.offset(facing);
            state = state.withProperty(prop,
                    worldIn.getBlockState(other)
                            .getBlockFaceShape(worldIn, other, facing.getOpposite())
                            == BlockFaceShape.SOLID);
        }


        return state;
    }
    /**
     * Used to determine ambient occlusion and culling when rebuilding chunks for render
     * @deprecated call via {@link IBlockState#isOpaqueCube()} whenever possible. Implementing/overriding is fine.
     */
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    /**
     * @deprecated call via {@link IBlockState#isFullCube()} whenever possible. Implementing/overriding is fine.
     */
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    /**
     * Whether this Block can be replaced directly by other blocks (true for e.g. tall grass)
     */
    public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos)
    {
        return true;
    }

    /**
     * Check whether this Block can be placed at pos, while aiming at the specified side of an adjacent block
     */
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side)
    {
        return this.canAttachTo(worldIn, pos, side);
    }

    public boolean canAttachTo(World world, BlockPos pos, EnumFacing facing)
    {
        return this.isAcceptableNeighbor(world, pos.offset(facing.getOpposite()), facing);
    }

    private boolean isAcceptableNeighbor(World p_193396_1_, BlockPos p_193396_2_, EnumFacing p_193396_3_)
    {
        IBlockState iblockstate = p_193396_1_.getBlockState(p_193396_2_);
        return iblockstate.getBlockFaceShape(p_193396_1_, p_193396_2_, p_193396_3_) == BlockFaceShape.SOLID && !isExceptBlockForAttaching(iblockstate.getBlock());
    }

    protected static boolean isExceptBlockForAttaching(Block p_193397_0_)
    {
        return p_193397_0_ instanceof BlockShulkerBox || p_193397_0_ == Blocks.BEACON || p_193397_0_ == Blocks.CAULDRON || p_193397_0_ == Blocks.GLASS || p_193397_0_ == Blocks.STAINED_GLASS || p_193397_0_ == Blocks.PISTON || p_193397_0_ == Blocks.STICKY_PISTON || p_193397_0_ == Blocks.PISTON_HEAD || p_193397_0_ == Blocks.TRAPDOOR;
    }

    private boolean recheckGrownSides(World worldIn, BlockPos pos, IBlockState state)
    {
        IBlockState iblockstate = state;

        for (EnumFacing enumfacing : EnumFacing.VALUES)
        {
            PropertyBool propertybool = getPropertyFor(enumfacing);
            state = state.withProperty(propertybool, this.canAttachTo(worldIn, pos, enumfacing.getOpposite()));
        }

        if (getNumGrownFaces(state) == 0)
        {
            return false;
        }
        else
        {
            if (iblockstate != state)
            {
                worldIn.setBlockState(pos, state, 2);
            }

            return true;
        }
    }

    /**
     * Called when a neighboring block was changed and marks that this state should perform any checks during a neighbor
     * change. Cases may include when redstone power is updated, cactus blocks popping off due to a neighboring solid
     * block, etc.
     */
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        if (!worldIn.isRemote && !this.recheckGrownSides(worldIn, pos, state))
        {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
        }
    }


    /**
     * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
     * IBlockstate
     */
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        IBlockState iblockstate = this.getDefaultState().withProperty(UP, Boolean.FALSE).withProperty(DOWN, Boolean.FALSE).withProperty(NORTH, Boolean.FALSE).withProperty(EAST, Boolean.FALSE).withProperty(SOUTH, Boolean.FALSE).withProperty(WEST, Boolean.FALSE);
        return getActualState(iblockstate, worldIn, pos);
    }

    /**
     * Get the Item that this Block should drop when harvested.
     */
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return Items.AIR;
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(Random random)
    {
        return 0;
    }

    /**
     * Spawns the block's drops in the world. By the time this is called the Block has possibly been set to air via
     * Block.removedByPlayer
     */
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack)
    {
        if (!worldIn.isRemote && stack.getItem() == Items.SHEARS)
        {
            player.addStat(Objects.requireNonNull(StatList.getBlockStats(this)));
            spawnAsEntity(worldIn, pos, new ItemStack(this, 1, 0));
        }
        else
        {
            super.harvestBlock(worldIn, player, pos, state, te, stack);
        }
    }

    /**
     * Convert the given metadata into a IBlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState();//.withProperty(SOUTH, (meta & 1) > 0).withProperty(WEST, (meta & 2) > 0).withProperty(NORTH, Boolean.valueOf((meta & 4) > 0)).withProperty(EAST, (meta & 8) > 0);//.withProperty(UP, (meta & 16) > 0).withProperty(DOWN, (meta & 32) > 0);
    }

    /**
     * Gets the render layer this block will render on. SOLID for solid blocks, CUTOUT or CUTOUT_MIPPED for on-off
     * transparency (glass, reeds), TRANSLUCENT for fully blended transparency (stained glass)
     */
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }

    /**
     * Convert the IBlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        /*
        int i = 0;

        if ((state.getValue(SOUTH)))
        {
            i |= 1;
        }

        if ((state.getValue(WEST)))
        {
            i |= 2;
        }

        if ((state.getValue(NORTH)))
        {
            i |= 4;
        }

        if ((state.getValue(EAST)))
        {
            i |= 8;
        }

         */

        return 0;
    }

    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, UP, DOWN, NORTH, EAST, SOUTH, WEST);
    }

    /**
     * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     * @deprecated call via {@link IBlockState#withRotation(Rotation)} whenever possible. Implementing/overriding is
     * fine.
     */
    public IBlockState withRotation(IBlockState state, Rotation rot)
    {
        switch (rot)
        {
            case CLOCKWISE_180:
                return state.withProperty(NORTH, state.getValue(SOUTH)).withProperty(EAST, state.getValue(WEST)).withProperty(SOUTH, state.getValue(NORTH)).withProperty(WEST, state.getValue(EAST));
            case COUNTERCLOCKWISE_90:
                return state.withProperty(NORTH, state.getValue(EAST)).withProperty(EAST, state.getValue(SOUTH)).withProperty(SOUTH, state.getValue(WEST)).withProperty(WEST, state.getValue(NORTH));
            case CLOCKWISE_90:
                return state.withProperty(NORTH, state.getValue(WEST)).withProperty(EAST, state.getValue(NORTH)).withProperty(SOUTH, state.getValue(EAST)).withProperty(WEST, state.getValue(SOUTH));
            default:
                return state;
        }
    }

    /**
     * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     * @deprecated call via {@link IBlockState#withMirror(Mirror)} whenever possible. Implementing/overriding is fine.
     */
    public IBlockState withMirror(IBlockState state, Mirror mirrorIn)
    {
        switch (mirrorIn)
        {
            case LEFT_RIGHT:
                return state.withProperty(NORTH, state.getValue(SOUTH)).withProperty(SOUTH, state.getValue(NORTH));
            case FRONT_BACK:
                return state.withProperty(EAST, state.getValue(WEST)).withProperty(WEST, state.getValue(EAST));
            default:
                return super.withMirror(state, mirrorIn);
        }
    }

    public static PropertyBool getPropertyFor(EnumFacing side)
    {
        switch (side)
        {
            case UP:
                return UP;
            case DOWN:
                return DOWN;
            case NORTH:
                return NORTH;
            case SOUTH:
                return SOUTH;
            case WEST:
                return WEST;
            case EAST:
                return EAST;
            default:
                throw new IllegalArgumentException(side + " is an invalid choice");
        }
    }

    public static int getNumGrownFaces(IBlockState state)
    {
        int i = 0;

        for (PropertyBool propertybool : ALL_FACES)
        {
            if (state.getValue(propertybool))
            {
                ++i;
            }
        }

        return i;
    }
    @Override public boolean isShearable(ItemStack item, IBlockAccess world, BlockPos pos){ return true; }
    @Override
    public java.util.List<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune)
    {
        return Collections.singletonList(new ItemStack(this, 1));
    }


    /**
     * Get the geometry of the queried face at the given position and state. This is used to decide whether things like
     * buttons are allowed to be placed on the face, or how glass panes connect to the face, among other things.
     * <p>
     * Common values are {@code SOLID}, which is the default, and {@code UNDEFINED}, which represents something that
     * does not fit the other descriptions and will generally cause other things not to connect to the face.
     *
     * @return an approximation of the form of the given face
     * @deprecated call via {@link IBlockState#getBlockFaceShape(IBlockAccess,BlockPos,EnumFacing)} whenever possible.
     * Implementing/overriding is fine.
     */
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
        return true;
    }

    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        return true;
    }




    @Override
    public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        ArrayList<EnumFacing> possible = Lists.newArrayList(EnumFacing.VALUES);
        EnumFacing firstFacing = getFirstFacing(state);
        possible.remove(firstFacing.getOpposite());
        spreadFromFaceTowardRandomDirection(worldIn, pos.offset(firstFacing), rand, possible, 1);
    }

    private EnumFacing getFirstFacing(IBlockState state) {
        for(EnumFacing facing : EnumFacing.VALUES){
            if(state.getValue(getPropertyFor(facing))){
                return facing;
            }
        }
        return EnumFacing.DOWN;
    }
}

