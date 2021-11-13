package net.mcreator.kobolds.procedures;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.GameType;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.InteractionHand;
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

import net.mcreator.kobolds.init.KoboldsModItems;
import net.mcreator.kobolds.init.KoboldsModEntities;
import net.mcreator.kobolds.entity.AbstractKoboldEntity;
import net.mcreator.kobolds.entity.KoboldWarriorEntity;
import net.mcreator.kobolds.entity.KoboldEntity;
import net.mcreator.kobolds.KoboldsMod;

import java.util.Map;
import java.util.Iterator;
import java.util.HashMap;

@Mod.EventBusSubscriber
public class KoboldTraderInteractionProcedure {
	@SubscribeEvent
	public static void onRightClickEntity(PlayerInteractEvent.EntityInteract event) {
		Player sourceentity = event.getPlayer();
		if (event.getHand() != sourceentity.getUsedItemHand()) {
			return;
		}
		Map<String, Object> dependencies = new HashMap<>();
		dependencies.put("x", event.getPos().getX());
		dependencies.put("y", event.getPos().getY());
		dependencies.put("z", event.getPos().getZ());
		dependencies.put("world", event.getWorld());
		dependencies.put("entity", event.getTarget());
		dependencies.put("sourceentity", sourceentity);
		dependencies.put("event", event);
		execute(dependencies);
	}

