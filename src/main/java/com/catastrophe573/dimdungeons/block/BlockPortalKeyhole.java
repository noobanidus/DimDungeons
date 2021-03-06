package com.catastrophe573.dimdungeons.block;

import java.util.ArrayList;
import java.util.Random;

import javax.annotation.Nullable;

import com.catastrophe573.dimdungeons.DimDungeons;
import com.catastrophe573.dimdungeons.item.ItemPortalKey;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.thread.EffectiveSide;

public class BlockPortalKeyhole extends Block
{
    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
    public static final BooleanProperty FILLED = BooleanProperty.create("filled");
    public static final BooleanProperty LIT = BooleanProperty.create("lit");

    public static final String REG_NAME = "block_portal_keyhole";

    public BlockPortalKeyhole()
    {
	super(Block.Properties.create(Material.PORTAL).hardnessAndResistance(2).sound(SoundType.METAL));
	this.setRegistryName(DimDungeons.MOD_ID, REG_NAME);
	this.setDefaultState(getMyCustomDefaultState());
    }

    // used by the constructor and I'm not sure where else anymore?
    public BlockState getMyCustomDefaultState()
    {
	return this.stateContainer.getBaseState().with(FACING, Direction.NORTH).with(FILLED, false).with(LIT, false);
    }

    // based on code from vanilla furnaces, which also play a sound effect and make particles when their TileEntity is being productive
    @OnlyIn(Dist.CLIENT)
    @Override
    //public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand)
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
	boolean hasPortalBlockBelow = worldIn.getBlockState(pos.down()).getBlock() == BlockRegistrar.block_gold_portal;

