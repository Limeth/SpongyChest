package io.github.hsyyid.spongychest.listeners;

import io.github.hsyyid.spongychest.data.isspongychest.IsSpongyChestData;
import io.github.hsyyid.spongychest.data.isspongychest.SpongeIsSpongyChestData;
import io.github.hsyyid.spongychest.data.uuidchest.UUIDChestData;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.block.tileentity.carrier.Chest;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.hanging.ItemFrame;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public class HitBlockListener {

    @Listener
    public void onPlayerHitBlock(InteractBlockEvent.Primary event, @Root Player player) {
        if (event.getTargetBlock().getState().getType() == BlockTypes.CHEST && event.getTargetBlock().getLocation().isPresent()
                && event.getTargetBlock().getLocation().get().getTileEntity().isPresent()) {
            Chest chest = (Chest) event.getTargetBlock().getLocation().get().getTileEntity().get();

            if (chest.get(IsSpongyChestData.class).isPresent() && chest.get(IsSpongyChestData.class).get().isSpongyChest().get()) {
                UUID uuid = chest.get(UUIDChestData.class).get().uuid().get();

                if (uuid.equals(player.getUniqueId()) || player.hasPermission("spongychest.shop.destroy")) {
                    player.sendMessage(Text.of(TextColors.BLUE, "[SpongyChest]: ", TextColors.GREEN, "Successfully deleted shop!"));
                    chest.offer(new SpongeIsSpongyChestData(false));
                    Collection<Entity> entities = chest.getLocation().getExtent().getEntities(t -> t.getType() == EntityTypes.ITEM_FRAME
                            && t.getLocation().sub(0, 1, 0).hasTileEntity() && t.getLocation().getBlockPosition().sub(0, 1, 0).equals(chest.getLocation().getBlockPosition()));

                    entities.forEach(e -> {
                        e.remove();
                    });
                } else {
                    event.setCancelled(true);
                }
            }
        }
    }

    @Listener
    public void onPlayerHitFrame(InteractEntityEvent.Primary event, @Root Player player) {
        if (event.getTargetEntity().getType() == EntityTypes.ITEM_FRAME) {
            ItemFrame itemFrame = (ItemFrame) event.getTargetEntity();
            Location<World> chestLocation = itemFrame.getLocation().sub(0, 1, 0);
            Optional<TileEntity> tile = chestLocation.getTileEntity();

            if (tile.isPresent() && tile.get() instanceof Chest) {
                Chest chest = (Chest) tile.get();

                if (chest.get(IsSpongyChestData.class).isPresent() && chest.get(IsSpongyChestData.class).get().isSpongyChest().get()) {
                    UUID uuid = chest.get(UUIDChestData.class).get().uuid().get();

                    if (uuid.equals(player.getUniqueId()) || player.hasPermission("spongychest.shop.destroy")) {
                        player.sendMessage(Text.of(TextColors.BLUE, "[SpongyChest]: ", TextColors.GREEN, "Successfully deleted shop!"));
                        chest.offer(new SpongeIsSpongyChestData(false));

                        itemFrame.offer(Keys.REPRESENTED_ITEM, ItemStackSnapshot.NONE);
                        itemFrame.remove();
                        event.setCancelled(true);
                    } else {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}
