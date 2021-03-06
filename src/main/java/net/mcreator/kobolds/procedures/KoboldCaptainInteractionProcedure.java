package net.mcreator.kobolds.procedures;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.GameType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.CommandSource;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.Advancement;

import javax.annotation.Nullable;

import java.util.Iterator;

@Mod.EventBusSubscriber
public class KoboldCaptainInteractionProcedure {
	@SubscribeEvent
	public static void onRightClickEntity(PlayerInteractEvent.EntityInteract event) {
		Player sourceentity = event.getPlayer();
		if (event.getHand() != sourceentity.getUsedItemHand())
			return;
		execute(event, event.getWorld(), event.getTarget(), sourceentity);
	}

	public static void execute(LevelAccessor world, Entity entity, Entity sourceentity) {
		execute(null, world, entity, sourceentity);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, Entity entity, Entity sourceentity) {
		if (entity == null || sourceentity == null)
			return;
		if (EntityTypeTags.getAllTags().getTagOrEmpty(new ResourceLocation("kobolds:captain")).contains(entity.getType())
				&& ((entity instanceof LivingEntity _livEnt ? _livEnt.getOffhandItem() : ItemStack.EMPTY).getItem() == (ItemStack.EMPTY)
						.getItem()) == true) {
			if (ItemTags.getAllTags().getTagOrEmpty(new ResourceLocation("kobolds:captain_tier_one"))
					.contains((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem())) {
				if ((new Object() {
					public boolean checkGamemode(Entity _ent) {
						if (_ent instanceof ServerPlayer _serverPlayer) {
							return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
						} else if (_ent.level.isClientSide() && _ent instanceof AbstractClientPlayer _clientPlayer) {
							PlayerInfo _pi = Minecraft.getInstance().getConnection().getPlayerInfo(_clientPlayer.getGameProfile().getId());
							return _pi != null && _pi.getGameMode() == GameType.CREATIVE;
						}
						return false;
					}
				}.checkGamemode(sourceentity)) == false) {
					new Object() {
						private int ticks = 0;
						private float waitTicks;
						private LevelAccessor world;

						public void start(LevelAccessor world, int waitTicks) {
							this.waitTicks = waitTicks;
							MinecraftForge.EVENT_BUS.register(this);
							this.world = world;
						}

						@SubscribeEvent
						public void tick(TickEvent.ServerTickEvent event) {
							if (event.phase == TickEvent.Phase.END) {
								this.ticks += 1;
								if (this.ticks >= this.waitTicks)
									run();
							}
						}

						private void run() {
							if (sourceentity instanceof Player _player) {
								ItemStack _stktoremove = (sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY);
								_player.getInventory().clearOrCountMatchingItems(p -> _stktoremove.getItem() == p.getItem(), 1,
										_player.inventoryMenu.getCraftSlots());
							}
							MinecraftForge.EVENT_BUS.unregister(this);
						}
					}.start(world, 1);
				}
				if (sourceentity instanceof LivingEntity _entity)
					_entity.swing(InteractionHand.MAIN_HAND, true);
				if (entity instanceof LivingEntity _entity)
					_entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 120, -10, (false), (false)));
				if (entity instanceof LivingEntity _entity) {
					ItemStack _setstack = ((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).copy());
					_setstack.setCount(1);
					_entity.setItemInHand(InteractionHand.OFF_HAND, _setstack);
					if (_entity instanceof ServerPlayer _serverPlayer)
						_serverPlayer.getInventory().setChanged();
				}
				sourceentity.getPersistentData().putBoolean("Captain", (true));
				new Object() {
					private int ticks = 0;
					private float waitTicks;
					private LevelAccessor world;

					public void start(LevelAccessor world, int waitTicks) {
						this.waitTicks = waitTicks;
						MinecraftForge.EVENT_BUS.register(this);
						this.world = world;
					}

					@SubscribeEvent
					public void tick(TickEvent.ServerTickEvent event) {
						if (event.phase == TickEvent.Phase.END) {
							this.ticks += 1;
							if (this.ticks >= this.waitTicks)
								run();
						}
					}

					private void run() {
						if (entity instanceof LivingEntity _entity)
							_entity.swing(InteractionHand.MAIN_HAND, true);
						if (world instanceof Level _level)
							_level.playSound(_level.isClientSide() ? Minecraft.getInstance().player : null, (entity.getX()), (entity.getY()),
									(entity.getZ()), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("kobolds:kobold_trade")),
									SoundSource.NEUTRAL, 1, 1);
						if (world instanceof ServerLevel _level)
							_level.getServer().getCommands().performCommand(
									new CommandSourceStack(CommandSource.NULL, new Vec3((entity.getX()), (entity.getY()), (entity.getZ())), Vec2.ZERO,
											_level, 4, "", new TextComponent(""), _level.getServer(), null).withSuppressedOutput(),
									"/loot spawn ~ ~ ~ loot kobolds:gameplay/captain_one_loot");
						new Object() {
							private int ticks = 0;
							private float waitTicks;
							private LevelAccessor world;

							public void start(LevelAccessor world, int waitTicks) {
								this.waitTicks = waitTicks;
								MinecraftForge.EVENT_BUS.register(this);
								this.world = world;
							}

							@SubscribeEvent
							public void tick(TickEvent.ServerTickEvent event) {
								if (event.phase == TickEvent.Phase.END) {
									this.ticks += 1;
									if (this.ticks >= this.waitTicks)
										run();
								}
							}

							private void run() {
								if (entity instanceof LivingEntity _entity) {
									ItemStack _setstack = (ItemStack.EMPTY);
									_setstack.setCount(1);
									_entity.setItemInHand(InteractionHand.OFF_HAND, _setstack);
									if (_entity instanceof ServerPlayer _serverPlayer)
										_serverPlayer.getInventory().setChanged();
								}
								sourceentity.getPersistentData().putBoolean("Captain", (false));
								if ((sourceentity instanceof ServerPlayer _plr && _plr.level instanceof ServerLevel
										? _plr.getAdvancements()
												.getOrStartProgress(_plr.server.getAdvancements()
														.getAdvancement(new ResourceLocation("kobolds:kobold_pirate_advancement")))
												.isDone()
										: false) == false) {
									if (sourceentity instanceof ServerPlayer _player) {
										Advancement _adv = _player.server.getAdvancements()
												.getAdvancement(new ResourceLocation("kobolds:kobold_pirate_advancement"));
										AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
										if (!_ap.isDone()) {
											Iterator _iterator = _ap.getRemainingCriteria().iterator();
											while (_iterator.hasNext())
												_player.getAdvancements().award(_adv, (String) _iterator.next());
										}
									}
								}
								MinecraftForge.EVENT_BUS.unregister(this);
							}
						}.start(world, 20);
						MinecraftForge.EVENT_BUS.unregister(this);
					}
				}.start(world, 100);
			} else if (ItemTags.getAllTags().getTagOrEmpty(new ResourceLocation("kobolds:captain_tier_two"))
					.contains((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem())) {
				if ((new Object() {
					public boolean checkGamemode(Entity _ent) {
						if (_ent instanceof ServerPlayer _serverPlayer) {
							return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
						} else if (_ent.level.isClientSide() && _ent instanceof AbstractClientPlayer _clientPlayer) {
							PlayerInfo _pi = Minecraft.getInstance().getConnection().getPlayerInfo(_clientPlayer.getGameProfile().getId());
							return _pi != null && _pi.getGameMode() == GameType.CREATIVE;
						}
						return false;
					}
				}.checkGamemode(sourceentity)) == false) {
					new Object() {
						private int ticks = 0;
						private float waitTicks;
						private LevelAccessor world;

						public void start(LevelAccessor world, int waitTicks) {
							this.waitTicks = waitTicks;
							MinecraftForge.EVENT_BUS.register(this);
							this.world = world;
						}

						@SubscribeEvent
						public void tick(TickEvent.ServerTickEvent event) {
							if (event.phase == TickEvent.Phase.END) {
								this.ticks += 1;
								if (this.ticks >= this.waitTicks)
									run();
							}
						}

						private void run() {
							if (sourceentity instanceof Player _player) {
								ItemStack _stktoremove = (sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY);
								_player.getInventory().clearOrCountMatchingItems(p -> _stktoremove.getItem() == p.getItem(), 1,
										_player.inventoryMenu.getCraftSlots());
							}
							MinecraftForge.EVENT_BUS.unregister(this);
						}
					}.start(world, 1);
				}
				if (sourceentity instanceof LivingEntity _entity)
					_entity.swing(InteractionHand.MAIN_HAND, true);
				if (entity instanceof LivingEntity _entity)
					_entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 120, -10, (false), (false)));
				if (entity instanceof LivingEntity _entity) {
					ItemStack _setstack = ((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).copy());
					_setstack.setCount(1);
					_entity.setItemInHand(InteractionHand.OFF_HAND, _setstack);
					if (_entity instanceof ServerPlayer _serverPlayer)
						_serverPlayer.getInventory().setChanged();
				}
				sourceentity.getPersistentData().putBoolean("Captain", (true));
				new Object() {
					private int ticks = 0;
					private float waitTicks;
					private LevelAccessor world;

					public void start(LevelAccessor world, int waitTicks) {
						this.waitTicks = waitTicks;
						MinecraftForge.EVENT_BUS.register(this);
						this.world = world;
					}

					@SubscribeEvent
					public void tick(TickEvent.ServerTickEvent event) {
						if (event.phase == TickEvent.Phase.END) {
							this.ticks += 1;
							if (this.ticks >= this.waitTicks)
								run();
						}
					}

					private void run() {
						if (entity instanceof LivingEntity _entity)
							_entity.swing(InteractionHand.MAIN_HAND, true);
						if (world instanceof Level _level)
							_level.playSound(_level.isClientSide() ? Minecraft.getInstance().player : null, (entity.getX()), (entity.getY()),
									(entity.getZ()), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("kobolds:kobold_trade")),
									SoundSource.NEUTRAL, 1, 1);
						if (world instanceof ServerLevel _level)
							_level.getServer().getCommands().performCommand(
									new CommandSourceStack(CommandSource.NULL, new Vec3((entity.getX()), (entity.getY()), (entity.getZ())), Vec2.ZERO,
											_level, 4, "", new TextComponent(""), _level.getServer(), null).withSuppressedOutput(),
									"/loot spawn ~ ~ ~ loot kobolds:gameplay/captain_two_loot");
						new Object() {
							private int ticks = 0;
							private float waitTicks;
							private LevelAccessor world;

							public void start(LevelAccessor world, int waitTicks) {
								this.waitTicks = waitTicks;
								MinecraftForge.EVENT_BUS.register(this);
								this.world = world;
							}

							@SubscribeEvent
							public void tick(TickEvent.ServerTickEvent event) {
								if (event.phase == TickEvent.Phase.END) {
									this.ticks += 1;
									if (this.ticks >= this.waitTicks)
										run();
								}
							}

							private void run() {
								if (entity instanceof LivingEntity _entity) {
									ItemStack _setstack = (ItemStack.EMPTY);
									_setstack.setCount(1);
									_entity.setItemInHand(InteractionHand.OFF_HAND, _setstack);
									if (_entity instanceof ServerPlayer _serverPlayer)
										_serverPlayer.getInventory().setChanged();
								}
								sourceentity.getPersistentData().putBoolean("Captain", (false));
								if ((sourceentity instanceof ServerPlayer _plr && _plr.level instanceof ServerLevel
										? _plr.getAdvancements()
												.getOrStartProgress(_plr.server.getAdvancements()
														.getAdvancement(new ResourceLocation("kobolds:kobold_pirate_advancement")))
												.isDone()
										: false) == false) {
									if (sourceentity instanceof ServerPlayer _player) {
										Advancement _adv = _player.server.getAdvancements()
												.getAdvancement(new ResourceLocation("kobolds:kobold_pirate_advancement"));
										AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
										if (!_ap.isDone()) {
											Iterator _iterator = _ap.getRemainingCriteria().iterator();
											while (_iterator.hasNext())
												_player.getAdvancements().award(_adv, (String) _iterator.next());
										}
									}
								}
								MinecraftForge.EVENT_BUS.unregister(this);
							}
						}.start(world, 20);
						MinecraftForge.EVENT_BUS.unregister(this);
					}
				}.start(world, 100);
			} else if (ItemTags.getAllTags().getTagOrEmpty(new ResourceLocation("kobolds:captain_tier_three"))
					.contains((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem())) {
				if ((new Object() {
					public boolean checkGamemode(Entity _ent) {
						if (_ent instanceof ServerPlayer _serverPlayer) {
							return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
						} else if (_ent.level.isClientSide() && _ent instanceof AbstractClientPlayer _clientPlayer) {
							PlayerInfo _pi = Minecraft.getInstance().getConnection().getPlayerInfo(_clientPlayer.getGameProfile().getId());
							return _pi != null && _pi.getGameMode() == GameType.CREATIVE;
						}
						return false;
					}
				}.checkGamemode(sourceentity)) == false) {
					new Object() {
						private int ticks = 0;
						private float waitTicks;
						private LevelAccessor world;

						public void start(LevelAccessor world, int waitTicks) {
							this.waitTicks = waitTicks;
							MinecraftForge.EVENT_BUS.register(this);
							this.world = world;
						}

						@SubscribeEvent
						public void tick(TickEvent.ServerTickEvent event) {
							if (event.phase == TickEvent.Phase.END) {
								this.ticks += 1;
								if (this.ticks >= this.waitTicks)
									run();
							}
						}

						private void run() {
							if (sourceentity instanceof Player _player) {
								ItemStack _stktoremove = (sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY);
								_player.getInventory().clearOrCountMatchingItems(p -> _stktoremove.getItem() == p.getItem(), 1,
										_player.inventoryMenu.getCraftSlots());
							}
							MinecraftForge.EVENT_BUS.unregister(this);
						}
					}.start(world, 1);
				}
				if (sourceentity instanceof LivingEntity _entity)
					_entity.swing(InteractionHand.MAIN_HAND, true);
				if (entity instanceof LivingEntity _entity)
					_entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 120, -10, (false), (false)));
				if (entity instanceof LivingEntity _entity) {
					ItemStack _setstack = ((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).copy());
					_setstack.setCount(1);
					_entity.setItemInHand(InteractionHand.OFF_HAND, _setstack);
					if (_entity instanceof ServerPlayer _serverPlayer)
						_serverPlayer.getInventory().setChanged();
				}
				sourceentity.getPersistentData().putBoolean("Captain", (true));
				new Object() {
					private int ticks = 0;
					private float waitTicks;
					private LevelAccessor world;

					public void start(LevelAccessor world, int waitTicks) {
						this.waitTicks = waitTicks;
						MinecraftForge.EVENT_BUS.register(this);
						this.world = world;
					}

					@SubscribeEvent
					public void tick(TickEvent.ServerTickEvent event) {
						if (event.phase == TickEvent.Phase.END) {
							this.ticks += 1;
							if (this.ticks >= this.waitTicks)
								run();
						}
					}

					private void run() {
						if (entity instanceof LivingEntity _entity)
							_entity.swing(InteractionHand.MAIN_HAND, true);
						if (world instanceof Level _level)
							_level.playSound(_level.isClientSide() ? Minecraft.getInstance().player : null, (entity.getX()), (entity.getY()),
									(entity.getZ()), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("kobolds:kobold_trade")),
									SoundSource.NEUTRAL, 1, 1);
						if (world instanceof ServerLevel _level)
							_level.getServer().getCommands().performCommand(
									new CommandSourceStack(CommandSource.NULL, new Vec3((entity.getX()), (entity.getY()), (entity.getZ())), Vec2.ZERO,
											_level, 4, "", new TextComponent(""), _level.getServer(), null).withSuppressedOutput(),
									"/loot spawn ~ ~ ~ loot kobolds:gameplay/captain_three_loot");
						new Object() {
							private int ticks = 0;
							private float waitTicks;
							private LevelAccessor world;

							public void start(LevelAccessor world, int waitTicks) {
								this.waitTicks = waitTicks;
								MinecraftForge.EVENT_BUS.register(this);
								this.world = world;
							}

							@SubscribeEvent
							public void tick(TickEvent.ServerTickEvent event) {
								if (event.phase == TickEvent.Phase.END) {
									this.ticks += 1;
									if (this.ticks >= this.waitTicks)
										run();
								}
							}

							private void run() {
								if (entity instanceof LivingEntity _entity) {
									ItemStack _setstack = (ItemStack.EMPTY);
									_setstack.setCount(1);
									_entity.setItemInHand(InteractionHand.OFF_HAND, _setstack);
									if (_entity instanceof ServerPlayer _serverPlayer)
										_serverPlayer.getInventory().setChanged();
								}
								sourceentity.getPersistentData().putBoolean("Captain", (false));
								if ((sourceentity instanceof ServerPlayer _plr && _plr.level instanceof ServerLevel
										? _plr.getAdvancements()
												.getOrStartProgress(_plr.server.getAdvancements()
														.getAdvancement(new ResourceLocation("kobolds:kobold_pirate_advancement")))
												.isDone()
										: false) == false) {
									if (sourceentity instanceof ServerPlayer _player) {
										Advancement _adv = _player.server.getAdvancements()
												.getAdvancement(new ResourceLocation("kobolds:kobold_pirate_advancement"));
										AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
										if (!_ap.isDone()) {
											Iterator _iterator = _ap.getRemainingCriteria().iterator();
											while (_iterator.hasNext())
												_player.getAdvancements().award(_adv, (String) _iterator.next());
										}
									}
								}
								MinecraftForge.EVENT_BUS.unregister(this);
							}
						}.start(world, 20);
						MinecraftForge.EVENT_BUS.unregister(this);
					}
				}.start(world, 100);
			}
		}
	}
}
