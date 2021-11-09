package net.mcreator.kobolds.procedures;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;

import net.mcreator.kobolds.KoboldsMod;

import java.util.Map;

public class KoboldPotionHasteProcedure {
	public static void execute(Map<String, Object> dependencies) {
		if (dependencies.get("entity") == null) {
			if (!dependencies.containsKey("entity"))
				KoboldsMod.LOGGER.warn("Failed to load dependency entity for procedure KoboldPotionHaste!");
			return;
		}
		Entity entity = (Entity) dependencies.get("entity");
		if (entity.isAlive()) {
			if (entity instanceof LivingEntity _entity)
				_entity.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 1200, 0));
		}
	}
}