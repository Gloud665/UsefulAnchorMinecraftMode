package ru.gloud.usefulanchor;

import java.util.HashMap;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod("usefulanchor")
public class UsefulAnchor
{
	//event handler
	public class UsefulAnchorEventHandler  {
		//dead players inventories list
		HashMap<UUID,PlayerInventory> inventores = new HashMap<UUID,PlayerInventory>();  
	    
	    @SubscribeEvent
	    public void drop(final LivingDropsEvent event) {
	    	//if died player use working anchor, inventory not drop
	    	try {
	    		if (event.getEntityLiving() instanceof PlayerEntity
	    				&& isAnchorRespawner((PlayerEntity)event.getEntityLiving())) {
	    			event.setCanceled(true);
	    		}
	    	}
	    	catch (NullPointerException ne) {
	    		LOGGER.info("Player "+event.getEntityLiving().getDisplayName().getString()+" has not the bed");
	    	}
	    }
	    
	    @SubscribeEvent
	    public void die(final LivingDeathEvent event) {
	    	
	    	//died player's inventory coping to list with his uid
	    	if (event.getEntityLiving() instanceof PlayerEntity) {
	    		PlayerEntity player = (PlayerEntity)event.getEntityLiving();
	    		PlayerInventory playerInventory;
	    		if (this.inventores.containsKey(player.getUniqueID()))
	    			playerInventory = (PlayerInventory) this.inventores.get(player.getUniqueID());
	    		else {
	    			playerInventory = new PlayerInventory(null);
	    			this.inventores.put(player.getUniqueID(), playerInventory);
	    		}
	    		playerInventory.copyInventory(player.inventory);
	    	}
	    }
	    
	    @SubscribeEvent
	    public void respawn(final PlayerRespawnEvent event) {
	    	//with respawn player using anchor giving his inventory from list
	    	try {
	    		PlayerEntity player = event.getPlayer();
	    		if (isAnchorRespawner(player))
	    			player.inventory.copyInventory(this.inventores.get(player.getUniqueID()));
	    	}catch (NullPointerException ne) {
	    		LOGGER.info("Player "+event.getEntityLiving().getDisplayName().getString()+" has not the bed");
	    	}
	    	
	    }
		private boolean isAnchorRespawner(PlayerEntity player) throws NullPointerException {
			return getPlayerRespawn(player) instanceof RespawnAnchorBlock;
		}
		
		private Block getPlayerRespawn(PlayerEntity Player) throws NullPointerException {
			//I dunno what I'm doing, but it works
			BlockPos blockPos = ((ServerPlayerEntity)Player).func_241140_K_();
			BlockState blockstate = Player.getEntityWorld().getBlockState(blockPos);
			Block block = blockstate.getBlock();
			return block;
		}
	    
	}
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public UsefulAnchor() {

		MinecraftForge.EVENT_BUS.register(new UsefulAnchorEventHandler());
    }
}