	if (stateIn.get(LIT) && hasPortalBlockBelow)
	{
	    Direction enumfacing = (Direction) stateIn.get(FACING);
	    double d0 = (double) pos.getX() + 0.5D;
	    double d1 = (double) pos.getY() + rand.nextDouble() * 6.0D / 16.0D;
	    double d2 = (double) pos.getZ() + 0.5D;
	    double d4 = rand.nextDouble() * 0.6D - 0.3D;

	    // play sound effects randomly
	    if (rand.nextDouble() < 0.1D)
	    {
		worldIn.playSound((double) pos.getX() + 0.5D, (double) pos.getY(), (double) pos.getZ() + 0.5D, SoundEvents.BLOCK_PORTAL_AMBIENT, SoundCategory.BLOCKS, 1.0F, 3.0F, false);
	    }

	    switch (enumfacing)
	    {
	    case WEST:
		worldIn.addParticle(ParticleTypes.PORTAL, d0 - 0.52D, d1, d2 + d4, -1.0D, 1.0D, 0.0D);
		//worldIn.addParticle(ParticleTypes.FIREWORK, d0 - 0.12D, d1 + 1.5D, d2 + d4, 0.0D, 0.0D, 0.23D);
		//worldIn.addParticle(ParticleTypes.FIREWORK, d0 - 0.12D, d1 + 1.5D, d2 + d4, 0.0D, 0.0D, -0.23D);
		break;
	    case EAST:
		worldIn.addParticle(ParticleTypes.PORTAL, d0 + 0.52D, d1, d2 + d4, 1.0D, 1.0D, 0.0D);
		//worldIn.addParticle(ParticleTypes.FIREWORK, d0 + 0.12D, d1 + 1.5D, d2 + d4, 0.0D, 0.0D, 0.23D);
		//worldIn.addParticle(ParticleTypes.FIREWORK, d0 + 0.12D, d1 + 1.5D, d2 + d4, 0.0D, 0.0D, -0.23D);
		break;
	    case NORTH:
		worldIn.addParticle(ParticleTypes.PORTAL, d0 + d4, d1, d2 - 0.52D, 0.0D, 1.0D, -1.0D);
		//worldIn.addParticle(ParticleTypes.FIREWORK, d0 + d4, d1 + 1.5D, d2 - 0.12D, 0.23D, 0.0D, 0.0D);
		//worldIn.addParticle(ParticleTypes.FIREWORK, d0 + d4, d1 + 1.5D, d2 - 0.12D, -0.23D, 0.0D, 0.0D);
		break;
	    default:
	    case SOUTH:
		worldIn.addParticle(ParticleTypes.PORTAL, d0 + d4, d1, d2 + 0.52D, 0.0D, 1.0D, 1.0D);
		//worldIn.addParticle(ParticleTypes.FIREWORK, d0 + d4, d1 + 1.5D, d2 + 0.12D, 0.23D, 0.0D, 0.0D);
		//worldIn.addParticle(ParticleTypes.FIREWORK, d0 + d4, d1 + 1.5D, d2 + 0.12D, -0.23D, 0.0D, 0.0D);
	    }
	}
    }

    // called when the player right clicks this block
    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
	ItemStack playerItem = player.getHeldItem(handIn);
	TileEntity tileEntity = worldIn.getTileEntity(pos);
	TileEntityPortalKeyhole myEntity = (TileEntityPortalKeyhole) tileEntity;

	// insert or remove an item from this block
	if (myEntity != null)
	{
	    ItemStack insideItem = myEntity.getObjectInserted();

	    // if the keyhole is currently empty
	    if (insideItem.isEmpty())
	    {
		if (!playerItem.isEmpty())
		{
		    // DimDungeons.LOGGER.info("Putting " + playerItem.getDisplayName().getString()
		    // + " inside keyhole...");
		    myEntity.setContents(playerItem.copy());
		    playerItem.shrink(1);

		    // recalculate the boolean block states
		    BlockState newBlockState = state.with(FACING, state.get(FACING)).with(FILLED, myEntity.isFilled()).with(LIT, myEntity.isActivated());
		    worldIn.setBlockState(pos, newBlockState);

		    // this function prints no message on success
		    checkForProblemsAndLiterallySpeakToPlayer(worldIn, pos, state, myEntity, player);

		    // should portal blocks be spawned?
		    if (isOkayToSpawnPortalBlocks(worldIn, pos, state, myEntity))
		    {
			worldIn.setBlockState(pos.down(), BlockRegistrar.block_gold_portal.getDefaultState());
			worldIn.setBlockState(pos.down(2), BlockRegistrar.block_gold_portal.getDefaultState());
		    }
		    return ActionResultType.SUCCESS;
		}
	    }
	    // if the keyhole is currently full
	    else
	    {
		// DimDungeons.LOGGER.info("Taking thing out of keyhole...");
		if (playerItem.isEmpty())
		{
		    player.setHeldItem(handIn, insideItem); // hand it to the player
		}
		else if (!player.addItemStackToInventory(insideItem)) // okay put it in their inventory
		{
		    player.dropItem(insideItem, false); // whatever drop it on the ground
		}

		myEntity.removeContents();

		// recalculate the boolean block states
		BlockState newBlockState = state.with(FACING, state.get(FACING)).with(FILLED, myEntity.isFilled()).with(LIT, myEntity.isActivated());
		worldIn.setBlockState(pos, newBlockState, 3);

		return ActionResultType.SUCCESS;
	    }
	}
	return ActionResultType.PASS;
    }

    // helper function for checkForPortalCreation
    protected boolean isOkayToSpawnPortalBlocks(World worldIn, BlockPos pos, BlockState state, TileEntityPortalKeyhole myEntity)
    {
	// if the portal is not lit, then that is a fast "no"
	if (!myEntity.isActivated())
	{
	    return false;
	}

	// check for air or existing portal blocks below this keyhole
	Block b1 = worldIn.getBlockState(pos.down()).getBlock();
	Block b2 = worldIn.getBlockState(pos.down(2)).getBlock();
	if (!(b1 == Blocks.AIR || b1 == BlockRegistrar.block_gold_portal))
	{
	    return false;
	}
	if (!(b2 == Blocks.AIR || b2 == BlockRegistrar.block_gold_portal))
	{
	    return false;
	}

	// awakened ItemPortalKeys will open a portal to the dungeon dimension
	ItemStack item = myEntity.getObjectInserted();
	if (item.getItem() instanceof ItemPortalKey)
	{
	    ItemPortalKey key = (ItemPortalKey) item.getItem();
	    return key.isActivated(item);
	}

	return false;
    }

    @Override
    public boolean hasTileEntity(BlockState state)
    {
	return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world)
    {
	return new TileEntityPortalKeyhole();
    }

    // Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the IBlockstate
    //public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
	BlockState retval = getMyCustomDefaultState();
	return retval.with(FACING, context.getPlacementHorizontalFacing().getOpposite());
    }

    // Called by ItemBlocks after a block is set in the world, to allow post-place logic
    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
	worldIn.setBlockState(pos, state.with(FACING, placer.getHorizontalFacing().getOpposite()), 2);
    }

    // Called server side after this block is replaced with another in Chunk, but before the TileEntity is updated
    // this function is now in charge of preserving TileEntities across block updates, too, instead of the former TileEntity->shouldRefresh()
    @SuppressWarnings("deprecation")
    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving)
    {
	TileEntity tileentity = worldIn.getTileEntity(pos);

	// DO NOT call super.onReplaced() unless this block has no TileEntity, or unless the block was deleted/changed to another block of course
	if (state.getBlock() != newState.getBlock())
	{
	    // if the block was destroyed and it held an item then spit the item out somewhere
	    if (tileentity instanceof TileEntityPortalKeyhole)
	    {
		ItemStack item = ((TileEntityPortalKeyhole) tileentity).getObjectInserted();
		if (!item.isEmpty())
		{
		    InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), item);
		}
		worldIn.updateComparatorOutputLevel(pos, this);
	    }

	    super.onReplaced(state, worldIn, pos, newState, isMoving);
	    worldIn.removeTileEntity(pos);
	}
    }

    @Override
    public boolean hasComparatorInputOverride(BlockState state)
    {
	return true;
    }

    // returns 1 number if the block is filled with any item stack, and 0 otherwise
    @Override
    public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos)
    {
	return blockState.get(FILLED) ? 1 : 0;
    }

    // returns the ItemStack that represents this block - this has nothing to do with the item placed inside
    @Override
    public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state)
    {
	return new ItemStack(this);
    }

    // The type of render function called. MODEL for mixed tesr and static model, MODELBLOCK_ANIMATED for TESR-only, LIQUID for vanilla liquids, INVISIBLE to skip all rendering
    @Override
    public BlockRenderType getRenderType(BlockState state)
    {
	return BlockRenderType.MODEL;
    }

    @Override
    // this replaces all metadata in 1.13 forwards
    //public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    public BlockState getExtendedState(BlockState state, IBlockReader world, BlockPos pos)
    {
	BlockState retval = state;
	TileEntity te = world.getTileEntity(pos);
	if (te instanceof TileEntityPortalKeyhole)
	{
	    retval.with(FILLED, ((TileEntityPortalKeyhole) te).isFilled());
	    retval.with(LIT, ((TileEntityPortalKeyhole) te).isActivated());
	}

	return retval;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
	builder.add(FACING, FILLED, LIT);
    }

    // Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed blockstate.
    @Override
    public BlockState rotate(BlockState state, Rotation rot)
    {
	return state.with(FACING, rot.rotate((Direction) state.get(FACING)));
    }

    // Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed blockstate.
    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn)
    {
	return state.with(FACING, mirrorIn.mirror(((Direction) state.get(FACING))));
    }

    public int predictPortalError(World worldIn, PlayerEntity playerIn)
    {
	return 0; // no error! We're good!
    }

    @SuppressWarnings("deprecation")
    protected void checkForProblemsAndLiterallySpeakToPlayer(World worldIn, BlockPos pos, BlockState state, TileEntityPortalKeyhole tileEntity, PlayerEntity player)
    {
	// only run this function once, either on the client or on the server. I choose client because this function does nothing logical.
	if (EffectiveSide.get() != LogicalSide.CLIENT)
	{
	    return;
	}
	
	// error #1: the key is not activated
	ItemStack item = tileEntity.getObjectInserted();
	int keyLevel = 0;
	if (item.getItem() instanceof ItemPortalKey)
	{
	    ItemPortalKey key = (ItemPortalKey) item.getItem();
	    if (!key.isActivated(item))
	    {
		speakLiterallyToPlayerAboutProblems(worldIn, player, 1, null);
		return;
	    }
	    keyLevel = key.getKeyLevel(item);
	}
	else
	{
	    return; // item inserted is not even a key!
	}

	// error #2: no room to spawn portal
	if (!isOkayToSpawnPortalBlocks(worldIn, pos, state, tileEntity))
	{
	    speakLiterallyToPlayerAboutProblems(worldIn, player, 2, null);
	    return;
	}

	// error #3, error #4: the frame is not complete or contains an invalid block
	ArrayList<BlockState> blocks;
	if (state.get(BlockPortalKeyhole.FACING) == Direction.WEST || state.get(BlockPortalKeyhole.FACING) == Direction.EAST)
	{
	    blocks = BlockGoldPortal.getPortalFrameMaterialsNorthSouth(worldIn, pos);
	}
	else
	{
	    blocks = BlockGoldPortal.getPortalFrameMaterialsWestEast(worldIn, pos);
	}
	for (int i = 0; i < 5; i++)
	{
	    BlockState b = blocks.get(i);
	    if (b.isAir())
	    {
		speakLiterallyToPlayerAboutProblems(worldIn, player, 3, null);
		return;
	    }
	    if (!BlockGoldPortal.isValidPortalFrameBlock(b.getBlock()))
	    {
		speakLiterallyToPlayerAboutProblems(worldIn, player, 4, b);
		return;
	    }
	}

	// error #5, error #6: the spires are incomplete
	for (int i = 5; i < 9; i++)
	{
	    BlockState b = blocks.get(i);
	    if (b.isAir())
	    {
		speakLiterallyToPlayerAboutProblems(worldIn, player, 5, null);
		return;
	    }
	    if (!BlockGoldPortal.isValidPortalFrameBlock(b.getBlock()))
	    {
		speakLiterallyToPlayerAboutProblems(worldIn, player, 6, b);
		return;
	    }
	}

	// error #7: the gilded portal blocks on top of the spires are missing
	for (int i = 9; i < 11; i++)
	{
	    BlockState b = blocks.get(i);
	    if (b.getBlock() != BlockRegistrar.block_gilded_portal)
	    {
		speakLiterallyToPlayerAboutProblems(worldIn, player, 7, b);
		return;
	    }
	}

	// only continue performing advanced checks for advanced portal keys
	if (keyLevel < 2)
	{
	    return; // success!
	}

	// error #8: missing crowns
	for (int i = 11; i < 13; i++)
	{
	    BlockState b = blocks.get(i);
	    if (b.getBlock() != BlockRegistrar.block_portal_crown)
	    {
		speakLiterallyToPlayerAboutProblems(worldIn, player, 8, null);
		return;
	    }
	}

	// error #9: missing banners
	BlockState leftSpireA = blocks.get(13);
	BlockState leftSpireB = blocks.get(14);
	BlockState rightSpireA = blocks.get(15);
	BlockState rightSpireB = blocks.get(16);

	if (!(leftSpireA.getBlock() == Blocks.WHITE_WALL_BANNER || leftSpireA.getBlock() == Blocks.PURPLE_WALL_BANNER || leftSpireB.getBlock() == Blocks.WHITE_WALL_BANNER || leftSpireB.getBlock() == Blocks.PURPLE_WALL_BANNER))
	{
	    speakLiterallyToPlayerAboutProblems(worldIn, player, 9, null);
	    return;
	}
	if (!(rightSpireA.getBlock() == Blocks.WHITE_WALL_BANNER || rightSpireA.getBlock() == Blocks.PURPLE_WALL_BANNER || rightSpireB.getBlock() == Blocks.WHITE_WALL_BANNER || rightSpireB.getBlock() == Blocks.PURPLE_WALL_BANNER))
	{
	    speakLiterallyToPlayerAboutProblems(worldIn, player, 9, null);
	    return;
	}

	return; // success!
    }

    public void speakLiterallyToPlayerAboutProblems(World worldIn, PlayerEntity playerIn, int problemID, @Nullable BlockState problemBlock)
    {
	// a message that does not call out a specific block
	if (problemBlock == null)
	{
	    ITextComponent text1 = new TranslationTextComponent(new TranslationTextComponent("error.dimdungeons.portal_error_" + problemID).getString());
	    playerIn.sendMessage(text1.setStyle(new Style().setItalic(true).setColor(TextFormatting.BLUE)));
	}
	else
	{
	    ITextComponent text1 = new TranslationTextComponent(new TranslationTextComponent("error.dimdungeons.portal_error_" + problemID).getString() + problemBlock.getBlock().getRegistryName() + ".");
	    playerIn.sendMessage(text1.setStyle(new Style().setItalic(true).setColor(TextFormatting.BLUE)));
	}
    }
}