	public static void execute(Map<String, Object> dependencies) {
		Entity entity = (Entity) dependencies.get("entity");
		Entity sourceentity = (Entity) dependencies.get("sourceentity");
		LevelAccessor world = (LevelAccessor) dependencies.get("world");
		if (EntityTypeTags.getAllTags().getTagOrEmpty(new ResourceLocation("kobolds:trader")).contains(entity.getType())
				&& ((entity instanceof LivingEntity _livEnt ? _livEnt.getOffhandItem() : ItemStack.EMPTY).getItem() == (ItemStack.EMPTY)
						.getItem()) == true) {
			if ((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == Items.EMERALD) {
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
					if (sourceentity instanceof Player _player) {
						ItemStack _stktoremove = new ItemStack(Items.EMERALD);
						_player.getInventory().clearOrCountMatchingItems(p -> _stktoremove.getItem() == p.getItem(), 1,
								_player.inventoryMenu.getCraftSlots());
					}
				}
				if (sourceentity instanceof LivingEntity _entity)
					_entity.swing(InteractionHand.MAIN_HAND, true);
				if (entity instanceof LivingEntity _entity)
					_entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 120, -10, (false), (false)));
				if (entity instanceof LivingEntity _entity) {
					ItemStack _setstack = new ItemStack(Items.EMERALD);
					_setstack.setCount(1);
					_entity.setItemInHand(InteractionHand.OFF_HAND, _setstack);
					if (_entity instanceof ServerPlayer _serverPlayer)
						_serverPlayer.getInventory().setChanged();
				}
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
									"/loot spawn ~ ~ ~ loot kobolds:gameplay/trader_loot");
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
								if ((sourceentity instanceof ServerPlayer _plr && _plr.level instanceof ServerLevel
										? _plr.getAdvancements()
												.getOrStartProgress(_plr.server.getAdvancements()
														.getAdvancement(new ResourceLocation("kobolds:kobold_trader_advancement")))
												.isDone()
										: false) == false) {
									if (sourceentity instanceof ServerPlayer _player) {
										Advancement _adv = _player.server.getAdvancements()
												.getAdvancement(new ResourceLocation("kobolds:kobold_trader_advancement"));
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
			} else if ((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY)
					.getItem() == KoboldsModItems.KOBOLD_IRON_AXE) {
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
					if (sourceentity instanceof LivingEntity _entity) {
						ItemStack _setstack = (ItemStack.EMPTY);
						_setstack.setCount(1);
						_entity.setItemInHand(InteractionHand.MAIN_HAND, _setstack);
						if (_entity instanceof ServerPlayer _serverPlayer)
							_serverPlayer.getInventory().setChanged();
					}
				}
				if (sourceentity instanceof LivingEntity _entity)
					_entity.swing(InteractionHand.MAIN_HAND, true);
				if (entity instanceof LivingEntity _entity)
					_entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 1200, -10, (false), (false)));
				if (entity instanceof LivingEntity _entity) {
					ItemStack _setstack = new ItemStack(KoboldsModItems.KOBOLD_IRON_AXE);
					_setstack.setCount(1);
					_entity.setItemInHand(InteractionHand.OFF_HAND, _setstack);
					if (_entity instanceof ServerPlayer _serverPlayer)
						_serverPlayer.getInventory().setChanged();
				}
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
						if (!entity.level.isClientSide())
							entity.discard();
						if (world instanceof ServerLevel _level) {
							Entity entityToSpawn = new KoboldWarriorEntity(KoboldsModEntities.KOBOLD_WARRIOR, _level);
							entityToSpawn.moveTo((entity.getX()), (entity.getY()), (entity.getZ()), world.getRandom().nextFloat() * 360F, 0);
							if (entityToSpawn instanceof Mob _mobToSpawn)
								_mobToSpawn.finalizeSpawn(_level, world.getCurrentDifficultyAt(entityToSpawn.blockPosition()),
										MobSpawnType.MOB_SUMMONED, null, null);
							world.addFreshEntity(entityToSpawn);
						}
						MinecraftForge.EVENT_BUS.unregister(this);
					}
				}.start(world, 600);
			} else if ((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == Items.BREAD) {
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
					if (sourceentity instanceof Player _player) {
						ItemStack _stktoremove = new ItemStack(Items.BREAD);
						_player.getInventory().clearOrCountMatchingItems(p -> _stktoremove.getItem() == p.getItem(), 1,
								_player.inventoryMenu.getCraftSlots());
					}
				}
				if (sourceentity instanceof LivingEntity _entity)
					_entity.swing(InteractionHand.MAIN_HAND, true);
				if (entity instanceof LivingEntity _entity)
					_entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 120, -10, (false), (false)));
				if (entity instanceof LivingEntity _entity) {
					ItemStack _setstack = new ItemStack(Items.BREAD);
					_setstack.setCount(1);
					_entity.setItemInHand(InteractionHand.OFF_HAND, _setstack);
					if (_entity instanceof ServerPlayer _serverPlayer)
						_serverPlayer.getInventory().setChanged();
				}
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
						if (entity instanceof LivingEntity _entity)
							_entity.addEffect(new MobEffectInstance(MobEffects.HEAL, 12, 0));
						MinecraftForge.EVENT_BUS.unregister(this);
					}
				}.start(world, 100);
			} else if ((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == Items.APPLE
					&& entity.getPersistentData().getDouble("TimerApple") == 0) {
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
					if (sourceentity instanceof Player _player) {
						ItemStack _stktoremove = new ItemStack(Items.APPLE);
						_player.getInventory().clearOrCountMatchingItems(p -> _stktoremove.getItem() == p.getItem(), 1,
								_player.inventoryMenu.getCraftSlots());
					}
				}
				if (sourceentity instanceof LivingEntity _entity)
					_entity.swing(InteractionHand.MAIN_HAND, true);
				if (entity instanceof LivingEntity _entity)
					_entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 120, -10, (false), (false)));
				if (entity instanceof LivingEntity _entity) {
					ItemStack _setstack = new ItemStack(Items.APPLE);
					_setstack.setCount(1);
					_entity.setItemInHand(InteractionHand.OFF_HAND, _setstack);
					if (_entity instanceof ServerPlayer _serverPlayer)
						_serverPlayer.getInventory().setChanged();
				}
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
						if (!world.getEntitiesOfClass(KoboldWarriorEntity.class,
								AABB.ofSize(new Vec3((entity.getX()), (entity.getY()), (entity.getZ())), 24, 24, 24), e -> true).isEmpty()) {
							if (entity instanceof LivingEntity _entity) {
								ItemStack _setstack = (ItemStack.EMPTY);
								_setstack.setCount(1);
								_entity.setItemInHand(InteractionHand.OFF_HAND, _setstack);
								if (_entity instanceof ServerPlayer _serverPlayer)
									_serverPlayer.getInventory().setChanged();
							}
							entity.getPersistentData().putDouble("TimerApple", 24000);
							if ((sourceentity instanceof ServerPlayer _plr && _plr.level instanceof ServerLevel
									? _plr.getAdvancements()
											.getOrStartProgress(_plr.server.getAdvancements()
													.getAdvancement(new ResourceLocation("kobolds:kobold_apple_advance")))
											.isDone()
									: false) == false) {
								if (sourceentity instanceof ServerPlayer _player) {
									Advancement _adv = _player.server.getAdvancements()
											.getAdvancement(new ResourceLocation("kobolds:kobold_apple_advance"));
									AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
									if (!_ap.isDone()) {
										Iterator _iterator = _ap.getRemainingCriteria().iterator();
										while (_iterator.hasNext())
											_player.getAdvancements().award(_adv, (String) _iterator.next());
									}
								}
							}
						} else {
							if (entity instanceof LivingEntity _entity) {
								ItemStack _setstack = (ItemStack.EMPTY);
								_setstack.setCount(1);
								_entity.setItemInHand(InteractionHand.OFF_HAND, _setstack);
								if (_entity instanceof ServerPlayer _serverPlayer)
									_serverPlayer.getInventory().setChanged();
							}
						}
						MinecraftForge.EVENT_BUS.unregister(this);
					}
				}.start(world, 100);
			}
		}
		if (entity instanceof AbstractKoboldEntity
				&& ((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getOffhandItem() : ItemStack.EMPTY).getItem() == Items.SHIELD) == false
				&& ((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == (ItemStack.EMPTY)
						.getItem()) == true) {
			if (sourceentity instanceof LivingEntity _entity)
				_entity.swing(InteractionHand.MAIN_HAND, true);
			if (world instanceof Level _level)
				_level.playSound(_level.isClientSide() ? Minecraft.getInstance().player : null, (entity.getX()), (entity.getY()), (entity.getZ()),
						ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("kobolds:kobold_purr")), SoundSource.NEUTRAL, 1, 1);
			if ((sourceentity instanceof ServerPlayer _plr && _plr.level instanceof ServerLevel
					? _plr.getAdvancements()
							.getOrStartProgress(_plr.server.getAdvancements().getAdvancement(new ResourceLocation("kobolds:kobold_pet_advancement")))
							.isDone()
					: false) == false) {
				if (sourceentity instanceof ServerPlayer _player) {
					Advancement _adv = _player.server.getAdvancements().getAdvancement(new ResourceLocation("kobolds:kobold_pet_advancement"));
					AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
					if (!_ap.isDone()) {
						Iterator _iterator = _ap.getRemainingCriteria().iterator();
						while (_iterator.hasNext())
							_player.getAdvancements().award(_adv, (String) _iterator.next());
					}
				}
			}
		}
	}
}
