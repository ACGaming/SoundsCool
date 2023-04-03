package com.dynious.soundscool.block;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.dynious.soundscool.SoundsCool;
import com.dynious.soundscool.lib.Names;
import com.dynious.soundscool.lib.Reference;
import com.dynious.soundscool.tileentity.TileSoundPlayer;

public class BlockSoundPlayer extends Block implements ITileEntityProvider
{
    public BlockSoundPlayer()
    {
        super(Material.ROCK);
        this.setCreativeTab(SoundsCool.tabSoundsCool);
        this.setHardness(2F);
        this.setResistance(10F);
        this.setSoundType(SoundType.STONE);
        this.setTranslationKey(Reference.modid + "." + Names.soundPlayer);
        this.setRegistryName(Names.soundPlayer);
    }

    @Override
    public TileEntity createNewTileEntity(World var1, int var2)
    {
        return new TileSoundPlayer();
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos)
    {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileSoundPlayer) ((TileSoundPlayer) tile).setPowered(world.isBlockPowered(pos));
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state)
    {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileSoundPlayer) ((TileSoundPlayer) tile).setPowered(world.isBlockPowered(pos));
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (player.isSneaking()) return false;
        else
        {
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof TileSoundPlayer) player.openGui(SoundsCool.instance, 1, world, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }

    @Override
    public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable EnumFacing side)
    {
        return true;
    }
}